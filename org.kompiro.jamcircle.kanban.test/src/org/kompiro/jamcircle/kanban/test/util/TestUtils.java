package org.kompiro.jamcircle.kanban.test.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;

public class TestUtils {
	public static final String LONG_TXT_FILE = "long.txt";
	public static final String LONG_TXT = "/org/kompiro/jamcircle/kanban/test/util/"
			+ LONG_TXT_FILE;

	public File getFile(Class<?> clazz, String path) throws Exception {
		URL url = clazz.getResource(path);
		if (!url.getProtocol().equals("file")) {
			url = FileLocator.toFileURL(url);
		}
		if ("file".equals(url.getProtocol())) {
			path = url.getPath().replaceAll("%20", " ");
			return new File(path);
		}
		return null;
	}

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

	public void setMockIcon(Lane target, String fileName) throws IOException {
		File base = File.createTempFile("image", ".png");
		File image = spy(base);
		when(image.getName()).thenReturn(fileName);
		when(target.getCustomIcon()).thenReturn(image);
	}

	public Lane createMockLane(final int id, String title, int x, int y, int width, int height) {
		return createMockLane(id, title, x, y, width, height, false);
	}

	public Lane createMockLane(final int id, String title, int x, int y, int width, int height, boolean iconized) {
		org.kompiro.jamcircle.kanban.model.mock.Lane lane = new org.kompiro.jamcircle.kanban.model.mock.Lane() {
			@Override
			public int getID() {
				return id;
			}
		};
		lane.setStatus(title);
		lane.setX(x);
		lane.setY(y);
		lane.setWidth(width);
		lane.setHeight(height);
		lane.setIconized(iconized);
		return lane;
	}

	public Board createMockBoard(String title) {
		org.kompiro.jamcircle.kanban.model.mock.Board board = new org.kompiro.jamcircle.kanban.model.mock.Board();
		board.setTitle(title);
		return board;
	}

	public Board createBoard(String title) {
		return createBoard(title, null);
	}

	public Board createBoard(String title, Lane[] lanes) {
		Board board = mock(Board.class);
		when(board.getTitle()).thenReturn(title);
		if (lanes != null) {
			when(board.getLanes()).thenReturn(lanes);
		}
		return board;
	}

	public Lane createLane(int id, String status, int locationX,
			int locationY, int width, int height) {
		return createLane(id, status, locationX, locationY, width, height, false);
	}

	public Lane createLane(int id, String status, int locationX,
			int locationY, int width, int height, boolean iconized) {
		Lane lane = mock(Lane.class);
		when(lane.getID()).thenReturn(id);
		when(lane.getStatus()).thenReturn(status);
		when(lane.getX()).thenReturn(locationX);
		when(lane.getY()).thenReturn(locationY);
		when(lane.getWidth()).thenReturn(width);
		when(lane.getHeight()).thenReturn(height);
		when(lane.isIconized()).thenReturn(iconized);
		return lane;
	}

}
