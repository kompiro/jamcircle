package org.kompiro.jamcircle.kanban.ui.util;


import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.kanban.ui.KanbanView;

public class WorkbenchUtil {

	public static KanbanView findKanbanView() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if(workbench == null) return null;
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if(activeWorkbenchWindow == null){
			if(workbench.getWorkbenchWindowCount() == 0){
				return null;
			}
			activeWorkbenchWindow = workbench.getWorkbenchWindows()[0];
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if(activePage == null) return null;
		IViewPart foundView = activePage.findView(KanbanView.ID);
		if (foundView instanceof KanbanView) {
			KanbanView view = (KanbanView) foundView;
			return view;
		}
		return null;
	}
	
	public static Display getDisplay(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		if(workbench == null) return null;
		return workbench.getDisplay();
	}
	
}
