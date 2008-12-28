package org.kompiro.jamcircle.kanban.ui.model;

import java.util.Collection;

public interface UserModelContainer {
	public abstract boolean addUser(UserModel user);

	public abstract void addAllUsers(Collection<? extends UserModel> users);

	public abstract boolean containsUser(UserModel arg0);

	public abstract UserModel getUser(String user);

	public abstract boolean isEmptyUsers();

	public abstract boolean removeUser(UserModel user);

}
