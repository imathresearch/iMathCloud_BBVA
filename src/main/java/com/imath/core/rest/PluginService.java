/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.rest;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

//import org.codehaus.jackson.JsonFactory;
//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.type.TypeReference;

//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.JsonParser;
//import org.codehaus.jackson.annotate.JsonAutoDetect;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.annotate.JsonDeserialize;
//import org.codehaus.jackson.type.TypeReference;

import com.imath.core.model.File;
import com.imath.core.model.Job;
import com.imath.core.model.Session;
import com.imath.core.model.Job.States;
import com.imath.core.model.JobResult;
import com.imath.core.model.IMR_User;
import com.imath.core.model.MathGroup;
import com.imath.core.model.MathFunction;

import com.imath.core.data.MainServiceDB;
import com.imath.core.rest.JobService.JobDTO;
import com.imath.core.security.SecurityManager;
import com.imath.core.service.PluginController;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
//import java.io.IOException;
//import java.io.IOException;
//import java.util.HashMap;
import java.util.HashSet;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// Just to check
import com.imath.core.service.JobController;

import java.util.logging.Logger;

/**
 * A REST web service that notifies output results from previously issued web services to python nodes 
 * 
 * @author ipinyol
 */
@Path("/plugin_service")
@RequestScoped
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateful
public class PluginService {
	@Inject private MainServiceDB db;
	@Inject private Logger LOG;
	
	@Inject private JobController jc;
	@Inject private PluginController pc;
	
	@GET
    @Path("/output/{idJob}/{result}")
    @Produces(MediaType.APPLICATION_JSON)
    public void REST_placeOutput(@PathParam("idJob") Long idJob, @PathParam("result") String result) {		
		try {
			LOG.info(result);
			States state = States.FINISHED_OK;
			if (result==null) { // TODO: We should establish an error codification, and messaging...
				state = States.FINISHED_ERROR;
			}
			jc.reportJobFinalization(idJob, state, result);
		}
		catch (Exception e) {
			LOG.severe("Error placing WS result. idJob: " + idJob);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	

	@GET
    @Path("/getMathFunctions/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<MathFunctionDTO> REST_getMathFunctions(@PathParam("userName") String userName, @Context SecurityContext sc) {		
		try {
		    SecurityManager.secureBasic(userName, sc);
			IMR_User user = db.getIMR_UserDB().findById(userName);
			Set<MathGroup> groups = user.getRole().getMathGroups();
			List<MathFunction> functions = new ArrayList<MathFunction>();
			Iterator<MathGroup> it = groups.iterator();
			while (it.hasNext()) {
				MathGroup p = it.next();
				List<MathFunction> faux = db.getMathFunctionDB().getMathFunctionsByGroup(p.getId());
				Iterator<MathFunction> itf = faux.iterator();
				while (itf.hasNext()) {
					functions.add(itf.next());
				}
			}
			return prepareToSubmit(functions);
		}
		catch (Exception e) {
			LOG.severe("Error getting math functions for userName: " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@POST
    @Path("/submitMathFunction/{userName}/{idMath}/{idFile}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response REST_submitMathFunction(@PathParam("userName") String userName, @PathParam("idMath") Long idMath, @PathParam("idFile") Long idFile, ParamDTO paramDTO, @Context SecurityContext sc) {		
		try {
			//TODO: Convert JSON to List
		    SecurityManager.secureBasic(userName, sc);
			Session session = getSession(userName);
			List<String> params = new ArrayList<String>();
			File file = getFile(idFile);
			params.add(file.getUrl());
			params.add(file.getDir().getUrl());
			params.add(paramDTO.toString());
			Set<File> files = new HashSet<File>();
			files.add(file);
			JobController.Pair pair = pc.callPlugin(idMath, session, params, files);
			jc.makeAJAXCall(pair);
			Job job = pair.job;
            JobDTO out = new JobDTO();
            out.jobToJobDTO(job);
            return Response.status(Response.Status.OK).entity(out).build();
		}
		catch (Exception e) {
		    e.printStackTrace();
			LOG.severe("Error submitting job for userName: " + userName + " - " + e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private Session getSession(String userName) {
		return db.getSessionDB().findByUser_and_OpenSession(userName);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private File getFile(Long idFile) {
		return db.getFileDB().findById(idFile);
	}
	
	private List<MathFunctionDTO> prepareToSubmit(List<MathFunction> functions) {
		Iterator<MathFunction> it = functions.iterator();
		List<MathFunctionDTO> out = new ArrayList<MathFunctionDTO>();
		while(it.hasNext()) {
			MathFunction function = it.next();
			MathFunctionDTO aux = new MathFunctionDTO();
			aux.id=function.getId();
			aux.description=function.getDescription();
			aux.shortName=function.getShortName();
			aux.idGroup=function.getMathGroup().getId();
			out.add(aux);
		}
		return out;
	}
	
	private class MathFunctionDTO {
		public Long id;
		public String description;
		public String shortName;
		public Long idGroup;
		
		public MathFunctionDTO() {}
	}
	
	private static class ParamDTO{
		
//		public String params;
//		
//		public ParamDTO(){}
//		public ParamDTO(String params) throws JsonParseException, JsonMappingException, IOException{
//			JsonFactory factory = new JsonFactory(); 
//		    ObjectMapper mapper = new ObjectMapper(factory); 
//		    //File from = new File("albumnList.txt"); 
//		    TypeReference<HashMap<String,Object>> typeRef 
//		          = new TypeReference< 
//		                 HashMap<String,Object> 
//		               >() {}; 
//		    HashMap<String,Object> o 
//		         = mapper.readValue(params, typeRef); 
//		    System.out.println("Got " + o);
//		}
//		
//		public void setParam(String params){
//			this.params = params;
//		}
//		public String getParam(){
//			return this.params;
//		}
		public List<Integer> columns;
		public boolean hasHeader;
		public Integer polinomialDegree;
		
		public ParamDTO(){}
		/**
		 * Construct ParamDTO objects from the GUI forms.
		 * @param columns Identification number of columns pre-selected of CSV files to calculate operations
		 * @param pol Linear Regresion polinomial degree, for 1 linear regresion, for 2 quadratic regresion, etc... 
		 */
		public ParamDTO(List<Integer> columns, boolean hasHeader, Integer pol){
			this.setColumns(columns);
			this.setHasHeader(hasHeader);
			this.setPolinomialDegree(pol);
		}	

		public void setColumns(List<Integer> params) {
		    this.columns = params;
		}
		public void setHasHeader(boolean params) {
			this.hasHeader = params;			
		}
		public void setPolinomialDegree(Integer params) {
			this.polinomialDegree = params;			
		}
	
		public List<Integer> getColumns(){
			return this.columns;
		}
		public boolean getHasHeader(){
			return this.hasHeader;
		}
		public Integer getPolinomialDegree(){
			return this.polinomialDegree;
		}
		
		public String toString(){
			Writer writer = new StringWriter();
			JsonFactory factory = new JsonFactory();
			JsonGenerator jGenerator = null;
			try {
				jGenerator = factory.createJsonGenerator(writer);
				jGenerator.writeStartObject(); //{
				 
				jGenerator.writeFieldName("columns"); // "messages" :
				jGenerator.writeStartArray(); // [
			 
				Iterator<Integer> it = this.columns.iterator();
				while (it.hasNext()) {
					jGenerator.writeNumber((Integer) it.next());
				}
				jGenerator.writeEndArray(); // ]
				
				jGenerator.writeBooleanField("hasHeader", this.hasHeader);
				jGenerator.writeNumberField("polinomialDegree", this.polinomialDegree);
				
				jGenerator.writeEndObject(); // }
				jGenerator.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return writer.toString();
		}
	}
	
}
