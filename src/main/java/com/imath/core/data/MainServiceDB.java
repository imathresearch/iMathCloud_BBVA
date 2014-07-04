/* (C) 2013 iMath Research S.L. - All rights reserved.  */
package com.imath.core.data;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
//import javax.persistence.EntityTransaction;
//import javax.transaction.UserTransaction;
import javax.persistence.EntityTransaction;

//import org.hibernate.SessionFactory;
//import org.hibernate.service.ServiceRegistry;
//import org.hibernate.service.ServiceRegistryBuilder;
//import org.hibernate.HibernateException;

//import org.hibernate.cfg.Configuration;

/**
 * It provides the main functionalities to access the Database of the system.
 * It also provides the main service for transactional DB access and persistence
 * 
 * @author ipinyol
 */
//@ApplicationScoped
@RequestScoped
public class MainServiceDB {
	
	@Inject private HostDB hostDB;
	@Inject private JobDB jobDB; 
	@Inject private SessionDB sessionDB; 
	@Inject private EntityManager em;
	@Inject private IMR_UserDB imr_userDB;
	@Inject private FileDB fileDB;
	@Inject private MathFunctionDB mathFunctionDB;
	@Inject private FileSharedDB fileSharedDB;
	
	//@Inject private UserTransaction userTransaction;
  //  private static SessionFactory sessionFactory;
  //  private static ServiceRegistry serviceRegistry;
/*    
    private static SessionFactory configureSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.configure();
        serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }
*/    
    public IMR_UserDB getIMR_UserDB() {
    	return this.imr_userDB;
    }
    
    public HostDB getHostDB() {
    	return this.hostDB;
    }
    
    public JobDB getJobDB() {
    	return this.jobDB;
    }
    
    public FileDB getFileDB() {
    	return this.fileDB;
    }
    
    public FileSharedDB getFileSharedDB() {
    	return this.fileSharedDB;
    }
    
    public SessionDB getSessionDB() {
    	return this.sessionDB;
    }
    
    public MathFunctionDB getMathFunctionDB() {
    	return this.mathFunctionDB;
    }
    
    //private static SessionFactory getSessionFactory() {
    //    return configureSessionFactory();
    //}
    
    
    public void makePersistent(Object obj) throws Exception {
    	em.persist(obj);
    	em.flush();
    	
    }
    
    public void remove(Object obj) throws Exception {
    	//em.contains(entity) ? entity : em.merge(entity)
    	em.remove(obj);
    	em.flush();
    }
    
	//public EntityTransaction beginTransaction() throws Exception {
		// TODO do it better. Capture the fact that begin transaction might fail...
		// Do Nothing? userTransaction.begin();
		//userTransaction.begin();
		//em.getTransaction().begin();
	//	EntityTransaction trans = em.getTransaction();
	//	trans.begin();
	//	return trans;
	//}
    
	public void beginTransaction() throws Exception {
		// TODO do it better. Capture the fact that begin transaction might fail...
		// Do Nothing? userTransaction.begin();
		//userTransaction.begin();
		//em.joinTransaction();

	}
	public void commitTransaction(EntityTransaction trans) throws Exception {
		//userTransaction.commit();
		//em.getTransaction().commit();
		trans.commit();
	}
	public void commitTransaction() throws Exception {
		//userTransaction.commit();
		//em.getTransaction().commit();
		//trans.commit();
	}
	public void rollBackTransaction(EntityTransaction trans) throws Exception {
		//userTransaction.rollback();
		//em.getTransaction().rollback();
		trans.rollback();
		
	}
	public void rollBackTransaction() throws Exception {
		//userTransaction.rollback();
		//em.getTransaction().rollback();
		//trans.rollback();
		
	}
	
	public EntityManager getEntityManager() {
	    return this.em;
	}
	
	// Public methods for testing purposes only. It simulates an injection
	public void setEntityManager(EntityManager em) {
	    this.em = em;
	}
	
	public void setJobDB(JobDB jobDB) {
	    this.jobDB = jobDB;
	}
	
	public void setFileDB(FileDB fileDB) {
	    this.fileDB = fileDB;
	}
}
