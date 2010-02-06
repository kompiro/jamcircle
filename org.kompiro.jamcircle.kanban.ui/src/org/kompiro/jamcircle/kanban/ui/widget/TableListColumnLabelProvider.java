package org.kompiro.jamcircle.kanban.ui.widget;


import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.kompiro.jamcircle.kanban.ui.KanbanJFaceResource;

public abstract class TableListColumnLabelProvider extends
		ColumnLabelProvider {
	
	public abstract String getText(Object element);

	@Override
	public Color getBackground(Object element) {
		if (!(element instanceof TableListWrapper))
			return null;
		TableListWrapper wrapper = (TableListWrapper) element;
		if (wrapper.isEven()) {
			return JFaceResources.getColorRegistry().get(
					KanbanJFaceResource.DARK_COLOR_KEY);
		}
		return null;
	}
}
