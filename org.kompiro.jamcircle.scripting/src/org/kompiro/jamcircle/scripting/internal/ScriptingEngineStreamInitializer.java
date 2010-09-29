package org.kompiro.jamcircle.scripting.internal;

import java.io.OutputStream;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.scripting.IScriptingEngineStreamLoader;
import org.kompiro.jamcircle.scripting.Messages;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class ScriptingEngineStreamInitializer {
	private static final String PLUGIN_ID = "org.kompiro.jamcircle.scripting"; //$NON-NLS-1$
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.scripting.scriptEngineStreamInitializer"; //$NON-NLS-1$
	static String ATTR_HANDLER_CLASS = "class"; //$NON-NLS-1$
	private IExtensionRegistry registry = Platform.getExtensionRegistry();
	private IScriptingEngineStreamLoader loader = null;

	public void activate() throws ScriptingException {
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR,
				Messages.ScriptingEngineInitializerLoaderImpl_initialize_error, null);
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		IExtension[] extenders = point.getExtensions();
		if (extenders == null)
			return;
		for (int i = 0; i < extenders.length; i++) {
			IConfigurationElement[] confElements = extenders[i].getConfigurationElements();
			if (confElements == null)
				continue;
			for (final IConfigurationElement element : confElements) {
				try {
					Object extension = element.createExecutableExtension(ATTR_HANDLER_CLASS);
					if (this.loader != null) {
						statuses.add(createErrorStatus(new ScriptingException("Illegal")));
						continue;
					}
					if (extension instanceof IScriptingEngineStreamLoader) {
						this.loader = (IScriptingEngineStreamLoader) extension;
					}
				} catch (CoreException e) {
					statuses.add(createErrorStatus(e));
				}
			}
		}
		if (!statuses.isOK()) {
			throw new ScriptingException(new CoreException(statuses));
		}
	}

	public OutputStream getOutputStream() {
		if (this.loader == null)
			return null;
		return this.loader.getOutputStream();
	}

	public OutputStream getErrorStream() {
		if (this.loader == null)
			return null;
		return this.loader.getErrorStream();
	}

	public void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;
	}

	private IStatus createErrorStatus(Throwable e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e);
	}

}
