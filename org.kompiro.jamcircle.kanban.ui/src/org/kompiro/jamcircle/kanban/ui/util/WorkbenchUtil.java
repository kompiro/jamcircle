package org.kompiro.jamcircle.kanban.ui.util;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

/**
 * This utility class provides some function for Workbench operation.
 * @author kompiro
 */
public class WorkbenchUtil {

	public static void openWarning(final String title,final String message){
		sync(new Runnable() {
			public void run() {
				Shell parent = getShell();
				MessageDialog.openWarning(parent, title, message);
			}
		});
	}
	
	
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
		IWorkbench workbench = getWorkbench();
		Display defDisplay = Display.getDefault();
		if(workbench == null){
			return defDisplay;
		}
		try{
			if(workbench == null) return defDisplay;
			Display display = workbench.getDisplay();
			if(display != null) return display;
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			if(activeWorkbenchWindow == null) return defDisplay;
			display = activeWorkbenchWindow.getShell().getDisplay();
			return display;
		}catch(IllegalStateException e){
			return defDisplay;
		}

	}
	
	public static void sync(Runnable runnable){
		Display display = getDisplay();
		if(display == null){
			runnable.run();
			return;
		}
		display.syncExec(runnable);
	}
	
	public static void async(Runnable runnable){
		Display display = getDisplay();
		if(display == null){
			runnable.run();
			return;
		}
		display.asyncExec(runnable);
	}

	public static IWorkbench getWorkbench() {
		if(Platform.isRunning()){
			if(PlatformUI.isWorkbenchRunning()){
				return PlatformUI.getWorkbench();
			}
		}
		return null;
	}
	
	public static IWorkbenchWindow getWorkbenchWindow(){
		IWorkbench workbench = getWorkbench();
		if(workbench == null) return null;
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if(window != null) return window;
		if(workbench.getWorkbenchWindowCount() == 0) return null;
		return workbench.getWorkbenchWindows()[0];
	}

	public static Shell getShell() {
		IWorkbenchWindow window = getWorkbenchWindow();
		if(window == null) return null;
		return window.getShell();
	}
	
}
