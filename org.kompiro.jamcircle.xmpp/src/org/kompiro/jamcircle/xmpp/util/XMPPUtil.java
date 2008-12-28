package org.kompiro.jamcircle.xmpp.util;

public class XMPPUtil {
	public static String getRemovedResourceUser(String from) {
		if(from == null) return null;
		int resourceSpritIndex = from.indexOf("/");
		if(resourceSpritIndex != -1) from = from.substring(0,resourceSpritIndex);
		return from;
	}
}
