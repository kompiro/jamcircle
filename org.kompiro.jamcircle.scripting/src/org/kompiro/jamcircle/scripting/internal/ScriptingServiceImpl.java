package org.kompiro.jamcircle.scripting.internal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.script.*;

import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingServiceImpl implements ScriptingService{

	private boolean initialized = false;
	private ScriptEngineManager manager;
	
	public ScriptingServiceImpl(){
		manager = new ScriptEngineManager();
	}
	
	public void init(Map<String, Object> beans) throws ScriptingException {
		if(!initialized){
			synchronized (this) {
				if(beans != null){
					for(Map.Entry<String, Object> entry : beans.entrySet()){
						Object bean = entry.getValue();
						manager.put(entry.getKey(), bean);
					}
				}
				initialized = true;
			}
		}
	}

	public void exec(ScriptTypes type, String scriptName, String script,
			Map<String, Object> beans
			) throws ScriptingException {
		for(Map.Entry<String, Object> entry : beans.entrySet()){
			manager.put(entry.getKey(), entry.getValue());
		}

		try {
			String templateName = null;
			ScriptEngine engine;
			switch (type) {
			case JavaScript:
				templateName = "init.js";
				engine = manager.getEngineByExtension("js");
				break;
			case JRuby:
				templateName = "init.rb";
				engine = manager.getEngineByExtension("rb");
				break;
			default:
				String message = "\"header\"'s script type is null.Please check the data. id='%d'";
				message = String.format(message,type);
				throw new ScriptingException(message);
			}
			InputStreamReader reader = new InputStreamReader(getClass().getResource(templateName).openStream());
			String header = IOUtils.getStringFromReader(reader);
			script = header + script;
			engine.eval(script);
		} catch (IOException e) {
			throw new ScriptingException("An Error is occured when reading template script.", e);
		} catch (ScriptException e) {
			throw new ScriptingException(e.getLocalizedMessage(), e);
		}
	}


}
