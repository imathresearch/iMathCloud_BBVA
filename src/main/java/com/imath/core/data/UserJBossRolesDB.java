package com.imath.core.data;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imath.core.model.UserJBoss;
import com.imath.core.model.UserJBossRoles;


@RequestScoped
public class UserJBossRolesDB {
	
	 
	@Inject private EntityManager em;
	 
	public UserJBossRoles findByUserName(String name) {
        em.flush();
        try {
            return em.find(UserJBossRoles.class, name);
        } catch (Exception e) {
            return null;
        }
    }

}