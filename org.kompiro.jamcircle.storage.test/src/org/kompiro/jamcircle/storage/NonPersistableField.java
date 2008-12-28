package org.kompiro.jamcircle.storage;

import net.java.ao.Entity;
import net.java.ao.Implementation;
import net.java.ao.schema.Ignore;

@Implementation(NonPersistableFieldImpl.class)
public interface NonPersistableField extends Entity{

	@Ignore
	public void setDeleted(boolean deleted);

	@Ignore
	public boolean isDeleted();
	
	public void setPerson(Person p);
	
	public Person getPerson();
	
	public void setName(String name);

	public String getName();

}
