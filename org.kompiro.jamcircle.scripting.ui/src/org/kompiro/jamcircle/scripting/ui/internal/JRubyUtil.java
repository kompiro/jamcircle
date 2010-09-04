package org.kompiro.jamcircle.scripting.ui.internal;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

public class JRubyUtil {
	public String getJRubyHomeFromBundle() {
		try {
			String path = new File(
					FileLocator.getBundleFile(Platform.getBundle(Constants.BUNDLE_OF_ORG_JRUBY)),
					Constants.PATH_OF_JRUBY_HOME).getAbsolutePath();
			return path;
		} catch (IOException e) {
		}
		return null;
	}

	public String getGemHome() {
		String instancePath = Platform.getInstanceLocation().getURL().getFile();
		if (instancePath.endsWith(File.separator) == false) {
			instancePath += File.separator;
		}
		String gemHome = instancePath + "gems" + File.separator;
		return gemHome;
	}

}
