package org.kompiro.jamcircle.storage.model;

import net.java.ao.Entity;
import net.java.ao.Implementation;
import net.java.ao.schema.*;

@Implementation(GraphicalEntityImpl.class)
public interface GraphicalEntity extends Entity {

	String PROP_ID = "id"; //$NON-NLS-1$
	String PROP_LOCATION_X = "x"; //$NON-NLS-1$
	String PROP_LOCATION_Y = "y"; //$NON-NLS-1$
	String PROP_PREPARE_LOCATION = "prepare_location"; //$NON-NLS-1$
	String PROP_COMMIT_LOCATION = "commit_location"; //$NON-NLS-1$
	String PROP_TRASHED = "trashed"; //$NON-NLS-1$

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
	
	/**
	 * prepared to move location
	 */
	@Ignore
	void prepareLocation();
	
	/**
	 * commited the location of this model
	 */
	@Ignore
	void commitLocation();
	
	@Ignore
	boolean isMock();

	boolean isTrashed();
	
	@Default(value="false")
	void setTrashed(boolean trashed);
	
	/**
	 * This method calls {@link net.java.ao.RawEntity#save(false)} and run in  Executor.
	 * @param directExecution set true if you need direct {@link net.java.ao.RawEntity#save(false)}
	 */
	void save(boolean directExecution);

}
