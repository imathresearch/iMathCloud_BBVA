package com.imath.core.rest;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.util.Constants;



@Path("/notebook_service")
@RequestScoped
@Stateful
public class NotebookService {
	
	final String type = new String();
	
	@GET
    @Path("/getNotebook/{id}/{port}")
    @Produces(MediaType.TEXT_HTML)
    public Response REST_getNotebookHTML(@PathParam("id") String idNotebook, @PathParam("port") String port, @Context SecurityContext sec){
		
		System.out.println("Getting notebook");
		String urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + idNotebook;
		try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();
            
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();
            String replacement = "$1=\"http://" + Constants.IMATH_HOST + ":" + Constants.IMATH_PORT + "/iMathCloud/rest/notebook_service/files/" + port + "/static/";    	    	
            while ((line = rd.readLine()) != null){            	
            	//For .js and .css files
            	line = line.replaceAll("(src|href)=\"/static/", replacement);
            	
            	//base urls required in some .js files loaded
            	if(line.equals("data-base-project-url=/") || line.equals("data-base-kernel-url=/")){
            		line = line + "iMathCloud/rest/notebook_service/"+port+"/";
            	}
            	
            	//print option
            	if(line.startsWith("<li id=\"print_notebook\">")){
            		String addon = "href=\"http://" + Constants.IMATH_HOST + ":" + Constants.IMATH_PORT + "/iMathCloud/rest/notebook_service/" + port + "/";
            		line = line.replaceAll("href=\"/", addon);
            	}
            	            	
            	sb.append(line + '\n');            	               
            }                      
            result = sb.toString(); 
            //System.out.println(result);
            return Response.status(Response.Status.OK).entity(result).build();            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
	}
	
	@GET
    @Path("/files/{port}/{path: .+}")
    @Produces({"text/css", "application/javascript", "image/png"})
    public Response REST_getNotebookFile(@PathParam("port") String port, @PathParam("path") String path, @Context SecurityContext sec){
		//System.out.println("Getting file for notebook");
		String urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + path;
		
		try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();

            String contentType = urlConn.getContentType();
            //System.out.println("Content type " + contentType);
           
            // if the file is an image
            if(contentType.equals("image/png")){
            	BufferedImage image = ImageIO.read(urlConn.getInputStream());
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	ImageIO.write(image, "png", baos);
            	byte[] imageData = baos.toByteArray();
            	return Response.ok(imageData).build();          
            	// return Response.ok(new ByteArrayInputStream(imageData)).build();
            }
                                    
            // if the file is css or javascript
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();                	    
            while ((line = rd.readLine()) != null){            	
            	sb.append(line + '\n');            	               
            }                      
            result = sb.toString();                         
            return Response.status(Response.Status.OK).entity(result).build();  
            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
	}
	
	@GET	
    @Path("/{port}/notebooks/{id}")
    @Produces({MediaType.APPLICATION_JSON, "application/x-python"})
    public Response REST_getNotebook(@PathParam("port") String port, @PathParam("id") String id, @QueryParam("format") String format, @Context SecurityContext sec){
		System.out.println("Getting notebook object");
		
		String urlString = new String(); 
		if(format == null){
			urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + "notebooks/" + id;
		}
		else{
			// case in which the notebook can be downloaded 
			urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + "notebooks/" + id + "?format=" + format;
		}
		
		try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();
            
            System.out.println(urlConn.getContentType());
         
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();                	    
            while ((line = rd.readLine()) != null){            	
            	sb.append(line + '\n');            	               
            }        
            
            result = sb.toString();
            
            Response response;             
            if(format != null){
            	// case in which the notebook can be downloaded 
	            response =  Response.status(Response.Status.OK).entity(result)
	            .header("Content-Disposition", urlConn.getHeaderField("Content-Disposition"))
	            .type(urlConn.getContentType()).build();
            }
            else{
            	response = Response.status(Response.Status.OK).entity(result).build(); 
            }
	            
            return response;                       
            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
	}
	
	@PUT
    @Path("/{port}/save_notebooks/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response REST_saveNotebook(@PathParam("port") String port, @PathParam("id") String id, String content, @Context SecurityContext sec){
		System.out.println("Saving notebook ");
		String urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + "notebooks/" + id;
		
		try {
            URL url = new URL(urlString);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("PUT");
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream());
            out.write(content);
            out.close();            
            urlConn.connect();
           
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();                	    
            while ((line = rd.readLine()) != null){            	
            	sb.append(line + '\n');            	               
            }                      
            result = sb.toString();                         
            return Response.status(Response.Status.OK).entity(result).build();  
            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
	}
	
	@POST
    @Path("/{port}/kernels")
    @Produces(MediaType.TEXT_HTML)
    public Response REST_startNotebookKernel(@PathParam("port") String port, @QueryParam("notebook") String id, @Context SecurityContext sec){
		System.out.println("Starting notebook kernel");
		String urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + "kernels?notebook=" + id;		
		
		try {
            URL url = new URL(urlString);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);     //Set method to POST
            urlConn.connect();
           
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();                	    
            while ((line = rd.readLine()) != null){            	
            	sb.append(line + '\n');            	               
            }                      
            result = sb.toString();           
            return Response.status(Response.Status.OK).entity(result).build();  
            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
	}
	

	@POST
    @Path("/{port}/kernels/{idKernel}/{action}")
    @Produces(MediaType.TEXT_HTML)
    public Response REST_interruptKernel(@PathParam("port") String port, @PathParam("idKernel") String id, @PathParam("action") String action, @Context SecurityContext sec){
		System.out.println("Interrupting/Restart notebook kernel");
		String urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + "kernels/" + id + "/" + action;
		
		try {
            URL url = new URL(urlString);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);     //Set method to POST
            urlConn.connect();
           
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();                	    
            while ((line = rd.readLine()) != null){            	
            	sb.append(line + '\n');            	               
            }                      
            result = sb.toString();           
            return Response.status(Response.Status.OK).entity(result).build();  
            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
	}	
	
	@GET
    @Path("/{port}/{notebook}/print")
    @Produces(MediaType.TEXT_HTML)
    public Response REST_printNotebook(@PathParam("port") String port, @PathParam("notebook") String id, @Context SecurityContext sec){
		System.out.println("Printing notebook");
		String urlString = "http://" + Constants.IMATH_HOST + ":" + port + "/" + id + "/print";
		
		try {
            URL url = new URL(urlString);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();
            
            String contentType = urlConn.getContentType();
            System.out.println("Content type " + contentType);
           
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();
            String replacement = "$1=\"http://" + Constants.IMATH_HOST + ":" + Constants.IMATH_PORT + "/iMathCloud/rest/notebook_service/files/" + port + "/static/";
            while ((line = rd.readLine()) != null){
            	//For .js and .css files
            	line = line.replaceAll("(src|href)=\"/static/", replacement);
            	
            	//base urls required in some .js files loaded
            	if(line.equals("data-base-project-url=/") || line.equals("data-base-kernel-url=/")){
            		line = line + "iMathCloud/rest/notebook_service/"+port+"/";
            	}
            	
            	sb.append(line + '\n');            	               
            }                      
            result = sb.toString();
            //String code_result = new String(result.getBytes("UTF-8"), "UTF-8");
            return Response.status(Response.Status.OK).entity(result).build();  
            
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
	}
	
	
	@GET
	@Path("/getNotebookList/{host}/{port}")
	@Produces(MediaType.APPLICATION_JSON)
	public String REST_getNotebookList(@PathParam("host") String host, @PathParam("port") String port){
	    //LOG.info(LOG_PRE + "[getNotebookList]" + host + " " + port);
		String urlString = "http://" + host + ":" + port + "/notebooks";
		try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();
            
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();
            while ((line = rd.readLine()) != null){
                  sb.append(line + '\n');
            }
            
            //System.out.println(sb.toString());
            result = sb.toString();
            System.out.println("Notebooks result ");
            System.out.println(result);
                       
            
            return result; //Response.status(Response.Status.OK).build();
        } catch(Exception e) {
            e.printStackTrace();
            return null; //Response.status(Response.Status.BAD_REQUEST).build();
        }
	}
	
	@GET
	@Path("/newNotebook/{host}/{port}")
	@Produces(MediaType.TEXT_PLAIN)
	public String REST_newNotebook(@PathParam("host") String host, @PathParam("port") String port){
	    //LOG.info(LOG_PRE + "[newNotebook]" + host + " " + port);
		String urlString = "http://" + host + ":" + port + "/new";
		try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();
            
            String result = new String();
    		BufferedReader rd  = null;
    	    StringBuilder sb = null;
    		//read the result from the server
            rd  = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            sb = new StringBuilder();
            String line = new String();
            while ((line = rd.readLine()) != null){
            	if(line.startsWith("<li id=\"print_notebook\">")){
            		System.out.println(line);
            		Pattern pattern = Pattern.compile("href=\"/(.*?)/print");
            	    Matcher matcher = pattern.matcher(line);            	   
            	    matcher.find();    
            	    result = matcher.group(1);            		            		            		
            		//result = line.substring(34,70);
            		break;
            	}                
            }
            
            //System.out.println(sb.toString());            
            System.out.println("New notebook result");
            System.out.println(result);
                       
            
            return result; //Response.status(Response.Status.OK).build();
        } catch(Exception e) {
            e.printStackTrace();
            return null; //Response.status(Response.Status.BAD_REQUEST).build();
        }
	}
	

}
