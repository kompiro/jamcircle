package org.kompiro.jamcircle.kanban.ui.model.mock;

import java.util.ArrayList;
import java.util.List;

import org.kompiro.jamcircle.kanban.model.*;

public class TrashModel extends
		org.kompiro.jamcircle.kanban.ui.model.TrashModel {

	private static final long serialVersionUID = 2380522436254409149L;
	private List<Card> cards;
	private List<Lane> lanes;
	public TrashModel(Icon icon) {
		super(icon);
		cards = new ArrayList<Card>();
		lanes = new ArrayList<Lane>();
	}
	@Override
	public boolean addCard(Card card) {
		return cards.add(card);
	}
	@Override
	public boolean addLane(Lane lane) {
		return lanes.add(lane);
	}
	@Override
	public boolean containCard(Card card) {
		return cards.contains(card);
	}
	@Override
	public int getCardCount() {
		return cards.size();
	}
	@Override
	public Card[] getCards() {
		return cards.toArray(new Card[]{});
	}

	@Override
	public String getContainerName() {
		return super.getContainerName();
	}

	@Override
	public Lane[] getLanes() {
		return lanes.toArray(new Lane[]{});
	}
	@Override
	public boolean isEmpty() {
		return cards.isEmpty() && lanes.isEmpty();
	}
	@Override
	public boolean removeCard(Card card) {
		return cards.remove(card);
	}
	
	@Override
	public boolean removeLane(Lane lane) {
		return lanes.remove(lane);
	}

}
