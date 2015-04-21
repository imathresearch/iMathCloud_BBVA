package com.imath.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateful;
import javax.inject.Inject;

import com.imath.core.config.AppConfig;
import com.imath.core.exception.IMathException;
import com.imath.core.rest.FileService.FileDTO;
import com.sun.jna.Library;
import com.sun.jna.Native;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;


/**
 * Class or useful methods for physical files treatment 
 * @author iMath
 *
 */
@Stateful
public class FileUtils {
    @Inject
    private Logger LOG;
    /**
     * Generates a zip file 
     * @param outputFile The output zip file name to generate
     * @param fileNames The list of filenames (URI) that must be
     * @throws IOException if some error is produced  
     */
    public void generateZip(String outputFile, List<String> fileNames) throws IOException{
        byte[] buffer = new byte[1024];
        
        URI u = URI.create(outputFile);
        java.nio.file.Path outputFilePath = Paths.get(u.getPath());
        
        FileOutputStream fos = new FileOutputStream(outputFilePath.toString());
        ZipOutputStream zos = new ZipOutputStream(fos);
    
        for(String file : fileNames){
            URI uFile = URI.create(file);
            java.nio.file.Path filePath = Paths.get(uFile.getPath());
            
            ZipEntry ze= new ZipEntry(filePath.getFileName().toString());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(filePath.toString());
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            in.close();
        }
        zos.closeEntry();
        zos.close();
    }
    
    public void addFileToZip(String path, File srcFile, ZipOutputStream zos) throws Exception {
		    
    	if (srcFile.isDirectory()){
    		addDirToZip(path, srcFile, zos);
		}
		else{
			byte[] buf = new byte[1024];
		    int len;
		    FileInputStream in = new FileInputStream(srcFile);
		    String relative_path = new String();
		    if(path.equals("")){
		    	relative_path = srcFile.getName();
		    }
		    else{
		    	relative_path = path + "/" + srcFile.getName();
		    }
		    zos.putNextEntry(new ZipEntry(relative_path));
		    while ((len = in.read(buf)) > 0){
		    	zos.write(buf, 0, len);
		    }
		}
    }
	
	public void addDirToZip(String path, File srcDir, ZipOutputStream zos) throws Exception {

		for (String fileName : srcDir.list()){
			if (path.equals("")){
				addFileToZip(srcDir.getName(), new File(srcDir.getPath() + "/" + fileName), zos);
		    } 
		    else{
		    	addFileToZip(path + "/" + srcDir.getName(), new File(srcDir.getPath() + "/" + fileName), zos);
		    }
		}
	}
	
	
    
    /**
     * Writes the content into the specified uri file
     * @param content The array of bytes to be written
     * @param uri The String containing the uri of the destination file
     * @throws IOException if some error is produced  
     */
    public void writeFile(byte[] content, String uri) throws IOException {
 
        URI u = URI.create(uri);
        java.nio.file.Path path = Paths.get(u.getPath());
        
        File file = new File(path.toString());
 
        if (!file.exists()) {
            file.createNewFile();
        }
 
        FileOutputStream fop = new FileOutputStream(file);
 
        fop.write(content);
        fop.flush();
        fop.close();
    }
    
    /**
     * Returns the content bytes coming from the specified InputStream
     * @param inputStream The input stream
     * @return an array of bytes
     * @throws IOException if a reading error is produced
     */
    public byte [] getBytesFromInputStream(InputStream inputStream) throws IOException {
        byte [] bytes = IOUtils.toByteArray(inputStream);
        return bytes;
    }
    
    public List<String> trashListFiles(List<com.imath.core.model.File> listFiles) throws Exception {
		
    	boolean recover = false;
		List<String> list_trashLocation = new ArrayList<String>();
		int i; 
		for (i = 0; i < listFiles.size(); i++){
			String trashLocation = trashFile(listFiles.get(i));
			if(trashLocation == null){
				recover = true;
				break;
			}
			list_trashLocation.add(trashLocation);
		}
		
		if(recover){
			for(int j = 0; j < list_trashLocation.size(); j++){
				if(!restoreFile(listFiles.get(j), list_trashLocation.get(j))){
					throw new IMathException(IMathException.IMATH_ERROR.RECOVER_PROBLEM, "data/" + listFiles.get(j).getId()); 
				}
			}
			throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + listFiles.get(i).getId());
		}
		
		return list_trashLocation;
    }
    
    /**
     * Return the size in bytes of the directory. If the directory does not exists, it returns 0
     * @param uri
     * @return
     */
    public long dirSize(String uri) {
        URI u = URI.create(uri);
        java.nio.file.Path path = Paths.get(u.getPath());
        
        File file = new File(path.toString());
        return dirSizeRec(file);
    }
    
    private long dirSizeRec(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += dirSizeRec(file);
        }
        return length;
    }
    
    
    /**
     * Return the tree files the given directory. If the directory does not exists, it returns an empty map
     * @param uri
     * @return
     */
    public Map<File,File> dirFiles(String uri) {
        URI u = URI.create(uri);
        java.nio.file.Path path = Paths.get(u.getPath());
        Map<File,File> treeFile = new HashMap<File,File>();
        File file = new File(path.toString());
        dirFilesRec(file, treeFile);        
        return treeFile;
    }
    
    private void dirFilesRec(File directory, Map<File,File> treeFile) {
        for (File file : directory.listFiles()) {
            treeFile.put(file,  directory);
            if (file.isDirectory())
                dirFilesRec(file, treeFile);
        }
    }
    
    public String trashFile(com.imath.core.model.File file) throws IOException{
  		
		File trashDirectory = new File(AppConfig.getProp(AppConfig.IMATH_TRASH));
		String [] trashFiles = trashDirectory.list();
		List <String> listTrashFiles = Arrays.asList(trashFiles);
		
		String fileName = file.getName();
		boolean present = true;
		String trashlocation = new String();
		while(present){
			if(listTrashFiles.contains(fileName)){
				String uid = "_" + UUID.randomUUID().toString();
				fileName = fileName.concat(uid);
			}
			else{
				trashlocation = AppConfig.getProp(AppConfig.IMATH_TRASH) + "/" + fileName;				
				if(!this.moveFile(file.getUrl(), trashlocation)){
					trashlocation = null;
				}
				present = false;
			}
		}
		return trashlocation;
    }
    
    public boolean restoreFile(com.imath.core.model.File file, String trashLocation){
    	  	
    	if(this.moveFile(trashLocation, file.getUrl())){
    		return true;
    	}
    	return false;
    }
    
    
    public void extractInitialFiles(String zipfile, com.imath.core.model.File root, String userName) {
        InputStream in = this.getClass().getResourceAsStream(zipfile);
        ZipInputStream stream = new ZipInputStream(in);
        byte[] buffer = new byte[2048];
        try  {
            // now iterate through each item in the stream. The get next
            // entry call will return a ZipEntry for each file in the
            // stream
            ZipEntry entry;
            while((entry = stream.getNextEntry())!=null)  {
                // Once we get the entry from the stream, the stream is
                // positioned read to read the raw data, and we keep
                // reading until read returns 0 or less.
                if (entry.isDirectory()) {
                    String url = root.getUrl()+"/"+entry;
                    this.createDirectory(url);
                    this.protectDirectory(url, userName);
                } else {
                    String urlRoot = root.getUrl();
                    URI uri = new URI(urlRoot);
                    String outpath = uri.getPath() + "/" + entry.getName();
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(outpath);
                        int len = 0;
                        while ((len = stream.read(buffer)) > 0)
                        {
                            output.write(buffer, 0, len);
                        }
                    } finally {
                        // we must always close the output file
                        if(output!=null) output.close();
                        this.protectFile(root.getUrl()+"/"+entry.getName(), userName);
                    }
                }
            }
        } catch (Exception e) {
            LOG.severe("Error unziping initial example files");
            e.printStackTrace();
        }
        finally
        {
            try {
                stream.close();
            } catch (Exception e) {
                LOG.severe("Error closing zip stream for initial examples");
                e.printStackTrace();
            }
        }
    }
    /**
     * Physically move an imath File to a different destination
     * @param file - imath file to be moved
     * @param destination - absolute path where the file is going to be moved
     * @return true is the move is performed, false in the other case
     */
    /*public boolean moveFile(com.imath.core.model.File file, String location, boolean inverse){
    	URI aux = URI.create(file.getUrl());
		java.nio.file.Path path = Paths.get(aux.getPath());
		
		File file_source; 
		File file_destination;
		
		if (!inverse){
			file_source = new File(path.toString());
			file_destination = new File(location);
		}
		else{ // to restore a file in its original location
			file_source = new File(location);
			file_destination = new File(path.toString());			
		}
		
		if(file_source.exists()){
			return file_source.renameTo(file_destination);
		}
		else{
			return false;
		}  
    }
    */
    /**
     * Physically move an imath File to a different destination
     * @param file - imath file to be moved
     * @param destination - absolute path where the file is going to be moved
     * @return true is the move is performed, false in the other case
     */
    public boolean moveFile(String source_location, String destination_location){
    	URI uri_source = URI.create(source_location);
		java.nio.file.Path path_source = Paths.get(uri_source.getPath());
    	
		URI uri_destination = URI.create(destination_location);
		java.nio.file.Path path_destination = Paths.get(uri_destination.getPath());
  	
		File file_source = new File(path_source.toString());
		File file_destination = new File(path_destination.toString());
		
		if(file_source.exists()){
			return file_source.renameTo(file_destination);
		}
		else{
			return false;
		}  
    }
    
    
    /**
     * Physically create a new directory
     * @param String urlDirectory - url of the directory to be created
     * @return true if the directory is created, false in the other case (cannot be created o already exists)
     */
    public boolean createDirectory(String urlDirectory){
    	
    	URI uri_directory = URI.create(urlDirectory);
		java.nio.file.Path path_directory = Paths.get(uri_directory.getPath());
    	
    	File directory = new File(path_directory.toString());
    	
    	return directory.mkdir();
    }
    
    // Only should be called in empty directories
    public void protectDirectory(String urlDirectory, String user) {
        URI uriDirectory = URI.create(urlDirectory);
        java.nio.file.Path pathDirectory = Paths.get(uriDirectory.getPath());
        if (uriDirectory.getHost().equals(Constants.LOCALHOST) || uriDirectory.getHost().equals(Constants.LOCALHOST_String)) {
            SystemUtil.chmodDir(pathDirectory.toString(), "700");    // Only the owner can read, write, execute in the directory
            SystemUtil.chownDir(pathDirectory.toString(), user, Constants.IMATHSYSTEMGROUP);
        } else {
            // TODO: Manage remote file systems
        }
    }
    
    public void protectFile(String urlDirectory, String user) {
        URI uriDirectory = URI.create(urlDirectory);
        java.nio.file.Path pathDirectory = Paths.get(uriDirectory.getPath());
        if (uriDirectory.getHost().equals(Constants.LOCALHOST) || uriDirectory.getHost().equals(Constants.LOCALHOST_String)) {
            SystemUtil.chmodFile(pathDirectory.toString(), "600");    // Only the owner can read, write, execute in the directory
            SystemUtil.chownFile(pathDirectory.toString(), user, Constants.IMATHSYSTEMGROUP);
        } else {
            // TODO: Manage remote file systems
        }
    }
    
    /**
     * Physically create a new file/directory
     * @param String urlParentDir - url of the parent directory
     * @param type - file type (directory or regular file)
     * @return "dir" of the file is a directory, the extension if the file is a regular file, or null if the file cannot be created
     * @throws IOException 
     */
    public String createFile(String urlParentDir, String name, String type) throws IOException{
    	
    	String urlFile = urlParentDir + "/" + name;
    	URI uriFile= URI.create(urlFile);
		java.nio.file.Path pathFile = Paths.get(uriFile.getPath());
    	
    	File file = new File(pathFile.toString());
    	
    	boolean success = false;
    	String typeFile = null;
    	if(type.equals("directory")){
    		success = file.mkdir();
    		if(success)
    			typeFile = "dir";
    	}
    	else{
    		if(type.equals("regular")){  			
				success = file.createNewFile();			
    			if(success){
    				String [] nameParts = name.split("\\.");
    				typeFile = nameParts[nameParts.length-1];
    			}
    		}
    	}
    	
    	return typeFile;
    }   
    
    public String getAbsolutePath(String file_url, String userName){
    
    	String [] parts = file_url.split("/");
    	
    	int start_index = 0;
		for(int i = 0; i < parts.length; i++){
			if(parts[i].equals(userName)){
				start_index = i + 1;
				break;
			}
		}
		
		String absolutePath = new String("/");
		
		//No ROOT
		if(start_index != parts.length){
			for(int h = start_index; h < parts.length-1; h++){
				absolutePath = absolutePath.concat(parts[h]);
				absolutePath = absolutePath.concat("/");
			}
		
			absolutePath = absolutePath.concat(parts[parts.length-1]);
		}
		
    	return absolutePath;
    	
    }
    
  
    public static class SystemUtil {
        // TODO: refactor as soon as possible to use only JNA calls
        //private static CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);
        
        //interface CLibrary extends Library {
        //    public int chmod(String path, int mode);
        //}
        
        public static void chownFile(String path, String user, String group) {
            String line = "chown " + user + ":" + group + " " + path;
            System.out.println(line);
            try {
                Process p = Runtime.getRuntime().exec(line);
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public static void chownDir(String path, String user, String group) {
            String line = "chown " + user + ":" + group + " -R " + path;
            System.out.println(line);
            try {
                Process p = Runtime.getRuntime().exec(line);
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public static void chmodDir(String path, String mode) {
            String line = "chmod -R " + mode + " " + path;
            try {
                Process p = Runtime.getRuntime().exec(line);
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public static void chmodFile(String path, String mode) {
            String line = "chmod  " + mode + " " + path;
            try {
                Process p = Runtime.getRuntime().exec(line);
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public static class CopyDirVisitor extends SimpleFileVisitor<Path> {
	    private Path fromPath;
	    private Path toPath;
	    private StandardCopyOption copyOption = java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
	    
	    public CopyDirVisitor(Path from, Path to){
	    	this.fromPath = from;
	    	this.toPath = to;	    	
	    }
	    
	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	    	Path target = toPath.resolve(fromPath.relativize(dir));
	    	if(!target.startsWith(dir)){
	            try {
	                Files.copy(dir, target, copyOption);
	            } catch (FileAlreadyExistsException e) {
	                 if (!Files.isDirectory(target))
	                     throw e;
	            }
            	return FileVisitResult.CONTINUE;
	    	}	    	
	    	return FileVisitResult.TERMINATE;
	    	
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	        Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
	        return FileVisitResult.CONTINUE;
	    }
	}
}
