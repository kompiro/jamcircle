package org.kompiro.jamcircle.xmpp.ui;

import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;

public class XMPPUIContext {
	
	private static XMPPUIContext context;
	private XMPPConnectionService xmppConnectionService;
	
	public XMPPUIContext(){
		XMPPUIContext.context = this;
	}
	
	public static XMPPUIContext getDefault(){
		return context;
	}

	public XMPPConnectionService getXMPPConnectionService() {
		return xmppConnectionService;
	}

	public void setXMPPConnectionService(XMPPConnectionService xmppConnectionService) {
		this.xmppConnectionService = xmppConnectionService;
	}

}
