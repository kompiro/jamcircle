package abbot.swt.tester;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Scale;

import abbot.swt.utilities.Displays.IntResult;

/**
 * Provides widget-specific actions, assertions, and getter methods for widgets of type Scale.
 */
public class ScaleTester extends ControlTester {

	/**
	 * Factory method.
	 */
	public static ScaleTester getScaleTester() {
		return (ScaleTester) getTester(Scale.class);
	}

	/**
	 * Constructs a new {@link ScaleTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ScaleTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Scale#addSelectionListener(SelectionListener listener)}.
	 */
	public void addSelectionListener(final Scale scale, final SelectionListener listener) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Scale#getIncrement()}.
	 */
	public int getIncrement(final Scale scale) {
		checkWidget(scale);
		return syncExec(new IntResult() {
			public int result() {
				return scale.getIncrement();
			}
		});
	}

	/**
	 * Proxy for {@link Scale#getMaximum()}.
	 */
	public int getMaximum(final Scale scale) {
		checkWidget(scale);
		return syncExec(new IntResult() {
			public int result() {
				return scale.getMaximum();
			}
		});
	}

	/**
	 * Proxy for {@link Scale#getMinimum()}.
	 */
	public int getMinimum(final Scale scale) {
		checkWidget(scale);
		return syncExec(new IntResult() {
			public int result() {
				return scale.getMinimum();
			}
		});
	}

	/**
	 * Proxy for {@link Scale#getPageIncrement()}.
	 */
	public int getPageIncrement(final Scale scale) {
		checkWidget(scale);
		return syncExec(new IntResult() {
			public int result() {
				return scale.getPageIncrement();
			}
		});
	}

	/**
	 * Proxy for {@link Scale#getSelection()}.
	 */
	public int getSelection(final Scale scale) {
		checkWidget(scale);
		return syncExec(new IntResult() {
			public int result() {
				return scale.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Scale#removeSelectionListener(SelectionListener listener)}.
	 */
	public void removeSelectionListener(final Scale scale, final SelectionListener listener) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Scale#setIncrement(int increment)}.
	 */
	public void setIncrement(final Scale scale, final int increment) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.setIncrement(increment);
			}
		});
	}

	/**
	 * Proxy for {@link Scale#setMinimum(int value)}.
	 */
	public void setMinimum(final Scale scale, final int value) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.setMinimum(value);
			}
		});
	}

	/**
	 * Proxy for {@link Scale#setMaximum(int value)}.
	 */
	public void setMaximum(final Scale scale, final int value) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.setMaximum(value);
			}
		});
	}

	/**
	 * Proxy for {@link Scale#setPageIncrement(int pageIncrement)}.
	 */
	public void setPageIncrement(final Scale scale, final int pageIncrement) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.setPageIncrement(pageIncrement);
			}
		});
	}

	/**
	 * Proxy for {@link Scale#setSelection(int value)}.
	 */
	public void setSelection(final Scale scale, final int value) {
		checkWidget(scale);
		syncExec(new Runnable() {
			public void run() {
				scale.setSelection(value);
			}
		});
	}
}
