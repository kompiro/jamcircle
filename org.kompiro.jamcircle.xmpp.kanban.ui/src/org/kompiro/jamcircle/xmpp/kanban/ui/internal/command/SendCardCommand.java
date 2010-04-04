package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import org.eclipse.gef.EditPart;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.command.CancelableCommand;
import org.kompiro.jamcircle.xmpp.kanban.ui.Messages;

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
		return String.format(Messages.SendCardCommand_confirm_message, part.getModel().toString());
	}

	@Override
	protected void initialize() {
		if(target == null){
			String message = String.format(Messages.SendCardCommand_target_is_null_error_message);
			throw new IllegalStateException(message);
		}
		boolean available = false;
		try {
			available = getConnectionService().isAvailable(target);
		} catch (NullPointerException e) {
		}
		if(available == false){
			String message = String.format(Messages.SendCardCommand_not_available_error_message, target.getUserName());
			throw new IllegalStateException(message);
		}
		if(part == null){
			String message = String.format(Messages.SendCardCommand_target_part_null_error);
			throw new IllegalStateException(message);
		}
		Object model = part.getModel();
		if( (model instanceof Card) == false){
			String message = String.format(Messages.SendCardCommand_not_supprt_send_error, part.getClass().getSimpleName());
			throw new IllegalStateException(message);
		}
		this.card = (Card)model;
		setExecute(true);
	}

}
