package org.kompiro.jamcircle;

import net.java.ao.Entity;
import net.java.ao.Generator;
import net.java.ao.Implementation;
import net.java.ao.schema.NotNull;

@Implementation(PersonImpl.class)
public interface Person extends Entity{
	public String getName();

	public void setName(String name);
	
	public boolean isClever();

	public void setClever(boolean clever);
	
	@Generator(UUIDValueGenerator.class)
	@NotNull
	public void setUUID(String uuid);
	
	public String getUUID();
}
