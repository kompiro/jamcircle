package org.kompiro.jamcircle.scripting;

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public interface ScriptingService extends IAdaptable{

	void init(Map<String, Object> beans) throws ScriptingException;

	Object eval(ScriptTypes scriptTypes, 
			String scriptName, 
            String script,
            Map<String, Object> beans)throws ScriptingException;

	void terminate();
}
