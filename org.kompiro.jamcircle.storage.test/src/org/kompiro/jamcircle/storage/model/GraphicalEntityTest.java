package org.kompiro.jamcircle.storage.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import net.java.ao.Transaction;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.AbstractStorageTest;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public class GraphicalEntityTest extends AbstractStorageTest{
		
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws Exception{
		manager.migrate(GraphicalEntity.class);
	}

	@Test
	public void assertNotPersistableFieldOfDeleted() throws Exception {

		GraphicalEntity e = new Transaction<GraphicalEntity>(manager){
			@Override
			protected GraphicalEntity run() throws SQLException {
				GraphicalEntity entity = manager.create(GraphicalEntity.class);
				entity.setDeletedVisuals(true);
				assertTrue(entity.isDeletedVisuals());
				entity.setX(100);
				entity.setY(10);
				entity.save();
				return entity;
			}
		}.execute();

		manager.flush(e);
		e = manager.get(GraphicalEntity.class,1);
		assertFalse(e.isDeletedVisuals());
		assertEquals(100,e.getX());
		assertEquals(10,e.getY());
		e = manager.get(GraphicalEntity.class,300);
		assertNotNull(e);
		assertEquals(300,e.getID());
		e.setX(100);
		e.setY(200);
		// e(id:300) doesn't persistent.
		e.save();
	}
	
}
