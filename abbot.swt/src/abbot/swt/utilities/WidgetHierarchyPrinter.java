package abbot.swt.utilities;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetHierarchy;
import abbot.swt.finder.generic.HierarchyPrinter;

public class WidgetHierarchyPrinter extends HierarchyPrinter<Widget> {

	public WidgetHierarchyPrinter(WidgetHierarchy hierarchy, Appendable appendable) {
		super(hierarchy, new WidgetFormatter(appendable));
	}

	public WidgetHierarchyPrinter(WidgetHierarchy hierarchy, Formatter<Widget> formatter) {
		super(hierarchy, formatter);
	}
}
