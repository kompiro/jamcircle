package org.kompiro.jamcircle.scripting.internal;

import java.io.*;
import java.util.*;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.exceptions.RaiseException;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingServiceImpl implements ScriptingService{

	private BSFManager manager;
	private boolean initialized = false;
	private Ruby runtime;
	private Map<String,Object> globalValues = new HashMap<String,Object>();
	
	public ScriptingServiceImpl(Map<String, Object> beans)throws ScriptingException{
		globalValues = beans;
		manager = new BSFManager();
		init();
	}
	
	private void init() throws ScriptingException {
		if(!initialized){
			synchronized (this) {
				RubyInstanceConfig config = new RubyInstanceConfig();
				config.setArgv(new String[]{"-Ku"});
				config.setJRubyHome(getJRubyHomeFromBundle());
		        config.setObjectSpaceEnabled(true);
				runtime = JavaEmbedUtils.initialize(new ArrayList<Object>(),config);
		        runtime.getGlobalVariables().defineReadonly("$bsf", new ValueAccessor(JavaEmbedUtils.javaToRuby(runtime, manager)));
		        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
				if(globalValues == null){
					initialized = true;
					return;
				}
				try{
					for(Map.Entry<String, Object> entry : globalValues.entrySet()){
						Object value = entry.getValue();
						String name = entry.getKey();
						manager.declareBean(name, value,value.getClass());
						runtime.defineGlobalConstant(name, JavaEmbedUtils.javaToRuby(runtime, value));
					}
				} catch (BSFException e) {
					Throwable targetException = e.getTargetException();
					if(targetException instanceof RaiseException){
						RaiseException ex = (RaiseException) targetException;
						throw new ScriptingException(ex.getException().asJavaString(), targetException);
					}
					throw new ScriptingException("Scripting Exception", targetException);
				}
				initialized = true;
			}
		}
	}

	public Object eval(ScriptTypes type, String scriptName, String script,
			Map<String, Object> beans
			) throws ScriptingException {
		if(initialized == false) throw new ScriptingException("uninitialized service.");
		if(beans != null){
			for(Map.Entry<String, Object> entry : beans.entrySet()){
				manager.registerBean(entry.getKey(), entry.getValue());
			}
		}

		try {
			int templateLines = 0;
			String templateName = null;
			switch (type) {
			case JavaScript:
				templateName = "init.js";
				templateLines = 5;
				break;
			case JRuby:
				templateName = "init.rb";
				templateLines = 8;
				break;
			default:
				String message = "\"header\"'s script type is null.Please check the data. id='%d'";
				message = String.format(message,type);
				throw new ScriptingException(message);
			}
			InputStreamReader reader = new InputStreamReader(getClass().getResource(templateName).openStream());
			String header = IOUtils.getStringFromReader(reader);
			script= header + script;
			return executeScript(type, scriptName, script, templateLines);
			
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

	private Object executeScript(ScriptTypes type, String scriptName,
			String script, int templateLines) throws BSFException {
		Object result = null;
		switch (type) {
		case JavaScript:
			result = manager.eval(type.getType(), scriptName, -templateLines, 0, script);
			break;
		case JRuby:
			result = JavaEmbedUtils.rubyToJava(runtime.evalScriptlet(script));
			break;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == null) return null;
		if(Ruby.class.equals(adapter)) return runtime;
		return null;
	}

	private String getJRubyHomeFromBundle() {
		try {
			String path = new File(FileLocator.getBundleFile(Platform.getBundle("org.jruby")),"META-INF/jruby.home").getAbsolutePath();
			return path;
		} catch (IOException e) {
		}
		return null;
	}

	public void terminate() {
		manager.terminate();
		JavaEmbedUtils.terminate(runtime);
	}

	public Map<String, Object> getGlovalValues() {
		return globalValues;
	}

}
