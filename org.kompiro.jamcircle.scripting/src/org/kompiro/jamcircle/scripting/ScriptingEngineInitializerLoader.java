package org.kompiro.jamcircle.scripting;

import java.util.Map;

public interface ScriptingEngineInitializerLoader {

	public Map<String, Object> getGrobalValues();

	public void loadExtendScript(ScriptingService service);

}