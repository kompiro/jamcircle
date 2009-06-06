package abbot.swt.tester;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.Robot;
import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.StringResult;

public class CLabelTester extends CompositeTester implements Textable {

	public static CLabelTester getCLabelTester() {
		return (CLabelTester) getTester(CLabel.class);
	}

	public CLabelTester(Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link CLabel#getText()}
	 */
	public String getText(final CLabel cLabel) {
		return syncExec(new StringResult() {
			public String result() {
				return cLabel.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((CLabel) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}

	/**
	 * Proxy for {@link CLabel#setText(String)}
	 */
	public void setText(final CLabel cLabel, final String string) {
		syncExec(new Runnable() {
			public void run() {
				cLabel.setText(string);
			}
		});
	}

}
