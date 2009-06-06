package abbot.swt.gef.util;

import org.eclipse.draw2d.IFigure;

import abbot.swt.finder.generic.HierarchyPrinter;
import abbot.swt.gef.finder.FigureHierarchy;

public class FigureHierarchyPrinter extends HierarchyPrinter<IFigure> {

	public FigureHierarchyPrinter(FigureHierarchy hierarchy, Appendable appendable) {
		super(hierarchy, new FigureFormatter(appendable));
	}
}
