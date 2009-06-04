package org.kompiro.jamcircle.kanban.model;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;


import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.osgi.framework.Bundle;

public class TestUtils {
	public static final String LONG_TXT = "/org/kompiro/jamcircle/kanban/model/long.txt";

	@Test
	public void testReadFile() throws Exception {
		assertTrue(Platform.isRunning());
		String stringFromFile = TestUtils.readFile();
		assertNotNull(stringFromFile);
		assertNotSame(0,stringFromFile.length());
		System.out.println(stringFromFile);
	}
	
	@Test
	public void testTarget() throws Exception {
		File target = TestUtils.target();
		assertNotNull(target);
		System.out.println(target.getAbsoluteFile());
	}
	
	public static String readFile() throws Exception{
		Bundle bundle = KanbanActivator.getDefault().getBundle();
		assertNotNull(bundle);
		URL resource = bundle.getResource(LONG_TXT);
		assertNotNull(resource);
		InputStream stream= resource.openStream();
		Reader r = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(r);
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null){
			builder.append(line + "\n");
		}
		return builder.toString();
	}
	
	public static File target() throws Exception{
		Bundle bundle = KanbanActivator.getDefault().getBundle();
		assertNotNull(bundle);
		URL resource = bundle.getResource(LONG_TXT);
		resource = FileLocator.resolve(resource);
		if ("file".equals(resource.getProtocol())){
			return new File(resource.getPath());
		}
		return null;
	}

}
