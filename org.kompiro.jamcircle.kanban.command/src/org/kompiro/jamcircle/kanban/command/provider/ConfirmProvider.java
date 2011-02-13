package org.kompiro.jamcircle.kanban.command.provider;

public interface ConfirmProvider {

	boolean confirm();

	public void setMessage(String message);

	public void setTitle(String title);

}
