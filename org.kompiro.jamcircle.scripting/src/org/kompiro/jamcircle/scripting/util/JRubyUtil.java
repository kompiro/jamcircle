package org.kompiro.jamcircle.scripting.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class JRubyUtil {

	private static final String GEMS_HOME = ".gems";

	public String getJRubyHomeFromBundle() {
		try {
			Bundle bundle = Platform.getBundle(Constants.BUNDLE_OF_ORG_JRUBY);
			File bundleRoot = FileLocator.getBundleFile(bundle);
			String path = new File(bundleRoot, Constants.PATH_OF_JRUBY_HOME).getAbsolutePath();
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
		String gemHome = instancePath + GEMS_HOME + File.separator;
		return gemHome;
	}

	public Map<String, String> getDefaultEnvironment() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(Constants.ENV_KEY_GEM_HOME, getGemHome());
		return result;
	}

}
