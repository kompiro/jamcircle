package org.kompiro.jamcircle.kanban.ui.internal.command;


import org.eclipse.draw2d.geometry.Point;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.command.MoveCommand;

public class MoveCardCommand extends MoveCommand<Card> {

	private Card card;
	private Point location ;
	private Point oldLocation ;
	
	public MoveCardCommand() {
	}
		
	@Override
	public void move() {
		moveCard(location);
	}

	@Override
	public void undo() {
		moveCard(oldLocation);
	}

	private void moveCard(Point location) {
		if (card != null){
			card.prepareLocation();
			card.setX(location.x);
			card.setY(location.y);
			card.commitLocation();
			card.save(false);
		}else{
			KanbanUIStatusHandler.fail(new RuntimeException(), "MoveCardCommand:0001:Exception is occured");
		}
	}

	@Override
	protected void initialize() {
		this.card = getModel();
		if(card != null && getRectangle() != null){
			setExecute(true);
			this.location = getRectangle().getLocation();
			this.oldLocation = new Point(card.getX(),card.getY());
			setLabel("Move Card '" + card.getSubject() + "'");
		}
	}
		
}