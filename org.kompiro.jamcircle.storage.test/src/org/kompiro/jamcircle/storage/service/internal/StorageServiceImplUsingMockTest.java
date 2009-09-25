package org.kompiro.jamcircle.storage.service.internal;

import static org.mockito.Mockito.*;

import java.util.UUID;

import net.java.ao.*;

import org.junit.Test;

public class StorageServiceImplUsingMockTest {
	
	@Test
	public void deleteAllEntity() throws Exception {
		StorageServiceImpl service = new StorageServiceImpl();
		EntityManager manager = mock(EntityManager.class);
		TestEntity entity = mock(TestEntity.class);
		service.setEntityManager(manager);
		when(manager.find(TestEntity.class)).thenReturn(new TestEntity[]{entity});
		service.deleteAllEntity(TestEntity.class);
		verify(manager,times(1)).delete(entity);
	}
	
	@Test
	public void createEntity() throws Exception{
		StorageServiceImpl service = new StorageServiceImpl();
		EntityManager manager = mock(EntityManager.class);
		service.setEntityManager(manager);
		DBParam[] params = new DBParam[2];
		params[0] = new DBParam(TestEntity.PROP_NAME,"name");
		params[1] = new DBParam(TestEntity.PROP_UUID,UUID.randomUUID());
		service.createEntity(TestEntity.class,params);
		verify(manager,times(1)).create(TestEntity.class,params);
	}
}
