package org.kompiro.jamcircle.kanban.ui.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.ui.model.DefaultIconModelFactory;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public class DefaultIconModelFactoryTest {

	private DefaultIconModelFactory factory;

	@Before
	public void init() throws Exception{
		this.factory = new DefaultIconModelFactory();
	}
	
	@Test
	public void createTrashModel() throws Exception{
		Icon icon = new org.kompiro.jamcircle.kanban.model.mock.Icon(){
			@Override
			public String getClassType() {
				return TrashModel.class.getName();
			}
		};
		System.out.println(icon.getClassType());
		IconModel iconModel = factory.create(icon);
		assertNotNull(iconModel);
		assertTrue(iconModel instanceof TrashModel);
	}
	
}
