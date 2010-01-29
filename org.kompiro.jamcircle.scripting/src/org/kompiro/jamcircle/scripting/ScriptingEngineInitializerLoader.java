package org.kompiro.jamcircle.scripting;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

public interface ScriptingEngineInitializerLoader {

	public Map<String, Object> getGrobalValues();

	public void loadExtendScript(ScriptingService service) throws CoreException;

}