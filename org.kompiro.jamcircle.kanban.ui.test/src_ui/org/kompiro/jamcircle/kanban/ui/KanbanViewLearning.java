package org.kompiro.jamcircle.kanban.ui;

import static org.junit.Assert.assertNotNull;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.Ignore;
import org.junit.Test;

public class KanbanViewLearning {

	@Test
	@Ignore
	public void learning() throws Exception {
		IWorkbench workbench = PlatformUI.getWorkbench();
		assertNotNull(workbench);
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IViewPart part = activePage.findView("org.kompiro.jamcircle.kanban.KanbanView");
		assertNotNull(part);
		assertNotNull(part.getAdapter(GraphicalViewer.class));
	}
}
