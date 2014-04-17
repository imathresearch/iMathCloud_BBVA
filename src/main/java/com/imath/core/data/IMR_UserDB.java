/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imath.core.model.File;
import com.imath.core.model.FileShared;
import com.imath.core.model.IMR_User;
import com.imath.core.model.Job;
import com.imath.core.model.Role;
import com.imath.core.model.MathGroup;

/**
 * The User repository. It provides access to {@link IMR_User} database and useful data queries
 * 
 * @author ipinyol
 */
//@ApplicationScoped
@RequestScoped
public class IMR_UserDB {
    @Inject
    private EntityManager em;

    /**
     * Returns a {@link IMR_User} from the given id
     * @param id
     * 		The id of the {@link IMR_User}  
     * @author ipinyol
     */
    public IMR_User findById(String id) {
    	IMR_User user = em.find(IMR_User.class, id);
    	Role role = user.getRole();
    	Iterator<MathGroup> it = role.getMathGroups().iterator();
    	//while(it.hasNext()) {
    	//	it.next().getRoles().iterator();
    	//}
    	return user;
    }
    
    /**
     * Returns a {@link IMR_User} from the given email
     * @param email
     * 		The email of the {@link IMR_User}  
     * @author ipinyol
     */
    public IMR_User findByEMail(String email) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMR_User> criteria = cb.createQuery(IMR_User.class);
        Root<IMR_User> user = criteria.from(IMR_User.class);
        Predicate p1 = cb.equal(user.get("eMail"), email);
        criteria.select(user).where(p1);
        List<IMR_User> out = em.createQuery(criteria).getResultList();
        IMR_User ret = null;
        if(out.size()>0) {
        	ret = out.get(0);
        }
        return ret;
    }
}
