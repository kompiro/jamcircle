package org.kompiro.jamcircle.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.ao.EntityManager;

import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kompiro.jamcircle.storage.service.StorageService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public abstract class AbstractStorageTest {
	private static final String FILE = "file:";
	protected static StorageActivator activator;
	protected static EntityManager manager;
	
//	public abstract void init() throws Exception;

	@BeforeClass
	public static void bundleInitialize() throws Exception{
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		if(!Platform.isRunning()) return;
		activator = StorageActivator.getDefault();
		String storePath = activator.getService().getDBPath();
		int schemeIndex = storePath.indexOf(FILE);
		if(schemeIndex != -1){
			storePath = storePath.substring(FILE.length());
		}
		System.out.println(storePath);
		File file = new File(storePath);
		String path = file.getAbsolutePath();
		assertNotSame("can't connect Test DB.'" + path + "'", -1 , path.indexOf("test"));
		BundleContext context = activator.getBundle().getBundleContext();
		ServiceReference reference = context.getServiceReference(StorageService.class.getName());
		StorageService service = (StorageService) context.getService(reference);
		manager = service.getEntityManager();
		assertNotNull(manager);
		assertAutoCommit(manager);
		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
	}
	
	@Before
	public void before()throws Exception{
		if(!Platform.isRunning()) throw new RuntimeException("This test needs to run on OSGi container.");		
	}
	
	@AfterClass
	public static void after() throws Exception{
	}
	
	private static void assertAutoCommit(EntityManager manager) throws SQLException {
		Connection conn = manager.getProvider().getConnection();
		assertTrue(conn.getAutoCommit());
	}
}
