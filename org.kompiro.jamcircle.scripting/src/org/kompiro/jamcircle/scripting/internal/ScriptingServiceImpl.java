package org.kompiro.jamcircle.scripting.internal;

import java.io.*;
import java.security.Permission;
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
import org.kompiro.jamcircle.scripting.*;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingServiceImpl implements ScriptingService {
	private final class SecurityManagerExtension extends SecurityManager {
		@Override
		public void checkExit(int status) {
			throw new SecurityException();
		}

		@Override
		public void checkPermission(final Permission perm) {
		}

		@Override
		public void checkPermission(final Permission perm, final Object context) {
		}
	}

	private static final String INIT_SCRIPT_JRUBY = "init.rb"; //$NON-NLS-1$
	private static final String INIT_SCRIPT_JS = "init.js"; //$NON-NLS-1$
	private static final String PATH_OF_JRUBY_HOME = "META-INF/jruby.home"; //$NON-NLS-1$
	private static final String NAME_OF_JRUBY_BUNDLE = "org.jruby.jruby"; //$NON-NLS-1$
	private static final String KEY_OF_RUNTIME = "$$"; //$NON-NLS-1$
	private static final String KEY_OF_BSF = "$bsf"; //$NON-NLS-1$
	private static final String ARGS_OF_JRUBY_ENGINE = "-Ku"; //$NON-NLS-1$
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

	private Ruby runtime;
	private Map<String, Object> globalValues = new HashMap<String, Object>();
	private ScriptingEngineInitializerLoader loader;
	private final SecurityManager defaultSecurityManeager = System.getSecurityManager();

	public void init() throws ScriptingException {
		if (!initialized) {
			synchronized (this) {
				manager = new BSFManager();
				RubyInstanceConfig config = new RubyInstanceConfig();
				config.setArgv(new String[] { ARGS_OF_JRUBY_ENGINE });
				try {
					setJRubyHome(config);
				} catch (IOException e) {
					throw new ScriptingException(e.getLocalizedMessage(), e);
				}
				config.setObjectSpaceEnabled(true);
				runtime = JavaEmbedUtils.initialize(new ArrayList<Object>(), config);
				runtime.getGlobalVariables().defineReadonly(KEY_OF_BSF,
						new ValueAccessor(JavaEmbedUtils.javaToRuby(runtime, manager)));
				runtime.getGlobalVariables().defineReadonly(KEY_OF_RUNTIME,
						new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
				if (loader != null) {
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

	private void setJRubyHome(RubyInstanceConfig config) throws IOException {
		String home = null;
		if (Platform.isRunning()) {
			home = new File(FileLocator.getBundleFile(Platform.getBundle(NAME_OF_JRUBY_BUNDLE)), PATH_OF_JRUBY_HOME)
						.getAbsolutePath();
		}
		if (home != null) {
			config.setJRubyHome(home);
		}
	}

	public Object eval(ScriptTypes type, String scriptName, String script,
			Map<String, Object> beans
			) throws ScriptingException {
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
					manager.unregisterBean(entry.getKey());
				}
			}
		}
	}

	public Object executeScript(ScriptTypes type, String scriptName,
			String script, int templateLines) throws BSFException {
		Object result = null;
		System.setSecurityManager(new SecurityManagerExtension());
		try {
			result = doExecuteScript(type, scriptName, script, templateLines, result);
		} finally {
			System.setSecurityManager(defaultSecurityManeager);
		}
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
				result = JavaEmbedUtils.rubyToJava(runtime.executeScript(script, scriptName));
				break;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == null)
			return null;
		if (Ruby.class.equals(adapter))
			return runtime;
		return null;
	}

	public void terminate() {
		System.setSecurityManager(defaultSecurityManeager);
		manager.terminate();
		JavaEmbedUtils.terminate(runtime);
	}

	public void setGlobalValues(Map<String, Object> globalValues) throws ScriptingException {
		this.globalValues = globalValues;
		try {
			for (Map.Entry<String, Object> entry : globalValues.entrySet()) {
				Object value = entry.getValue();
				String name = entry.getKey();
				manager.declareBean(name, value, value.getClass());
				runtime.defineGlobalConstant(name, JavaEmbedUtils.javaToRuby(runtime, value));
			}
		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			if (targetException instanceof RaiseException) {
				RaiseException ex = (RaiseException) targetException;
				throw new ScriptingException(ex.getException().asJavaString(), targetException);
			}
			throw new ScriptingException(Messages.ScriptingServiceImpl_error_title, targetException);
		}
	}

	public void setScriptingEngineInitializerLoader(ScriptingEngineInitializerLoader loader) {
		this.loader = loader;
	}

	public Map<String, Object> getGlovalValues() {
		return globalValues;
	}

}
