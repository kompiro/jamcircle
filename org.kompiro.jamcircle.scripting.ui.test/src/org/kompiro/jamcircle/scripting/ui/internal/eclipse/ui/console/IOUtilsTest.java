package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class IOUtilsTest {

	@Test
	public void getStringFromResource() throws Exception {
		String string = IOUtils.getStringFromResource("test.txt");
		assertThat(string,is("This is a test document.\n"));
		
	}
	
}
