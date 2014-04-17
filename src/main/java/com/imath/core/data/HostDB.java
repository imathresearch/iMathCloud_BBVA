/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import java.util.List;

import com.imath.core.model.Host;

/**
 * The Host repository. It provides access to {@link Host} database and useful data queries
 * 
 * @author ipinyol
 */
//@ApplicationScoped
@RequestScoped
public class HostDB {

    @Inject
    private EntityManager em;

    /**
     * Returns a {@link Host} from the given dd
     * @param id
     * 		The id of the {@link Host}  
     * @author ipinyol
     */
    public Host findById(Long id) {
    	//EntityTransaction tx = em.getTransaction();
    	//tx.begin();
        Host host =  em.find(Host.class, id);
        //tx.commit();
        return host;
    }

    /**
     * Returns a list of {@link Host} with the selected 'console' and 'active' criteria.
     * @param console 
     * 		Whether the requested {@link Host} will have an interactive console active. 
     * @param active
     * 		Whether the requested {@link Host} will be in the 'active' state. 
     * @author ipinyol
     */
    public List<Host> getHostByConsole_and_State(boolean console, boolean active) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Host> criteria = cb.createQuery(Host.class);
        Root<Host> job = criteria.from(Host.class);
        Predicate p1 = cb.equal(job.get("console"), console);
        Predicate p2 = cb.equal(job.get("active"), active);
        criteria.select(job).where(cb.and(p1,p2));
        return em.createQuery(criteria).getResultList();
    }
    
    /**
     * Returns a list of {@link Host} that support with the selected IMR_Type and 'active' criteria.
     * @param IMR_type 
     * 		The supported type of source file (py, r ...) 
     * @param active
     * 		Whether the requested {@link Host} will be in the 'active' state. 
     * @author ipinyol
     */
    // TODO: Currently, all host are returned, since we only accept python files.
    // An entire metadata information should be added to the host to refine such search
    public List<Host> getHostByType_and_State(String IMR_type, boolean active) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Host> criteria = cb.createQuery(Host.class);
        Root<Host> job = criteria.from(Host.class);
        Predicate p = cb.equal(job.get("active"), active);
        criteria.select(job).where(p);
        return em.createQuery(criteria).getResultList();
    }
}