package org.kompiro.jamcircle.kanban;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;


import net.java.ao.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;

public abstract class AbstractKanbanTest {
	private static final String FILE = "file:";
	protected EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws Exception{
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
		System.out.println(storePath);
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
		System.out.println("AbstractKanbanTest.init()");
	}

	protected KanbanServiceImpl getKanbanService() {
		KanbanService kanbanService = getActivator().getKanbanService();
		return (KanbanServiceImpl)kanbanService;
	}

	private KanbanActivator getActivator() {
		return KanbanActivator.getDefault();
	}
	
	@After
	public void after() throws Exception{
		assertNotNull(entityManager);
		Card[] cards = entityManager.find(Card.class);
		if(cards != null) entityManager.delete(cards);

		Lane[] lanes = entityManager.find(Lane.class);
		if(lanes != null) entityManager.delete(lanes);
		
		User[] users = entityManager.find(User.class);
		if(users != null) entityManager.delete(users);
		Icon[] icons = entityManager.find(Icon.class);
		if(icons != null) entityManager.delete(icons);
		Board[] boards = entityManager.find(Board.class);
		if(boards != null) entityManager.delete(boards);
	}

}
