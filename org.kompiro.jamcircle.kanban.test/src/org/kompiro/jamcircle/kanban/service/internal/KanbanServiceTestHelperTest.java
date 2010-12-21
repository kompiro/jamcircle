package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.java.ao.EntityManager;

import org.junit.*;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.internal.FileStorageServiceImpl;

public class KanbanServiceTestHelperTest {

	private KanbanServiceTestHelper helper;
	private KanbanServiceImpl kanbanService;
	private FileStorageServiceImpl fileService;

	@Before
	public void before() throws Exception {
		helper = new KanbanServiceTestHelper();
		kanbanService = mock(KanbanServiceImpl.class);
		StorageService storageService = mock(StorageService.class);
		when(kanbanService.getStorageService()).thenReturn(storageService);
		fileService = mock(FileStorageServiceImpl.class);
		when(storageService.getFileService()).thenReturn(fileService);
		helper.setKanbanService(kanbanService);
	}

	@Test
	public void call_createBoardForTest() throws Exception {
		helper.createBoardForTest("Test");
		verify(kanbanService).createBoard("Test");
	}

	@Test
	public void call_createCardForTest() throws Exception {
		Board board = mock(Board.class);
		helper.createCardForTest(board, "test");
		verify(kanbanService).createCard(board, "test", null, 0, 0);
	}

	@Test
	public void call_createIconForTest() throws Exception {
		helper.createIconForTest("Test");
		verify(kanbanService).addIcon("Test", 0, 0);
	}

	@Test
	public void call_createLaneForTest() throws Exception {
		Board board = mock(Board.class);
		helper.createLaneForTest(board, "Test");
		verify(kanbanService).createLane(board, "Test", 0, 0, 200, 500);
	}

	@Test
	public void call_createUserForTest() throws Exception {
		helper.createUserForTest("test_user");
		verify(kanbanService).addUser("test_user");
	}

	@Test
	public void call_forceInitKanbanService() throws Exception {
		helper.forceInitKanbanService();
		verify(kanbanService).forceInit();
	}

	@Test
	public void call_getEntityManager() throws Exception {
		EntityManager manager = mock(EntityManager.class);
		when(kanbanService.getEntityManager()).thenReturn(manager);
		EntityManager entityManager = helper.getEntityManager();
		assertThat(entityManager, is(manager));
	}

	@Test
	public void call_tearDownKanbanService() throws Exception {
		helper.tearDownKanbanService();
		verify(kanbanService).deleteAllCards();
		verify(kanbanService).deleteAllLanes();
		verify(kanbanService).deleteAllUsers();
		verify(kanbanService).deleteAllIcons();
		verify(kanbanService).deleteAllBoards();
		verify(fileService).deleteAll();
		verify(kanbanService).setInitialized(eq(false));
	}

	@Test
	public void deleteAllFile() throws Exception {
		helper.deleteAllFile();
		verify(fileService).deleteAll();
	}

	@Ignore("This test is ran on only non OSGi environment.")
	@Test(expected = IllegalStateException.class)
	public void throw_error_when_service_is_null() throws Exception {
		KanbanServiceTestHelper dummy = spy(helper);
		when(dummy.getKanbanService()).thenReturn(null);
		Board board = mock(Board.class);
		dummy.createCardForTest(board, "subject");
	}
}
