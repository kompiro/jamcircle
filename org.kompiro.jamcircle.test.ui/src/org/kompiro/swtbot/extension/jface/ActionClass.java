package org.kompiro.swtbot.extension.jface;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ActionClass<T extends Action> implements MethodRule {

	private static final long TIMEOUT_BY_SYSTEM = 1000L * 60 * 3;
	private Window newWindow = null;
	private SWTBot bot;
	private T action;

	public ActionClass(T action) {
		this.action = action;
	}

	public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Shell parentShell = new Shell();
				try {
					newWindow = newWindow(parentShell, method);
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				bot = new SWTBot();
				RunOnNonUIThread r = new RunOnNonUIThread(base, newWindow.getShell());
				Thread nonUIThread = new Thread(r);
				nonUIThread.setName("Runnning Test Thread");//$NON-NLS-1$
				nonUIThread.start();
				newWindow.open();

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

	private Window newWindow(Shell parentShell, FrameworkMethod method) throws InstantiationException,
			IllegalAccessException {
		Window window = new ApplicationWindow(parentShell) {
			{
				addMenuBar();
				addToolBar(SWT.FLAT);
			}

			@Override
			protected void configureShell(Shell shell) {
				action.setImageDescriptor(getImageDescriptor());
				getToolBarManager().add(action);
				super.configureShell(shell);
			}

			@Override
			protected Control createContents(Composite parent) {
				Composite comp = new Composite(parent, SWT.None);

				comp.setLayout(new FillLayout());

				return comp;
			}

		};
		window.create();
		return window;
	}

	private ImageDescriptor getImageDescriptor() {
		URL resource = getClass().getResource("dummy.png");
		return ImageDescriptor.createFromURL(resource);
	}

	public SWTBot getBot() {
		return bot;
	}

}
