package org.kompiro.jamcircle.kanban.ui.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.ui.util.XMPPUtil;


public class XMPPUtilTest {
	@Test
	public void getRemovedResourceUser() throws Exception {
		assertEquals("",XMPPUtil.getRemovedResourceUser(""));
		assertEquals(null,XMPPUtil.getRemovedResourceUser(null));
		assertEquals("kompiro@jabber.org",XMPPUtil.getRemovedResourceUser("kompiro@jabber.org"));
		assertEquals("kompiro@jabber.org",XMPPUtil.getRemovedResourceUser("kompiro@jabber.org/JAM_CIRCLE"));
	}
}
