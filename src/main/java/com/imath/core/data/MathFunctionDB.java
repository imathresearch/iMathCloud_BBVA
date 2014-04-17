/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.data;

import java.util.List;
import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imath.core.model.MathFunction;
import com.imath.core.model.MathGroup;

/**
 * The MathFunction repository. It provides access to {@link File} database and useful data queries
 * 
 * @author ipinyol
 */
//@ApplicationScoped
@RequestScoped
public class MathFunctionDB {

    @Inject
    private EntityManager em;

    /**
     * Returns a {@link MathFunction} from the given id
     * @param id
     * 		The id of the {@link MathFunction}  
     * @author ipinyol
     */
    public MathFunction findById(Long id) {
    	//EntityTransaction tx = em.getTransaction();
    	//tx.begin();
        MathFunction mf = em.find(MathFunction.class, id);
        return mf;
    }
    
    /**
     * Returns the complete list of {@link MathFunction} that belongs to the {@link MathGroup} id
     * @param userId 
     * 		The userId of the owner of the jobs 
     * @author ipinyol
     */
    public List<MathFunction> getMathFunctionsByGroup(Long idMathGroup) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MathFunction> criteria = cb.createQuery(MathFunction.class);
        Root<MathFunction> mathFunction = criteria.from(MathFunction.class);
        Predicate p1 = cb.equal(mathFunction.get("mathGroup").get("id"), idMathGroup);
        criteria.select(mathFunction).where(p1);
        List<MathFunction> out = em.createQuery(criteria).getResultList();
        Iterator<MathFunction> it = out.iterator();
        while (it.hasNext()) {
        	it.next().getMathGroup().getRoles().iterator();
        }
        return out;
    }
}