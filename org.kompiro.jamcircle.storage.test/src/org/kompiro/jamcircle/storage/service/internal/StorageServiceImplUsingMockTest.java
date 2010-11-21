package org.kompiro.jamcircle.storage.service.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.junit.Before;
import org.junit.Test;

/**
 * @TestContext StorageServiceImpl
 * @TestContext StorageServiceImplTest
 */
public class StorageServiceImplUsingMockTest {

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

	@Before
	public void initialize() throws Exception {
		service = new StorageServiceImpl();
		manager = mock(EntityManager.class);
		service.setEntityManager(manager);
	}

}
