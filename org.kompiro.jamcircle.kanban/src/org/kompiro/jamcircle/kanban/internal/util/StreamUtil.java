package org.kompiro.jamcircle.kanban.internal.util;

import java.io.*;
import java.net.URL;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;

public class StreamUtil {

	private static final String LINE_BREAK = System.getProperty("line.separator");
	private static final String STREAM_UTIL_READ_FROM_RESOURCE = "StreamUtil#readFromResource"; //$NON-NLS-1$

	private StreamUtil() {
	}

	public static String readFromResource(URL resource) {
		try {
			InputStream stream = resource.openStream();
			return readFromStream(stream);
		} catch (IOException e) {
			KanbanStatusHandler.fail(e, STREAM_UTIL_READ_FROM_RESOURCE, true);
			return null;
		}
	}

	public static String readFromStream(InputStream stream) throws IOException {
		Reader r = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(r);
		String line = null;
		StringBuilder builder = new StringBuilder();
		while ((line = br.readLine()) != null) {
			builder.append(line + LINE_BREAK);
		}
		try {
			br.close();
		} catch (IOException e) {
			KanbanStatusHandler.fail(e, STREAM_UTIL_READ_FROM_RESOURCE, true);
		}
		try {
			stream.close();
		} catch (IOException e) {
			KanbanStatusHandler.fail(e, STREAM_UTIL_READ_FROM_RESOURCE, true);
		}
		return builder.toString();
	}

}
