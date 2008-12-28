package abbot.swt.tester;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Composite}s.
 * <p>
 * Formerly extended ControlTester: thanks Markus Kuhn <markuskuhn@users.sourceforge.net>.
 */
public class CompositeTester extends ScrollableTester {

	/**
	 * Factory method.
	 */
	public static CompositeTester getCompositeTester() {
		return (CompositeTester) getTester(Composite.class);
	}

	/**
	 * Constructs a new {@link CompositeTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public CompositeTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Composite#getChildren()}. <p/>
	 * 
	 * @param composite
	 *            the control under test.
	 * @return the children of the composite.
	 */
	public Control[] getChildren(final Composite composite) {
		checkWidget(composite);
		return (Control[]) syncExec(new Result<Control[]>() {
			public Control[] result() {
				return composite.getChildren();
			}
		});
	}

	/**
	 * Proxy for {@link Composite#getLayout()}. <p/>
	 * 
	 * @param composite
	 *            the control under test.
	 * @return the layout of the composite.
	 */
	public Layout getLayout(final Composite composite) {
		checkWidget(composite);
		return (Layout) syncExec(new Result<Layout>() {
			public Layout result() {
				return composite.getLayout();
			}
		});
	}

	/**
	 * Proxy for {@link Composite#getTabList()}. <p/>
	 * 
	 * @param composite
	 *            the control under test.
	 * @return the tab list of the composite.
	 */
	public Control[] getTabList(final Composite composite) {
		checkWidget(composite);
		return (Control[]) syncExec(new Result<Control[]>() {
			public Control[] result() {
				return composite.getTabList();
			}
		});
	}

	/**
	 * Proxy for {@link Composite#setFocus()}
	 */
	public boolean setFocus(final Composite composite) {
		checkWidget(composite);
		return (boolean) syncExec(new BooleanResult() {
			public boolean result() {
				return composite.setFocus();
			}
		});
	}
}
