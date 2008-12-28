package abbot.swt.gef.util;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetHierarchy;
import abbot.swt.gef.finder.FigureHierarchy;
import abbot.swt.gef.finder.FigureHierarchyImpl;
import abbot.swt.utilities.WidgetFormatter;
import abbot.swt.utilities.WidgetHierarchyPrinter;

public class WidgetFigureHierarchyPrinter extends WidgetHierarchyPrinter {

	public WidgetFigureHierarchyPrinter(WidgetHierarchy hierarchy, Appendable appendable) {
		super(hierarchy, new WidgetFigureFormatter(appendable));
	}

	private static class WidgetFigureFormatter extends WidgetFormatter {

		public WidgetFigureFormatter(Appendable appendable) {
			super(appendable);
		}

		public void format(Widget widget, int indent) {

			// Let the super do its thing.
			super.format(widget, indent);

			// If it's a FigureCanvas, dive into the IFigure hierarchy.
			if (widget instanceof FigureCanvas) {
				formatFigureCanvas((FigureCanvas) widget, indent + 1);
			}
		}

		private void formatFigureCanvas(final FigureCanvas canvas, final int indent) {

			// Viewport
			Viewport viewport = canvas.getViewport();
			indent(indent);
			printf("viewport: %s\n", viewport);

			// Lightweight system
			LightweightSystem system = canvas.getLightweightSystem();
			indent(indent);
			printf("lightweight system: %s (root: %s)\n", system, system.getRootFigure());

			// Contents
			IFigure root = canvas.getContents();
			FigureHierarchy hierarchy = new FigureHierarchyImpl(root);
			FigureHierarchyPrinter printer = new FigureHierarchyPrinter(hierarchy, out());
			printer.print(indent);
		}

	}

}
