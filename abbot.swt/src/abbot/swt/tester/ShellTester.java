package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.WaitTimedOutError;
import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.generic.MultipleFoundException;
import abbot.swt.finder.generic.NotFoundException;
import abbot.swt.finder.matchers.WidgetMatcher;
import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.script.Condition;
import abbot.swt.utilities.Wait;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Shell}s.
 */
public class ShellTester extends DecorationsTester {

	/**
	 * Waits for a {@link Shell} to become visible.
	 * 
	 * @param title
	 *            the title of the {@link Shell}
	 * @return the visible {@link Shell}
	 * @throws WaitTimedOutError
	 *             if the {@link Shell} is not visible within the default period of time.
	 */
	public static Shell waitVisible(String title) {
		return waitVisible(title, null, DEFAULT_WAIT_TIMEOUT);
	}

	/**
	 * Waits for a {@link Shell} to become visible.
	 * 
	 * @param title
	 *            the title of the {@link Shell}
	 * @param timeout
	 *            the number of milliseconds to wait
	 * @return the visible {@link Shell}
	 * @throws WaitTimedOutError
	 *             if the {@link Shell} is not visible within the specified period of time.
	 */
	public static Shell waitVisible(final String title, long timeout) {
		return waitVisible(title, null, timeout);
	}

	/**
	 * Waits for a {@link Shell} to become visible.
	 * 
	 * @param title
	 *            the title of the {@link Shell}
	 * @param parent
	 *            the Shell whose children will be searched (recursively). If <code>null</code>
	 *            then the entire hierarchy will be searched.
	 * @return the visible {@link Shell}
	 * @throws WaitTimedOutError
	 *             if the {@link Shell} is not visible within the default period of time.
	 */
	public static Shell waitVisible(String title, Shell parent) {
		return waitVisible(title, parent, DEFAULT_WAIT_TIMEOUT);
	}

	/**
	 * Waits for a {@link Shell} to become visible.
	 * 
	 * @param title
	 *            the title of the {@link Shell}
	 * @param parent
	 *            the Shell whose children will be searched (recursively). If <code>null</code>
	 *            then the entire hierarchy will be searched.
	 * @param timeout
	 *            the number of milliseconds to wait
	 * @return the visible {@link Shell}
	 * @throws WaitTimedOutError
	 *             if the {@link Shell} is not visible within the specified period of time.
	 */
	public static Shell waitVisible(final String title, final Shell parent, long timeout) {
		final Shell[] shell = new Shell[1];
		final WidgetFinder finder = WidgetFinderImpl.getDefault();
		final WidgetMatcher matcher = new WidgetTextMatcher(title, Shell.class, true);
		Condition condition = new Condition() {
			public boolean test() {
				try {
					shell[0] = (Shell) (parent == null ? finder.find(matcher) : finder.find(
							parent,
							matcher));
					return shell[0] != null;
				} catch (NotFoundException exception) {
					return false;
				} catch (MultipleFoundException e) {
					throw new ActionFailedException(e);
				}
			}
			
			public String toString() {
				return String.format("shell %s visible", title);
			}
		};
		Wait.wait(condition, timeout);
		return shell[0];
	}

	/**
	 * Factory method.
	 */
	public static ShellTester getShellTester() {
		return (ShellTester) WidgetTester.getTester(Shell.class);
	}

	/**
	 * Constructs a new {@link ShellTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ShellTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}
	
	/**
	 * @see Shell#addShellListener(ShellListener)
	 */
	public void addShellListener(final Shell shell, final ShellListener listener) {
		checkWidget(shell);
		syncExec(new Runnable() {
			public void run() {
				shell.addShellListener(listener);
			}
		});
	}

	/**
	 * @see Shell#removeShellListener(ShellListener)
	 */
	public void removeShellListener(final Shell shell, final ShellListener listener) {
		checkWidget(shell);
		syncExec(new Runnable() {
			public void run() {
				shell.removeShellListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Shell#forceActive()}.
	 */
	public void forceActive(final Shell shell) {
		checkWidget(shell);
		syncExec(new Runnable() {
			public void run() {
				shell.forceActive();
			}
		});
	}

	/**
	 * Proxy for {@link Shell#setActive()}.
	 */
	public void setActive(final Shell shell) {
		checkWidget(shell);
		syncExec(new Runnable() {
			public void run() {
				shell.setActive();
			}
		});
	}

	/**
	 * Proxy for {@link Shell#getImeInputMode()}.
	 * 
	 * @param shell
	 *            the shell under test.
	 * @return the input mode.
	 */
	public int getImeInputMode(final Shell shell) {
		checkWidget(shell);
		return syncExec(new IntResult() {
			public int result() {
				return shell.getImeInputMode();
			}
		});
	}

	/**
	 * Proxy for {@link Shell#getShell()}. <p/>
	 * 
	 * @param shell
	 *            the shell under test.
	 * @return the parent shell.
	 */
	public Shell getShell(final Shell shell) {
		checkWidget(shell);
		return (Shell) syncExec(new Result() {
			public Object result() {
				return shell.getShell();
			}
		});
	}

	/**
	 * Proxy for {@link Shell#getShells()}. <p/>
	 * 
	 * @param shell
	 *            the shell under test.
	 * @return the child shells.
	 */
	public Shell[] getShells(final Shell shell) {
		checkWidget(shell);
		return (Shell[]) syncExec(new Result() {
			public Object result() {
				return shell.getShells();
			}
		});
	}

	/**
	 * This method will see if a Shell has a modal style. SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL |
	 * SWT.PRIMARY_MODAL
	 * 
	 * @param shell
	 * @return true if the Shell has a modal style.
	 */
	public boolean isModal(final Shell shell) {
		checkWidget(shell);
		return syncExec(new BooleanResult() {
			public boolean result() {
				int style = shell.getStyle();
				if (style <= 0)
					return false;
				int bitmask = SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL | SWT.PRIMARY_MODAL;
				if ((style & bitmask) > 0)
					return true;
				return false;
			}
		});
	}

}
