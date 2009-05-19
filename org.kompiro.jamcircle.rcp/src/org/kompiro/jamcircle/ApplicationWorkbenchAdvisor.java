package org.kompiro.jamcircle;

import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    private static final String ID_OF_PERSPECTIVE_KANBAN = "org.kompiro.jamcircle.kanban.ui.perspective.kanban";
    private static final String ID_OF_PERSPECTIVE_FRIENDS = "org.kompiro.jamcircle.xmpp.ui.perspective.friends";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return ID_OF_PERSPECTIVE_KANBAN;
	}
	
	@Override
	public void preStartup() {
		String defaultPerspective = ID_OF_PERSPECTIVE_KANBAN + "," + ID_OF_PERSPECTIVE_FRIENDS;
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, defaultPerspective);
	}
}
