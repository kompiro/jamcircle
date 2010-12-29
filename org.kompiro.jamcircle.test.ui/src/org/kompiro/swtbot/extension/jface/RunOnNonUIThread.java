package org.kompiro.swtbot.extension.jface;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.asyncExec;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.junit.runners.model.Statement;

public final class RunOnNonUIThread implements Runnable {
	private final Statement base;
	private Throwable throwable;
	private Shell shell;

	public RunOnNonUIThread(Statement base, Shell shell) {
		this.base = base;
		this.shell = shell;
	}

	public void run() {
		try {
			base.evaluate();
		} catch (Throwable e) {
			this.throwable = e;
		} finally {
			closeDialogs();
		}
	}

	private void closeDialogs() {
		asyncExec(new VoidResult() {
			@Override
			public void run() {
				shell.close();
			}
		});
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
