package abbot.swt.tester;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.StringResult;

public class LinkTester extends ControlTester implements Textable {

	/**
	 * Factory method.
	 */
	public static LinkTester getLinkTester() {
		return (LinkTester) getTester(Link.class);
	}

	/**
	 * Constructs a new {@link LinkTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public LinkTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Link#addSelectionListener(SelectionListener listener)}.
	 */
	public void addSelectionListener(final Link link, final SelectionListener listener) {
		checkWidget(link);
		syncExec(new Runnable() {
			public void run() {
				link.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Link#getText()}.
	 */
	public String getText(final Link link) {
		checkWidget(link);
		return syncExec(new StringResult() {
			public String result() {
				return link.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Link) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}

	/**
	 * Proxy for {@link Link#removeSelectionListener(SelectionListener listener)}.
	 */
	public void removeSelectionListener(final Link link, final SelectionListener listener) {
		checkWidget(link);
		syncExec(new Runnable() {
			public void run() {
				link.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Link#setText(String string)}.
	 */
	public void setText(final Link link, final String string) {
		checkWidget(link);
		syncExec(new Runnable() {
			public void run() {
				link.setText(string);
			}
		});
	}

}
