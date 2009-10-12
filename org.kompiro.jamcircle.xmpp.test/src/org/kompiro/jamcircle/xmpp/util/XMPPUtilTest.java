package org.kompiro.jamcircle.xmpp.util;

import static org.junit.Assert.*;

import org.junit.Test;


public class XMPPUtilTest {
	@Test
	public void getRemovedResourceUser() throws Exception {
		assertEquals("",XMPPUtil.getRemovedResourceUser(""));
		assertNull(XMPPUtil.getRemovedResourceUser(null));
		assertEquals("kompiro@jabber.org",XMPPUtil.getRemovedResourceUser("kompiro@jabber.org"));
		assertEquals("kompiro@jabber.org",XMPPUtil.getRemovedResourceUser("kompiro@jabber.org/JAM_CIRCLE"));
	}

	@Test
	public void getResourceUser() throws Exception {
		assertNull(null,XMPPUtil.getResource(""));
		assertNull(null,XMPPUtil.getResource(null));
		assertNull(null,XMPPUtil.getResource("kompiro@jabber.org"));
		assertEquals("JAM_CIRCLE",XMPPUtil.getResource("kompiro@jabber.org/JAM_CIRCLE"));
	}

}
