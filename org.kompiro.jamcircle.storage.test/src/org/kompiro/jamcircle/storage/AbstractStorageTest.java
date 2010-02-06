package org.kompiro.jamcircle.storage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.EntityManager;

import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public abstract class AbstractStorageTest {
	protected static StorageActivator activator;
	protected static EntityManager manager;
	
	@BeforeClass
	public static void bundleInitialize() throws Exception{
		if(!Platform.isRunning()){
			throw new IllegalStateException("Please launch on PDE Environment");
		}
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		activator = StorageActivator.getDefault();
		StorageService service = getStorageService();
		manager = service.getEntityManager();
		assertNotNull(manager);
		assertAutoCommit(manager);
	}

	public static StorageService getStorageService() {
		BundleContext context = activator.getBundle().getBundleContext();
		ServiceReference reference = context.getServiceReference(StorageService.class.getName());
		return (StorageService) context.getService(reference);
	}
	
	@Before
	public void before()throws Exception{
		if(!Platform.isRunning()) throw new RuntimeException("This test needs to run on OSGi container.");		
	}
		
	private static void assertAutoCommit(EntityManager manager) throws SQLException {
		Connection conn = manager.getProvider().getConnection();
		assertTrue(conn.getAutoCommit());
	}
}
