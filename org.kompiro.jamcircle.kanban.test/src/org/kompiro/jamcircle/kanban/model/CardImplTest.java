package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.kompiro.jamcircle.storage.service.FileStorageService;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.mockito.ArgumentCaptor;

public class CardImplTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private Card card;
	private CardImpl cardImpl;
	private FileStorageService fileStorageService;

	@Before
	public void before() throws Exception {
		card = mock(Card.class);
		cardImpl = new CardImpl(card);

		StorageService storageService = mock(StorageService.class);
		cardImpl.setStorageService(storageService);

		fileStorageService = mock(FileStorageService.class);
		when(storageService.getFileService()).thenReturn(fileStorageService);

	}

	@Test
	public void call_addFile() throws Exception {

		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		cardImpl.addPropertyChangeListener(listener);

		File srcFile = folder.newFile("test.txt");
		cardImpl.addFile(srcFile);

		verify(fileStorageService).addFile(CardImpl.CARD_PATH + card.getID(), srcFile);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());

		PropertyChangeEvent value = captor.getValue();
		assertThat((File) value.getNewValue(), is(srcFile));
		assertThat((Card) value.getSource(), is(card));
	}

	@Test(expected = IllegalArgumentException.class)
	public void call_addFile_is_null() throws Exception {
		cardImpl.addFile(null);
	}

}
