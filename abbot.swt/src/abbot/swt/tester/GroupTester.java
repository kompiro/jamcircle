package abbot.swt.tester;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link Group}s.
 */
public class GroupTester extends CompositeTester implements Textable {

	/**
	 * Factory method.
	 */
	public static GroupTester getGroupTester() {
		return (GroupTester) WidgetTester.getTester(Group.class);
	}

	/**
	 * Constructs a new {@link GroupTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public GroupTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Group#getClientArea()}. <p/>
	 * 
	 * @param group
	 *            the group under test.
	 * @return the client area.
	 */
	public Rectangle getClientArea(final Group group) {
		checkWidget(group);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return group.getClientArea();
			}
		});
	}

	/**
	 * Proxy for {@link Group#getText()}. <p/>
	 * 
	 * @param group
	 *            the group under test.
	 * @return the text (title)
	 */
	public String getText(final Group group) {
		checkWidget(group);
		return syncExec(new StringResult() {
			public String result() {
				return group.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Group) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}
}
