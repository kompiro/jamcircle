package org.kompiro.jamcircle.storage.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.UUID;

import net.java.ao.*;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.kompiro.jamcircle.storage.exception.DBMigrationNeededException;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.kompiro.jamcircle.storage.service.StorageSetting;

/**
 * @TestContext StorageServiceImpl
 * @TestContext StorageServiceImplTest
 */
public class StorageServiceImplUsingMockTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private StorageServiceImpl service;
	private EntityManager manager;

	@Test
	public void deleteAllEntity() throws Exception {
		GraphicalTestEntity entity = mock(GraphicalTestEntity.class);
		when(manager.find(GraphicalTestEntity.class)).thenReturn(new GraphicalTestEntity[] { entity });
		service.deleteAllEntity(GraphicalTestEntity.class);
		verify(manager, times(1)).delete(entity);
	}

	@Test
	public void createEntity() throws Exception {
		DBParam[] params = new DBParam[2];
		params[0] = new DBParam(GraphicalTestEntity.PROP_NAME, "name");
		params[1] = new DBParam(GraphicalTestEntity.PROP_UUID, UUID.randomUUID());
		service.createEntity(GraphicalTestEntity.class, params);
		verify(manager, times(1)).create(GraphicalTestEntity.class, params);
	}

	@Test
	public void discard() throws Exception {
		GraphicalTestEntity entity = mock(GraphicalTestEntity.class);
		service.discard(entity);
		verify(entity, times(1)).setTrashed(true);
	}

	@Test
	public void pickup() throws Exception {
		GraphicalTestEntity entity = mock(GraphicalTestEntity.class);
		service.discard(entity);
		service.pickup(entity);
		verify(entity, times(1)).setTrashed(false);
	}

	@Test(expected = DBMigrationNeededException.class)
	public void migrate_needed() throws Exception {
		service = new StorageServiceImpl() {
			@Override
			protected DatabaseProvider createDatabaseProvider(String uri, String username, String password) {
				DatabaseProvider provider = new DatabaseProvider(uri, username, password) {

					@Override
					protected String renderAutoIncrement() {
						return null;
					}

					protected java.sql.Connection getConnectionImpl() throws SQLException {
						throw new SQLException("", "90048", 90048);
					};

					@Override
					public Class<? extends Driver> getDriverClass() throws ClassNotFoundException {
						return null;
					}
				};
				return provider;
			}
		};
		StorageSetting setting = new StorageSetting(0, folder.getRoot().getAbsolutePath(),
				StorageService.ConnectionMode.MEM.toString(), "sa", "");
		service.loadStorage(setting, new NullProgressMonitor());
	}

	@Test
	public void learn_SQLException() throws Exception {
		SQLException exception = new SQLException("migrate_need", "90048", 90048);
		int errorCode = exception.getErrorCode();
		assertThat(errorCode, is(90048));
	}

	@Before
	public void initialize() throws Exception {
		service = new StorageServiceImpl();
		manager = mock(EntityManager.class);
		service.setEntityManager(manager);
	}

}
