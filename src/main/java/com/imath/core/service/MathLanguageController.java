/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.imath.core.model.MathLanguage;


/**
 * The MathFunction Controller class. It offers a set of methods CRUD for MathFunctions
 * @author ipinyol
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MathLanguageController extends AbstractController {

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public MathLanguage createNewMathLanguage(String baseName, String consoleCode, String version) throws Exception {
		MathLanguage mathLanguage = new MathLanguage();
		mathLanguage.setBaseName(baseName);
		mathLanguage.setConsoleCode(consoleCode);
		mathLanguage.setVersion(version);
		db.makePersistent(mathLanguage);
		return mathLanguage;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public MathLanguage modifyMathLanguage(MathLanguage mathLanguage) throws Exception {
		db.makePersistent(mathLanguage);
		return mathLanguage;
	}
	
}
