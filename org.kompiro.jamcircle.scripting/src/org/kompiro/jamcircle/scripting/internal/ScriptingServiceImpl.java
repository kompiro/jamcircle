package org.kompiro.jamcircle.scripting.internal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.jruby.exceptions.RaiseException;
import org.kompiro.jamcircle.kanban.model.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingServiceImpl implements ScriptingService{

	private BSFManager manager;

	public ScriptingServiceImpl(){
		manager = new BSFManager();
	}
	
	public void init() {
	}

	public void exec(ScriptTypes type, String scriptName, String script,
			Map<String, Object> beans) throws ScriptingException {
		
		try {
//			Board mock = new org.kompiro.jamcircle.kanban.model.mock.Board();
			for(Map.Entry<String, Object> entry : beans.entrySet()){
				System.out.println(entry);
				manager.registerBean(entry.getKey(), entry.getValue());
			}
			String templateName = null;
			int templateLines = 0;
			switch (type) {
			case JavaScript:
				templateName = "init.js";
				templateLines = 20;
				break;
			case JRuby:
				templateName = "init.rb";
				templateLines = 23;
				break;
			default:
				// TODO: Replacing
//				String message = "%s's script type is null.Please check the data. id='%d'";
				String message = "Script type is null.Please check the data.";
				throw new ScriptingException(message);
			}
			InputStreamReader reader = new InputStreamReader(getClass().getResource(templateName).openStream());
			script = IOUtils.getStringFromReader(reader) + script;
			manager.exec(type.getType(), scriptName, -templateLines, 0, script);
		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			if(targetException instanceof RaiseException){
				RaiseException ex = (RaiseException) targetException;
				throw new ScriptingException(ex.getException().asJavaString(), targetException);
			}
			throw new ScriptingException("Scripting Exception", targetException);
		} catch (IOException e) {
			throw new ScriptingException("An Error is occured when reading template script.", e);
		}

	}


}
