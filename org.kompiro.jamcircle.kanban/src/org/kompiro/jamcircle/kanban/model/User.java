package org.kompiro.jamcircle.kanban.model;

import net.java.ao.Preload;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

/**
 * This interface describes User model.
 * @author kompiro
 *
 */
@Preload
public interface User extends GraphicalEntity{
	
	public String PROP_USERID = "userId"; //$NON-NLS-1$
	public String PROP_USERName = "userName"; //$NON-NLS-1$
	
	void setUserId(String userId);
	String getUserId();
	String getUserName();
	void setUserName(String userName);

	
}
