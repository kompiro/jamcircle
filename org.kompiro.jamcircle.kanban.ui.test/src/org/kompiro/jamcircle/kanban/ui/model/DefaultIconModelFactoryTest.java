package org.kompiro.jamcircle.kanban.ui.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public class DefaultIconModelFactoryTest {

	private DefaultIconModelFactory factory;
	private KanbanService service;

	@Before
	public void init() throws Exception{
		this.service = mock(KanbanService.class);
		this.factory = new DefaultIconModelFactory(service);
	}
	
	@Test
	public void createTrashModel() throws Exception{
		Icon icon = new org.kompiro.jamcircle.kanban.model.mock.Icon(){
			@Override
			public String getClassType() {
				return TrashModel.class.getName();
			}
		};
		IconModel iconModel = factory.create(icon);
		assertNotNull(iconModel);
		assertTrue(iconModel instanceof TrashModel);
		TrashModel trash = (TrashModel) iconModel;
		Card card = mock(Card.class);
		trash.addCard(card);
		verify(service,times(1)).discardToTrash(card);
	}
	
	@Test
	public void throwExceptionWhenUnsupportedIcon() throws Exception {
		Icon icon = new org.kompiro.jamcircle.kanban.model.mock.Icon();
		try {
			factory.create(icon);
			fail("can't throw a exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
}
