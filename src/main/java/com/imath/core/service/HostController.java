/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.imath.core.model.Host;

/**
 * The Host Controller class. It offers a set of methods to create/query/modify hosts 
 * @author ipinyol
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class HostController extends AbstractController {
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Host createNewHost(String alias, boolean active, boolean console, String url) throws Exception {
		Host host = new Host();
		host.setActive(active);
		host.setAlias(alias);
		host.setIsConsole(console);
		host.setUrl(url);
		db.makePersistent(host);
		return host;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Host modifyHost(Host host) throws Exception {
		Host hostO = db.getHostDB().findById(host.getId());
		hostO.copyValues(host);
		db.makePersistent(hostO);
		return host;
	}
}
