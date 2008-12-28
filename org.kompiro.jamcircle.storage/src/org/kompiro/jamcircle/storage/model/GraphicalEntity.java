package org.kompiro.jamcircle.storage.model;

import net.java.ao.Entity;
import net.java.ao.Implementation;
import net.java.ao.schema.Default;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.NotNull;

@Implementation(GraphicalEntityImpl.class)
public interface GraphicalEntity extends Entity {

	String PROP_ID = "id";
	String PROP_LOCATION_X = "x";
	String PROP_LOCATION_Y = "y";
	String PROP_LOCATION = "location";
	String PROP_TRASHED = "trashed";

	@Ignore
	public void setDeletedVisuals(boolean deleted);

	@Ignore
	public boolean isDeletedVisuals();
	
	int getX();
	int getY();

	@NotNull
	@Default(value="0")
	void setX(int x);

	@NotNull
	@Default(value="0")
	void setY(int y);

	void commitLocation();
	
	@Ignore
	boolean isMock();

	boolean isTrashed();
	
	@Default(value="false")
	void setTrashed(boolean trashed);

}
