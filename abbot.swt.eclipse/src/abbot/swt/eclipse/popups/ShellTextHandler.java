package abbot.swt.eclipse.popups;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.eclipse.popups.Popups.Handler;
import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.generic.FinderException;
import abbot.swt.finder.matchers.WidgetMatcher;
import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.script.Condition;
import abbot.swt.tester.ButtonTester;
import abbot.swt.tester.AbstractTester;
import abbot.swt.tester.ShellTester;

public class ShellTextHandler implements Handler {

	private static final long BUTTON_ENABLED_TIMEOUT = 5000L;

	private static final long BUTTON_ENABLED_INTERVAL = 200L;

	private static final long SHELL_CLOSED_TIMEOUT = 3000L;

	private static final long SHELL_CLOSED_INTERVAL = 200L;

	private final String title;

	private final String buttonText;

	public ShellTextHandler(String title, String buttonText) {
		this.title = title;
		this.buttonText = buttonText;
	}

	public boolean canHandle(Event event) {
		Shell shell = (Shell) event.widget;
		return title.equals(shell.getText());
	}

	public void handle(Event event) {

		final Shell shell = (Shell) event.widget;
		if (!shell.isDisposed()) {
			try {

				// Find the button.
				WidgetMatcher matcher = new WidgetTextMatcher(buttonText, Button.class);
				WidgetFinder finder = WidgetFinderImpl.getDefault();
				final Button button = (Button) finder.find(shell, matcher);

				// Wait for it to become enabled.
				final ButtonTester buttonTester = ButtonTester.getButtonTester();
				AbstractTester.getDefault().wait(new Condition() {
					public boolean test() {
						return buttonTester.isEnabled(button);
					}
				}, BUTTON_ENABLED_TIMEOUT, BUTTON_ENABLED_INTERVAL);

				// Click it.
				buttonTester.actionClick(button);

			} catch (FinderException exception) {

				// Error: Problem finding button. Forcibly close the shell and throw a
				// RuntimeException.
				shell.getDisplay().syncExec(new Runnable() {
					public void run() {
						shell.close();
					}
				});
				throw new RuntimeException(exception);
			} finally {

				// Wait for the shell to close.
				final ShellTester shellTester = ShellTester.getShellTester();
				shellTester.wait(new Condition() {
					public boolean test() {
						return shell.isDisposed();
					}
				}, SHELL_CLOSED_TIMEOUT, SHELL_CLOSED_INTERVAL);
			}
		}
	}

	public String toString() {
		return String.format("%s[\"%s\",\"%s\"]", getClass().getSimpleName(), title, buttonText);
	}

}
