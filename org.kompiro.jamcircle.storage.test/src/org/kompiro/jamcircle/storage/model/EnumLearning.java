package org.kompiro.jamcircle.storage.model;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.java.ao.Transaction;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.storage.AbstractStorageTest;


public class EnumLearning extends AbstractStorageTest{
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws SQLException{
		// checked migrate sequence
		manager.migrate(GraphicalEntity.class,EnumModel.class);
	}

	@Test
	public void leaningEnum() throws Exception {
		new Transaction<EnumModel>(manager){
			
			@Override
			protected EnumModel run() throws SQLException {
				EnumModel model = manager.create(EnumModel.class);
				model.setState(Niko2State.GOOD);
				model.save(false);
				return model;
			}
			
		}.execute();
		new Transaction<EnumModel>(manager){
			
			@Override
			protected EnumModel run() throws SQLException {
				EnumModel model = manager.create(EnumModel.class);
				model.setState(Niko2State.NORMAL);
				model.save(false);
				return model;
			}
			
		}.execute();
		new Transaction<EnumModel>(manager){
			
			@Override
			protected EnumModel run() throws SQLException {
				EnumModel model = manager.create(EnumModel.class);
				model.setState(Niko2State.BAD);
				model.save(false);
				return model;
			}
			
		}.execute();

		EnumModel model = manager.get(EnumModel.class, 1);
		assertEquals(Niko2State.GOOD,model.getState());
		EnumModel model2 = manager.get(EnumModel.class, 2);
		assertEquals(Niko2State.NORMAL,model2.getState());
		EnumModel model3 = manager.get(EnumModel.class, 3);
		assertEquals(Niko2State.BAD,model3.getState());
	}
	
}
