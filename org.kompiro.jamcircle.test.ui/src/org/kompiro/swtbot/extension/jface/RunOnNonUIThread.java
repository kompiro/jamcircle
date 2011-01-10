package org.kompiro.swtbot.extension.jface;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.asyncExec;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForShell;
import static org.hamcrest.CoreMatchers.is;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.junit.runners.model.Statement;

public final class RunOnNonUIThread implements Runnable {
	private final Statement base;
	private Throwable throwable;
	private Shell shell;
	private SWTBot bot;

	public RunOnNonUIThread(SWTBot bot, Statement base, Shell shell) {
		this.bot = bot;
		this.base = base;
		this.shell = shell;
	}

	public void run() {
		WaitForObjectCondition<Shell> condition = waitForShell(is(shell));
		bot.waitUntil(condition);
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
