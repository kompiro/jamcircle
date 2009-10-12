package org.kompiro.jamcircle.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import net.java.ao.EntityManager;

import org.eclipse.core.runtime.Platform;
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
		if(!Platform.isRunning()){
			throw new IllegalStateException("Please launch on PDE Environment");
		}
//		Logger.getLogger("net.java.ao").setLevel(Level.FINE);
		activator = StorageActivator.getDefault();
		String storePath = activator.getService().getDBPath();
		int schemeIndex = storePath.indexOf(FILE);
		if(schemeIndex != -1){
			storePath = storePath.substring(FILE.length());
		}
		System.out.println(storePath);
		File file = new File(storePath);
		if(file.exists()){
			if(!file.delete()){
				System.err.println("can't delete file");
			}
		}
		String path = file.getAbsolutePath();
		assertNotSame("can't connect Test DB.'" + path + "'", -1 , path.indexOf("test"));
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
