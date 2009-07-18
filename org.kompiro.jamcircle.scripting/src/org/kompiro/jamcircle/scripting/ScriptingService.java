package org.kompiro.jamcircle.scripting;

import java.util.Map;

import org.kompiro.jamcircle.kanban.model.ScriptTypes;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public interface ScriptingService {

	void init();

	void exec(ScriptTypes scriptTypes, 
			String scriptName, 
            String script,
            Map<String, Object> beans)throws ScriptingException;

}
