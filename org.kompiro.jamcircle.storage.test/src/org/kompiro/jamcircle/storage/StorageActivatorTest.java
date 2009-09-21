package org.kompiro.jamcircle.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import net.java.ao.EntityManager;
import net.java.ao.Transaction;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kompiro.jamcircle.storage.service.internal.StorageServiceImpl;

public class StorageActivatorTest extends AbstractStorageTest{
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws SQLException{
		try {
			((StorageServiceImpl)AbstractStorageTest.getStorageService()).recreateEntityManagerForTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		manager.migrate(Person.class,DeletedModel.class,NonPersistableField.class);
	}
	
	
	@Ignore
	@Test
	public void learningTransient() throws Exception {
		DeletedModel model = manager.create(DeletedModel.class);
		model.setDeleted(true);
		model.save();
		model = manager.get(DeletedModel.class, 1);
		assertFalse(model.isDeleted());
	}
	
	@Test
	public void notPersistant() throws Exception {
		NonPersistableField p = new Transaction<NonPersistableField>(manager){
			@Override
			protected NonPersistableField run() throws SQLException {
				EntityManager manager = getEntityManager();
				Person p = manager.create(Person.class);
				p.setName("Hiroki Kondo");
				p.setFool(true);
				p.save();
				NonPersistableField model = manager.create(NonPersistableField.class);
				model.setName("fool");
				model.setDeleted(true);
				assertTrue(model.isDeleted());
				model.setPerson(p);
				model.save();
				return model;
			}
		}.execute();
		manager.flush(p);
		NonPersistableField[] result = manager.find(NonPersistableField.class);
		assertEquals(1, result.length);
		NonPersistableField model = result[0];
		assertTrue(model.isDeleted());
		assertEquals("Hiroki Kondo",model.getPerson().getName());
	}

	@Test
	public void storageData() throws Exception {
		assertCommit(manager);
		assertRollback(manager);
		assertRollbackWhenDontSave(manager);
		assertUpdateAndCommit(manager);
	}

	private void assertCommit(EntityManager manager) throws SQLException {
		Person person = new Transaction<Person>(manager){
			@Override
			protected Person run() throws SQLException {
				EntityManager manager = getEntityManager();
				Person p = manager.create(Person.class);
				p.setName("Hiroki Kondo");
				p.setFool(true);
				p.save();
				return p;
			}
		}.execute();
		assertEquals(1,manager.count(Person.class));
//		Person person = manager.get(Person.class, 1);
		assertEquals("Hiroki Kondo",person.getName());
		assertTrue(person.isFool());
	}

	private void assertRollback(EntityManager manager) throws SQLException {
		new Transaction<Person>(manager){
			@Override
			protected Person run() throws SQLException {
				EntityManager manager = getEntityManager();
				Person p = manager.create(Person.class);
				p.setName("Hiroki Kondo");
				p.save();
				Connection conn = manager.getProvider().getConnection();
				conn.rollback();
				return p;
			}
		}.execute();
		assertEquals(1,manager.count(Person.class));
	}

	private void assertRollbackWhenDontSave(EntityManager manager)
			throws SQLException {
		new Transaction<Person>(manager){
			@Override
			protected Person run() throws SQLException {
				EntityManager manager = getEntityManager();
				Person p = manager.get(Person.class,1);
				assertEquals("Hiroki Kondo",p.getName());
				p.setName("kompiro");
				return p;
			}
		}.execute();
		Person p = manager.get(Person.class,1);
		// committed
		assertEquals("kompiro",p.getName());
	}
	
	private void assertUpdateAndCommit(EntityManager manager) throws SQLException {
		new Transaction<Person>(manager){
			@Override
			protected Person run() throws SQLException {
				EntityManager manager = getEntityManager();
				Person p = manager.get(Person.class,1);
				assertEquals("kompiro",p.getName());
				p.setName("Hiroki Kondo");
				p.save();
				return p;
			}
		}.execute();
		Person p = manager.get(Person.class,1);
		assertEquals("Hiroki Kondo",p.getName());		
	}

}
