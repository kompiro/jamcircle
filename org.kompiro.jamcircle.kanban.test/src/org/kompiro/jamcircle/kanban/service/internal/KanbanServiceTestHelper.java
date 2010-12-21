package org.kompiro.jamcircle.kanban.service.internal;

import static org.kompiro.jamcircle.kanban.model.Lane.VALUE_OF_HEIGHT;
import static org.kompiro.jamcircle.kanban.model.Lane.VALUE_OF_WIDTH;
import net.java.ao.EntityManager;

import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.functional.model.*;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.storage.service.FileStorageService;

public class KanbanServiceTestHelper {

	private KanbanServiceImpl kanbanService;

	public KanbanServiceImpl getKanbanService() {
		if (kanbanService == null) {
			kanbanService = (KanbanServiceImpl) KanbanActivator.getKanbanService();
		}
		if (kanbanService == null)
			throw new IllegalStateException("Could not get KanbanService.");
		return kanbanService;
	}

	public void setKanbanService(KanbanServiceImpl kanbanService) {
		this.kanbanService = kanbanService;
	}

	public Board createBoardForTest(String title) {
		return getKanbanService().createBoard(title);
	}

	public Card createCardForTest(Board board, String subject) {
		return getKanbanService().createCard(board, subject, null, 0, 0);
	}

	public Lane createLaneForTest(Board board, String status) {
		return getKanbanService().createLane(board, status, 0, 0, VALUE_OF_WIDTH, VALUE_OF_HEIGHT);
	}

	public Icon createIconForTest(String type) {
		return getKanbanService().addIcon(type, 0, 0);
	}

	public User createUserForTest(String userId) {
		return getKanbanService().addUser(userId);
	}

	public void forceInitKanbanService() {
		getKanbanService().forceInit();
	}

	public EntityManager getEntityManager() {
		return getKanbanService().getEntityManager();
	}

	public void tearDownKanbanService() {
		try {
			getKanbanService().deleteAllCards();
		} catch (Exception e) {
			showErrorInAfterMethods("AllCards", e.getLocalizedMessage());
		}

		try {
			getKanbanService().deleteAllLanes();
		} catch (Exception e) {
			showErrorInAfterMethods("AllLanes", e.getLocalizedMessage());
		}

		try {
			getKanbanService().deleteAllUsers();
		} catch (Exception e) {
			showErrorInAfterMethods("AllUsers", e.getLocalizedMessage());
		}

		try {
			getKanbanService().deleteAllIcons();
		} catch (Exception e) {
			showErrorInAfterMethods("AllIcons", e.getLocalizedMessage());
		}

		try {
			getKanbanService().deleteAllBoards();
		} catch (Exception e) {
			showErrorInAfterMethods("AllBoards", e.getLocalizedMessage());
		}
		deleteAllFile();

		KanbanServiceImpl service = getKanbanService();
		service.setInitialized(false);
	}

	public void deleteAllFile() {
		FileStorageService fileService = getKanbanService().getStorageService().getFileService();
		((org.kompiro.jamcircle.storage.service.internal.FileStorageServiceImpl) fileService).deleteAll();
	}

	private void showErrorInAfterMethods(String methodName, String localizedMessage) {
		String message = String.format("%s:%s", methodName, localizedMessage);
		System.err.println(message);
	}
}
