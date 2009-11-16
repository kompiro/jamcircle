package org.kompiro.jamcircle.kanban.model;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

import net.java.ao.Preload;

@Preload
public interface User extends GraphicalEntity{
	
	public String PROP_USERID = "userId";
	public String PROP_USERName = "userName";
	
	void setUserId(String userId);
	String getUserId();
	String getUserName();
	void setUserName(String userName);

	
}
