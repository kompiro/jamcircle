package abbot.swt.tester;

import org.eclipse.swt.widgets.ProgressBar;

import abbot.swt.utilities.Displays.IntResult;

/**
 * A tester for {@link ProgressBar}s.
 */
public class ProgressBarTester extends ControlTester {

	/**
	 * Factory method.
	 */
	public static ProgressBarTester getProgressBarTester() {
		return (ProgressBarTester) getTester(ProgressBar.class);
	}

	/**
	 * Constructs a new {@link ProgressBarTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public ProgressBarTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link ProgressBar#getMaximum()}.
	 */
	public int getMaximum(final ProgressBar progressBar) {
		checkWidget(progressBar);
		return syncExec(new IntResult() {
			public int result() {
				return progressBar.getMaximum();
			}
		});
	}

	/**
	 * Proxy for {@link ProgressBar#getMinimum()}.
	 */
	public int getMinimum(final ProgressBar progressBar) {
		checkWidget(progressBar);
		return syncExec(new IntResult() {
			public int result() {
				return progressBar.getMinimum();
			}
		});
	}

	/**
	 * Proxy for {@link ProgressBar#getSelection()}.
	 */
	public int getSelection(final ProgressBar progressBar) {
		checkWidget(progressBar);
		return syncExec(new IntResult() {
			public int result() {
				return progressBar.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link ProgressBar#setMinimum(int minimum)}.
	 */
	public void setMinimum(final ProgressBar progressBar, final int minimum) {
		checkWidget(progressBar);
		syncExec(new Runnable() {
			public void run() {
				progressBar.setMinimum(minimum);
			}
		});
	}

	/**
	 * Proxy for {@link ProgressBar#setMaximum(int maximum)}.
	 */
	public void setMaximum(final ProgressBar progressBar, final int maximum) {
		checkWidget(progressBar);
		syncExec(new Runnable() {
			public void run() {
				progressBar.setMaximum(maximum);
			}
		});
	}

	/**
	 * Proxy for {@link ProgressBar#setSelection(int value)}.
	 */
	public void setSelection(final ProgressBar progressBar, final int value) {
		checkWidget(progressBar);
		syncExec(new Runnable() {
			public void run() {
				progressBar.setSelection(value);
			}
		});
	}
}
