package org.eclipse.swtbot.jface;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class WizardDialogTestRunner extends SWTBotJunit4ClassRunner {

	private IWizard newWizard = null;
	private SWTBot bot;

	public WizardDialogTestRunner(Class<?> klass) throws Exception {
		super(klass);
	}

	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
		Shell parentShell = new Shell();
		try {
			newWizard = newWizard();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		bot = new SWTBot();
		final boolean[] finishPressed = new boolean[1];
		WizardDialog dialog = new WizardDialogForTest(parentShell, newWizard, finishPressed);
		final Exception[] ex = new Exception[1];
		Runnable r = new Runnable() {
			public void run() {
				try {
					WizardDialogTestRunner.super.runChild(method, notifier);
					closeDialogs();
				} catch (Exception e) {
					ex[0] = e;
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
				if (title.equals(newWizard.getWindowTitle())) {
					activeShell.close();
				}
			}
		};
		Thread nonUIThread = new Thread(r);
		nonUIThread.setName("Runnning Test Thread");//$NON-NLS-1$
		nonUIThread.start();
		dialog.open();

		waitForNonUIThreadFinished(parentShell, nonUIThread);
		parentShell.dispose();
		if (ex[0] != null) {
			throw new IllegalStateException(ex[0]);
		}
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
		if (newWizard != null) {
			inject(test, "wizard", newWizard);
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

	private IWizard newWizard() throws InstantiationException, IllegalAccessException {
		TestClass testClass = getTestClass();
		Annotation[] annotations = testClass.getAnnotations();
		IWizard newWizard = null;
		for (Annotation annotation : annotations) {
			if (annotation instanceof WithWizard) {
				WithWizard wizardAnnotation = (WithWizard) annotation;
				Class<? extends IWizard> value = wizardAnnotation.value();
				newWizard = value.newInstance();
			}
		}
		return newWizard;
	}

}
