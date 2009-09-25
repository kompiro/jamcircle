package org.kompiro.jamcircle.storage.service.internal;

import net.java.ao.Entity;

public interface TestEntity extends Entity{
	String PROP_NAME = "name";
	String PROP_UUID = "uuid";
	
	public String getName();
	public void setName(String name);
	
	public void setUuid(String uuid);
	public String getUuid();
}
