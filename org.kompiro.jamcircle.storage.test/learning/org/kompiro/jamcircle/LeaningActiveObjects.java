package org.kompiro.jamcircle;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import net.java.ao.EntityManager;

import org.junit.Test;
import org.kompiro.jamcircle.storage.H2DatabaseProvider;

public class LeaningActiveObjects {
	
	@Test
	public void learningEntityManger_get() throws Exception {
		EntityManager manager = new EntityManager(null);
		Person p = manager.get(Person.class, 0);
		assertNotNull(p);
		p.setName("Motsube");
		UUID randomUUID = UUID.randomUUID();
		p.setUUID(randomUUID.toString());
		Person get = manager.get(Person.class, 0);
		assertEquals(p.getName(),get.getName());
		assertEquals(randomUUID.toString(),get.getUUID());
	}
	
	@Test
	public void learningEntityManager_create_NullPointerException_is_occured_if_provider_is_null() throws Exception {
		EntityManager manager = new EntityManager(null);
		Person p = null;
		try{
			p = manager.create(Person.class, new HashMap<String, Object>()); // Null Pointer Exception is occur.
			fail("Null Pointer Exception hasn't occured.");
		}catch(NullPointerException e){
		}
		assertNull(p);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void learningEntityManager_create_learn_about_generator() throws Exception {
		String learningStoreDirectory = "/tmp/learning/";
		File learning = new File(learningStoreDirectory);
		if(learning.exists()){
			deleteChildFiles(learning);
		}
		EntityManager manager = new EntityManager(new H2DatabaseProvider("jdbc:h2:" +
				learningStoreDirectory + "test","sa",""));
		manager.migrate(Person.class);
		Person p = manager.create(Person.class, new HashMap<String,Object>());
		assertEquals(1,p.getID());
		String uuid = p.getUUID();
		assertNotNull(uuid);
		Person p2 = manager.create(Person.class, new HashMap<String,Object>());
		String uuid2 = p2.getUUID();
		assertNotNull(uuid2);
		assertFalse(p.getUUID().equals(uuid2));
		System.out.println(uuid);
		System.out.println(uuid2);
	}

	private void deleteChildFiles(File file) {
		if(file.isDirectory()){
			for(File child : file.listFiles()){
				deleteChildFiles(child);
			}
		}
		System.out.println(file.getName());
		file.delete();
	}

}
