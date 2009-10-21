package org.kompiro.jamcircle.kanban.ui.editpart;

import org.eclipse.gef.EditPart;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public interface IBoardEditPart extends EditPart{
	public BoardModel getBoardModel();
}