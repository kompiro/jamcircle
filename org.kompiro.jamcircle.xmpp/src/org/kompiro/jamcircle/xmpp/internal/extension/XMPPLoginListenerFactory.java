package org.kompiro.jamcircle.xmpp.internal.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.xmpp.Messages;
import org.kompiro.jamcircle.xmpp.XMPPActivator;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;
import org.kompiro.jamcircle.xmpp.service.internal.XMPPConnectionServiceImpl;

public class XMPPLoginListenerFactory {
	
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.xmpp.xmppLoginListener"; //$NON-NLS-1$
	static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	
	private IExtensionRegistry registry = RegistryFactory.getRegistry();
	private List<XMPPLoginListener> listeners = new ArrayList<XMPPLoginListener>();

	public void bind(XMPPConnectionServiceImpl impl){
		if(registry == null) return;
		IExtensionPoint point = registry.getExtensionPoint(POINT_CALLBACK);
		IExtension[] extensions = point.getExtensions();
		MultiStatus statuses = new MultiStatus(XMPPActivator.PLUGIN_ID, Status.ERROR, Messages.XMPPLoginListenerFactory_initialized_error_message, null);
		for(IExtension extension: extensions){
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement elem : elements){
				try {
					XMPPLoginListener listener = (XMPPLoginListener)elem.createExecutableExtension(ATTR_CLASS);
					impl.addXMPPLoginListener(listener);
					listeners.add(listener);
				} catch (Exception e) {
					statuses.add(XMPPActivator.createErrorStatus(e));
				}
			}
		}
		if(!statuses.isOK()){
			throw new IllegalStateException(statuses.getException());
		}
	}
	
	public void unbind(XMPPConnectionServiceImpl impl){
		for(XMPPLoginListener listener : listeners){
			impl.removeXMPPLoginListener(listener);
		}
	}

	void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;
	}
	
	IExtensionRegistry getRegistry(){
		return this.registry;
	}

}
