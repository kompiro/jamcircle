package org.kompiro.jamcircle.storage.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.java.ao.Entity;
import net.java.ao.EntityManager;

import org.junit.Test;

public class EntityImplTest {

	@Test
	public void save() throws Exception {
		Entity entity = mock(Entity.class);
		EntityManager manager = mock(EntityManager.class);
		when(entity.getEntityManager()).thenReturn(manager);
		EntityImpl impl = new EntityImpl(entity) {
		};
		impl.save(false);

	}

}
