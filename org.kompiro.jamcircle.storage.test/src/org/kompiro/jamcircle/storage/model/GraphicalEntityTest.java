package org.kompiro.jamcircle.storage.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.AbstractStorageTest;
import org.kompiro.jamcircle.storage.service.internal.StorageServiceImpl;

public class GraphicalEntityTest extends AbstractStorageTest{
		
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws Exception{
		try {
			((StorageServiceImpl)AbstractStorageTest.getStorageService()).recreateEntityManagerForTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		manager.migrate(GraphicalEntity.class);
	}

	@Test
	public void assertNotPersistableFieldOfDeleted() throws Exception {

		GraphicalEntity entity = manager.create(GraphicalEntity.class);
		entity.setDeletedVisuals(true);
		assertTrue(entity.isDeletedVisuals());
		entity.setX(100);
		entity.setY(10);
		entity.save(false);
		assumeThat(manager.count(GraphicalEntity.class), is(1));
		entity = manager.get(GraphicalEntity.class, 1);
		assertTrue("Entity is deleted visual because it isn't reloaded.",entity.isDeletedVisuals());
		assertEquals(100,entity.getX());
		assertEquals(10,entity.getY());

		entity = manager.find(GraphicalEntity.class,"id = ?",1)[0];
		assertTrue("Entity is deleted visuals,too using find method",entity.isDeletedVisuals());
		assertEquals(100,entity.getX());
		assertEquals(10,entity.getY());
		entity = manager.get(GraphicalEntity.class,300);
		assertNotNull(entity);
		assertEquals(300,entity.getID());
		entity.setX(100);
		entity.setY(200);
		
		// e(id:300) hasn't persistent. But if it called,then returns old value.
		entity.save(false);
		entity = manager.get(GraphicalEntity.class,300);
		assertNotNull(entity);
		assertEquals(300,entity.getID());
		assertEquals(100,entity.getX());
		assertEquals(200,entity.getY());
		
		// e(id:300) hasn't persistent and call flush.Then accessors are throw NullPointerException
		manager.flush(entity);
		entity = manager.get(GraphicalEntity.class,300);
		assertNotNull(entity);
		assertEquals(300,entity.getID());
		try {
			assertEquals(100,entity.getX());
			fail();
		} catch (Exception e) {
		}
		try {
			assertEquals(200,entity.getY());
			fail();
		} catch (Exception e) {
		}
	}
	
}
