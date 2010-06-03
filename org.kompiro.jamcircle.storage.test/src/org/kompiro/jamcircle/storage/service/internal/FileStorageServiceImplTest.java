package org.kompiro.jamcircle.storage.service.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;


public class FileStorageServiceImplTest {
	
	private FileStorageServiceImpl impl;
	private File root;

	@Test
	public void addFile() throws Exception {
		
		File file1 = File.createTempFile("test", ".png");
		impl.addFile("", file1);
		assertThat(root.getAbsolutePath(),root.list().length,is(1));
		File file2 = File.createTempFile("test", ".png");
		impl.addFile("", file2);
		assertThat(root.getAbsolutePath(),root.list().length,is(2));
		File file3 = File.createTempFile("test", ".png");
		impl.addFile("", file3);
		assertThat(root.getAbsolutePath(),root.list().length,is(3));
	}
	
	@Test
	public void deleteAll() throws Exception {
		
		File file1 = File.createTempFile("test", ".png");
		impl.addFile("", file1);
		File file2 = File.createTempFile("test", ".png");
		impl.addFile("", file2);
		File file3 = File.createTempFile("test", ".png");
		impl.addFile("", file3);

		impl.deleteAll();
		
		assertThat(root.list(),is(nullValue()));

	}
	
	@Test
	public void delete() throws Exception {
		
		File file1 = File.createTempFile("test", ".png");
		impl.addFile("", file1);
		impl.deleteFile("", file1);

		assertThat(root.list().length,is(0));
		
	}

	@Before
	public void before() {
		impl = new FileStorageServiceImpl();
		root = new File(impl.getStoreRoot());
		File[] files = root.listFiles();
		if(files == null) return;
		for(File file:files){
			file.delete();
		}
	}

}
