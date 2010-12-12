package org.kompiro.jamcircle.storage;

import org.eclipse.core.runtime.*;

public class StorageCallbackHandlerLoader {
	static final String POINT_CALLBACK = StorageActivator.ID + "." + "settingUI"; //$NON-NLS-1$ //$NON-NLS-2$
	static final String ATTR_HANDLER_CLASS = "class"; //$NON-NLS-1$
	private IExtensionRegistry registry = RegistryFactory.getRegistry();

	public void setupStorageSetting() {
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		IExtension[] extenders = point.getExtensions();
		IUIStorageCallbackHandler executable = null;
		if (extenders == null)
			return;
		for (int i = 0; i < extenders.length; i++) {
			IConfigurationElement[] confElements = extenders[i].getConfigurationElements();
			if (confElements == null)
				return;
			for (IConfigurationElement element : confElements) {
				try {
					executable = (IUIStorageCallbackHandler) element.createExecutableExtension(ATTR_HANDLER_CLASS);
					break;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		if (executable != null) {
			executable.setupStorageSetting();
		}
	}

	public void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;

	}
}
