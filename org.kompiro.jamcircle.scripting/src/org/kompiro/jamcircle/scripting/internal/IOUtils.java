package org.kompiro.jamcircle.scripting.internal;

import java.io.*;

public class IOUtils {

	public static String getStringFromReader (Reader reader) throws IOException {
	BufferedReader bufIn = new BufferedReader(reader);
	StringWriter   swOut = new StringWriter();
	PrintWriter    pwOut = new PrintWriter(swOut);
	String         tempLine;

	while ((tempLine = bufIn.readLine()) != null) {
	  pwOut.println(tempLine);
	}

	pwOut.flush();

	return swOut.toString();
  }
}
