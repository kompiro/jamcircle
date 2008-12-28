package org.kompiro.jamcircle.kanban.ui.widget;

import org.eclipse.swt.dnd.DragSourceEvent;

public interface CardListListener {

	public void dragFinished(DragSourceEvent event, CardListTableViewer viewer);	
	
}
