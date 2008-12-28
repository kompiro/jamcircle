package abbot.swt.gef.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import abbot.swt.finder.generic.FormatterImpl;

public class FigureFormatter extends FormatterImpl<IFigure> {

	public FigureFormatter(Appendable appendable) {
		super(appendable);
	}

	public void format(IFigure figure, int indent) {
		indent(indent);
		printf(getClassName(figure));
		printText(figure);
		printStyle(figure);
		printLocation(figure);
		println();
	}

	private String getClassName(IFigure figure) {
		String className = figure.getClass().getName();
		String[] prefixes = { "org.eclipse.draw2d.", "org.eclipse.gef." };
		for (int i = 0; i < prefixes.length; i++) {
			String prefix = prefixes[i];
			if (className.startsWith(prefix))
				return className.substring(prefix.length());
		}
		return className;
	}

	private void printStyle(IFigure figure) {

		List styles = new ArrayList();
		if (figure.isVisible())
			styles.add("visible");
		if (figure.isEnabled())
			styles.add("enabled");
		if (figure.isShowing())
			styles.add("showing");
		// styles.add(getColor("fg", figure.getForegroundColor()));
		// styles.add(getColor("bg", figure.getBackgroundColor()));

		printf(" [");

		boolean first = true;
		for (Iterator iterator = styles.iterator(); iterator.hasNext();) {
			Object style = (Object) iterator.next();
			if (first) {
				first = false;
			} else {
				print(',');
			}
			printf(style.toString());
		}

		print(']');
	}

	protected void printLocation(IFigure figure) {
		Rectangle bounds = figure.getBounds();
		org.eclipse.swt.graphics.Rectangle rectangle = new org.eclipse.swt.graphics.Rectangle(
				bounds.x, bounds.y, bounds.width, bounds.height);
		super.printLocation(rectangle);
	}

	// private String getColor(String tag, Color color) {
	// return String.format("%s(%02x,%02x,%02x)", tag, color.getRed(), color.getGreen(), color
	// .getBlue());
	// }
}
