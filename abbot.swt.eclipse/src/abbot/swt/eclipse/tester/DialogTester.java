package abbot.swt.eclipse.tester;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.tester.ButtonTester;

public class DialogTester extends JFaceTester {

	public static final DialogTester Default = new DialogTester();

	public void actionClickButton(Shell dialogShell, String buttonText) {
		Button button = (Button) find(dialogShell, new WidgetTextMatcher(buttonText, Button.class));
		ButtonTester.getButtonTester().actionClick(button);
	}

}
