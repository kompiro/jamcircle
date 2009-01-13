/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.releng;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import java.io.File;

/*
 * A class that strips version numbers off built plugin directory names.  This
 * is helpful when prebuilt plugins are used in generating javadoc (on the
 * classpath).
 */
 
public class VersionNumberStripper extends Task {

	//the directory containing the directories and files from which to remove version information
	private String directory;
	
	public VersionNumberStripper() {
		super();
	}

	public void setDirectory(String dir){directory=dir;}
	
	public String getDirectory(){return directory;}

  	public void execute() throws BuildException {
  		setDirectory(directory);
  		stripVersions();
  	}

	public static void main(String[] args) {
		new VersionNumberStripper().execute();
	}

	private void stripVersions(){
		/* rename directories by removing anything from an underscore onward,
		 * assuming that anything following the first
		 * occurence of an underscore is a version number
		 */
		 
		File file=new File(directory);
		
		File [] files = file.listFiles();
		
		for (int i=0; i<files.length; i++){
		int underScorePos = files[i].getAbsolutePath().indexOf("_");
			int jarExtPos = files[i].getAbsolutePath().indexOf(".jar");
			if (underScorePos != -1) {
				if (jarExtPos != -1)
					files[i].renameTo(new File((files[i].getAbsolutePath())
							.substring(0, underScorePos)
							+ ".jar"));
				else
					files[i].renameTo(new File((files[i].getAbsolutePath())
							.substring(0, underScorePos)));
			}

		}
	}
}

