package org.kompiro.jamcircle.kanban.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.osgi.framework.Bundle;

public class TestUtils {
	public static final String LONG_TXT_FILE = "long.txt";
	public static final String LONG_TXT = "/org/kompiro/jamcircle/kanban/model/" + LONG_TXT_FILE;
	private static Bundle bundle;
		
	@Test
	public void testReadFile() throws Exception {
		String stringFromFile = readFile();
		assertNotNull(stringFromFile);
		assertNotSame(0,stringFromFile.length());
		System.out.println(stringFromFile);
	}
	
	@Test
	public void testTarget() throws Exception {
		File target = target();
		assertNotNull(target);
		System.out.println(target.getAbsoluteFile());
	}
	
	public String readFile() throws Exception{
		URL resource = getResource();
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
	
	public File target() throws Exception{
		URL resource = getResource();
		if (!resource.getProtocol().equals("file")){
			resource = FileLocator.resolve(resource);
		}
		if ("file".equals(resource.getProtocol())){
			return new File(resource.getPath());
		}
		return null;
	}

	private URL getResource() {
		KanbanActivator activator = KanbanActivator.getDefault();
		if(activator == null){
			return getClass().getResource(LONG_TXT_FILE);
		}
		bundle = activator.getBundle();

		return bundle.getResource(LONG_TXT);
	}

}
