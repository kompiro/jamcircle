package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class KanbanPerspective implements IPerspectiveFactory {

	public static String ID = "org.kompiro.jamcircle.kanban.ui.perspective.kanban";
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView("org.kompiro.jamcircle.kanban.KanbanView", false, IPageLayout.TOP, 0.95f, "");
		layout.addNewWizardShortcut("org.kompiro.jamcircle.kanban.ui.action.BoardNewWizard");
	}

}
