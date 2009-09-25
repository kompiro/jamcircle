package org.kompiro.jamcircle.storage.service.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.util.UUID;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.kompiro.jamcircle.debug.SysoutProgressMonitor;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.StorageSetting;

public class StorageServiceImplTest {

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
