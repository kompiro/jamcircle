//package org.kompiro.jamcircle.kanban.ui.widget;
//
//import org.eclipse.jface.dialogs.Dialog;
//import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
//import org.eclipse.swtbot.swt.finder.results.VoidResult;
//
//public  class ShellBotThread extends Thread {
//	private final Shell shell;
//	private final Throwable[] ex;
//	{
//		setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			public void uncaughtException(Thread t, Throwable e) {
//				UIThreadRunnable.syncExec(new VoidResult() {
//					public void run() {
//						shell.close();
//					}
//				});
//				ex[0] = e;
//			}
//		});
//		
//	}
//
//	public ShellBotThread(Dialog dialog,Throwable[] ex,Runnable target) {
//		super(target);
//		this.shell = dialog;
//		this.ex = ex;
//	}
//}
