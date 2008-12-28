package org.kompiro.jamcircle.storage;

import net.java.ao.Entity;
import net.java.ao.Implementation;

@Implementation(PersonImpl.class)
	public interface Person extends Entity{
		public String getName();

		public void setName(String name);
		
		public boolean isFool();

		public void setFool(boolean clever);
	}
