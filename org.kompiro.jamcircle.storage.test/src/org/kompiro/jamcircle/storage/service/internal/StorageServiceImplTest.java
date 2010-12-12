package org.kompiro.jamcircle.storage.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.jobs.*;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.kompiro.jamcircle.debug.SysoutProgressMonitor;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.StorageSetting;
import org.kompiro.jamcircle.test.OSGiEnvironment;

public class StorageServiceImplTest {

	private StorageServiceImpl service;
	private EntityManager entityManager;

	@Rule
	public OSGiEnvironment env = new OSGiEnvironment();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private FileStorageServiceImpl impl;

	@BeforeClass
	public static void initialize() {
		StorageStatusHandler.addStatusHandler(new StandardOutputHandler());
		Job.getJobManager().addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				System.out.println(event.getJob().getName());
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Before
	public void init() throws Exception {
		service = new StorageServiceImpl();
		service.loadStorage(
				new StorageSetting(0, folder.getRoot().getAbsolutePath(), StorageService.ConnectionMode.MEM.toString(),
						"sa", "")
				, new SysoutProgressMonitor());
		entityManager = service.getEntityManager();
		assertNotNull(entityManager);
		entityManager.migrate(GraphicalTestEntity.class);
		entityManager.delete(entityManager.find(GraphicalTestEntity.class));
		assertEquals(0, entityManager.count(GraphicalTestEntity.class));
		impl = (FileStorageServiceImpl) service.getFileService();
	}

	@After
	public void tearDown() throws Exception {
		assertNotNull(entityManager);
		GraphicalTestEntity[] entities = entityManager.find(GraphicalTestEntity.class);
		entityManager.delete(entities);
	}

	@Test
	public void exportAndImport() throws Exception {
		GraphicalTestEntity entity = createTestEntity();
		entity.setName("test");
		entity.save();

		assertEquals(1, entityManager.count(GraphicalTestEntity.class));

		File testFile = folder.newFile("testEntity.csv");
		System.out.println(testFile.getAbsolutePath());
		assertTrue(service.exportEntity(testFile, GraphicalTestEntity.class));
		assertTrue(service.importEntity(testFile, GraphicalTestEntity.class));
		GraphicalTestEntity[] entities = entityManager.find(GraphicalTestEntity.class);
		assertEquals(2, entities.length);
	}

	@Test
	public void file_control() throws Exception {
		File testFile = folder.newFile("test.txt");
		long current = System.currentTimeMillis();
		FileUtils.writeStringToFile(testFile, "" + current);
		File root = folder.newFolder("root");
		impl.setStoreRoot(root.getAbsolutePath());
		service.getFileService().addFile("test_dir", testFile);
		File destFile = new File(new File(root, FileStorageServiceImpl.addtionalPath), "test_dir");
		destFile = new File(destFile, "test.txt");
		assertTrue(destFile.exists());
	}

	@Test
	public void deleteTrashedEntity() throws Exception {
		GraphicalTestEntity entity = createTestEntity();
		entity.setName("test");
		entity.save(false);
		assertThat(service.count(GraphicalTestEntity.class), is(1));

		service.deleteTrashedEntity(GraphicalTestEntity.class);
		assertThat(service.count(GraphicalTestEntity.class), is(1));

		entity.setTrashed(true); // trash
		entity.save(false);
		service.deleteTrashedEntity(GraphicalTestEntity.class);
		assertThat(service.count(GraphicalTestEntity.class), is(0));
	}

	@Test
	public void create_and_count() throws Exception {
		GraphicalTestEntity entity = createTestEntity();
		entity.setName("test");
		entity.save(false);
		assertEquals(1, service.count(GraphicalTestEntity.class));
	}

	@Test
	public void delete() throws Exception {
		GraphicalTestEntity entity = createTestEntity();
		entity.setName("test");
		entity.save(false);
		service.delete(entity);
		assertThat(service.count(GraphicalTestEntity.class), is(0));

	}

	@Test
	public void discard_and_pickup() throws Exception {
		GraphicalTestEntity entity = createTestEntity();
		service.discard(entity);
		assertThat(entity.isTrashed(), is(true));
		service.pickup(entity);
		assertThat(entity.isTrashed(), is(false));
	}

	@Test
	public void findInTrash() throws Exception {
		GraphicalTestEntity entity1 = createTestEntity();
		entity1.setName("entity1");
		entity1.save(false);

		GraphicalTestEntity entity2 = createTestEntity();
		entity2.setName("entity2");
		entity2.save(false);

		GraphicalTestEntity trashed = createTestEntity();
		trashed.setName("trashed");
		trashed.setTrashed(true);
		trashed.save(false);

		GraphicalTestEntity[] findInTrash = service.findInTrash(GraphicalTestEntity.class);
		assertThat(findInTrash.length, is(1));

		entity2.setTrashed(true);
		entity2.save(false);

		findInTrash = service.findInTrash(GraphicalTestEntity.class);
		assertThat(findInTrash.length, is(2));
	}

	@Test
	public void countInTrash() throws Exception {
		int count = service.countInTrash(GraphicalTestEntity.class);
		assertThat(count, is(0));

		GraphicalTestEntity entity1 = createTestEntity();
		entity1.setName("entity1");
		entity1.save(false);
		count = service.countInTrash(GraphicalTestEntity.class);
		assertThat(count, is(0));

		GraphicalTestEntity trashed = createTestEntity();
		trashed.setName("trashed");
		trashed.setTrashed(true);
		trashed.save(false);
		count = service.countInTrash(GraphicalTestEntity.class);
		assertThat(count, is(1));

	}

	private GraphicalTestEntity createTestEntity() throws SQLException {
		return entityManager.create(GraphicalTestEntity.class,
				new DBParam[] {
				new DBParam("uuid", UUID.randomUUID().toString())
				});
	}

}
