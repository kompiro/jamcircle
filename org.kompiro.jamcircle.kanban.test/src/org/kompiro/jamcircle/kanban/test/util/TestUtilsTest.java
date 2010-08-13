package org.kompiro.jamcircle.kanban.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.BoardConverterImplTest;

public class TestUtilsTest {
	private TestUtils testUtils;

	@Test
	public void getFile() throws Exception {
		File file = testUtils.getFile(BoardConverterImplTest.class, "Sample Board.zip");
		assertThat(file, not(nullValue()));
		System.out.println(file.getAbsolutePath());
		assertThat(file.exists(), is(true));
	}

	@Test
	public void readFile_absolute_path() throws Exception {
		String stringFromFile = testUtils.readFile(TestUtils.LONG_TXT_FILE);
		assertNotNull(stringFromFile);
		assertNotSame(0, stringFromFile.length());
	}

	@Test
	public void readFile_canonical_path() throws Exception {
		String stringFromFile = testUtils.readFile(TestUtils.LONG_TXT);
		assertNotNull(stringFromFile);
		assertNotSame(0, stringFromFile.length());
	}

	@Test
	public void readFile_canonical_path_and_specify_class() throws Exception {
		String stringFromFile = testUtils.readFile(getClass(), TestUtils.LONG_TXT);
		assertNotNull(stringFromFile);
		assertNotSame(0, stringFromFile.length());
	}

	@Test
	public void target() throws Exception {
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
