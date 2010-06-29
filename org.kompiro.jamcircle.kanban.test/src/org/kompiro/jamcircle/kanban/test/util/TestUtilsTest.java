package org.kompiro.jamcircle.kanban.test.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;


public class TestUtilsTest {
	private TestUtils testUtils;

	@Test
	public void testReadFile_absolute_path() throws Exception {
		String stringFromFile = testUtils.readFile(TestUtils.LONG_TXT_FILE);
		assertNotNull(stringFromFile);
		assertNotSame(0, stringFromFile.length());
	}

	@Test
	public void testReadFile_canonical_path() throws Exception {
		String stringFromFile = testUtils.readFile(TestUtils.LONG_TXT);
		assertNotNull(stringFromFile);
		assertNotSame(0, stringFromFile.length());
	}

	@Test
	public void testReadFile_canonical_path_and_specify_class() throws Exception {
		String stringFromFile = testUtils.readFile(getClass(),TestUtils.LONG_TXT);
		assertNotNull(stringFromFile);
		assertNotSame(0, stringFromFile.length());
	}

	
	@Test
	public void testTarget() throws Exception {
		File target = testUtils.target();
		assertNotNull(target);
	}
	
	@Test
	public void travasalDelteWhenFileListIsNull() throws Exception {
		File file = mock(File.class);
		
		when(file.listFiles()).thenReturn(null);
		testUtils.travasalDelete(file);
	}

	@Before
	public void before() throws Exception {
		testUtils = new TestUtils();
	}
}
