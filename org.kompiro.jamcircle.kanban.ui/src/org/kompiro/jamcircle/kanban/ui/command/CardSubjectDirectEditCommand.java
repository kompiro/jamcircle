package org.kompiro.jamcircle.kanban.ui.command;

import org.kompiro.jamcircle.kanban.model.Card;

public class CardSubjectDirectEditCommand extends AbstractCommand {

	private Card card;
	private String oldSubject;
	private String subject;
	public CardSubjectDirectEditCommand(Card card,String subject){
		this.card = card;
		this.oldSubject = card.getSubject();
		this.subject = subject;
	}
	
	@Override
	public void doExecute() {
		card.setSubject(subject);
		card.save();
	}
	
	@Override
	public void undo() {
		card.setSubject(oldSubject);
		card.save();
	}

}
