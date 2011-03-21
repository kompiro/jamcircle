package org.kompiro.swtbot.extension.jface;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class WindowClass<T extends Window> implements MethodRule {

	private static final long TIMEOUT_BY_SYSTEM = 1000L * 60 * 3;
	private T window = null;
	private SWTBot bot;

	public WindowClass(T window) {
		this.window = window;
		window.create();
	}

	public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Shell parentShell = new Shell();
				bot = new SWTBot();
				RunOnNonUIThread r = new RunOnNonUIThread(bot, base, window.getShell());
				Thread nonUIThread = new Thread(r);
				nonUIThread.setName("Runnning Test Thread");//$NON-NLS-1$
				nonUIThread.start();
				window.open();

				waitForNonUIThreadFinished(parentShell, nonUIThread);
				parentShell.dispose();
				if (r.getThrowable() != null)
					throw r.getThrowable();
			}
		};
	}

	private void waitForNonUIThreadFinished(Shell parentShell, Thread nonUIThread) {
		Display display = bot.getDisplay();
		long timeout = System.currentTimeMillis() + TIMEOUT_BY_SYSTEM;
		while (nonUIThread.isAlive()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
				if (System.currentTimeMillis() > timeout) {
					break;
				}
			} catch (Throwable e) {
			}
		}
	}

	public SWTBot getBot() {
		return bot;
	}

}
