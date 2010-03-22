package org.kompiro.jamcircle.storage.service.internal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kompiro.jamcircle.debug.StandardOutputHandler;
import org.kompiro.jamcircle.storage.StorageStatusHandler;


public class StorageServiceImplInitializeTest {
	
	private StorageServiceImpl service;

	@BeforeClass
	public static void initialize(){
		StorageStatusHandler.addStatusHandler(new StandardOutputHandler());		
	}


	@Test
	public void init() throws Exception {
		service = new StorageServiceImpl();
//		assertThat(service.getStoreRoot(),is("/tmp/JAM_CIRCLE/"));
		
	}

}
