package org.kompiro.jamcircle.kanban.ui.util;

/**
 * @TestContext org.kompiro.jamcircle.kanban.ui.util.XMPPUtilTest
 */
public class XMPPUtil {
	public static String getRemovedResourceUser(String from) {
		return org.kompiro.jamcircle.xmpp.util.XMPPUtil.getRemovedResourceUser(from);
	}

	public static String getResource(String from) {
		return org.kompiro.jamcircle.xmpp.util.XMPPUtil.getResource(from);
	}
	
}
