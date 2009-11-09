package org.kompiro.jamcircle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class RCPUtils {

	public static class AsyncRunnerDelegator implements IAsyncRunnerDelegator {
		public void run(Runnable runnable) {
			getDisplay().timerExec(50, runnable);
		}
		private Display getDisplay() {
			if(PlatformUI.isWorkbenchRunning()){
				return PlatformUI.getWorkbench().getDisplay();
			}else{
				return new Display();
			}
		}
	}

	private RCPUtils(){}
	
	public interface IAsyncRunnerDelegator{
		public void run(Runnable runnable);
	}
	
	static IAsyncRunnerDelegator delegator = new AsyncRunnerDelegator();
	
	public static boolean isWindows(){
		String platform = SWT.getPlatform();
		return "win32".equals (platform) || "wpf".equals (platform);
	}
	
	public static void modifyAlphaForSurface(Shell shell) {
		modifyAlphaForSurface(shell, 255);
		shell.setVisible(true);
		shell.setActive();
		shell.setFocus();
	}

	private static void modifyAlphaForSurface(final Shell shell, final int alpha) {
		if(alpha <= 0){ 
			shell.setAlpha(255);
			return;
		}
		delegator.run(new Runnable() {
			public void run() {
				shell.setAlpha(255 - alpha);
				modifyAlphaForSurface(shell, alpha / 2);
			}
		});
	}
	
	public static void modifyAlphaForDropout(Shell shell){
		modifyAlphaForDropout(shell, 1);
	}

	private static void modifyAlphaForDropout(final Shell shell,final int alpha) {
		if(alpha >= 256){ 
			shell.setAlpha(0);
			shell.setVisible(false);
			return;
		}
		delegator.run(new Runnable() {
			public void run() {
				shell.setAlpha(256 - alpha);
				modifyAlphaForDropout(shell,alpha * 2);
			}
		});
	}

}
