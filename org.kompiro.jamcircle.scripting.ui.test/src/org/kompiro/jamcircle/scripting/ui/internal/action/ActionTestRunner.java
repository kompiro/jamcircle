package org.kompiro.jamcircle.scripting.ui.internal.action;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.asyncExec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class ActionTestRunner extends SWTBotJunit4ClassRunner {

	private Window newWindow = null;
	private SWTBot bot;
	private Action action;

	public ActionTestRunner(Class<?> klass) throws Exception {
		super(klass);
	}

	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
		Shell parentShell = new Shell();
		try {
			newWindow = newWindow(parentShell);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		bot = new SWTBot();
		Runnable r = new Runnable() {
			public void run() {
				try {
					ActionTestRunner.super.runChild(method, notifier);
				} finally {
					closeDialogs();
				}
			}

			private void closeDialogs() {
				SWTBotShell activeShell;
				try {
					activeShell = bot.activeShell();
				} catch (WidgetNotFoundException e) {
					// already closed.
					return;
				}
				if (activeShell.widget == null)
					return;
				String title = activeShell.getText();
				final String[] targetTitle = new String[1];
				asyncExec(new VoidResult() {
					public void run() {
						targetTitle[0] = newWindow.getShell().getText();
					}
				});
				if (title.equals(targetTitle[0])) {
					activeShell.close();
					asyncExec(new VoidResult() {

						public void run() {
							bot.getDisplay().dispose();
						}
					});
				}
			}
		};
		Thread nonUIThread = new Thread(r);
		nonUIThread.setName("Runnning Test Thread");//$NON-NLS-1$
		nonUIThread.start();
		newWindow.open();

		waitForNonUIThreadFinished(parentShell, nonUIThread);
		parentShell.dispose();
	}

	private void waitForNonUIThreadFinished(Shell parentShell, Thread nonUIThread) {
		Display display = bot.getDisplay();
		long timeout = System.currentTimeMillis() + 5000L;
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

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		inject(test, "bot", bot);
		inject(test, "action", action);
		if (newWindow != null) {
			inject(test, "wizard", newWindow);
		}

		return test;
	}

	private void inject(Object test, String fieldName, Object param) throws IllegalAccessException {
		Field field;
		try {
			field = test.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(test, param);
		} catch (NoSuchFieldException e) {
		}
	}

	private Window newWindow(Shell parentShell) throws InstantiationException, IllegalAccessException {
		TestClass testClass = getTestClass();
		Annotation[] annotations = testClass.getAnnotations();
		final Action[] newAction = new Action[1];
		for (Annotation annotation : annotations) {
			if (annotation instanceof WithAction) {
				WithAction windowAnnotation = (WithAction) annotation;
				Class<? extends Action> value = windowAnnotation.value();
				newAction[0] = value.newInstance();
				newAction[0].setImageDescriptor(getImageDescriptor());
			}
		}
		Window window = new ApplicationWindow(parentShell) {
			{
				addMenuBar();
				addToolBar(SWT.FLAT);
			}

			@Override
			protected void configureShell(Shell shell) {
				getToolBarManager().add(newAction[0]);
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
		this.action = newAction[0];
		return window;
	}

	private ImageDescriptor getImageDescriptor() {
		URL resource = GemInstallAction.class.getResource("dummy.png");
		return ImageDescriptor.createFromURL(resource);
	}

}
