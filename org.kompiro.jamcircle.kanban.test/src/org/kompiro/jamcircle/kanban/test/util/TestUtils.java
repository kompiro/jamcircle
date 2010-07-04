package org.kompiro.jamcircle.kanban.test.util;

import static org.junit.Assert.assertNotNull;

import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

public class TestUtils {
	public static final String LONG_TXT_FILE = "long.txt";
	public static final String LONG_TXT = "/org/kompiro/jamcircle/kanban/test/util/"
			+ LONG_TXT_FILE;

	public String readFile(String path) throws Exception {
		return readFile(getClass(), path);
	}

	public String readFile(Class<?> clazz, String path) throws Exception {
		URL resource = getResource(clazz, path);
		assertNotNull(resource);
		InputStream stream = resource.openStream();
		Reader r = new InputStreamReader(stream);
		return readFromReader(r);
	}

	public String readFromReader(Reader r) throws IOException {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(r);
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line + "\n");
			}
		} finally {
			r.close();
		}
		return builder.toString();
	}

	public File target() throws Exception {
		URL resource = getResource(LONG_TXT);
		if (!resource.getProtocol().equals("file")) {
			resource = FileLocator.resolve(resource);
		}
		if ("file".equals(resource.getProtocol())) {
			return new File(resource.getPath());
		}
		return null;
	}

	private URL getResource(String path) {
		return getResource(getClass(), path);
	}

	private URL getResource(Class<?> clazz, String path) {
		return clazz.getResource(path);
	}

	public void travasalDelete(File parent) {
		if (parent == null)
			return;
		if (parent.isFile() || parent.listFiles() == null) {
			parent.delete();
			return;
		}
		for (File file : parent.listFiles()) {
			if (file.isDirectory()) {
				travasalDelete(file);
			}
			file.delete();
		}
	}

}
