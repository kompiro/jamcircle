package org.kompiro.jamcircle.kanban.ui.internal.command;


import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;

public class MoveCardCommand extends MoveCommand {

	private Card card;
	private Point location ;
	private Point oldLocation ;
	
	public MoveCardCommand() {
	}
		
	@Override
	public void doExecute() {
		moveCard(location);
	}

	@Override
	public void undo() {
		moveCard(oldLocation);
	}

	private void moveCard(Point location) {
		if (card != null){
			card.setX(location.x);
			card.setY(location.y);
			card.commitLocation();
			card.save();
		}else{
			KanbanUIStatusHandler.fail(new RuntimeException(), "MoveCardCommand:0001:Exception is occured");
		}
	}

	@Override
	protected void initialize() {
		this.card = (Card) getModel();
		setLabel("Move Card '" + card.getSubject() + "'");
		this.oldLocation = new Point(card.getX(),card.getY());
		this.location = getRectangle().getLocation();
	}
		
}
