package org.kompiro.jamcircle.storage.service.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.util.UUID;

import net.java.ao.DBParam;
import net.java.ao.Entity;
import net.java.ao.EntityManager;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.StorageSetting;

public class StorageServiceImplTest {

	public class SysoutProgressMonitor implements IProgressMonitor{
		
		private String taskName;

		public void beginTask(String name, int totalWork) {
			System.out.println(String.format("%s - beginTask[%s]", taskName,name));
		}

		public void done() {
			System.out.println("done!");
		}

		public void internalWorked(double work) {
		}

		public boolean isCanceled() {
			return false;
		}

		public void setCanceled(boolean value) {
		}

		public void setTaskName(String name) {
			taskName = name;
			System.out.println(taskName);
		}

		public void subTask(String name) {
			System.out.println(String.format("%s - subTask[%s]", taskName,name));
		}

		public void worked(int work) {
		}
		
	}
	
	public interface TestEntity extends Entity{
		public String getName();
		public void setName(String name);
		
		public void setUuid(String uuid);
		public String getUuid();
		
	}

	private StorageServiceImpl service;
	private EntityManager entityManager;
	private File tempDir;
	
	@BeforeClass
	public static void initialize(){
		StorageStatusHandler.addStatusHandler(new StandardOutputHandler());		
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws Exception{
		service = new StorageServiceImpl();
		tempDir = new File(System.getProperty("java.io.tmpdir"));
		String path = "jdbc:h2:" + tempDir.getAbsolutePath() + File.separator + "test";
		System.out.println(path);
		service.loadStorage(new StorageSetting(0,tempDir.getAbsolutePath(),StorageService.CONNECTION_MODE.FILE.toString(),"","")
		, new SysoutProgressMonitor());
		entityManager = service.getEntityManager();
		assertNotNull(entityManager);
		entityManager.migrate(TestEntity.class);
		TestEntity[] entities = entityManager.find(TestEntity.class);
		entityManager.delete(entities);
	}
	
	@After
	public void tearDown() throws Exception{
		assertNotNull(entityManager);
		TestEntity[] entities = entityManager.find(TestEntity.class);
		entityManager.delete(entities);
	}
	
	@Test
	public void exportAndImport() throws Exception {
		assertEquals(0,entityManager.count(TestEntity.class));
		TestEntity entity = entityManager.create(TestEntity.class, new DBParam[]{new DBParam("uuid",UUID.randomUUID().toString())});
		entity.setName("test");
		entity.save();
		
		assertEquals(1,entityManager.count(TestEntity.class));
		
		File testFile = File.createTempFile("testEntity",".csv");
		System.out.println(testFile.getAbsolutePath());
		assertTrue(service.exportEntity(testFile, TestEntity.class));
		assertTrue(service.importEntity(testFile, TestEntity.class));
		TestEntity[] entities = entityManager.find(TestEntity.class);
		assertEquals(2,entities.length);
	}
	
	@Test
	public void fileControl() throws Exception {
		File testFile = File.createTempFile("test",".txt");
		long current = System.currentTimeMillis();
		FileUtils.writeStringToFile(testFile, "" + current);
		service.addFile("test_dir", testFile);
		File destFile = new File(tempDir,"test" + File.separator + "test_dir" + File.separator + testFile.getName());
		System.out.println(destFile.getAbsolutePath());
		assertTrue(destFile.exists());
		
	}
	
}
