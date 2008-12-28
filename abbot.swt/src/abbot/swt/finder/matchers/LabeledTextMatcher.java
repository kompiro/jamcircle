package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.CompositeTester;
import abbot.swt.tester.LabelTester;
import abbot.swt.tester.TextTester;

public class LabeledTextMatcher implements WidgetMatcher {

	private final String labelText;

	public LabeledTextMatcher(String labelText) {
		this.labelText = labelText;
	}

	public boolean matches(Widget widget) {
		if (widget instanceof Text) {
			Composite parent = TextTester.getTextTester().getParent((Text) widget);
			Control[] siblings = CompositeTester.getCompositeTester().getChildren(parent);
			for (Control sibling : siblings) {
				if (sibling instanceof Label) {
					String labelText = LabelTester.getLabelTester().getText((Label) sibling);
					if (this.labelText.equals(labelText))
						return true;
				}
			}
		}
		return false;
	}

}
