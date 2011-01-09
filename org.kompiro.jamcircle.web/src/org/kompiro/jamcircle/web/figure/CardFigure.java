package org.kompiro.jamcircle.web.figure;

import static java.lang.String.format;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.kompiro.jamcircle.kanban.model.Card;

import com.vaadin.ui.*;

public class CardFigure implements PropertyChangeListener {

	private Panel container;
	private Label subject;
	private Card card;

	public CardFigure(AbsoluteLayout parentLayout, Card card) {
		card.addPropertyChangeListener(this);
		this.card = card;
		container = new Panel();
		container.setData(card);
		AbsoluteLayout layout = new AbsoluteLayout();
		container.setWidth(toPx(138));
		container.setHeight(toPx(76));
		subject = new Label();
		setSubject();
		int x = card.getX();
		int y = card.getY();
		String location = getLocation(x, y);
		layout.addComponent(subject, getLocation(0, 0));
		container.setContent(layout);
		parentLayout.addComponent(container, location);
	}

	private void setSubject() {
		subject.setValue(format("%d : %s", card.getID(), card.getSubject()));
	}

	private String toPx(int value) {
		return value + "px";
	}

	private String getLocation(int x, int y) {
		String location = String.format("left: %dpx;top: %dpx;", x, y);
		return location;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Card.PROP_SUBJECT)) {
			setSubject();
		}
	}

}
