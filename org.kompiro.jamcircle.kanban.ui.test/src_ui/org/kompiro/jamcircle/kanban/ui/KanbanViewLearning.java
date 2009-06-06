package org.kompiro.jamcircle.kanban.ui;

import static org.junit.Assert.*;
import net.java.ao.EntityManager;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.junit.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.gcontroller.BoardEditPart;

import abbot.swt.eclipse.utils.WorkbenchUtilities;
import abbot.swt.gef.tester.FigureCanvasTester;
import abbot.swt.gef.util.GEFWorkbenchUtilities;

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
