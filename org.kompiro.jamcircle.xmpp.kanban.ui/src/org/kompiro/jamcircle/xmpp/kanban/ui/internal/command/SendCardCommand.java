package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import org.eclipse.gef.EditPart;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.command.CancelableCommand;

public class SendCardCommand extends AbstractCommand implements CancelableCommand{

	private EditPart part;
	private User target;
	private Card card;

	public SendCardCommand() {
	}
	
	public void setPart(EditPart part) {
		this.part = part;
	}
	
	public void setTarget(User target) {
		this.target = target;
	}
	
	@Override
	public void doExecute() {
		getConnectionService().sendCard(target, card);
	}


	@Override
	public boolean canUndo() {
		return false;
	}

	public String getComfirmMessage() {
		return String.format("Are you realy want to send the card '%s'?", part.getModel().toString());
	}

	@Override
	protected void initialize() {
		if(target == null){
			String message = String.format("Target user is null.");
			throw new IllegalStateException(message);
		}
		if(getConnectionService().isAvailable(target) == false){
			String message = String.format("The user '%s' is not available.", target.getUserName());
			throw new IllegalStateException(message);
		}
		if(part == null){
			String message = String.format("Target part is null.");
			throw new IllegalStateException(message);
		}
		Object model = part.getModel();
		if( (model instanceof Card) == false){
			String message = String.format("The object '%s' is not support to send.", part.getClass().getSimpleName());
			throw new IllegalStateException(message);
		}
		this.card = (Card)model;
		setExecute(true);
	}

}
