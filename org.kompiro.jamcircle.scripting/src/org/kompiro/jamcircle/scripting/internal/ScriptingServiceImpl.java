package org.kompiro.jamcircle.scripting.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

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
import org.kompiro.jamcircle.scripting.ScriptingEngineInitializerLoader;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingServiceImpl implements ScriptingService{
	private static Boolean testmode = false;

	static{
		try{
			ResourceBundle r = ResourceBundle.getBundle("script");
			if(r != null){
				testmode = Boolean.valueOf(r.getString("testmode"));
			}
		}catch(MissingResourceException e){
			// Noting because there wouldn't like to set testmode.
		}
	}
	private BSFManager manager;
	private boolean initialized = false;

	private Ruby runtime;
	private Map<String,Object> globalValues = new HashMap<String,Object>();
	private ScriptingEngineInitializerLoader loader = new ScriptingEngineInitializerLoaderImpl();
	
	public void init() throws ScriptingException{
		if(!initialized){
			synchronized (this) {
				manager = new BSFManager();
				RubyInstanceConfig config = new RubyInstanceConfig();
				config.setArgv(new String[]{"-Ku"});
				setJRubyHome(config);
		        config.setObjectSpaceEnabled(true);
				runtime = JavaEmbedUtils.initialize(new ArrayList<Object>(),config);
		        runtime.getGlobalVariables().defineReadonly("$bsf", new ValueAccessor(JavaEmbedUtils.javaToRuby(runtime, manager)));
		        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
		        if(loader != null){
		    		setGlobalValues(loader.getGrobalValues());
		    		try {
						loader.loadExtendScript(this);
					} catch (Exception e) {
						throw new ScriptingException(e.getLocalizedMessage(), e);
					}
		        }
		        initialized = true;
			}
		}
	}

	private void setJRubyHome(RubyInstanceConfig config) {
		String home = null;
		if(Platform.isRunning()){
			try{
				home = new File(FileLocator.getBundleFile(Platform.getBundle("org.jruby")),"META-INF/jruby.home").getAbsolutePath();
			} catch (IOException e) {
			}
		}
		if(home != null){
			config.setJRubyHome(home);
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
//	        runtime.getGlobalVariables().defineReadonly("$bsf", new ValueAccessor(JavaEmbedUtils.javaToRuby(runtime, manager)));
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
			Object result = executeScript(type, scriptName, script, templateLines);
			return result;
			
		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			if(targetException instanceof RaiseException){
				RaiseException ex = (RaiseException) targetException;
				throw new ScriptingException(ex.getException().asJavaString(), targetException);
			}
			throw new ScriptingException("Scripting Exception", targetException);
		} catch (IOException e) {
			throw new ScriptingException("An Error is occured when reading template script.", e);
		}finally{
			if(beans != null){
				for(Map.Entry<String, Object> entry : beans.entrySet()){
					manager.unregisterBean(entry.getKey());
				}
			}			
		}
	}

	public Object executeScript(ScriptTypes type, String scriptName,
			String script, int templateLines) throws BSFException {
		Object result = null;
		if(testmode == false){
			
			switch (type) {
			case JavaScript:
				result = manager.eval(type.getType(), scriptName, -templateLines, 0, script);
				break;
			case JRuby:
				result = JavaEmbedUtils.rubyToJava(runtime.executeScript(script, scriptName));
				break;
			}
			
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == null) return null;
		if(Ruby.class.equals(adapter)) return runtime;
		return null;
	}

	public void terminate() {
		manager.terminate();
		JavaEmbedUtils.terminate(runtime);
	}
	
	public void setGlobalValues(Map<String, Object> globalValues) throws ScriptingException {
		this.globalValues = globalValues;
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
	}
	
	public void setScriptingEngineInitializerLoader(ScriptingEngineInitializerLoader loader){
		this.loader = loader;
	}

	public Map<String, Object> getGlovalValues() {
		return globalValues;
	}

}
