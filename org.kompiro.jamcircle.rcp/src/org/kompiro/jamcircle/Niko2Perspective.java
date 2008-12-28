package org.kompiro.jamcircle;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Niko2Perspective implements IPerspectiveFactory {

	public static String ID = "org.kompiro.trichord.rcp.neo.perspective.niko2";
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView("org.kompiro.trichord.niko2.neo.views.Niko2NeoView", false, IPageLayout.TOP, 1.0f, "");
	}
}
