package org.kompiro.jamcircle.scripting.ui.internal.ui.console;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class IOUtilsTest {

	@Test
	public void getStringFromResource() throws Exception {
		String string = IOUtils.getStringFromResource(IOUtils.class, "test.txt");
		assertThat(string, is("This is a test document.\n"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStringFromResource_throw_IllegalArgumentException_when_not_exist_resource() throws Exception {
		IOUtils.getStringFromResource(IOUtils.class, "not_exist.txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStringFromResource_throw_IllegalArgumentException_when_class_is_null() throws Exception {
		IOUtils.getStringFromResource(null, "dummy.txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getStringFromResource_throw_IllegalArgumentException_when_resource_is_null() throws Exception {
		IOUtils.getStringFromResource(IOUtils.class, null);
	}

}
