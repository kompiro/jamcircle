package learning;

import java.io.InputStream;

import org.junit.Test;

/**
 * This class is implemented only for learning.
 * Not Testing.
 */
public class Learning {

	@Test
	public void name() throws Exception {

		ProcessBuilder builder = new ProcessBuilder("/usr/local/git/bin/git");
		Process process = builder.start();
		InputStream stream = process.getInputStream();
		while (true) {
			int c = stream.read();
			if (c == -1) {
				stream.close();
				break;
			}
			System.out.print((char) c);
		}
	}

	@Test
	public void testname() throws Exception {

		System.out.println(System.getProperty("file.separator"));

	}
}
