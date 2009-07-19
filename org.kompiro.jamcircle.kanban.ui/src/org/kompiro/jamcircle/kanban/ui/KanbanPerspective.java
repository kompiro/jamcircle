package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class KanbanPerspective implements IPerspectiveFactory {

	public static String ID = "org.kompiro.jamcircle.kanban.ui.perspective.kanban";

	private static final String BOARD_NEW_WIZARD = "org.kompiro.jamcircle.kanban.ui.action.BoardNewWizard";
	
	private static final String CONSOLE_VIEW = "org.eclipse.ui.console.ConsoleView";
	private static final String PROGRESS_VIEW = "org.eclipse.ui.views.ProgressView";
	private static final String KANBAN_VIEW = "org.kompiro.jamcircle.kanban.KanbanView";

	private static final boolean TITLE_SHOWN = false;
	private static final boolean EDITOR_AREA_VISIBLED = false;
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(EDITOR_AREA_VISIBLED);
		layout.addFastView(PROGRESS_VIEW);
		layout.addStandaloneView(KANBAN_VIEW,TITLE_SHOWN, IPageLayout.TOP, 0.80f, null);
		layout.addNewWizardShortcut(BOARD_NEW_WIZARD);
		layout.addFastView(CONSOLE_VIEW);
	}

}
