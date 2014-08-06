/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.imath.core.model.File;
import com.imath.core.model.Job;
import com.imath.core.model.MathGroup;
import com.imath.core.model.Job.*;
/**
 * The Jobs repository. It provides access to {@link Job} database and useful data queries
 * 
 * @author iMath
 */
//@ApplicationScoped
@RequestScoped
public class JobDB {

    @Inject
    private EntityManager em;

    /**
     * Returns a {@link Job} from the given id
     * @param id
     * 		The id of the {@link Job}  
     * @author iMath
     */
    public Job findById(Long id) {
    	em.flush();
        return em.find(Job.class, id);
    }

    /**
     * Returns a {@link Job} from the given id if the job is accessible by the userName
     * @param id The id of the {@link File}
     * @param userName The logged user name  
     * @author iMath
     */
    public Job findByIdSecured(Long id, String userName) {
    	
        em.flush();
        Job job = em.find(Job.class, id);     
        if (job!=null) {
            if (!job.getOwner().getUserName().equals(userName)) {
                job = null;
            }
        }
        return job;
    }
    
    /**
     * Returns a list of {@link Job} that belongs to the userId and that are in the State state.
     * @param userId 
     * 		The userId of the owner of the jobs 
     * @param active
     * 		The {@link State} of the jobs. 
     * @author iMath
     */
    public List<Job> getJobsByUser_and_State(String userId, States state) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Job> criteria = cb.createQuery(Job.class);
        Root<Job> job = criteria.from(Job.class);
        Predicate p1 = cb.equal(job.get("owner").get("userName"), userId);
        Predicate p2 = cb.equal(job.get("state"), state);
        criteria.select(job).where(cb.and(p1,p2));
        List<Job> out =  em.createQuery(criteria).getResultList();
        Iterator<Job> it = out.iterator();
        while (it.hasNext()) {
        	Set<File> aux = it.next().getFiles();
        	Iterator<File> itFile = aux.iterator();
        	while (itFile.hasNext()) {
        		File file = itFile.next();
        		Iterator<MathGroup> itMathGroup = file.getOwner().getRole().getMathGroups().iterator();
        		while (itMathGroup.hasNext()) {
        			itMathGroup.next().getRoles().iterator();
        		}
        	}
        	 
        }
        return out;
    }
    
    /**
     * Returns a list of {@link Job} that belongs to the userId.
     * @param userId 
     * 		The userId of the owner of the jobs 
     * @param active
     * 		The {@link State} of the jobs. 
     * @author iMath
     */
    public List<Job> getJobsByUser(String userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Job> criteria = cb.createQuery(Job.class);
        Root<Job> job = criteria.from(Job.class);
        Predicate p1 = cb.equal(job.get("owner").get("userName"), userId);
        criteria.select(job).where(p1);
        List<Job> out =  em.createQuery(criteria).getResultList();
        Iterator<Job> it = out.iterator();
        while (it.hasNext()) {
        	Set<File> aux = it.next().getFiles();
        	Iterator<File> itFile = aux.iterator();
        	while (itFile.hasNext()) {
        		File file = itFile.next();
        		Iterator<MathGroup> itMathGroup = file.getOwner().getRole().getMathGroups().iterator();
        		while (itMathGroup.hasNext()) {
        			itMathGroup.next().getRoles().iterator();
        		}
        	}
        }
        return out;
    }
}