package org.kompiro.jamcircle.xmpp.util;

public class XMPPUtil {
	private static final String SLASH = "/"; //$NON-NLS-1$

	public static String getRemovedResourceUser(String from) {
		if(from == null) return null;
		int resourceSpritIndex = from.indexOf(SLASH);
		if(resourceSpritIndex != -1) from = from.substring(0,resourceSpritIndex);
		return from;
	}
	
	public static String getResource(String from) {
		if(from == null) return null;
		int resourceSpritIndex = from.indexOf(SLASH);
		if(resourceSpritIndex == -1) return null;
		return from = from.substring(resourceSpritIndex + 1);
	}

}
