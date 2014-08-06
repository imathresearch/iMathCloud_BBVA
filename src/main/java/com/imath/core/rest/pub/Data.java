package com.imath.core.rest.pub;

import com.imath.core.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;
import com.imath.core.model.Job;
import com.imath.core.model.Job.States;
import com.imath.core.service.FileController;
import com.imath.core.service.JobController;
import com.imath.core.util.Constants;
import com.imath.core.util.FileUtils;
import com.imath.core.util.PublicResponse;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;

/**
 * Public REST web services for Data Access that provides access to file controller
 * 
 * @author iMath
 */

@RequestScoped
@Stateful
@Path(Constants.urlDataPath)
public class Data {
    @Inject private Logger LOG;
    @Inject private FileController fileController;
    @Inject private FileUtils fileUtils;
    @Inject private JobController jobController;
    
    
    @GET
    @Path("/{resourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PublicResponse.StateDTO REST_getDataStatus(@PathParam("resourceId") String resourceId, @Context SecurityContext sc){
        //TODO: Authentication
        //TODO: Now idResource is a Long that maps directly to the id of the File. We have to generate a unique MD5
        //TODO: All files that are in DB will be READY. Otherwise, NOTFOUND. We have to add an status field in the file model to manage this 
        try {
            Long idFile = Long.parseLong(resourceId);       // It will throw exception is resourceId is not numerical, but not its fine
            com.imath.core.model.File file = fileController.getFileStructure(idFile, sc);
            if (file==null) {
                throw new Exception("File not Found exception");
            }
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "data/"+resourceId, file.getName(), PublicResponse.Status.READY); 
            return out;
        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.severe("Error accessing resource data/" + resourceId);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "data/"+resourceId, "", PublicResponse.Status.NOTFOUND); 
            return out;
        }
    }
    
    @GET
    @Path("/status/{fileName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PublicResponse.StateDTO> REST_getDataStatusByName(@PathParam("fileName") String fileName, @Context SecurityContext sc){
        //TODO: Authentication
        //TODO: Now idResource is a Long that maps directly to the id of the File. We have to generate a unique MD5
        //TODO: All files that are in DB will be READY. Otherwise, NOTFOUND. We have to add an status field in the file model to manage this
        List<PublicResponse.StateDTO> outList = new ArrayList<PublicResponse.StateDTO>();
        try {
            List<com.imath.core.model.File> files = fileController.getFileStructure(fileName, sc);
            if (files.size()==0) {
                PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "", fileName, PublicResponse.Status.NOTFOUND);
                outList.add(out);
            } else {
                Iterator<com.imath.core.model.File> it = files.iterator();
                while(it.hasNext()) {
                    com.imath.core.model.File file = it.next();
                    PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "data/"+file.getId(), file.getName(), PublicResponse.Status.READY);
                    outList.add(out);
                }
            }
            return outList;
        }
        catch (Exception e) {
            LOG.severe("Error accessing resource data by fileName " + fileName);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL);
            outList.add(out);
        }
        return outList;
    }
    
    @POST
    @Path("/upload_depracted")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PublicResponse.StateDTO> deprecated_REST_uploadData(MultipartFormDataInput input, @Context SecurityContext sc) {
        //TODO: do it well! This is provisional, to see if files can be really stored
        //TODO: warning when a filename exists. Add some flag to notify about overwriting files etc... 
        
    	String userName = sc.getUserPrincipal().getName();
    	
        String fileName = "";
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        
        List<PublicResponse.StateDTO> listOut = new ArrayList <PublicResponse.StateDTO>();
        List<InputPart> inputParts = uploadForm.get("uploadedFile");
 
        for (InputPart inputPart : inputParts) {
 
        try {
 
            MultivaluedMap<String, String> header = inputPart.getHeaders();
            fileName = getFileName(header);
 
            //convert the uploaded file to inputstream
            InputStream inputStream = inputPart.getBody(InputStream.class,null);
 
            byte [] bytes = fileUtils.getBytesFromInputStream(inputStream);
            LOG.info("Filename: " + fileName);
            com.imath.core.model.File file = this.fileController.createNewFileInROOTDirectory(fileName, sc);
            fileUtils.writeFile(bytes,file.getUrl());
            fileUtils.protectFile(file.getUrl(), userName);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "data/" + file.getId(), fileName, PublicResponse.Status.READY);
            listOut.add(out);
            
          } catch (IOException e) {
              LOG.severe("Error uploading file " + fileName);
              //e.printStackTrace();
              PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
              listOut.add(out);
          } catch (Exception e) {
              LOG.severe("Error uploading file Exception" + fileName);
              //e.printStackTrace();
              PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
              listOut.add(out);
          }
        }
        return listOut;
    }
    
    /**
     * Permits uploading several files, each one at an specific location
     */
    @POST
    @Path("/uploadAdvanced")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PublicResponse.StateDTO> REST_uploadDataAdvanced(MultipartFormDataInput input, @Context SecurityContext sc) {
        //TODO: do it well! This is provisional, to see if files can be really stored
        //TODO: warning when a filename exists. Add some flag to notify about overwriting files etc... 
   	
    	String userName = sc.getUserPrincipal().getName();   	
        String fileName = "";
        List<PublicResponse.StateDTO> listOut = new ArrayList <PublicResponse.StateDTO>();
        
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        
        //1. We determine the number of files to be uploaded
        List<InputPart> numfiles = uploadForm.get("numberFiles");
            
        int total_files = 0;
        if(numfiles != null && !numfiles.isEmpty()){	
        	try {
        		total_files = Integer.parseInt(numfiles.get(0).getBodyAsString());
        		
        	} catch (NumberFormatException | IOException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	}
        }
        else{
        	return listOut;
        }

        //2. We process each file
        String uploaded_file = "uploadedFile-";
        String destination_dir = "destinationDir-";
        String uploaded_f_i = "";
        String destination_d_i = "";
        
        int contador = 0;
        int i = 0;
        while(contador < total_files){
        	i=i+1;
        	
        	try{
        		uploaded_f_i = uploaded_file.concat(String.valueOf(i));
        		destination_d_i = destination_dir.concat(String.valueOf(i));
        		     	
        		List<InputPart> f_i = uploadForm.get(uploaded_f_i);		
        		List<InputPart> d_i  = uploadForm.get(destination_d_i);
        		if(f_i != null  && !f_i.isEmpty()){
        			
        			for(InputPart f : f_i){	
        				//2.1 Process the file i
        				MultivaluedMap<String, String> header = f.getHeaders();
        				fileName = getFileName(header); 
        				
        				//2.2 convert the uploaded file to inputstream
        				InputStream inputStream = f.getBody(InputStream.class,null);
        				byte [] bytes = fileUtils.getBytesFromInputStream(inputStream);
        				
        				//2.3 File extension
        				String imrType = "";
        				String parts[] = fileName.split("\\.");
        				if (parts.length>1) {
        					imrType = parts[parts.length-1];
        					LOG.info("TYPE: " + imrType);
        				}
 
        				//2.4 Process the directory associated with the file i
        				//And store the file in the directory   		       				
        				com.imath.core.model.File file = new com.imath.core.model.File();
        				if(d_i != null){
        					if(d_i.size() == 1){
        						String directory_path = d_i.get(0).getBodyAsString();        						
        						String fileNamePath = directory_path + "/" + fileName;
        						// BUG#20 
        						PublicResponse.StateDTO out = null;
        						try {
        						    file = this.fileController.writeFileFromUpload(bytes, fileNamePath, userName, fileName, imrType);
                                    out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "data/" + file.getId(), fileName, PublicResponse.Status.READY);
        						} catch (Exception e) {
        						    out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL);        						    
        						}
        						// End BUG#20
        						
                				listOut.add(out);
                				contador ++;
        					}
        					else{
        						LOG.severe("More than one directory path has been found under the tag " + destination_d_i);
        						PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
                                listOut.add(out);
                                contador ++;
        					}
        				}
        				else{
        					// The file is stored in the root directory
        					// This is checked inside createNewFileInROOTDirectory
        				    PublicResponse.StateDTO out = null;
        				    try {
        				        file = this.fileController.writeFileFromUploadInROOT(bytes, fileName, sc);
        				        out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "data/" + file.getId(), fileName, PublicResponse.Status.READY);
        				    } catch (Exception e) {
        				        out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL);
        				    }
            				listOut.add(out);
            				contador++;
        				}      				
        			}        			   		
        		}
        		else{
        			//throw exception
        			LOG.severe("No file has been found under the tag " + uploaded_f_i);
        			PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
                    listOut.add(out);
                    contador++;
        		}
        	}
        	catch(Exception e){
        		 LOG.severe("Error uploading file Exception " + fileName);
                 //e.printStackTrace();
                 PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
                 listOut.add(out);
        	}
        }       
        return listOut;     
    }
    
    /**
     * Permits uploading several files at a specific location (only one location)
     */
    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PublicResponse.StateDTO> REST_uploadData(MultipartFormDataInput input, @Context SecurityContext sc) {
        //TODO: do it well! This is provisional, to see if files can be really stored
        //TODO: warning when a filename exists. Add some flag to notify about overwriting files etc... 
        
    	
    	String userName = sc.getUserPrincipal().getName(); 
    	
    	
        String fileName = "";
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        
        List<PublicResponse.StateDTO> listOut = new ArrayList <PublicResponse.StateDTO>();
        
 
        // We process the directory
        List<InputPart> directory = uploadForm.get("destinationDir");
        com.imath.core.model.File dir = new com.imath.core.model.File();
        if(directory != null){
        	
        	if(directory.size()==1){
        		try {
					String directory_path = directory.get(0).getBodyAsString();
					dir = this.fileController.getDir(directory_path,userName);				
				}
        		
        		catch(IMathException em){
            		LOG.severe("Incorrect path specified in tag destinationDir");
                    //e.printStackTrace();
                    PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", "", PublicResponse.Status.FAIL); 
                    listOut.add(out);
                    return listOut;
        		} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
        		catch (Exception e){
					e.printStackTrace();
				}
        		
        	}
        	else{
        		LOG.severe("More than one directory path has been found under the tag destinationDir");
    			PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", "", PublicResponse.Status.FAIL); 
                listOut.add(out);
                return listOut;
        	}
        }
        
        	       
        // We process each file
        List<InputPart> inputParts = uploadForm.get("uploadedFile");       
        if(inputParts == null) {
            return listOut;
        }
        
        for (InputPart inputPart : inputParts) {
 
        try {
 
            MultivaluedMap<String, String> header = inputPart.getHeaders();
            fileName = getFileName(header);
            
 
            //convert the uploaded file to inputstream
            InputStream inputStream = inputPart.getBody(InputStream.class,null);
 
            byte [] bytes = fileUtils.getBytesFromInputStream(inputStream);
            com.imath.core.model.File file = new com.imath.core.model.File();
            
            
            if(directory != null){					
				//File extension
    			String imrType = "";
    			String parts[] = fileName.split("\\.");
    			if (parts.length>1) {
    				imrType = parts[parts.length-1];
    			}
				file = this.fileController.createNewFileInDirectory(dir,fileName, imrType);           	
            }
            else{
            	// no directory path has been specified, default storing in the root directory
            	file = this.fileController.createNewFileInROOTDirectory(fileName, sc);
            }
            
            fileUtils.writeFile(bytes,file.getUrl());
            fileUtils.protectFile(file.getUrl(), userName);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "data/" + file.getId(), fileName, PublicResponse.Status.READY);
            listOut.add(out);
          
            
        }
        catch (IOException e) {
              LOG.severe("Error uploading file " + fileName);
              //e.printStackTrace();
              PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
              listOut.add(out);
        } 
        catch (Exception e) {
              LOG.severe("Error uploading file Exception" + fileName);
              //e.printStackTrace();
              PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", fileName, PublicResponse.Status.FAIL); 
              listOut.add(out);
         }
        }
        return listOut;
    }

    
    /*
     * This service allows for downloading, as a zip file, a file or directory given its id or its absolute path .
     * Also, it is possible to specify the name for the zip file.
     */
    @GET
    @Path("/download")
    @Produces({"application/zip",  MediaType.APPLICATION_OCTET_STREAM }) 
    public Response REST_download(@QueryParam("pathDownloadFile") String pathName_FileDirectory, @QueryParam("idDownloadFile") String id_FileDirectory, @QueryParam("zipFile") String zipFile, @Context SecurityContext sc) {
	
    	Response.ResponseBuilder response = null;
    	
    	if((pathName_FileDirectory == null && id_FileDirectory == null) || (pathName_FileDirectory != null && id_FileDirectory != null)){
    		LOG.severe("Incorrect sintax call");
    		response = Response.status(400);
			return response.build() ;
    	}
    	
    	String userName = sc.getUserPrincipal().getName(); 
     	
    	com.imath.core.model.File f_d = new com.imath.core.model.File();
    	
    	
    	//only one of the following if sentences must be true
    	if(pathName_FileDirectory != null){
    		try {	
				f_d = this.fileController.checkIfFileExistInUser(pathName_FileDirectory, userName);
        	}
        	catch(Exception e){
        		throw new WebApplicationException(e.getCause(), Response.Status.INTERNAL_SERVER_ERROR);
        	}
    	}
    	
    	if(id_FileDirectory != null){
    		try {
        		Long id_file_directory = Long.parseLong(id_FileDirectory);
				f_d = this.fileController.getFile(id_file_directory, userName);
        	}
        	catch(Exception e){
        		throw new WebApplicationException(e.getCause(), Response.Status.INTERNAL_SERVER_ERROR);
        	}	
    	}
    	
    	
    	if(f_d != null){
    		String nameZipFile = f_d.getName();			
			if(zipFile != null){
				nameZipFile = zipFile;
			}

			// We generate directly a Zip OutputStream shall be returned as Response without being stored. 
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			
			URI fileUri = URI.create(f_d.getUrl());
		        java.nio.file.Path pathFile = Paths.get(fileUri.getPath()); 
		        File fileToDownload = new File(pathFile.toString());
		        try {
		            this.fileUtils.addFileToZip("", fileToDownload, zos);
                            zos.close();
                            baos.close();
                        } catch (Exception e) {
                            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                        }
		        
		        response = Response.status(Response.Status.OK)
                              .entity(new ByteArrayInputStream(baos.toByteArray()))
                              .header("Content-Disposition","attachment; filename=\"" + nameZipFile + ".zip\"")
                              .type(MediaType.APPLICATION_OCTET_STREAM);
                        return response.build() ;
    	}
    	else{
    		LOG.severe("The file or directory does not exist");
			response = Response.status(404);
			return response.build();
    	}

    }
    
    /**
    * Permits erasing several files 
    */
   @POST
   @Path("/erase")
   @Consumes("multipart/form-data")
   @Produces(MediaType.APPLICATION_JSON)
   public PublicResponse.StateDTO REST_eraseFiles(MultipartFormDataInput input, @Context SecurityContext sc) {
	   	   
       // We process the input parameters
       Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
       List<InputPart> list_idFiles = uploadForm.get("idDeletedFile");
       List<InputPart> list_pathFiles = uploadForm.get("pathDeletedFile");
       
       if(list_idFiles == null && list_pathFiles == null){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;
       }
       else{
    	   if(list_idFiles != null && list_idFiles.isEmpty() && list_pathFiles != null && list_pathFiles.isEmpty()){
    		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
               return out;
    	   }
       }
       
       String userName = sc.getUserPrincipal().getName(); 
       com.imath.core.model.File f_d = new com.imath.core.model.File();
       
       List<com.imath.core.model.File> all_files = new ArrayList<com.imath.core.model.File>();
       
       Set<String> all_idFiles = new HashSet<String>();     
       
       // Structure to control when the deleted file is an output file of a job
       // In the case that there is a problem deleting the file, the output file has to be recover as part of the job
       HashMap<Long, Long> outputFileJob = new HashMap<Long, Long>();
       
       boolean found = false;
       if(list_idFiles != null){    	   
    	   // We get the user's jobs to check if the file that we want to delete is a job output file.
    	   List<Job> user_jobs = jobController.getUserJobs(userName); 
    	   
    	   String id_file;
    	   for(InputPart idFile: list_idFiles){
    		   try{
				
    			   id_file = idFile.getBodyAsString();
    			   com.imath.core.model.File f = this.fileController.getFile(Long.valueOf(id_file), userName);
    			   if(f != null){
    				   //SPECIAL CASE: we do not allow for deleting the root directory
    				   if(f.getName().equals("ROOT")){
    					   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
        				   return out;
    				   }
    				  
    				   // We do not allow for deleting a file which is an output file of a job in state running or paused.
    				   for (Job j : user_jobs){
    					   Set<com.imath.core.model.File> outputFiles = j.getOutputFiles();    					   
    					   for(com.imath.core.model.File file : outputFiles){
    						   if (file.getId() == Long.valueOf(id_file)){
    							   // The file cannot be deleted because its associated job is still running
    							   if (j.getState() == States.PAUSED || j.getState() == States.RUNNING){
    								   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		        				   return out;
    							   }
    							   jobController.removeOutputFileFromJob(j.getId(), Long.valueOf(id_file), userName);
    							   outputFileJob.put(f_d.getId(), j.getId());
    							   found = true;
    							   break;
    						   }
    					   }
    					   if(found){
    						   break;
    					   }
    				    }    	    				   
    				   all_idFiles.add(id_file);
    			   }   			   
    		   }
    		   catch (Exception e){
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
				   return out;
    		   }
    	   }
       }
       
       found = false;
       if(list_pathFiles != null){
    	   for(InputPart pathFile: list_pathFiles){
    		   
    		   // We get the user's jobs to check if the file that we want to delete is a job output file.
        	   List<Job> user_jobs = jobController.getUserJobs(userName);
        	   
        	   
    		   try{
    			   String path_file = pathFile.getBodyAsString();
    			   
    			   //SPECIAL CASE: we do not allow for deleting the root directory
        		   if(path_file.equals("/")){
        			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    				   return out;
        		   }
        		      			   
    			   f_d = this.fileController.checkIfFileExistInUser(path_file, userName);
    			   if(f_d != null){   				   
    				   // We do not allow for deleting a file which is an output file of a job in state running or paused.
    				   for (Job j : user_jobs){
    					   Set<com.imath.core.model.File> outputFiles = j.getOutputFiles();
    					   for(com.imath.core.model.File file : outputFiles){
    						   if (file.getId() == f_d.getId()){
    							   // The file cannot be deleted because its associated job is still running
    							   if (j.getState() == States.PAUSED || j.getState() == States.RUNNING){
    								   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		        				   return out;
    							   }
    							   jobController.removeOutputFileFromJob(j.getId(),  f_d.getId(), userName);    							   
    							   outputFileJob.put(f_d.getId(), j.getId());
    							   found = true;
    							   break;
    						   }
    					   }
    					   
    					   if(found){
    						   break;
    					   }
    				   }   				     				   
    				   all_idFiles.add(String.valueOf(f_d.getId()));
    			   }
    			   else{
    				   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    				   return out;
    			   }
    		   }
    		   catch (IOException e){
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
				   return out;
    		   }
    		   catch (Exception e) {
    			   e.printStackTrace();   			
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
        	       return out; 
    		   }
    	   }
    	   
       }
       
       try{
    	   this.fileController.eraseListFiles(all_idFiles, sc);
		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "", "", PublicResponse.Status.READY); 
		   return out;
       }
       catch(Exception e){
    	   // The output files of a job that were trying to be deleted have to be recovered as part of the job 
    	   this.fileController.recoverOutputFiles(outputFileJob);
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
	       return out;
    	   
       }
       
      
    }
   
   /**
    * Permits rename a file 
    */
   @POST
   @Path("/rename")
   @Consumes("multipart/form-data")
   @Produces(MediaType.APPLICATION_JSON)
   public PublicResponse.StateDTO REST_renameFiles(MultipartFormDataInput input, @Context SecurityContext sc) {
	   
	   // We process the input parameters
       Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
       List<InputPart> list_idFiles = uploadForm.get("idFile");
       List<InputPart> list_pathFiles = uploadForm.get("pathNameFile");
       List<InputPart> list_newNames = uploadForm.get("newName");
       
       
       //Check parameters
       //newName
       if(list_newNames == null){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;
       }
       if(list_newNames.isEmpty() || (list_newNames.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;
       }
       
       //idFile and pathNameFile
       if((list_idFiles != null && list_pathFiles != null) || (list_idFiles == null && list_pathFiles == null)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;  	   
       }
       if(list_idFiles != null && (list_idFiles.isEmpty() || list_idFiles.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       if(list_pathFiles != null && (list_pathFiles.isEmpty() || list_pathFiles.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       
       String userName = sc.getUserPrincipal().getName(); 
       com.imath.core.model.File f_d = new com.imath.core.model.File();
       
       String id_file = new String();    
       if(list_idFiles != null){
    	   try{				
    		   id_file = list_idFiles.get(0).getBodyAsString();
    		   com.imath.core.model.File f = this.fileController.getFile(Long.valueOf(id_file), userName);
			   if(f != null){
				   //SPECIAL CASE: we do not allow for renaming the root directory
				   if(f.getName().equals("ROOT")){
					   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    				   return out;
				   }	   
			   }
			   else{
				   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
				   return out;
			   }
    	   }
    	   catch (Exception e){
    		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		   return out;
    	   }
    	     	   
       }
       
       if(list_pathFiles != null){
    	   String pathNameFile;
    	   try{
    		   pathNameFile = list_pathFiles.get(0).getBodyAsString();
    		   
    		   //SPECIAL CASE: we do not allow for renaming the root directory
    		   if(pathNameFile.equals("/")){
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
				   return out;
    		   }
    		   
    		   f_d = this.fileController.checkIfFileExistInUser(pathNameFile, userName);
    		   if(f_d != null){
    			   id_file = String.valueOf(f_d.getId());
    		   }
    		   else{
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    			   return out;
    		   }
    	    		   
    	   }
    	   catch (IOException e){
			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
			   return out;
		   }
		   catch (Exception e) {   			
			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    	       return out; 
		   }
       }
       
       try{
    	   String newName = list_newNames.get(0).getBodyAsString();
    	   this.fileController.renameFile(id_file, newName, sc);
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "", "", PublicResponse.Status.READY); 
		   return out;
       }
       catch(Exception e){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
	       return out;
       }
	   
   }
   
   /**
    * Permits create an empty directory 
    */
   @POST
   @Path("/createDirectory")
   @Consumes("multipart/form-data")
   @Produces(MediaType.APPLICATION_JSON)
   public PublicResponse.StateDTO REST_createDirectory(MultipartFormDataInput input, @Context SecurityContext sc) {
	   
       // We process the input parameters
       Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
       List<InputPart> list_idParentDir = uploadForm.get("idParentDir");
       List<InputPart> list_pathParentDir = uploadForm.get("pathParentDir");
       List<InputPart> list_dirName = uploadForm.get("name");
       
       //Check parameters
       //name
       if(list_dirName == null){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;
       }
       if(list_dirName.isEmpty() || (list_dirName.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;
       }
       
       //idParentDir and pathParentDir
       if((list_idParentDir != null && list_pathParentDir != null) || (list_idParentDir == null && list_pathParentDir == null)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;  	   
       }
       if(list_idParentDir != null && (list_idParentDir.isEmpty() || list_idParentDir.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       if(list_pathParentDir != null && (list_pathParentDir.isEmpty() || list_pathParentDir.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       
       String idParentDir = new String();  
       
       if(list_idParentDir != null){
    	   try{				
    		   idParentDir = list_idParentDir.get(0).getBodyAsString();
    	   }
    	   catch (Exception e){
    		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		   return out;
    	   }  	     	   
       }
       
       String userName = sc.getUserPrincipal().getName(); 
       com.imath.core.model.File f_d = new com.imath.core.model.File();
       
       
       if(list_pathParentDir != null){
    	   String pathNameFile;
    	   try{
    		   pathNameFile = list_pathParentDir.get(0).getBodyAsString();
    		   //Check that the parent directory exists
    		   f_d = this.fileController.checkIfFileExistInUser(pathNameFile, userName);
    		   if(f_d != null){
    			   idParentDir = String.valueOf(f_d.getId());
    		   }
    		   else{
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    			   return out;
    		   }
    	    		   
    	   }
    	   catch (IOException e){
			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
			   return out;
		   }
		   catch (Exception e) {   			
			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    	       return out; 
		   }
       }
       
       try{
    	   String dirName = list_dirName.get(0).getBodyAsString();
    	   this.fileController.createDirectory(idParentDir, dirName, sc);
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.OK.getStatusCode(), "", "", PublicResponse.Status.READY); 
		   return out;
       }
       catch(Exception e){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
	       return out;
       }
 
   }
   
   /**
    * Permits create an empty directory or file 
    */
   @POST
   @Path("/createFile")
   @Consumes("multipart/form-data")
   @Produces(MediaType.APPLICATION_JSON)
   public PublicResponse.StateDTO REST_createFile(MultipartFormDataInput input, @Context SecurityContext sc) {
	   
       // We process the input parameters
       Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
       List<InputPart> list_idParentDir = uploadForm.get("idParentDir");
       List<InputPart> list_pathParentDir = uploadForm.get("pathParentDir");
       List<InputPart> list_dirName = uploadForm.get("dirName");
       List<InputPart> list_fileName = uploadForm.get("fileName");
       
       //Check parameters
       //list_dirName and list_fileName
       if((list_dirName != null && list_fileName != null) || (list_dirName == null && list_fileName == null)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;  	   
       }
       if(list_dirName != null && (list_dirName.isEmpty() || list_dirName.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       if(list_fileName != null && (list_fileName.isEmpty() || list_fileName.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       
       //idParentDir and pathParentDir
       if((list_idParentDir != null && list_pathParentDir != null) || (list_idParentDir == null && list_pathParentDir == null)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out;  	   
       }
       if(list_idParentDir != null && (list_idParentDir.isEmpty() || list_idParentDir.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       if(list_pathParentDir != null && (list_pathParentDir.isEmpty() || list_pathParentDir.size()>1)){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
           return out; 
       }
       
       String name_file_directory = new String();
       String typeFile = new String();
       
       if(list_dirName != null){
    	   try {
    		   name_file_directory = list_dirName.get(0).getBodyAsString();
    		   typeFile = "directory";
    	   } 
    	   catch (IOException e) {
    		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		   return out;
    	   }	   
       }
       
       if(list_fileName != null){
    	   try {
    		   name_file_directory = list_fileName.get(0).getBodyAsString();
    		   typeFile = "regular";
    	   } 
    	   catch (IOException e) {
    		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		   return out;
    	   }
       }
            
       String idParentDir = new String();  
       
       if(list_idParentDir != null){
    	   try{				
    		   idParentDir = list_idParentDir.get(0).getBodyAsString();
    	   }
    	   catch (Exception e){
    		   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    		   return out;
    	   }  	     	   
       }
      
       String userName = sc.getUserPrincipal().getName(); 
       com.imath.core.model.File f_d = new com.imath.core.model.File();
       
       if(list_pathParentDir != null){
    	   String pathNameFile;
    	   try{
    		   pathNameFile = list_pathParentDir.get(0).getBodyAsString();
    		   //Check that the parent directory exists
    		   f_d = this.fileController.checkIfFileExistInUser(pathNameFile, userName);
    		   if(f_d != null){
    			   idParentDir = String.valueOf(f_d.getId());
    		   }
    		   else{
    			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    			   return out;
    		   }
    	    		   
    	   }
    	   catch (IOException e){
			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
			   return out;
		   }
		   catch (Exception e) {   			
			   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
    	       return out; 
		   }
       }
      
       try{
    	   //String dirName = list_dirName.get(0).getBodyAsString();
    	   this.fileController.createFile(idParentDir, name_file_directory, typeFile, sc);
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "", "", PublicResponse.Status.READY); 
		   return out;
       }
       catch(Exception e){
    	   PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", "", PublicResponse.Status.NOTFOUND); 
	       return out;
       }
 
   }
    
    
    /**
     * header sample
     * {
     *  Content-Type=[image/png], 
     *  Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     **/
    //get uploaded filename, is there a easy way in RESTEasy? 
    private String getFileName(MultivaluedMap<String, String> header) {
 
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
        	
            if ((filename.trim().startsWith("filename"))) {
 
                String[] name = filename.split("=");
 
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }
 
    // Only for unit test. It simulates the injection process
    public void setFileController (FileController fileController) {
        this.fileController = fileController;
    }
    
    public void setLOG(Logger LOG) {
        this.LOG = LOG;
    }
    
    public void setFileUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }
}
