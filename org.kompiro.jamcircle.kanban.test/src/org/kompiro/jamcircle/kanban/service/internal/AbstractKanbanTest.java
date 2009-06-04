package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.EntityManager;

import org.junit.*;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public abstract class AbstractKanbanTest {
	private static final String FILE = "file:";
	protected static EntityManager entityManager;
	
	@BeforeClass
	public static void initializeEnvironment() throws Exception{
		try{
			KanbanActivator activator = getActivator();
			if(activator == null){
				throw new IllegalStateException("Please launch on PDE Environment");
			}
			entityManager = activator.getEntityManager();
			Logger.getLogger("net.java.ao").setLevel(Level.FINE);
			assertNotNull(entityManager);
			String storePath = activator.getStorageService().getDBPath();
			int schemeIndex = storePath.indexOf(FILE);
			if(schemeIndex != -1){
				storePath = storePath.substring(FILE.length());
			}
			assertTrue(storePath.indexOf("test") != -1);
			System.out.println(storePath);
			File file = new File(storePath);
			File parentFile = file.getParentFile();
			
			parentFile.list(new FilenameFilter(){

				public boolean accept(File file, String name) {
					File target = new File(file,name);
					Logger.getLogger("net.java.ao").info("deleted : " + target.getAbsolutePath());
					return target.delete();
				}
			
			});
			assertNotNull(getKanbanService());
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		System.out.println("AbstractKanbanTest.init() end");
	}
	
	@Before
	public void init() throws Exception{
		getKanbanService().init();
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
			e.printStackTrace();
		}

		try {
			getKanbanService().deleteAllLanes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			getKanbanService().deleteAllUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			getKanbanService().deleteAllIcons();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			getKanbanService().deleteAllBoards();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		KanbanServiceImpl service = getKanbanService();
		service.setInitialized(false);
//		DatabaseProvider provider = entityManager.getProvider();
//		Connection connection = provider.getConnection();
//		try {
//			connection.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		provider.dispose();
	}

}
