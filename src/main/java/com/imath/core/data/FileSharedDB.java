/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;


import java.util.List;
import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imath.core.model.File;
import com.imath.core.model.IMR_User;
import com.imath.core.model.FileShared;

/**
 * The FileShared repository. It provides access to {@link FileShared} database and useful data queries
 * 
 * @author ipinyol
 */

@RequestScoped
public class FileSharedDB {
	@Inject
    private EntityManager em;

    /**
     * Returns a {@link FileShared} from the given id
     * @param id
     * 		The id of the {@link FileShared}  
     * @author ipinyol
     */
    public FileShared findById(Long id) {
        return em.find(FileShared.class, id);
    }
    
    /**
     * Returns the complete list of {@link FileShared} that a userId has access
     * @param userId 
     * 		The userId to be queried
     * @author ipinyol
     */
    public List<FileShared> getFilesSharedByUser(String userId) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FileShared> criteria = cb.createQuery(FileShared.class);
        Root<FileShared> fileShared = criteria.from(FileShared.class);
        Predicate p1 = cb.equal(fileShared.get("userSharedWith").get("userName"), userId);
        criteria.select(fileShared).where(p1);
        List<FileShared> out = em.createQuery(criteria).getResultList();
        
        Iterator<FileShared> it = out.iterator();
        while (it.hasNext()) {
        	File aux = it.next().getFileShared();
        }
        return out;
    }
    
    /**
     * Returns the complete list of {@link FileShared} that share a given fileId directory
     * @param fileId 
     * 		The fileId to be queried 
     * @author ipinyol
     */
    public List<FileShared> getFilesSharedByFile(Long fileId) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FileShared> criteria = cb.createQuery(FileShared.class);
        Root<FileShared> fileShared = criteria.from(FileShared.class);
        Predicate p1 = cb.equal(fileShared.get("fileShared").get("id"), fileId);
        criteria.select(fileShared).where(p1);
        List<FileShared> out = em.createQuery(criteria).getResultList();
        
        Iterator<FileShared> it = out.iterator();
        while (it.hasNext()) {
        	IMR_User aux = it.next().getUserSharedWith();
        }
        return out;
    }
    
    /**
     * Returns the {@link FileShared} given a fileId and a userName
     * @param fileId 
     * 		The fileId to be queried 
     * @author ipinyol
     */
    public List<FileShared> getFileShared(Long fileId, String userName) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FileShared> criteria = cb.createQuery(FileShared.class);
        Root<FileShared> fileShared = criteria.from(FileShared.class);
        Predicate p1 = cb.equal(fileShared.get("fileShared").get("id"), fileId);
        Predicate p2 = cb.equal(fileShared.get("userSharedWith").get("userName"), userName);
        criteria.select(fileShared).where(cb.and(p1,p2));
        List<FileShared> out = em.createQuery(criteria).getResultList();
        return out;
    }
}
