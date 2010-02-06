package org.kompiro.jamcircle.storage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

public class StorageCallbackHandlerLoader {
	private static final String POINT_CALLBACK = StorageActivator.ID + "." +"settingUI";
	private String ATTR_HANDLER_CLASS = "class";

	public boolean setupStorageSetting(){
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		IExtension[] extenders = point.getExtensions();
		IUIStorageCallbackHandler executable = null;
		for (int i = 0; i < extenders.length; i++) {
			IConfigurationElement[] confElements = extenders[i].getConfigurationElements();
			for(IConfigurationElement element : confElements){
				try {
					executable = (IUIStorageCallbackHandler) element.createExecutableExtension(ATTR_HANDLER_CLASS);
					break;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		if(executable != null) executable.setupStorageSttting();
		return false;
	}
}
