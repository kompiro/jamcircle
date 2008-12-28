package org.kompiro.jamcircle.xmpp.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class XMPPPerspective implements IPerspectiveFactory {

	public static String ID = "org.kompiro.jamcircle.perspective.xmpp";
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView("org.kompiro.jamcircle.xmpp.ui.views.RoasterView", true, IPageLayout.TOP, 1.0f, "");
	}

}
