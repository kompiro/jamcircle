package org.kompiro.jamcircle.kanban.ui.internal.command;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.ui.command.AbstractCommand;

public class CardSubjectDirectEditCommand extends AbstractCommand {

	private Card card;
	private String oldSubject;
	private String subject;
	public CardSubjectDirectEditCommand(Card card,String subject){
		this.card = card;
		this.subject = subject;
	}
	
	@Override
	public void doExecute() {
		card.setSubject(subject);
		card.save(false);
		setUndoable(true);
	}
	
	@Override
	public void undo() {
		card.setSubject(oldSubject);
		card.save(false);
	}

	@Override
	protected void initialize() {
		if(card != null){
			this.oldSubject = card.getSubject();
			setExecute(true);
		}
	}

}
