package abbot.swt.tester;

import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.StringResult;

public class ToolTipTester extends WidgetTester implements Textable {

	/**
	 * Factory method.
	 */
	public static ToolTipTester getToolTipTester() {
		return (ToolTipTester) WidgetTester.getTester(ToolTip.class);
	}

	/**
	 * Constructs a new {@link ToolTipTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ToolTipTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	public boolean isVisible(final ToolTip toolTip) {
		return Displays.syncExec(toolTip.getDisplay(), new BooleanResult() {
			public boolean result() {
				return toolTip.isVisible();
			}
		});
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof ToolTip)
			return isVisible((ToolTip) widget);
		return super.isVisible(widget);
	}

	public String getText(final ToolTip toolTip) {
		return syncExec(new StringResult() {
			public String result() {
				return toolTip.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((ToolTip) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}

	public void setText(final ToolTip toolTip, final String string) {
		syncExec(new Runnable() {
			public void run() {
				toolTip.setText(string);
			}
		});
	}

}
