package org.kompiro.jamcircle.storage;

import net.java.ao.Entity;
import net.java.ao.Transient;

public interface DeletedModel extends Entity {

	@Transient
	public boolean isDeleted();
	
	@Transient
	public void setDeleted(boolean deleted);
}
