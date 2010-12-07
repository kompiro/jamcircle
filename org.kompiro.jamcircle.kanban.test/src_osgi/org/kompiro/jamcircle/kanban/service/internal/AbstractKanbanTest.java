package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assume.assumeNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.EntityManager;

import org.junit.*;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.model.*;

public abstract class AbstractKanbanTest {

	protected KanbanServiceTestHelper helper;
	protected EntityManager entityManager;

	@BeforeClass
	public static void initializeEnvironment() throws Exception {
		KanbanActivator activator = getActivator();
		assumeNotNull("Please launch on PDE Environment", activator);
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
	}

	@Before
	public void init() throws Exception {
		helper = new KanbanServiceTestHelper();
		helper.forceInitKanbanService();
		entityManager = helper.getEntityManager();
	}

	protected static KanbanActivator getActivator() {
		return KanbanActivator.getDefault();
	}

	protected Board createBoardForTest(String title) {
		return helper.createBoardForTest(title);
	}

	protected Card createCardForTest(Board board, String subject) {
		return helper.createCardForTest(board, subject);
	}

	protected Lane createLaneForTest(Board board, String status) {
		return helper.createLaneForTest(board, status);
	}

	protected Icon createIconForTest(String type) {
		return helper.createIconForTest(type);
	}

	protected User createUserForTest(String userId) {
		return helper.createUserForTest(userId);
	}

	@SuppressWarnings("restriction")
	@After
	public void after() throws Exception {
		helper.tearDownKanbanService();
	}

}
