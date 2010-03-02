package org.kompiro.jamcircle.kanban.ui.editpart;

import org.kompiro.jamcircle.kanban.model.*;

public interface IBoardCommandExecuter {
	public void add(Card card);
	public void add(Lane lane);
//	public void add(Icon icon);
	public void remove(Card card);
	public void remove(Lane lane);
//	public void remove(Icon icon);
}
