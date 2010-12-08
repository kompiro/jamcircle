package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.kompiro.jamcircle.storage.service.FileStorageService;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.internal.FileStorageServiceImpl;
import org.mockito.ArgumentCaptor;

@SuppressWarnings("restriction")
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

		FileStorageServiceImpl service = new FileStorageServiceImpl();
		fileStorageService = spy(service);
		when(storageService.getFileService()).thenReturn(fileStorageService);
		when(fileStorageService.getFiles(getPath())).thenCallRealMethod();

	}

	@After
	public void after() throws Exception {
		fileStorageService.deleteAll()
	}

	@Test
	public void call_addFile() throws Exception {

		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		cardImpl.addPropertyChangeListener(listener);

		File srcFile = folder.newFile("test.txt");
		cardImpl.addFile(srcFile);

		verify(fileStorageService).addFile(getPath(), srcFile);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());

		PropertyChangeEvent value = captor.getValue();
		assertThat((File) value.getNewValue(), is(srcFile));
		assertThat((Card) value.getSource(), is(card));
		assertThat(value.getPropertyName(), is(Card.PROP_FILES));

	}

	private String getPath() {
		return CardImpl.CARD_PATH + card.getID();
	}

	@Test(expected = IllegalArgumentException.class)
	public void call_addFile_is_null() throws Exception {
		cardImpl.addFile(null);
	}

	@Test
	public void call_getFiles() throws Exception {

		File srcFile = folder.newFile("test.txt");
		cardImpl.addFile(srcFile);
		assertThat(cardImpl.getFiles().size(), is(1));
		assertThat(cardImpl.getFiles().get(0).getName(), is(srcFile.getName()));

		File src2File = folder.newFile("test2.txt");
		cardImpl.addFile(src2File);
		assertThat(cardImpl.getFiles().size(), is(2));
		assertThat(cardImpl.getFiles().get(1).getName(), is(src2File.getName()));
	}

	@Test
	public void call_hasFile() throws Exception {

		File srcFile = folder.newFile("test.txt");
		File src2File = folder.newFile("test2.txt");

		cardImpl.addFile(srcFile);

		assertThat(cardImpl.hasFile(cardImpl.getFiles().get(0)), is(true));
		assertThat(cardImpl.hasFile(src2File), is(false));
	}

}
