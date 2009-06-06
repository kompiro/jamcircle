package abbot.swt.gef.util;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetHierarchy;
import abbot.swt.gef.finder.EditPartHierarchy;
import abbot.swt.gef.finder.EditPartHierarchyImpl;
import abbot.swt.gef.tester.FigureTester;
import abbot.swt.utilities.WidgetFormatter;
import abbot.swt.utilities.WidgetHierarchyPrinter;

public class WidgetEditPartHierarchyPrinter extends WidgetHierarchyPrinter {

	public WidgetEditPartHierarchyPrinter(WidgetHierarchy hierarchy, Appendable appendable) {
		super(hierarchy, new WidgetEditPartFormatter(appendable));
	}

	private static class WidgetEditPartFormatter extends WidgetFormatter {

		public WidgetEditPartFormatter(Appendable appendable) {
			super(appendable);
		}

		public void format(Widget widget, int indent) {

			// Let the super do its thing.
			super.format(widget, indent);

			// If it's a FigureCanvas, dive into the EditPart hierarchy.
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
			
			IFigure root = canvas.getContents();
			EditPartViewer viewer = FigureTester.getFigureTester().findViewer(root);
			indent(indent);
			printf("viewer: %s\n", viewer);
			if (viewer != null) {
				RootEditPart rootEditPart = viewer.getRootEditPart();
				EditPartHierarchy hierarchy = new EditPartHierarchyImpl(rootEditPart);
				EditPartHierarchyPrinter printer = new EditPartHierarchyPrinter(hierarchy, out());
				printer.print(indent);
			}
			
			// Contents
//			FigureHierarchy hierarchy = new FigureHierarchyImpl(root);
//			FigureHierarchyPrinter printer = new FigureHierarchyPrinter(hierarchy, appendable);
//			printer.print(indent);
		}

	}

}
