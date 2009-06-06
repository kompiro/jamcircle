package org.kompiro.jamcircle.kanban.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.internal.KanbanMigrator;
import org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImpl;

public class KanbanMigratorTest {
	
	private KanbanActivator activator;
	private static final String FILE = "file:";


	@Before
	public void init() throws Exception{
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		activator = KanbanActivator.getDefault();
		assertNotNull(activator);
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
	}
	
	@Test
	public void migrate() throws Exception {
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		KanbanServiceImpl service = new KanbanServiceImpl(activator);
		KanbanMigrator migrator = new KanbanMigrator(service);
		migrator.migrate();
		Board[] boards = service.findAllBoard();
		assertEquals(0,boards.length);
		Lane[] lanes = service.findAllLanes();
		assertEquals(0,lanes.length);
	}
}
