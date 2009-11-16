/*
 * Copyright 2004,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import java.io.*;

/**
 * This file is a collection of input/output utilities.
 * 
 * @author   Sanjiva Weerawarana
 * @author   Matthew J. Duftler
 *
 * This class is copyed from org.apache.bsf
 */
public class IOUtils {
  static boolean debug = false;

  public static String getStringFromResource (String path) {
	StringWriter swOut = new StringWriter();

	try {
		InputStreamReader reader = new InputStreamReader(IOUtils.class.getResource(path).openStream());
		BufferedReader bufIn = new BufferedReader(reader);
		PrintWriter    pwOut = new PrintWriter(swOut);
		String         tempLine;

		while ((tempLine = bufIn.readLine()) != null) {
		  pwOut.println(tempLine);
		}

		pwOut.flush();
	} catch (IOException e) {
		
	}

	return swOut.toString();
  }
}
