package org.kompiro.jamcircle.scripting.internal;

import java.io.IOException;
import java.util.*;

import org.apache.bsf.BSFException;
import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.scripting.*;
import org.kompiro.jamcircle.scripting.internal.ScriptingInitializerLoaderDescriptor.ScriptExtendDescriptor;
import org.osgi.service.component.ComponentContext;

public class ScriptingEngineInitializerLoaderImpl implements ScriptingEngineInitializerLoader {
	public abstract class ElementRunner {

		public abstract void run(ScriptingInitializerLoaderDescriptor desc,MultiStatus statuses);

	}

	private static final String PLUGIN_ID = ScriptingActivator.ID;
	private static final String POINT_CALLBACK = "org.kompiro.jamcircle.scripting.scriptEngineInitializer";
	private Map<String,Object> result = new HashMap<String, Object>();
		
	public void init(ComponentContext context) throws CoreException{
		result.put("BUNDLE_CONTEXT", context.getBundleContext());
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR, "error has occured when initializing scripting engines.", null);
		ElementRunner runner = new ElementRunner(){
			@Override
			public void run(ScriptingInitializerLoaderDescriptor desc,MultiStatus statuses) {
				try {
					IScriptingEngineInitializer executable = 
						(IScriptingEngineInitializer) desc.createClass();
					executable.init(result);
				} catch (Exception e) {
					statuses.add(createErrorStatus(e));
				}
			}
		};
		handle(runner,statuses);
		if(!statuses.isOK()){
			throw new CoreException(statuses);
		}				
	}
	
	private void handle(ElementRunner runner,MultiStatus statuses) {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		IExtension[] extenders = point.getExtensions();
		for (int i = 0; i < extenders.length; i++) {
			IConfigurationElement[] confElements = extenders[i].getConfigurationElements();
			for(final IConfigurationElement element : confElements){
				runner.run(new ScriptingInitializerLoaderDescriptor(element),statuses);
			}
		}
	}

	public Map<String,Object> getGrobalValues(){
		return Collections.unmodifiableMap(result);
	}

	private IStatus createErrorStatus(Throwable e){
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(),e);
	}

	public void loadExtendScript(final ScriptingService service) {
		MultiStatus statuses = new MultiStatus(PLUGIN_ID, Status.ERROR, "error has occured when initializing scripting engines.", null);

		ElementRunner runner = new ElementRunner(){
			@Override
			public void run(ScriptingInitializerLoaderDescriptor desc,MultiStatus statuses) {
				ScriptExtendDescriptor[] scripts = desc.getScripts();
				for(ScriptExtendDescriptor script :scripts){
					try{
						ScriptingServiceImpl impl = (ScriptingServiceImpl) service;
						impl.executeScript(script.getType(), script.getScriptName(), script.getScript(), 0);
					} catch (BSFException e) {
						statuses.add(createErrorStatus(e));
					} catch (IOException e) {
						statuses.add(createErrorStatus(e));
					}
				}
			}
		};
		handle(runner,statuses);
	}

}