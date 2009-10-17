package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;

import org.junit.*;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public abstract class AbstractKanbanTest {
	private static final String FILE = "file:";
	protected static EntityManager entityManager;
	
	@BeforeClass
	public static void initializeEnvironment() throws Exception{
//		try{
			KanbanActivator activator = getActivator();
			if(activator == null){
				throw new IllegalStateException("Please launch on PDE Environment");
			}
			entityManager = activator.getEntityManager();
			Logger.getLogger("net.java.ao").setLevel(Level.FINE);
			assertNotNull(entityManager);
//			String storePath = activator.getStorageService().getDBPath();
//			int schemeIndex = storePath.indexOf(FILE);
//			if(schemeIndex != -1){
//				storePath = storePath.substring(FILE.length());
//			}
//			assertTrue(storePath.indexOf("test") != -1);
//			System.out.println(storePath);
//			assertNotNull(getKanbanService());
//			
//		}catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}
//		System.out.println("AbstractKanbanTest.init() end");
	}
	
	@Before
	public void init() throws Exception{
		getKanbanService().forceInit();
	}

	protected static KanbanServiceImpl getKanbanService() {
		KanbanService kanbanService = getActivator().getKanbanService();
		return (KanbanServiceImpl)kanbanService;
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
	
	@AfterClass
	public static void afterAll() throws Exception{
		DatabaseProvider provider = entityManager.getProvider();
		Connection connection = provider.getConnection();
		connection.prepareStatement("DROP TABLE card,lane,user,icon,board").execute();
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		provider.dispose();

	}

	private void showErrorInAfterMethods(String methodName,String localizedMessage) {
		String message = String.format("%s:%s",methodName,localizedMessage);
		System.err.println(message);
	}

}
