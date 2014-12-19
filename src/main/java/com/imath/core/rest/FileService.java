/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.imath.core.model.File;
import com.imath.core.model.FileShared;
import com.imath.core.model.IMR_User;
import com.imath.core.data.MainServiceDB;
import com.imath.core.service.FileController;
import com.imath.core.util.Constants;
import com.imath.core.util.FileUtils;
import com.imath.core.security.SecurityManager;

import java.util.logging.Logger;

/**
 * A REST web service that provides access to file controller
 * 
 * @author ipinyol
 */
@Path("/file_service")
@RequestScoped
@Stateful
public class FileService {
	@Inject private FileController fc;
	@Inject private MainServiceDB db;
	@Inject private Logger LOG;
	
	private static String LOG_PRE = Constants.LOG_PREFIX_SYSTEM + "[FileService]";
	
	@GET
    @Path("/getFileContent/{userName}/{id}")
	//@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FileContentDTO REST_getFileContent(@PathParam("userName") String userName, @PathParam("id") Long id, @QueryParam("page") Integer page, @Context SecurityContext sc){
	    LOG.info(LOG_PRE + "[getFileContent]" + userName + " " + id.toString() );
		try { 
		    SecurityManager.secureBasic(userName, sc);
			FileContentDTO out = new FileContentDTO();
			File file = db.getFileDB().findById(id);
			if (page == null){
				out.content = fc.getFileContent(userName, file);
			}else{
				out.content = fc.getFileContent(userName, file, page);
			}
			out.type=file.getIMR_Type();
			out.name=file.getName();
			out.id = file.getId();
			return out;
		}
		catch (Exception e) {
			LOG.severe("Error reading file id: " + id + " from user: "+ userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@GET
    @Path("/getFile/{userName}/{id}")
	//@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public FileDTO REST_getFile(@PathParam("userName") String userName, @PathParam("id") Long id, @Context SecurityContext sc){
	    LOG.info(LOG_PRE + "[getFile]" + userName + " " + id.toString());
		//TODO: Test needed!!
		try { 
		    SecurityManager.secureBasic(userName, sc);
		    
			File file = db.getFileDB().findByIdSecured(id, userName);
			FileDTO filedto = new FileDTO();
			filedto.id = file.getId();
			filedto.userNameOwner = file.getOwner().getUserName();
			filedto.name = file.getName();
			filedto.type = file.getIMR_Type();
			if(file.getDir() != null)
				filedto.dir = file.getDir().getId();
			FileUtils fu = new FileUtils();			
			
			filedto.absolutePath = fu.getAbsolutePath(file.getUrl(), userName);
			return filedto;
		}
		catch (Exception e) {
			LOG.severe("Error reading file id: " + id + " from user: "+ userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	/**
	 * Generates a new image file (png) from a base64 coded string. This is the back end for the drag&drop functionality.
	 * @param userName
	 * @param id       The directory id
	 * @param iMathConnectUser
	 * @param content
	 * @param sc
	 * @return
	 */
	@POST
	@Path("/generateImageFile/{userName}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response REST_generateImageFile(@PathParam("userName") String userName, @PathParam("id") Long id, ImageDTO image, @Context SecurityContext sc) {
	    // TODO: Test
	    LOG.info(LOG_PRE + "[generateImageFile]" + userName);
	    Response.ResponseBuilder builder = null;
	    try {
            SecurityManager.secureBasic(userName, sc);
        }
        catch (Exception e) {
            LOG.severe("Not enough provileges:" + userName);
            builder = Response.status(Response.Status.UNAUTHORIZED);
            return builder.build();
        }
	    try {
	        String [] c = image.data.split("image/png;base64,");
	        if (c.length==2) {
	            fc.createImage(id, "png", "base64", c[1]);
	        }         
	    } catch (Exception e) {
	        LOG.severe("Internal error: " + userName);
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return builder.build();
	    }
	    builder = Response.ok();
        return builder.build();
	}
	
	@POST
    @Path("/saveFileContent/{userName}/{id}/{iMathConnectUser}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response REST_saveFileContent(@PathParam("userName") String userName, @PathParam("id") Long id, @PathParam("iMathConnectUser") String iMathConnectUser, List<String> content, @Context SecurityContext sc) {
	    LOG.info(LOG_PRE + "[saveFileContent]" + userName + " " + id.toString());
		Map<String, String> responseObj = new HashMap<String, String>();
		responseObj.put("ok","200");
		Response.ResponseBuilder builder = Response.ok().entity(responseObj);
		try {
		    SecurityManager.secureBasic(userName, sc);
			File file = db.getFileDB().findById(id);
			if (file.getOpenByUser() == null || file.getOpenByUser().equals(iMathConnectUser)){
				fc.saveFileContent(userName, file, content);
			}
			else{
				builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
			}
		}
		catch (Exception e) {
			LOG.severe("Error saving file id: " + id + " from user: "+ userName);
			responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		//return new Long(1);
		return builder.build();
    }
	
	@POST
	@Path("/saveFileContentPage/{userName}/{id}/{page}/{iMathConnectUser}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response REST_saveFileContent(@PathParam("userName") String userName, @PathParam("id") Long id, @PathParam("page") Long page, @PathParam("iMathConnectUser") String iMathConnectUser,List<String> content, @Context SecurityContext sc) {
	    LOG.info(LOG_PRE + "[saveFileContent]" + userName + " " + id.toString() + " " + page.toString());
        Map<String, String> responseObj = new HashMap<String, String>();
        Response.ResponseBuilder builder = null;
        try {
            SecurityManager.secureBasic(userName, sc);
            File file = db.getFileDB().findById(id);
            if (file.getOpenByUser() == null || file.getOpenByUser().equals(iMathConnectUser)){
            	fc.saveFileContent(userName, file, content, page);
            	int upToPage = (int) Math.ceil((double)content.size()/fc.getPagination());
            	responseObj.put("upToPage", ""+upToPage);
            	builder = Response.ok().entity(responseObj);
            }
            else{
            	builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
            }
        }
        catch (Exception e) {
            LOG.severe("Error saving file id: " + id + " from user: "+ userName);
            responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        }
        //return new Long(1);
        return builder.build();
    }


	@POST
	@Path("pasteItem/{userName}/{action}/{origin}/{destiny}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response REST_pasteItem(@PathParam("userName") String userName, 
				@PathParam("action") String action,
				@PathParam("origin") Long origin,
				@PathParam("destiny") Long destiny,
				@Context SecurityContext sc) {
	    
	    LOG.info(LOG_PRE + "[pasteItem]" + userName + " " + action + " " + origin.toString() + " " + destiny.toString());
        // TODO: Handle Response object properly.
        Response.ResponseBuilder builder = null;
		try {
			SecurityManager.secureBasic(userName, sc);
			fc.pasteItem(userName, action, origin, destiny);	
		} catch (Exception e) {
            e.printStackTrace();
			LOG.severe("Error for action " + action + " of item " + origin + " to " + destiny + " from user: "+userName + " - " + e.getMessage());
			throw new WebApplicationException(Response.Status.BAD_REQUEST); 
		}

        builder = Response.ok();
        return builder.build();
	}
   
	
	@GET
    @Path("/getFiles/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileDTO> REST_getFiles(@PathParam("userName") String userName, @Context SecurityContext sc) {
	    LOG.info(LOG_PRE + "[getFiles]" + userName);
        List<FileDTO> ret = null;
        if (sc== null) {
            ret = internalGetFiles(userName, null);
        } else if (sc.getUserPrincipal().getName().equals(userName)) {
            try {
                SecurityManager.secureBasic(userName, sc);
                fc.updateFilesFromStorage(userName);
                ret = internalGetFiles(userName, null);
            } catch (Exception e) {
                LOG.severe("Error getting files from user: "+ userName + " - " + e.getMessage());
                throw new WebApplicationException(Response.Status.NOT_FOUND); 
            }
        }
        return ret;
    }
	
	
    @GET
    @Path("/getFiles/{userName}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileDTO> REST_getFiles(@PathParam("userName") String userName, @PathParam("id") Long id, @Context SecurityContext sc) {
        LOG.info(LOG_PRE + "[getFiles]" + userName);
        List<FileDTO> ret = null;
        if (sc== null) {
            ret = internalGetFiles(userName, id);
        } else if (sc.getUserPrincipal().getName().equals(userName)) {
            ret = internalGetFiles(userName, id);
        } 
        return ret;
    }
    
    private List<FileDTO> internalGetFiles(String userName , Long id){
      //TODO: Test needed!!
        try {
            List<File> files = db.getFileDB().getFilesByUser(userName, id);
            List<FileDTO> out = new ArrayList<FileDTO>();
            Iterator<File> it = files.iterator();
            while(it.hasNext()) {
                    FileDTO filedto = new FileDTO();
                    File file = it.next();
                    filedto.id = file.getId();
                    filedto.name=file.getName();
                    filedto.type = file.getIMR_Type();                    
                    filedto.absolutePath = file.getPath();
                    filedto.dir = null;
                    if(file.getDir()!=null) {
                            filedto.dir = file.getDir().getId();
                    }
                    filedto.sharingState = file.getSharingState();
                    filedto.openByUser = file.getOpenByUser();
                    System.out.println("FileName " + filedto.name + " open by " + filedto.openByUser);
                    out.add(filedto);
            }
            return out;
        }
        catch (Exception e) {
                LOG.severe("Error getting the files from user: "+ userName + " - " + e.getMessage());
                throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@GET
    @Path("/getSharedFiles/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileDTO> REST_getSharedFiles(@PathParam("userName") String userName, @Context SecurityContext sc) {
		//TODO: Test needed!!
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
	    LOG.info(LOG_PRE + "[getSharedFiles]" + userName);
		try {
		    SecurityManager.secureBasic(userName, sc);
			List<FileShared> filesShared = db.getFileSharedDB().getFilesSharedByUser(userName);
			List<FileDTO> out = new ArrayList<FileDTO>();

			// A virtual ROOT, so all shared directories are attached from here. 
			// TODO: Do it right!
			FileDTO filedtoVirtual = new FileDTO(new Long(-100000),"ROOT-EXTERNAL",null, "dir", File.Sharing.NO,"");
			out.add(filedtoVirtual);
			
			Iterator<FileShared> it = filesShared.iterator();
			while(it.hasNext()) {
				FileShared fileShared = it.next(); 
				File file = fileShared.getFileShared();
				Long dirId = filedtoVirtual.id;	// All first level files are attached to the virtual root
				
				FileDTO filedto = new FileDTO(file.getId(),file.getName(),dirId, file.getIMR_Type(),File.Sharing.NO, file.getOwner().getUserName());
				filedto.permission = fileShared.getPermission();
				
				out.add(filedto);
				// if the shared file is a directory, all sub directories and files are also shared
				if (filedto.type.equals("dir")) {
					
					List<File> files = db.getFileDB().getFilesByDir(filedto.id,true);
					Iterator<File> it2 = files.iterator();
					while(it2.hasNext()) {
						File auxF = it2.next();
						dirId =  auxF.getDir().getId();	
						FileDTO filedtoAux = new FileDTO(auxF.getId(),auxF.getName(),dirId, auxF.getIMR_Type(),File.Sharing.NO, auxF.getOwner().getUserName());
						filedtoAux.permission = fileShared.getPermission();
						out.add(filedtoAux);
					}
				}
			}
			return out;
		}
		catch (Exception e) {
			LOG.severe("Error getting shared files from user: "+ userName + " - " + e.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@GET
    @Path("/getUsersShared/{idFile}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> REST_getUsersShared(@PathParam("idFile") Long idFile) {
		//TODO: Test needed!!
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
	    LOG.info(LOG_PRE + "[getUsersShared]" + idFile.toString());
		try {
			List<FileShared> filesShared = db.getFileSharedDB().getFilesSharedByFile(idFile);
			List<UserDTO> out = new ArrayList<UserDTO>();
			Iterator<FileShared> it = filesShared.iterator();
			while(it.hasNext()) {
				FileShared act = it.next();
				UserDTO userDTO = new UserDTO();
				userDTO.userName = act.getUserSharedWith().getUserName();
				userDTO.name = act.getUserSharedWith().getFirstName() + " " + act.getUserSharedWith().getLastName();
				userDTO.email = act.getUserSharedWith().getEMail();
				userDTO.permission = act.getPermission();
				out.add(userDTO);
			}
			return out;
		}
		catch (Exception e) {
			LOG.severe("Error getting shared users from file: "+ idFile + " - " + e.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@POST
    @Path("/addUserShared/{idFile}/{user}/{perm}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO REST_addUserShared(@PathParam("idFile") Long idFile, @PathParam("user") String user, @PathParam("perm") FileShared.Permission perm) {
		//TODO: Test needed!!
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		//TODO:Error logs more accurate
		//TODO: Important: If a parent folder is already shared, then it should not be allowed.
		//TODO: Important: All son folders that are already shared should become unshared.
	    LOG.info(LOG_PRE + "[addUserShared]" + idFile.toString() + " " + user);
		try {
			UserDTO out = new UserDTO();
			IMR_User userObj = null;
			if(user.indexOf('@')>0) {
				// It means we have to look the user by its email
				userObj = db.getIMR_UserDB().findByEMail(user);
			} else {
				try {
					userObj = db.getIMR_UserDB().findById(user);
				} catch (Exception e) {
					userObj = null;
				}
			}
			if (userObj != null) { 
				List<FileShared> filesShared = db.getFileSharedDB().getFileShared(idFile, userObj.getUserName());
				if (filesShared.size() == 0) {
					File file = db.getFileDB().findById(idFile);
					file.setSharingState(File.Sharing.YES);
					FileShared fileShared = new FileShared();
					fileShared.setUserSharedWith(userObj);
					fileShared.setFileShared(file);
					fileShared.setPermission(perm);
					db.makePersistent(fileShared);
					db.makePersistent(file);
					out.permission = perm;
					out.name = userObj.getFirstName() + " " + userObj.getLastName();
					out.userName = userObj.getUserName();
					out.email = userObj.getEMail();
				} else {
					LOG.info("Info - Error: User already sharing the folder");
				}
			} else {
				LOG.info("Info - Error: Username or email not found");
			}
			return out;
		}
		catch (Exception e) {
			return null;
			//LOG.severe("Error getting shared users from file: "+ idFile + " - " + e.getMessage());
			//throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@POST
    @Path("/removeUserShared/{idFile}/{user}")
    //@Produces(MediaType.APPLICATION_JSON)
    public void REST_removeUserShared(@PathParam("idFile") Long idFile, @PathParam("user") String userName) {
		//TODO: Test needed!!
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		// Error logs more accurate
	    LOG.info(LOG_PRE + "[removeUserShared]" + idFile.toString() + " " + userName);
		try {
			List<FileShared> filesShared = db.getFileSharedDB().getFileShared(idFile, userName);
			if (filesShared != null) {
				if (filesShared.size() == 1) {
					db.remove(filesShared.get(0));
					filesShared = db.getFileSharedDB().getFilesSharedByFile(idFile);
					if (filesShared.size() == 0) {
						// This is the case when the folder is no longer shared by anybody.
						File auxFile = db.getFileDB().findById(idFile);
						auxFile.setSharingState(File.Sharing.NO);
						db.makePersistent(auxFile);
					}
				} else {
					LOG.severe("More than one instance of file " + idFile + " and " + userName + " in FileShared");
				}
			} else {
				LOG.severe("No entrance of file " + idFile + " and " + userName + " in FileShared");
			}
		}
		catch (Exception e) {
			LOG.severe("Error getting shared users from file: "+ idFile + " - " + e.getMessage());
			//throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@POST
    @Path("/blockFile/{id}/{userName}/{iMathUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response REST_blockFile(@PathParam("id") Long idFile, @PathParam("userName") String userName, @PathParam("iMathUser") String blockByUser, @Context SecurityContext sc) {
	    LOG.info(LOG_PRE + "[blockFile]" + idFile + " " + blockByUser);
	    try { 
		    SecurityManager.secureBasic(userName, sc);
		    
			File file = db.getFileDB().findByIdSecured(idFile, userName);
			System.out.println(file.getId());
			if(file.getOpenByUser() == null){
				file.setOpenByUser(blockByUser);
				db.makePersistent(file);
				FileDTO filedto = new FileDTO();
				filedto.id = file.getId();
				filedto.userNameOwner = file.getOwner().getUserName();
				filedto.name = file.getName();
				filedto.type = file.getIMR_Type();
				return Response.status(Response.Status.OK).entity(filedto).build();
			}
			else{
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
		catch (Exception e) {
			LOG.severe("Error blocking file id: " + idFile + " by iMathConnectUser: "+ blockByUser);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }
	
	@POST
    @Path("/unBlockFile/{id}/{userName}/{iMathUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response REST_unBlockFile(@PathParam("id") Long idFile, @PathParam("userName") String userName, @PathParam("iMathUser") String blockByUser, @Context SecurityContext sc) {
	    LOG.info(LOG_PRE + "[unBlockFile]" + idFile + " " + blockByUser);
	    try { 
		    SecurityManager.secureBasic(userName, sc);
		    
			File file = db.getFileDB().findByIdSecured(idFile, userName);
			if(file.getOpenByUser().equals(blockByUser)){
				file.setOpenByUser(null);
				FileDTO filedto = new FileDTO();
				filedto.id = file.getId();
				filedto.userNameOwner = file.getOwner().getUserName();
				filedto.name = file.getName();
				filedto.type = file.getIMR_Type();
				return Response.status(Response.Status.OK).entity(filedto).build();
			}
			return Response.status(Response.Status.BAD_REQUEST).build();
				
		}
		catch (Exception e) {
			LOG.severe("Error blocking file id: " + idFile + " by iMathConnectUser: "+ blockByUser);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }
	
	
	
	private class FileContentDTO {
		public List<String> content;
		public String type;
		public String name;
		public Long id;
		public FileContentDTO() {}
	}
	
	private class UserDTO {
		public String userName;
		public String name;
		public String email;
		public String rootName;
		public FileShared.Permission permission;
		public UserDTO() {}
		
		public UserDTO(String userName, String name, String email) {
			this.userName = userName;
			this.name = name;
			this.email = email;
		}
	}
	
	public static class FileDTO {
		public Long id;
		public String name;
		public Long dir;
		public String type;
		public File.Sharing sharingState;
		public FileShared.Permission permission;	// Only for FileShared purposes
		public String userNameOwner;
		public String absolutePath;
		public String openByUser;
		
		
		public FileDTO() {}
		
		public FileDTO(Long id, String name, Long dir, String type, File.Sharing sharingState, String userNameOwner) {
			this.id = id;
			this.name = name;
			this.dir = dir;
			this.type = type;
			this.sharingState = sharingState;
			this.userNameOwner = userNameOwner;
		
		}
		
	}
	
	public static class ImageDTO {
	    public String data;
	}
	
	public static class FilePath{
		public String absolutePath;
		public Long id;
		public String userNameOwner;
		
		public FilePath(){}
		
	}
	

}
