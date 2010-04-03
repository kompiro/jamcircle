package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class KanbanPerspective implements IPerspectiveFactory {

	public static String ID = "org.kompiro.jamcircle.kanban.ui.perspective.kanban"; //$NON-NLS-1$

	private static final String BOARD_NEW_WIZARD = "org.kompiro.jamcircle.kanban.ui.action.BoardNewWizard"; //$NON-NLS-1$
	
	private static final String PROGRESS_VIEW = "org.eclipse.ui.views.ProgressView"; //$NON-NLS-1$

	private static final boolean EDITOR_AREA_VISIBLED = false;
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(EDITOR_AREA_VISIBLED);
		layout.addFastView(PROGRESS_VIEW);
		layout.addNewWizardShortcut(BOARD_NEW_WIZARD);
	}

}
