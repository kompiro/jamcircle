package abbot.swt.gef.util;

import java.util.Formatter;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;

import abbot.swt.finder.generic.FormatterImpl;

public class EditPartFormatter extends FormatterImpl<EditPart> {

	public EditPartFormatter(Appendable appendable) {
		super(appendable);
	}

	public void format(EditPart editPart, int indent) {

		indent(indent);
		printf(editPart.toString()); // Includes class name and model.
		printText(editPart);
		printState(editPart);
		println();
		printModel(editPart, indent + 1);
		if (editPart instanceof GraphicalEditPart)
			printGraphical((GraphicalEditPart) editPart, indent + 1);
		if (editPart instanceof ConnectionEditPart)
			printConnection((ConnectionEditPart) editPart, indent + 1);
		// Useful?
		// if (editPart instanceof NodeEditPart)
		// appendNode(appendable, (NodeEditPart) editPart);
		// if (editPart instanceof RootEditPart)
		// appendNode(appendable, (RootEditPart) editPart);
		// if (editPart instanceof TreeEditPart)
		// appendNode(appendable, (TreeEditPart) editPart);
	}

	private void printConnection(ConnectionEditPart editPart, int indent) {
		printEditPart(editPart.getSource(), "source", indent + 1);
		printEditPart(editPart.getTarget(), "target", indent + 1);
	}

	private void printModel(EditPart editPart, int indent) {
		indent(indent);
		Object model = editPart.getModel();
		printf("model: %s\n", model);
	}

	private void printEditPart(EditPart editPart, String label, int indent) {
		indent(indent);
		printf("%s: %s\n", label, editPart);
	}

	private static class ListFormatter {

		private final String opener;

		private final String closer;

		private final String separator;

		private final Formatter formatter;

		private boolean first = true;

		public ListFormatter(String opener, String closer, String separator, Formatter formatter) {
			this.opener = opener;
			this.closer = closer;
			this.separator = separator;
			this.formatter = formatter;
		}

		public void printf(String format, Object... args) {
			if (first) {
				first = false;
				formatter.format(opener);
			} else {
				formatter.format(separator);
			}
			formatter.format(format, args);
		}

		public void close() {
			formatter.format(closer);
		}
	}

	private void printState(EditPart editPart) {

		ListFormatter list = new ListFormatter("[", "]", ",", formatter);

		if (editPart.isActive())
			list.printf("active");

		if (editPart.hasFocus())
			list.printf("focus");

		if (editPart.isSelectable())
			list.printf("selectable");

		int selected = editPart.getSelected();
		switch (selected) {
			case EditPart.SELECTED_NONE:
				break;
			case EditPart.SELECTED:
				list.printf("selected");
				break;
			case EditPart.SELECTED_PRIMARY:
				list.printf("selected-primary");
				break;
			default:
				list.printf(String.format("selected:%d?", selected));
				break;
		}

		list.close();
	}

	private void printGraphical(GraphicalEditPart editPart, int indent) {
		IFigure figure = editPart.getFigure();
		if (figure == null) {
			indent(indent);
			printf("*no figure*\n");
		} else {
			new FigureFormatter(out()).format(editPart.getFigure(), indent);
		}
	}

}
