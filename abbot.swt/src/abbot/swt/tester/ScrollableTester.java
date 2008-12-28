package abbot.swt.tester;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Scrollable}s.
 */
public class ScrollableTester extends ControlTester {

	/**
	 * Factory method.
	 */
	public static ScrollableTester getScrollableTester() {
		return (ScrollableTester) getTester(Scrollable.class);
	}

	/**
	 * Constructs a new {@link ScrollableTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public ScrollableTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Scrollable#getClientArea()}. <p/>
	 */
	public Rectangle getClientArea(final Scrollable scrollable) {
		checkWidget(scrollable);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return scrollable.getClientArea();
			}
		});
	}

	/**
	 * Proxy for {@link Scrollable#getHorizontalBar()}. <p/>
	 * 
	 * @param scrollable
	 *            the scrollable under test.
	 * @return the horizontal bar.
	 */
	public ScrollBar getHorizontalBar(final Scrollable scrollable) {
		checkWidget(scrollable);
		return (ScrollBar) syncExec(new Result() {
			public Object result() {
				return scrollable.getHorizontalBar();
			}
		});
	}

	/**
	 * Proxy for {@link Scrollable#getVerticalBar()}. <p/>
	 * 
	 * @param scrollable
	 *            the scrollable under test.
	 * @return the vertical bar.
	 */
	public ScrollBar getVerticalBar(final Scrollable scrollable) {
		checkWidget(scrollable);
		return (ScrollBar) syncExec(new Result() {
			public Object result() {
				return scrollable.getVerticalBar();
			}
		});
	}
}
