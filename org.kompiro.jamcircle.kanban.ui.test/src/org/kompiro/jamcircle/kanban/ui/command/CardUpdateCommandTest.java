package org.kompiro.jamcircle.kanban.ui.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.Card;
import org.kompiro.jamcircle.kanban.ui.command.provider.ConfirmProviderMock;
import org.kompiro.jamcircle.kanban.ui.internal.command.CardUpdateCommand;

public class CardUpdateCommandTest {

	private Card card;
	private Command command;
	private String expectedSubject = null;
	private String expectedContent = null;
	private Date expectedDueDate = null;
	private List<File> expectedFiles = null;

	@Before
	public void setup() throws Exception{
		card = new Card();
	}
	
	@Test
	public void executeSubject() throws Exception {
		expectedSubject = "Test";
		command = createCommand();

		assertNull(card.getSubject());
		command.execute();
		assertEquals(expectedSubject,card.getSubject());		
	}
	
	@Test
	public void undoRedoSubject() {
		expectedSubject = "Test";
		command = createCommand();
		
		assertTrue(command.canUndo());
		command.execute();
		assertTrue(command.canUndo());
		command.undo();
		assertFalse(command.canUndo());
		assertNull(card.getSubject());
		command.redo();
		assertTrue(command.canUndo());
		assertEquals(expectedSubject,card.getSubject());		
	}

	@Test
	public void executeContent() throws Exception {
		expectedContent  = "Test";
		command = createCommand();

		assertNull(card.getContent());
		command.execute();
		assertEquals(expectedContent,card.getContent());
	}
	
	@Test
	public void undoRedoContent() {
		expectedContent = "Test";
		command = createCommand();
		
		assertTrue(command.canUndo());
		command.execute();
		assertEquals(expectedContent,card.getContent());
		assertTrue(command.canUndo());
		command.undo();
		assertFalse(command.canUndo());
		assertNull(card.getContent());
		command.redo();
		assertTrue(command.canUndo());
		assertEquals(expectedContent,card.getContent());
	}

	@Test
	public void executeDueDate() throws Exception {
		expectedDueDate = new Date();
		command = createCommand();

		assertNull(card.getDueDate());
		command.execute();
		assertEquals(expectedDueDate,card.getDueDate());
	}
	
	@Test
	public void undoRedoDueDate() {
		expectedDueDate = new Date();
		command = createCommand();
		
		assertTrue(command.canUndo());
		command.execute();
		assertEquals(expectedDueDate,card.getDueDate());
		assertTrue(command.canUndo());
		command.undo();
		assertFalse(command.canUndo());
		assertNull(card.getDueDate());
		command.redo();
		assertTrue(command.canUndo());
		assertEquals(expectedDueDate,card.getDueDate());
	}
	
	@Test
	public void executeAddAndRemoveOneFile() throws Exception {
		expectedFiles = new ArrayList<File>();
		assertEquals(0,card.getFiles().size());

		File tempFile1 = File.createTempFile("test", "txt");
		expectedFiles.add(tempFile1);
		command = createCommand();
		command.execute();
		assertNotNull(card.getFiles());
		assertEquals(1,card.getFiles().size());

		expectedFiles.remove(tempFile1);
		command = createCommand();
		command.execute();
		assertEquals(0,card.getFiles().size());
	}
	
	@Test
	public void executeAddUndoAndRedoOneFile() throws Exception {
		expectedFiles = new ArrayList<File>();
		assertEquals(0,card.getFiles().size());

		File tempFile1 = File.createTempFile("test", "txt");
		expectedFiles.add(tempFile1);
		command = createCommand();
		command.execute();
		assertEquals(1,card.getFiles().size());
		assertTrue(command.canUndo());
		command.undo();
		assertEquals(0,card.getFiles().size());
		command.redo();
		assertEquals(1,card.getFiles().size());
	}

	@Test
	public void executeAddAndRemoveComplexFiles() throws Exception {
		expectedFiles = new ArrayList<File>();
		assertEquals(0,card.getFiles().size());

		File tempFile1 = File.createTempFile("1_test", "txt");
		File tempFile2 = File.createTempFile("2_test", "txt");
		File tempFile3 = File.createTempFile("3_test", "txt");

		expectedFiles.add(tempFile1);
		expectedFiles.add(tempFile2);
		expectedFiles.add(tempFile3);
		command = createCommand();
		command.execute();
		List<File> files = card.getFiles();
		assertNotNull(files);
		assertEquals(3,files.size());
		assertEquals(expectedFiles,files);

		expectedFiles.remove(tempFile2);

		command = createCommand();
		command.execute();
		files = card.getFiles();
		assertEquals(2,files.size());
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));

		File tempFile4 = File.createTempFile("4_test", "txt");
		expectedFiles.add(tempFile4);

		command = createCommand();
		command.execute();
		files = card.getFiles();
		assertEquals(3,files.size());
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));
		assertEquals(tempFile4,files.get(2));
	}

	@Test
	public void executeUndoAndRedoComplexFiles() throws Exception {
		expectedFiles = new ArrayList<File>();
		assertEquals(0,card.getFiles().size());

		File tempFile1 = File.createTempFile("1_test", "txt");
		File tempFile2 = File.createTempFile("2_test", "txt");
		File tempFile3 = File.createTempFile("3_test", "txt");

		expectedFiles.add(tempFile1);
		expectedFiles.add(tempFile2);
		expectedFiles.add(tempFile3);
		command = createCommand();
		command.execute();
		assertTrue(command.canUndo());
		List<File> files = card.getFiles();
		assertEquals(expectedFiles,files);

		command.undo();
		files = card.getFiles();
		assertEquals(0,files.size());

		command.redo();
		assertEquals(expectedFiles,files);
		
		expectedFiles.remove(tempFile2);

		command = createCommand();
		command.execute();
		files = card.getFiles();
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));

		command.undo();
		assertEquals(expectedFiles,files);

		command.redo();
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));

		File tempFile4 = File.createTempFile("4_test", "txt");
		expectedFiles.add(tempFile4);

		command = createCommand();
		command.execute();
		files = card.getFiles();
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));
		assertEquals(tempFile4,files.get(2));

		command.undo();
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));

		command.redo();
		assertEquals(tempFile1,files.get(0));
		assertEquals(tempFile3,files.get(1));
		assertEquals(tempFile4,files.get(2));
	}

	
	protected Command createCommand() {
		return new CardUpdateCommand(new ConfirmProviderMock(),card , expectedSubject, expectedContent, expectedDueDate , expectedFiles );
	}


}
