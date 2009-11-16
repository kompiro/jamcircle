package org.kompiro.jamcircle.scripting;

import java.util.Map;

import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public interface IScriptingEngineInitializer {

	void init(Map<String, Object> beans) throws ScriptingException;
	
}
