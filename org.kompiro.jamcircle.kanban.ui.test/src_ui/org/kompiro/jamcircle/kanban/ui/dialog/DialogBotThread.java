package org.kompiro.jamcircle.kanban.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

public  class DialogBotThread extends Thread {
	private final Dialog dialog;
	private final Throwable[] ex;
	{
		setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				UIThreadRunnable.syncExec(new VoidResult() {
					public void run() {
						dialog.close();
					}
				});
				ex[0] = e;
			}
		});
		
	}

	public DialogBotThread(Dialog dialog,Throwable[] ex,Runnable target) {
		super(target);
		this.dialog = dialog;
		this.ex = ex;
	}
}
