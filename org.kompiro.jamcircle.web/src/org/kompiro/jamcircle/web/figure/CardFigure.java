package org.kompiro.jamcircle.web.figure;

import org.kompiro.jamcircle.kanban.model.Card;

import com.vaadin.ui.*;

public class CardFigure {

	private Panel container;

	public CardFigure(AbsoluteLayout parentLayout, Card card) {
		container = new Panel();
		container.setData(card);
		AbsoluteLayout layout = new AbsoluteLayout();
		container.setWidth(toPx(138));
		container.setHeight(toPx(76));
		Label label = new Label(card.getSubject());
		int x = card.getX();
		int y = card.getY();
		String location = getLocation(x, y);
		layout.addComponent(label, getLocation(0, 0));
		container.setContent(layout);
		parentLayout.addComponent(container, location);
	}

	private String toPx(int value) {
		return value + "px";
	}

	private String getLocation(int x, int y) {
		String location = String.format("left: %dpx;top: %dpx;", x, y);
		return location;
	}

}
