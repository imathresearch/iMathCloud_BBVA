package com.imath.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityNotFoundException;

import com.imath.core.model.UserJBoss;
import com.imath.core.model.UserJBossRoles;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserJBossRolesController extends AbstractController {
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public UserJBossRoles newUserJBossRoles(String userName, String role) throws Exception {
		UserJBossRoles user = new UserJBossRoles();
		user.setUsername(userName);
		user.setRole(role);
        this.db.makePersistent(user);
        return user;
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeUserJBossRoles(String userName) throws Exception {
		UserJBossRoles user = this.getUserJBossRoles(userName);		
        this.db.remove(user);
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public UserJBossRoles getUserJBossRoles(String userName) throws EntityNotFoundException {
		UserJBossRoles peer = this.db.getUserJBossRolesDB().findByUserName(userName);
        if (peer == null) {
            throw new EntityNotFoundException();  
        }
        return peer;
    }

}
