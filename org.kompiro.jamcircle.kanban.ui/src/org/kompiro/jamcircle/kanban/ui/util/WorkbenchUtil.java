package org.kompiro.jamcircle.kanban.ui.util;


import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public class WorkbenchUtil {

	public static KanbanView findKanbanView() {
		final Object[] results = new Object[1];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if(workbench == null){
					results[0] = null;
					return;
				}
				IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
				if(activeWorkbenchWindow == null){
					if(workbench.getWorkbenchWindowCount() == 0){
						results[0] = null;
						return;
					}
					activeWorkbenchWindow = workbench.getWorkbenchWindows()[0];
				}
				IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
				if(activePage == null){
					results[0] = null;
					return;
				}
				IViewPart foundView = activePage.findView(KanbanView.ID);
				if (foundView instanceof KanbanView) {
					KanbanView view = (KanbanView) foundView;
					results[0] = view;
				}
				results[0] = null;				
			}
		});
		return (KanbanView)results[0];
	}
	
	public static BoardModel getCurrentKanbanBoard(){
		return (BoardModel)findKanbanView().getAdapter(Board.class);
	}
	
	public static Display getDisplay(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		if(workbench == null) return null;
		return workbench.getDisplay();
	}
	
}
