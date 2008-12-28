package org.kompiro.jamcircle;

import static org.junit.Assert.*;

import java.util.HashMap;

import net.java.ao.EntityManager;

import org.junit.Test;

public class LeaningActiveObjects {
	
	@Test
	public void learningEntityManger_get() throws Exception {
		EntityManager manager = new EntityManager(null);
		Person p = manager.get(Person.class, 0);
		assertNotNull(p);
		p.setName("Motsube");
		Person get = manager.get(Person.class, 0);
		assertEquals(p.getName(),get.getName());
	}
	
	@Test
	public void learningEntityManager_create() throws Exception {
		EntityManager manager = new EntityManager(null);
		Person p = manager.create(Person.class, new HashMap<String, Object>()); // Null Pointer Exception is occur.
		assertNotNull(p);
	}

}
