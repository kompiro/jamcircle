package org.kompiro.jamcircle.scripting;

import java.util.Map;

import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public interface ScriptingService {

	void init(Map<String, Object> beans) throws ScriptingException;

	void exec(ScriptTypes scriptTypes, 
			String scriptName, 
            String script,
            Map<String, Object> beans)throws ScriptingException;

}
