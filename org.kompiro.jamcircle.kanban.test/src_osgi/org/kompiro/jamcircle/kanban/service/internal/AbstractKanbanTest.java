package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assume.assumeNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kompiro.jamcircle.kanban.KanbanActivator;

public abstract class AbstractKanbanTest {

	private static KanbanServiceImpl kanbanService;
	protected EntityManager entityManager;
	
	@BeforeClass
	public static void initializeEnvironment() throws Exception{
		KanbanActivator activator = getActivator();
		assumeNotNull("Please launch on PDE Environment",activator);
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
	}
	
	@Before
	public void init() throws Exception{
		getKanbanService().forceInit();
		entityManager = getKanbanService().getEntityManager();
	}

	protected static KanbanServiceImpl getKanbanService() {
		if(kanbanService == null){
			kanbanService = (KanbanServiceImpl)KanbanActivator.getKanbanService();
		}
		return kanbanService;
	}

	protected static KanbanActivator getActivator() {
		return KanbanActivator.getDefault();
	}
		
	@After
	public void after() throws Exception{
		try {
			getKanbanService().deleteAllCards();
		} catch (Exception e) {
			showErrorInAfterMethods("AllCards",e.getLocalizedMessage());
		}
		
		try {
			getKanbanService().deleteAllLanes();
		} catch (Exception e) {
			showErrorInAfterMethods("AllLanes",e.getLocalizedMessage());
		}
		
		try {
			getKanbanService().deleteAllUsers();
		} catch (Exception e) {
			showErrorInAfterMethods("AllUsers",e.getLocalizedMessage());
		}
		
		try {
			getKanbanService().deleteAllIcons();
		} catch (Exception e) {
			showErrorInAfterMethods("AllIcons",e.getLocalizedMessage());
		}

		try {
			getKanbanService().deleteAllBoards();
		} catch (Exception e) {
			showErrorInAfterMethods("AllBoards",e.getLocalizedMessage());
		}
		
		KanbanServiceImpl service = getKanbanService();
		service.setInitialized(false);
	}

	private void showErrorInAfterMethods(String methodName,String localizedMessage) {
		String message = String.format("%s:%s",methodName,localizedMessage);
		System.err.println(message);
	}

}
