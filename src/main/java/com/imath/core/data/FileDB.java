/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;

import com.imath.core.util.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imath.core.model.File;
import com.imath.core.model.MathGroup;
import com.imath.core.model.Job;
import com.imath.core.model.Job.States;
import com.imath.core.util.Constants;
/**
 * The File repository. It provides access to {@link File} database and useful data queries
 * 
 * @author iMath
 */
//@ApplicationScoped
@RequestScoped
public class FileDB {

    @Inject
    private EntityManager em;

    /**
     * Returns a {@link File} from the given id
     * @param id
     * 		The id of the {@link File}  
     * @author iMath
     */
    public File findById(Long id) {
        return em.find(File.class, id);
    }
    
    /**
     * Returns a {@link File} from the given id if the file is accessible by the userName
     * @param id The id of the {@link File}
     * @param userName The logged user name  
     * @author iMath
     */
    public File findByIdSecured(Long id, String userName) {
        File file = em.find(File.class, id);
        if (file != null) {
            if (!file.getOwner().getUserName().equals(userName)) {
                file = null;
            }
        }
        return file;
    }
    
    /**
     * Returns a root {@link File} from a given user id
     * @param userId
     *      The id of the {@link IMR_User}  
     * @author iMath
     */
    public File findROOTByUserId(String userId) throws Exception {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("owner").get("userName"), userId);
        Predicate p2 = cb.equal(file.get("name"), Constants.rootNAME);
        criteria.select(file).where(cb.and(p1,p2));
        List<File> out = em.createQuery(criteria).getResultList();
        if (out.size()==0) {
            throw new Exception ("Critical: No ROOT file found for user: " + userId);
        }
        if (out.size()>1) {
            throw new Exception ("Critical: More than one ROOT file found for user: " + userId);
        }
        return out.get(0);      // only one file is expected
    }
    
    /**
     * Returns the file which url contains
     * @param fileName The file name
     * @param userId The user id  
     * @author iMath
     */
    public File findByPath(String path, String userId) throws Exception{
    	
    	String uriFile = Constants.URI_HEAD + Constants.LOCALHOST + path;
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("owner").get("userName"), userId);
        Predicate p2 = cb.equal(file.get("url"), uriFile);
        Predicate pAND = cb.and(p1,p2);
        criteria.select(file).where(pAND);
        List<File> out = em.createQuery(criteria).getResultList();
        
        if(out.size() > 1){
        	throw new Exception();
        }
        
        return out.get(0);
    	
    }
    
    /**
     * Returns the list of files with the given name that hang from ROOT
     * @param fileName The file name
     * @param userId The user id  
     * @author iMath
     */
    public List<File> findByName(String fileName, String userId) throws Exception {
        File root = this.findROOTByUserId(userId);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("owner").get("userName"), userId);
        Predicate p2 = cb.equal(file.get("name"), fileName);
        Predicate p3 = cb.equal(file.get("dir"), root);
        Predicate pAND = cb.and(p1,p2);
        criteria.select(file).where(cb.and(pAND,p3));
        List<File> out = em.createQuery(criteria).getResultList();
        return out;
    }
    
    /**
     * Returns all the files from a user with the given name 
     * @param fileName The file name
     * @param userId The user id  
     * @author iMath
     */
    public List<File> findAllByName(String fileName, String userId) throws Exception {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("owner").get("userName"), userId);
        Predicate p2 = cb.equal(file.get("name"), fileName);
        Predicate pAND = cb.and(p1,p2);
        criteria.select(file).where(pAND);
        List<File> out = em.createQuery(criteria).getResultList();
        return out;
    }
    
    public List<File> findAll(String userId) throws Exception {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("owner").get("userName"), userId);      
        criteria.select(file).where(p1);
        List<File> out = em.createQuery(criteria).getResultList();
        return out;
    }
    
  
    
    /**
     * Returns the complete list of {@link File} that belongs to the userId
     * @param userId 
     * 		The userId of the owner of the files 
     * @author iMath
     */
    public List<File> getFilesByUser(String userId, Long dirId) {
    	// We look for the directories with no parent directories, and query each one of them with getFilesByDir. In this way we
    	// return the list of files in pre-order, which is perfect for plotting it.
    	
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("owner").get("userName"), userId);
        Predicate p2 = (dirId == null) ? cb.isNull(file.get("dir")) : cb.equal(file.get("dir").get("id"), dirId);
        criteria.select(file).where(cb.and(p1,p2));
        List<File> out = em.createQuery(criteria).getResultList();
        
        Iterator<File> it = out.iterator();
        List<File> ret = new ArrayList<File>();
        while (it.hasNext()) {
        	File fileAux = it.next();
        	List<File> aux = this.getFilesByDir(fileAux.getId(),true);
        	ret.addAll(aux);
        }
        return ret;
    }
    
    /**
     * Returns the complete list of {@link File} that are below a given directory file. (It's a recursive method)
     * @param fileId 
     * 		The fileId of the directory
     * @param includeRoot
     * 		Indicates whether to include the root file directory as an element of the returned list or not.   
     * @author iMath
     */
    public List<File> getFilesByDir(Long fileId, boolean includeRoot) {
    	
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<File> criteria = cb.createQuery(File.class);
        Root<File> file = criteria.from(File.class);
        Predicate p1 = cb.equal(file.get("dir").get("id"), fileId);
        criteria.select(file).where(p1);
        // Order the files by name
        criteria.orderBy(cb.asc(file.get("name")));        
        List<File> out = em.createQuery(criteria).getResultList();
        
        Iterator<File> it = out.iterator();
        List<File> globalOutput = new ArrayList<File>();
        if (includeRoot) {
        	File root = this.findById(fileId);
        	globalOutput.add(root);    
        }                
        
        globalOutput.addAll(out);
        while (it.hasNext()) {
        	File fileAux = it.next();
        	Set<Job> aux = fileAux.getJobs();
        	Iterator<Job> itJob = aux.iterator();
        	Iterator<MathGroup> itMathGroup = fileAux.getOwner().getRole().getMathGroups().iterator();
        	while(itMathGroup.hasNext()) {
        		itMathGroup.next().getRoles().iterator();
        	}
        	if (fileAux.getIMR_Type().equals("dir")) {
        		List<File> auxList = this.getFilesByDir(fileAux.getId(),false);
        		globalOutput.addAll(auxList);
        	}
        }
        
        
        return globalOutput;
    }
    
   
    
    
}