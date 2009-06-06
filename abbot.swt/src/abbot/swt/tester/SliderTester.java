package abbot.swt.tester;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Slider;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;

/**
 * Provides widget-specific actions, assertions, and getter methods for widgets of type Slider.
 */
public class SliderTester extends ControlTester {

	/**
	 * Factory method.
	 */
	public static SliderTester getSliderTester() {
		return (SliderTester) getTester(Slider.class);
	}

	/**
	 * Constructs a new {@link SliderTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public SliderTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Slider#addSelectionListener(SelectionListener listener)}.
	 */
	public void addSelectionListener(final Slider slider, final SelectionListener listener) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getEnabled()}.
	 */
	public boolean getEnabled(final Slider slider) {
		checkWidget(slider);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return slider.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getIncrement()}.
	 */
	public int getIncrement(final Slider slider) {
		checkWidget(slider);
		return syncExec(new IntResult() {
			public int result() {
				return slider.getIncrement();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getMaximum()}.
	 */
	public int getMaximum(final Slider slider) {
		checkWidget(slider);
		return syncExec(new IntResult() {
			public int result() {
				return slider.getMaximum();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getMinimum()}.
	 */
	public int getMinimum(final Slider slider) {
		checkWidget(slider);
		return syncExec(new IntResult() {
			public int result() {
				return slider.getMinimum();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getPageIncrement()}.
	 */
	public int getPageIncrement(final Slider slider) {
		checkWidget(slider);
		return syncExec(new IntResult() {
			public int result() {
				return slider.getPageIncrement();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getSelection()}.
	 */
	public int getSelection(final Slider slider) {
		checkWidget(slider);
		return syncExec(new IntResult() {
			public int result() {
				return slider.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#getThumb()}.
	 */
	public int getThumb(final Slider slider) {
		checkWidget(slider);
		return syncExec(new IntResult() {
			public int result() {
				return slider.getThumb();
			}
		});
	}

	/**
	 * Proxy for {@link Slider#removeSelectionListener(SelectionListener listener)}.
	 */
	public void removeSelectionListener(final Slider slider, final SelectionListener listener) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#setIncrement(int value)}.
	 */
	public void setIncrement(final Slider slider, final int value) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.setIncrement(value);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#setMinimum(int value)}.
	 */
	public void setMinimum(final Slider slider, final int value) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.setMinimum(value);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#setMaximum(int value)}.
	 */
	public void setMaximum(final Slider slider, final int value) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.setMaximum(value);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#setPageIncrement(int value)}.
	 */
	public void setPageIncrement(final Slider slider, final int value) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.setPageIncrement(value);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#setSelection(int value)}.
	 */
	public void setSelection(final Slider slider, final int value) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.setSelection(value);
			}
		});
	}

	/**
	 * Proxy for {@link Slider#setThumb(int value)}.
	 */
	public void setThumb(final Slider slider, final int value) {
		checkWidget(slider);
		syncExec(new Runnable() {
			public void run() {
				slider.setThumb(value);
			}
		});
	}
}
