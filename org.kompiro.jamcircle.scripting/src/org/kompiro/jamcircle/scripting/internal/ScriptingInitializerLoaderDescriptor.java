package org.kompiro.jamcircle.scripting.internal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.bsf.util.IOUtils;
import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.osgi.framework.Bundle;

public class ScriptingInitializerLoaderDescriptor {
	
	public class ScriptExtendDescriptor{
		private IConfigurationElement scriptElement;

		public ScriptExtendDescriptor(
				IConfigurationElement element) {
			this.scriptElement = element;
		}

		public ScriptTypes getType(){
			String type = scriptElement.getAttribute("type");
			return ScriptTypes.valueOf(type);
		}
		
		public String getScript() throws IOException{
			String scriptName = scriptElement.getAttribute("file");
			IExtension extension = scriptElement.getDeclaringExtension();
			String symbolicName = extension.getNamespaceIdentifier();
			Bundle bundle = Platform.getBundle(symbolicName);
			URL resource = bundle.getResource(scriptName);
			InputStreamReader reader = new InputStreamReader(resource.openStream());
			return IOUtils.getStringFromReader(reader);
		}
		
		public String getScriptName(){
			IExtension extension = scriptElement.getDeclaringExtension();
			String symbolicName = extension.getNamespaceIdentifier();
			String scriptName = scriptElement.getAttribute("file");
			return String.format("%s/%s",symbolicName,scriptName);
		}
	}
	
	private String ATTR_HANDLER_CLASS = "class";
	private IConfigurationElement element;
	
	public ScriptingInitializerLoaderDescriptor(IConfigurationElement element){
		this.element = element;
	}
	
	public Object createClass() throws CoreException{
		return element.createExecutableExtension(ATTR_HANDLER_CLASS);
	}

	public ScriptExtendDescriptor[] getScripts() {
		IConfigurationElement[] children = element.getChildren();
		ScriptExtendDescriptor[] results = new ScriptExtendDescriptor[children.length];
		for(int i = 0; i < children.length; i++){
			results[i] = new ScriptExtendDescriptor(children[i]);
		}
		return results;
	}
	
	

}
