package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.java.ao.EntityManager;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;
import org.kompiro.jamcircle.storage.service.FileStorageService;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.mockito.ArgumentCaptor;

public class LaneImplTest {

	private EntityManager manager;
	private LaneImpl impl;
	private Lane lane;
	private PropertyChangeListener listener;
	private ExecutorHandler handler;
	private List<Card> mockCards;
	private StorageService storageService;

	@SuppressWarnings("unchecked")
	@Before
	public void before() throws Exception {
		lane = mock(Lane.class);

		Board board = mock(Board.class);
		when(lane.getBoard()).thenReturn(board);

		manager = mock(EntityManager.class);
		when(lane.getEntityManager()).thenReturn(manager);

		impl = new LaneImpl(lane);
		listener = mock(PropertyChangeListener.class);
		impl.addPropertyChangeListener(listener);
		mockCards = mock(List.class);
		impl.setMockCards(mockCards);
		fileService = mock(FileStorageService.class);
		impl.setFileService(fileService);

		storageService = mock(StorageService.class);
		impl.setStorageService(storageService);

		handler = mock(ExecutorHandler.class);
	}

	@Test
	public void add_card() throws Exception {
		Card card = mock(Card.class);
		impl.addCard(card);

		verify(card).setLane(lane);
		verify(card).save(false);
		verify(card).setTrashed(false);
		verify(card).setDeletedVisuals(false);
		verify(storageService).flushEntity(lane);
		verify(mockCards, never()).add(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getNewValue(), is(card));
	}

	@Test
	public void add_mock_card() throws Exception {

		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);

		impl.addCard(card);

		verify(card).setLane(lane);
		verify(card).save(false);
		verify(card).setTrashed(false);
		verify(card).setDeletedVisuals(false);
		verify(storageService, never()).flushEntity(lane);
		verify(mockCards).add(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getNewValue(), is(card));

	}

	@Test(expected = IllegalArgumentException.class)
	public void add_null_card() throws Exception {
		impl.addCard(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void add_card_when_lanes_board_is_null() throws Exception {

		when(lane.getBoard()).thenReturn(null);
		Card card = mock(Card.class);
		impl.addCard(card);

	}

	@Test
	public void remove_card() throws Exception {
		Card card = mock(Card.class);

		impl.removeCard(card);

		verify(card).setLane(null);
		verify(card).setBoard(null);
		verify(card).save(false);
		verify(card).setDeletedVisuals(true);
		verify(storageService).flushEntity(lane);
		verify(mockCards, never()).remove(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getOldValue(), is(card));

	}

	@Test
	public void remove_mock_card() throws Exception {
		Card card = mock(Card.class);
		when(card.isMock()).thenReturn(true);
		impl.removeCard(card);

		verify(card).setLane(null);
		verify(card).setBoard(null);
		verify(card).save(false);
		verify(card).setDeletedVisuals(true);
		verify(manager, never()).flush(card);
		verify(mockCards).remove(card);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CARD));
		assertThat((Card) value.getOldValue(), is(card));

	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_null_card() throws Exception {
		impl.removeCard(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_card_when_lanes_board_is_null() throws Exception {

		when(lane.getBoard()).thenReturn(null);
		Card card = mock(Card.class);
		impl.removeCard(card);

	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private FileStorageService fileService;

	@Test
	public void set_custom_icon_when_empty() throws Exception {
		File file = folder.newFile("temp.png");
		impl.setCustomIcon(file);
		verify(fileService).addFile(anyString(), eq(file));

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CUSTOM_ICON));
		assertThat((File) value.getNewValue(), is(file));
	}

	@Test
	public void set_custom_icon_when_registered() throws Exception {
		List<File> files = new LinkedList<File>();
		File registered = folder.newFile("already.png");
		files.add(registered);
		when(fileService.getFiles(impl.getIconPath())).thenReturn(files);

		File file = folder.newFile("temp.png");
		impl.setCustomIcon(file);
		verify(fileService).addFile(anyString(), eq(file));

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CUSTOM_ICON));
		assertThat((File) value.getNewValue(), is(file));
		assertThat((File) value.getOldValue(), is(registered));
	}

	@Test
	public void set_custom_icon_when_null_is_set() throws Exception {
		List<File> files = new LinkedList<File>();
		File registered = folder.newFile("already.png");
		files.add(registered);
		when(fileService.getFiles(impl.getIconPath())).thenReturn(files);

		impl.setCustomIcon(null);
		verify(fileService, never()).addFile(anyString(), (File) any());

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CUSTOM_ICON));
		assertThat((File) value.getNewValue(), is(nullValue()));
		assertThat((File) value.getOldValue(), is(registered));

	}

	@Test
	public void set_custom_icon_when_empty_and_null_is_set() throws Exception {
		impl.setCustomIcon(null);
		verify(fileService, never()).addFile(anyString(), (File) any());

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());
		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_CUSTOM_ICON));
		assertThat((File) value.getNewValue(), is(nullValue()));
		assertThat((File) value.getOldValue(), is(nullValue()));

	}

	@Test
	public void get_custom_icon_when_empty() throws Exception {
		File file = impl.getCustomIcon();
		assertThat(file, is(nullValue()));
	}

	@Test
	public void get_custom_icon() throws Exception {
		List<File> files = new LinkedList<File>();
		File registered = folder.newFile("already.png");
		files.add(registered);
		when(fileService.getFiles(impl.getIconPath())).thenReturn(files);
		File customIcon = impl.getCustomIcon();
		assertThat(customIcon, is(registered));
	}

	@Test
	public void should_call_save_by_handler() throws Exception {

		impl.setHandler(handler);
		impl.save(false);

		verify(handler, only()).handle((Runnable) anyObject());
	}

	@Test
	public void should_not_call_save_by_handler_when_set_directory() throws Exception {

		impl.setHandler(handler);
		impl.save(true);

		verify(handler, never()).handle((Runnable) anyObject());
	}
}
