package org.kompiro.jamcircle.scripting.ui.util;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class UIUtil {
	
	public static void openWarning(final String title,final String message){
		sync(new Runnable() {
			
			public void run() {
				Shell parent = getShell();
				MessageDialog.openWarning(parent, title, message);
			}
		});
	}
	
	private static Display getDisplay(){
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
	
	public static void async(Runnable runnable){
		Display display = getDisplay();
		if(display == null){
			runnable.run();
			return;
		}
		display.asyncExec(runnable);
	}
	
	public static void sync(Runnable runnable){
		Display display = getDisplay();
		if(display == null){
			runnable.run();
			return;
		}
		display.syncExec(runnable);
	}

	private static IWorkbench getWorkbench() {
		if(Platform.isRunning()){
			if(PlatformUI.isWorkbenchRunning()){
				return PlatformUI.getWorkbench();
			}
		}
		return null;
	}
	
	private static IWorkbenchWindow getWorkbenchWindow(){
		IWorkbench workbench = getWorkbench();
		if(workbench == null) return null;
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if(window != null) return window;
		if(workbench.getWorkbenchWindowCount() == 0) return null;
		return workbench.getWorkbenchWindows()[0];
	}

	private static Shell getShell() {
		IWorkbenchWindow window = getWorkbenchWindow();
		if(window == null) return null;
		return window.getShell();
	}
	
}
