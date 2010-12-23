package org.kompiro.jamcircle.scripting.internal;

import java.io.*;
import java.util.*;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.jruby.Ruby;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.exceptions.RaiseException;
import org.jruby.util.KCode;
import org.kompiro.jamcircle.scripting.*;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.scripting.util.JRubyUtil;
import org.kompiro.jamcircle.scripting.util.ScriptingSecurityManager;

public class ScriptingServiceImpl implements ScriptingService {

	private static final String INIT_SCRIPT_JRUBY = "init.rb"; //$NON-NLS-1$
	private static final String INIT_SCRIPT_JS = "init.js"; //$NON-NLS-1$
	private static final String KEY_OF_BSF = "$bsf"; //$NON-NLS-1$
	private static final String KEY_OF_TESTMODE = "testmode"; //$NON-NLS-1$
	private static final String RESOURCE_NAME = "script"; //$NON-NLS-1$
	private static Boolean testmode = false;

	static {
		try {
			ResourceBundle r = ResourceBundle.getBundle(RESOURCE_NAME);
			if (r != null) {
				testmode = Boolean.valueOf(r.getString(KEY_OF_TESTMODE));
			}
		} catch (MissingResourceException e) {
			// Noting because there wouldn't like to set testmode.
		}
	}
	private BSFManager manager;
	private boolean initialized = false;

	// private Ruby runtime;
	private Map<String, Object> globalValues = new HashMap<String, Object>();
	private ScriptingEngineInitializerLoader scriptLoader;
	private ScriptingEngineStreamInitializer streamInitializer = new ScriptingEngineStreamInitializer();
	private ScriptingContainer container;

	public ScriptingServiceImpl() {
	}

	public void init() throws ScriptingException {
		synchronized (this) {
			if (initialized == false) {
				initializeBSFManager();
				initializeJRubyContainer();
				initializedStream(this);
				initializedVarMap();
				loadScript();
				initialized = true;
			}
		}
	}

	private void initializeBSFManager() {
		manager = new BSFManager();
	}

	private void initializeJRubyContainer() {
		LocalContextScope scope = LocalContextScope.SINGLETHREAD;
		JRubyUtil jRubyUtil = new JRubyUtil();
		container = new ScriptingContainer(scope);
		container.setRunRubyInProcess(true);
		container.setKCode(KCode.UTF8);
		container.setHomeDirectory(jRubyUtil.getJRubyHomeFromBundle());
		container.setEnvironment(jRubyUtil.getDefaultEnvironment());
		container.setObjectSpaceEnabled(true);
	}

	private void loadScript() throws ScriptingException {
		if (scriptLoader != null) {
			setGlobalValues(scriptLoader.getGrobalValues());
			try {
				scriptLoader.loadExtendScript(this);
			} catch (Exception e) {
				throw new ScriptingException(e.getLocalizedMessage(), e);
			}
		}
	}

	private void initializedStream(ScriptingServiceImpl service) throws ScriptingException {
		if (streamInitializer != null) {
			streamInitializer.activate();
			OutputStream out = streamInitializer.getOutputStream();
			if (out != null) {
				service.setOutputStream(new PrintStream(out));
			}
			OutputStream error = streamInitializer.getErrorStream();
			if (error != null) {
				service.setErrorStream(new PrintStream(error));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initializedVarMap() {
		container.getVarMap().put(KEY_OF_BSF, manager);
	}

	public Object eval(ScriptTypes type, String scriptName, String script,
				Map<String, Object> beans) throws ScriptingException {
		if (initialized == false)
			throw new ScriptingException(Messages.ScriptingServiceImpl_initialized_error_message);
		if (beans != null) {
			for (Map.Entry<String, Object> entry : beans.entrySet()) {
				manager.registerBean(entry.getKey(), entry.getValue());
			}
			// runtime.getGlobalVariables().defineReadonly("$bsf", new
			// ValueAccessor(JavaEmbedUtils.javaToRuby(runtime, manager)));
		}

		try {
			int templateLines = 0;
			String templateName = null;
			switch (type) {
			case JavaScript:
				templateName = INIT_SCRIPT_JS;
				templateLines = 5;
				break;
			case JRuby:
				templateName = INIT_SCRIPT_JRUBY;
				templateLines = 8;
				break;
			default:
				String message = Messages.ScriptingServiceImpl_script_type_error;
				message = String.format(message, type);
				throw new ScriptingException(message);
			}
			InputStreamReader reader = new InputStreamReader(getClass().getResource(templateName).openStream());
			String header = IOUtils.getStringFromReader(reader);
			script = header + script;
			Object result = executeScript(type, scriptName, script, templateLines);
			return result;

		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			if (targetException instanceof RaiseException) {
				RaiseException ex = (RaiseException) targetException;
				throw new ScriptingException(ex.getException().asJavaString(), targetException);
			}
			throw new ScriptingException(Messages.ScriptingServiceImpl_error_title, targetException);
		} catch (IOException e) {
			throw new ScriptingException(Messages.ScriptingServiceImpl_reading_template_error_message, e);
		} finally {
			if (beans != null) {
				for (Map.Entry<String, Object> entry : beans.entrySet()) {
					String key = entry.getKey();
					manager.unregisterBean(key);
					container.remove(key);
				}
			}
		}
	}

	public Object executeScript(ScriptTypes type, String scriptName,
			String script, int templateLines) throws BSFException {
		Object result = null;
		ScriptingSecurityManager.runScript();
		result = doExecuteScript(type, scriptName, script, templateLines, result);
		ScriptingSecurityManager.finishedScript();
		return result;
	}

	private Object doExecuteScript(ScriptTypes type, String scriptName, String script, int templateLines, Object result)
			throws BSFException {
		if (testmode == false) {
			switch (type) {
			case JavaScript:
				result = manager.eval(type.getType(), scriptName, -templateLines, 0, script);
				break;
			case JRuby:
				result = container.runScriptlet(script);
				break;
			}
		}
		return result;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == null)
			return null;
		if (Ruby.class.equals(adapter))
			return container.getProvider().getRuntime();
		return null;
	}

	public void terminate() {
		manager.terminate();
		container.terminate();
	}

	private void setOutputStream(PrintStream stream) {
		container.setOutput(stream);
	}

	private void setErrorStream(PrintStream stream) {
		container.setError(stream);
	}

	public void setGlobalValues(Map<String, Object> globalValues) throws ScriptingException {
		this.globalValues = globalValues;
		try {
			for (Map.Entry<String, Object> entry : globalValues.entrySet()) {
				Object value = entry.getValue();
				String name = entry.getKey();
				manager.declareBean(name, value, value.getClass());
			}
		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			if (targetException instanceof RaiseException) {
				RaiseException ex = (RaiseException) targetException;
				throw new ScriptingException(ex.getException().asJavaString(), targetException);
			}
			throw new ScriptingException(Messages.ScriptingServiceImpl_error_title, targetException);
		}
		setGlobalValuesToScriptingContainer(globalValues);
	}

	@SuppressWarnings("unchecked")
	private void setGlobalValuesToScriptingContainer(Map<String, Object> globalValues) {
		container.getVarMap().putAll(globalValues);
	}

	public void setScriptingEngineInitializerLoader(ScriptingEngineInitializerLoader loader) {
		this.scriptLoader = loader;
	}

	public Map<String, Object> getGlovalValues() {
		return globalValues;
	}

}
