package org.kompiro.jamcircle.scripting.internal;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.scripting.IScriptingEngineInitializer;
import org.kompiro.jamcircle.scripting.ScriptingEngineInitializerLoader;
import org.osgi.service.component.ComponentContext;

public class ScriptingEngineInitializerLoaderImpl implements ScriptingEngineInitializerLoader {
	private static final String PLUGIN_ID = ScriptingActivator.ID;
	private static final String POINT_CALLBACK = "org.kompiro.jamcircle.scripting.scriptEngineInitializer";
	private String ATTR_HANDLER_CLASS = "class";
	private Map<String,Object> result = new HashMap<String, Object>();
		
	public void init(ComponentContext context) throws CoreException{
		result.put("BUNDLE_CONTEXT", context.getBundleContext());
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		if(point == null) return;
		IExtension[] extenders = point.getExtensions();
		IScriptingEngineInitializer executable = null;
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR, "error has occured when initializing scripting engines.", null);
		for (int i = 0; i < extenders.length; i++) {
			IConfigurationElement[] confElements = extenders[i].getConfigurationElements();
			for(IConfigurationElement element : confElements){
				try {
					executable = (IScriptingEngineInitializer) element.createExecutableExtension(ATTR_HANDLER_CLASS);
					executable.init(result);
				} catch (Exception e) {
					statuses.add(createErrorStatus(e));
				}
			}
		}
		if(!statuses.isOK()){
			throw new CoreException(statuses);
		}				
	}
	
	public Map<String,Object> getGrobalValues(){
		return Collections.unmodifiableMap(result);
	}

	private IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(),e);
	}

}