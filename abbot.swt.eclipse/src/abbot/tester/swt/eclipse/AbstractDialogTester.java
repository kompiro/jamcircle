package abbot.tester.swt.eclipse;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.WaitTimedOutError;
import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.generic.FinderException;
import abbot.swt.finder.matchers.WidgetMatcher;
import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.script.Condition;
import abbot.swt.tester.ActionFailedException;
import abbot.swt.tester.ButtonTester;
import abbot.swt.tester.AbstractTester;
import abbot.swt.tester.ShellTester;
import abbot.swt.utilities.Displays;

/**
 * An abstract class to facilitate the testing of dialogs in eclipse. Currently, this class only
 * supports dialogs which have a title. protected abstract void invokeDialog() throws Throwable;
 * protected abstract void doTestDialog() throws Throwable; protected abstract void doCloseDialog(
 * boolean ok ) throws Throwable; Nesting of AbstractDialogTesters IS supported. All dialogs should
 * be launched from their respective invokeDialog methods. e.g. AbstractDialogTester one =
 * AbstractDialogTester(firstTitle,_display) { invokeDialog() throws Throwable { //invoke first
 * dialog } doTestDialog() throws Throwable { AbstractDialogTester two = new
 * AbstractDialogTester(secondTitle,_display) { protected void invokeDialog) throws Throwable {
 * //invoke second dialog } protected void doTestDialog() throws Throwable { ... } protected void
 * doCloseDialog(boolean ok) throws Throwable { //close second dialog } }; //maybe run some tests on
 * first dialog before launching second. two.runDialog(); //maybe run some tests on first dialog
 * after closing second. } doCloseDialog(boolean ok) throws Throwable { //close first dialog } }
 * one.runDialog(); //run all of the above code.
 */
public abstract class AbstractDialogTester {

	public static final int DEFAULT_TIMEOUT_MINUTES = 5;

	public static final int DEFAULT_TIMEOUT_CLOSE_SECONDS = 25;

	public static final int DEFAULT_TIMEOUT_INVOKEFAILURE_OR_SHELLSHOWING_SECONDS = 60;

	protected final Display display;

	protected final String title;

	protected AbstractTester robot;

	protected int timeoutMinutes;

	protected int timeoutCloseSeconds;

	protected int timeoutForInvokeFailureOrShellShowingSeconds;

	protected Shell shell;

	protected final WidgetFinder finder;

	public AbstractDialogTester(String title, Display display) {
		this(title, display, DEFAULT_TIMEOUT_MINUTES);
	}

	public AbstractDialogTester(String title, Display display, int timeoutMinutes) {
		this.display = display;
		this.title = title;
		this.timeoutMinutes = timeoutMinutes;
		this.timeoutCloseSeconds = DEFAULT_TIMEOUT_CLOSE_SECONDS;
		this.timeoutForInvokeFailureOrShellShowingSeconds = DEFAULT_TIMEOUT_INVOKEFAILURE_OR_SHELLSHOWING_SECONDS;
		this.finder = WidgetFinderImpl.getDefault();
	}

	protected AbstractTester getRobot() {
		if (robot == null)
			robot = new AbstractTester(new abbot.swt.Robot(display));
		return robot;
	}

	protected void clickButton(Composite root, String label) {
		clickButton(root, new WidgetTextMatcher(label, Button.class));
	}

	protected void clickButton(String label) {
		clickButton(shell, new WidgetTextMatcher(label, Button.class));
	}

	protected void clickButton(Composite root, WidgetMatcher matcher) {

		// Find the button.
		final Button button = findButton((root == null) ? shell : root, matcher);

		// Wait for it to be enabled.
		final ButtonTester tester = ButtonTester.getButtonTester();
		tester.wait(new Condition() {
			public boolean test() {
				return tester.getEnabled(button);
			}
		});

		// Click it.
		tester.actionClick(button);
	}

	private Button findButton(Composite composite, WidgetMatcher matcher) {
		try {
			return (Button) finder.find(composite, matcher);
		} catch (FinderException exception) {
			throw new ActionFailedException(exception);
		}
	}

	/**
	 * This wait is for the Condition that waits on doCloseDialog().
	 */
	public void setTimeoutCloseSeconds(int timeoutSeconds) {
		this.timeoutCloseSeconds = timeoutSeconds;
	}

	/**
	 * This wait is for the Condition that waits on doTestDialog()
	 */
	public void setTimeoutMinutes(int timeoutMinutes) {
		this.timeoutMinutes = timeoutMinutes;
	}

	/**
	 * This wait is for the Condition that waits on invokeDialog()
	 */
	public void setTimeoutForInvokeFailureOrShellShowingSeconds(int timeoutSeconds) {
		this.timeoutForInvokeFailureOrShellShowingSeconds = timeoutSeconds;
	}

	/**
	 * Invoke (open) the dialog to be tested.
	 */
	protected abstract void invokeDialog();

	/**
	 * Open, test and close the dialog.
	 */
	public void runDialog() {

		// Invoke the dialog and wait for it to show.
		invokeDialog();
		waitForDialogShowing();

		// Test (run) and close the dialog.
		try {
			doTestDialog();
		} finally {
			closeDialog();
		}
	}

	/***********************************************************************************************
	 * Similar to a JUnit test tearDown method, this method is responsible for backing out any
	 * changes (if the test requires) and closing the dialog.
	 **********************************************************************************************/
	protected abstract void doCloseDialog();

	/**
	 * All 'testing' or dialog manipulation should be done in this method.
	 */
	protected abstract void doTestDialog();

	/*
	 * Close the dialog and wait for it to close. If it doesn't close in a reasonable amount of
	 * time, force it to close.
	 */
	private void closeDialog() {
		doCloseDialog();
		try {
			getRobot().wait(new Condition() {
				public boolean test() {
					return shell != null && shell.isDisposed();
				}
			}, timeoutCloseSeconds * 1000L, 1000L);
		} catch (WaitTimedOutError error) {
			Displays.syncExec(new Runnable() {
				public void run() {
					if (shell != null && !shell.isDisposed())
						shell.close();
				}
			});
		}
	}

	protected void waitForDialogShowing() {
		shell = ShellTester.waitVisible(title, 30000L);
		ShellTester.getShellTester().actionFocus(shell);
	}

}
