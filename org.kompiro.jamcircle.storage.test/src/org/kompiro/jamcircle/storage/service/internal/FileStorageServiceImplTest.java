package org.kompiro.jamcircle.storage.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

public class FileStorageServiceImplTest {

	private FileStorageServiceImpl impl;
	private File root;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() {
		impl = new FileStorageServiceImpl();
		root = new File(impl.getStoreRoot());
		FileUtils.deleteQuietly(root);
	}

	@Test
	public void addFile() throws Exception {

		File file1 = folder.newFile("test1.png");
		impl.addFile("", file1);
		assertThat(root.getAbsolutePath(), root.list().length, is(1));
		File file2 = folder.newFile("test2.png");
		impl.addFile("", file2);
		assertThat(root.getAbsolutePath(), root.list().length, is(2));
		File file3 = folder.newFile("test3.png");
		impl.addFile("", file3);
		assertThat(root.getAbsolutePath(), root.list().length, is(3));
	}

	@Test
	public void deleteAll() throws Exception {

		File file1 = folder.newFile("test1.png");
		impl.addFile("", file1);
		File file2 = folder.newFile("test2.png");
		impl.addFile("", file2);
		File file3 = folder.newFile("test3.png");
		impl.addFile("", file3);

		impl.deleteAll();

		assertThat(root.list(), is(nullValue()));

	}

	@Test
	public void fileExists() throws Exception {
		assertThat(impl.fileExists("test1.png"), is(false));
		File file = folder.newFile("test1.png");
		impl.addFile("", file);
		assertThat(impl.fileExists("test1.png"), is(true));
	}

	@Test
	public void fileExists_different_path() throws Exception {
		assertThat(impl.fileExists("test1.png"), is(false));
		File file = folder.newFile("test1.png");
		impl.addFile("test", file);
		assertThat(impl.fileExists("test/test1.png"), is(true));
	}

	@Test
	public void getFiles() throws Exception {
		File file = folder.newFile("test1.png");
		impl.addFile("", file);
		List<File> files = impl.getFiles("");
		assertThat(files.size(), is(1));
		assertThat(files.get(0).getName(), is(file.getName()));
	}

	@Test
	public void delete() throws Exception {

		File file1 = folder.newFile("test1.png");
		impl.addFile("", file1);
		impl.deleteFile("", file1);

		assertThat(root.list().length, is(0));

	}

}
