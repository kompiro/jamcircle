package org.kompiro.jamcircle.scripting.internal;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.scripting.*;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.scripting.internal.ScriptingInitializerLoaderDescriptor.ScriptExtendDescriptor;
import org.osgi.service.component.ComponentContext;

public class ScriptingEngineInitializerLoaderImpl implements ScriptingEngineInitializerLoader {
	public abstract class ElementRunner {

		public abstract void run(ScriptingInitializerLoaderDescriptor desc, MultiStatus statuses);

	}

	private static final String PLUGIN_ID = "org.kompiro.jamcircle.scripting"; //$NON-NLS-1$
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.scripting.scriptEngineInitializer"; //$NON-NLS-1$
	private Map<String, Object> result = new HashMap<String, Object>();
	private IExtensionRegistry registry = RegistryFactory.getRegistry();

	public void activate(ComponentContext context) throws ScriptingException {
		if (context != null) {
			result.put("BUNDLE_CONTEXT", context.getBundleContext()); //$NON-NLS-1$
		}
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR,
				Messages.ScriptingEngineInitializerLoaderImpl_initialize_error, null);
		ElementRunner runner = new ElementRunner() {
			@Override
			public void run(ScriptingInitializerLoaderDescriptor desc, MultiStatus statuses) {
				try {
					IScriptingEngineInitializer executable =
							(IScriptingEngineInitializer) desc.createClass();
					executable.init(result);
				} catch (Exception e) {
					statuses.add(createErrorStatus(e));
				}
			}
		};
		handle(runner, statuses);
		if (!statuses.isOK()) {
			throw new ScriptingException(new CoreException(statuses));
		}
	}

	private void handle(ElementRunner runner, MultiStatus statuses) {
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		IExtension[] extenders = point.getExtensions();
		if (extenders == null)
			return;
		for (int i = 0; i < extenders.length; i++) {
			IConfigurationElement[] confElements = extenders[i].getConfigurationElements();
			if (confElements == null)
				continue;
			for (final IConfigurationElement element : confElements) {
				runner.run(new ScriptingInitializerLoaderDescriptor(element), statuses);
			}
		}
	}

	public Map<String, Object> getGrobalValues() {
		return Collections.unmodifiableMap(result);
	}

	private IStatus createErrorStatus(Throwable e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e);
	}

	public void loadExtendScript(ScriptingService service) throws CoreException {
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR,
				Messages.ScriptingEngineInitializerLoaderImpl_initialize_error, null);
		final ScriptingServiceImpl impl = (ScriptingServiceImpl) service;

		ElementRunner runner = new ElementRunner() {
			@Override
			public void run(ScriptingInitializerLoaderDescriptor desc, MultiStatus statuses) {
				ScriptExtendDescriptor[] scripts = desc.getScripts();
				for (ScriptExtendDescriptor script : scripts) {
					try {
						impl.executeScript(script.getType(), script.getScriptName(), script.getScript(), 0);
					} catch (Exception e) {
						statuses.add(createErrorStatus(e));
					}
				}
			}
		};
		handle(runner, statuses);
		if (!statuses.isOK()) {
			throw new CoreException(statuses);
		}
	}

	public void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;
	}

}