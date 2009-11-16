package org.kompiro.jamcircle.kanban.model;

import net.java.ao.Entity;
import net.java.ao.Transient;

public interface Model extends Entity {

	@Transient
	public boolean isDeleted();
	
	@Transient
	public void setDeleted(boolean deleted);
}
