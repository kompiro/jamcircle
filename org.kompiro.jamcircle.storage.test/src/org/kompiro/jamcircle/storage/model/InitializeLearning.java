package org.kompiro.jamcircle.storage.model;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.java.ao.DBParam;
import net.java.ao.Transaction;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.AbstractStorageTest;

public class InitializeLearning extends AbstractStorageTest{
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws Exception {
//		bundleInitialize();
//		manager.migrate(GraphicalEntity.class,Location.class);
		manager.migrate(NeedInitializeModel.class);
	}
	
	@Test
	public void initialize() throws Exception {
		NeedInitializeModel model =new Transaction<NeedInitializeModel>(manager){
			@Override
			protected NeedInitializeModel run() throws SQLException {
				NeedInitializeModel model = manager.create(NeedInitializeModel.class, new DBParam("name","new card"));
				return model;
			}
		}.execute();
		model = manager.get(NeedInitializeModel.class, 1);
		assertEquals("new card",model.getName());
	}

}
