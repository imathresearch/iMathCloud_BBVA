/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import java.util.List;

import com.imath.core.model.Session;

/**
 * The Sessions repository. It provides access to {@link Session} database and useful data queries
 * 
 * @author ipinyol
 */
//@ApplicationScoped
@RequestScoped
public class SessionDB {

    @Inject
    private EntityManager em;

    /**
     * Returns a {@link Session} from the given id
     * @param id
     * 		The id of the {@link Session}  
     * @author ipinyol
     */
    public Session findById(String id) {
        return em.find(Session.class, id);
    }

    /**
     * Returns the active {@link Session} of the given user, the one with no endTime
     * @param idUser 
     * 		The id of the {@link IMR_User}. 
     * @author ipinyol
     */
    public Session findByUser_and_OpenSession(String userName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Session> criteria = cb.createQuery(Session.class);
        Root<Session> job = criteria.from(Session.class);
        Predicate p1 = cb.equal(job.get("user").get("userName"), userName);
        Predicate p2 = cb.isNull(job.get("endDate"));
        criteria.select(job).where(cb.and(p1,p2));
        try { 
        	Session session = em.createQuery(criteria).getSingleResult();
        	return session;
        }
        catch (Exception e) {
        	// Curiously, getSingleResult return an exception when no single result exists.
        	// TODO: find a better way of doing it
        	return null;
        }
         
    }
    
    /**
     * Returns the last session {@link Session} of the given user
     * @param idUser 
     *      The id of the {@link IMR_User}. 
     * @author ipinyol
     */
    public Session findByUserLastSession(String userName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Session> criteria = cb.createQuery(Session.class);
        Root<Session> job = criteria.from(Session.class);
        Predicate p1 = cb.equal(job.get("user").get("userName"), userName);
        criteria.select(job).where(p1);
        criteria.orderBy(cb.desc(job.get("startDate")));
        
        try { 
            List<Session> sessions = em.createQuery(criteria).getResultList();
            if (sessions.size()>0) return sessions.get(0);
            return null;
        }
        catch (Exception e) {
            // Curiously, getSingleResult return an exception when no single result exists.
            // TODO: find a better way of doing it
            return null;
        }
         
    }
    /**
     * Returns the list of open {@link Session}, the ones with endDate=null
     * @author ipinyol
     */
    public List<Session> getOpenSessions() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Session> criteria = cb.createQuery(Session.class);
        Root<Session> job = criteria.from(Session.class);
        Predicate p1 = cb.isNull(job.get("endDate"));
        criteria.select(job).where(p1);
        criteria.orderBy(cb.asc(job.get("portConsole")));
        return em.createQuery(criteria).getResultList();
    }
    
    public List<Session> getAllSessions() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Session> criteria = cb.createQuery(Session.class);
        Root<Session> job = criteria.from(Session.class);
        //Predicate p1 = cb.isNull(job.get("endDate"));
        criteria.select(job);//.where(p1);
        criteria.orderBy(cb.desc(job.get("portConsole")));
        return em.createQuery(criteria).getResultList();
    }
}