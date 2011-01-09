package org.kompiro.jamcircle.web.figure.dd;

import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.web.client.ui.dd.VCardAccept;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.ClientCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.ui.Panel;

@ClientCriterion(VCardAccept.class)
final public class CardAccept extends ClientSideCriterion {

	private static final long serialVersionUID = -2515981634029336423L;
	private static final CardAccept singleton = new CardAccept();

	private CardAccept() {

	}

	public static CardAccept get() {
		return singleton;
	}

	@Override
	public boolean accept(DragAndDropEvent event) {
		Panel component = getComponent(event);
		if (component.getData() instanceof Card) {
			return true;
		}
		return false;
	}

	private Panel getComponent(DragAndDropEvent event) {
		return (Panel) event.getTransferable().getData("component");
	}
}
