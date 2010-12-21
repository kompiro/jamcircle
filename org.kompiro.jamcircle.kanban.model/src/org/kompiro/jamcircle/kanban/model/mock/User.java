package org.kompiro.jamcircle.kanban.model.mock;

/**
 * This implementation describes User of Mock and isn't able to store any persistence.
 * @author kompiro
 */
public class User extends MockGraphicalEntity implements org.kompiro.jamcircle.kanban.model.User{

	private String userId;
	private String userName;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}