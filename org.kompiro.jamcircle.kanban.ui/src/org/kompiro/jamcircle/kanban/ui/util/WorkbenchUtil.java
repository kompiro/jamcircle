package org.kompiro.jamcircle.kanban.ui.util;


import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class WorkbenchUtil {

	public static KanbanView findKanbanView() {
		final Object[] results = new Object[1];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				IWorkbench workbench = getWorkbench();
				if(workbench == null) return;
				if(workbench.getWorkbenchWindowCount() == 0) return;
				IWorkbenchPage activePage = null;
				for(IWorkbenchWindow workbenchWindow: workbench.getWorkbenchWindows()){
					activePage = workbenchWindow.getActivePage();
					if(activePage == null){
						continue;
					}
				}
				if(activePage == null) return;
				
				IViewPart foundView = activePage.findView(KanbanView.ID);
				if (foundView instanceof KanbanView) {
					KanbanView view = (KanbanView) foundView;
					results[0] = view;
				}
			}
		});
		return (KanbanView)results[0];
	}
	
	public static BoardModel getCurrentKanbanBoard(){
		KanbanView findKanbanView = findKanbanView();
		if(findKanbanView == null) return null;
		return (BoardModel)findKanbanView.getAdapter(Board.class);
	}
	
	public static Display getDisplay(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		if(workbench == null) return null;
		return workbench.getDisplay();
	}

	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}
	
	public static IWorkbenchWindow getWorkbenchWindow(){
		IWorkbench workbench = getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if(window != null) return window;
		if(workbench.getWorkbenchWindowCount() == 0) return null;
		return workbench.getWorkbenchWindows()[0];
	}
	
}
