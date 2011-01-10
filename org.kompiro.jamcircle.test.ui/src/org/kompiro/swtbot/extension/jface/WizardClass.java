package org.kompiro.swtbot.extension.jface;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class WizardClass<T extends Wizard> implements MethodRule {

	private T wizard;

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				Shell parentShell = new Shell();
				bot = new SWTBot();
				wizard = wizardClass.newInstance();
				RunOnNonUIThread runOnNonUIThread = new RunOnNonUIThread(bot, base, parentShell);
				Thread nonUIThread = new Thread(runOnNonUIThread);
				nonUIThread.setName("Runnning Test Thread");//$NON-NLS-1$
				nonUIThread.start();
				WizardDialog dialog = new WizardDialog(parentShell, wizard);
				dialog.create();
				dialog.open();

				waitForNonUIThreadFinished(parentShell, nonUIThread);
				if (runOnNonUIThread.getThrowable() != null) {
					throw runOnNonUIThread.getThrowable();
				}

			}
		};
	}

	private Class<T> wizardClass = null;
	private SWTBot bot;

	public WizardClass(Class<T> wizardClass) {
		this.wizardClass = wizardClass;
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

	public SWTBot getBot() {
		return bot;
	}

	public T getWizard() {
		return wizard;
	}

}
