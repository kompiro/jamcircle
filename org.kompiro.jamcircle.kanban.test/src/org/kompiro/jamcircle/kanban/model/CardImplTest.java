package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

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
	public void call_addFile_arg_is_null() throws Exception {
		cardImpl.addFile(null);
	}

	@Test
	public void call_getFiles() throws Exception {

		cardImpl.getFiles();
		verify(fileStorageService).getFiles(getPath());
	}

	@Test
	public void return_false_called_hasFile_when_no_files_are_added() throws Exception {
		File file = folder.newFile("test.txt");
		boolean result = cardImpl.hasFile(file);

		assertThat(result, is(false));
	}

	@Test
	public void return_true_called_hasFile_when_the_file_is_existed() throws Exception {
		ArrayList<File> files = new ArrayList<File>();
		File file = folder.newFile("test.txt");
		files.add(file);
		when(fileStorageService.getFiles(getPath())).thenReturn(files);
		boolean result = cardImpl.hasFile(file);
		assertThat(result, is(true));
	}

	@Test
	public void return_false_called_hasFile_when_arg_is_not_exist() throws Exception {
		File file = folder.newFile("test.txt");
		ArrayList<File> files = new ArrayList<File>();
		files.add(file);
		when(fileStorageService.getFiles(getPath())).thenReturn(files);

		File notExisted = folder.newFile("not_exist.txt");
		boolean result = cardImpl.hasFile(notExisted);
		assertThat(result, is(false));
	}

	@Test
	public void return_false_called_hasFile_when_arg_is_null() throws Exception {
		boolean result = cardImpl.hasFile(null);
		assertThat(result, is(false));
	}

}
