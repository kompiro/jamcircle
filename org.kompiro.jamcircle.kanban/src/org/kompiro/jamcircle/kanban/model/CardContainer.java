package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;


public interface CardContainer {

	boolean addCard(Card card);

	boolean removeCard(Card card);
	
	boolean containCard(Card card);
	
	Card[] getCards();
	
	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
	
	String getContainerName();
	
	Board getBoard();
	
	public class Mock implements CardContainer{
		
		private List<Card> cards = new ArrayList<Card>();

		public boolean addCard(Card card) {
			return cards.add(card);
		}

		public void addPropertyChangeListener(PropertyChangeListener listener) {
		}

		public Card[] getCards() {
			return cards.toArray(new Card[]{});
		}

		public String getContainerName() {
			return "Mock";
		}

		public boolean containCard(Card card) {
			return cards.indexOf(card) != -1;
		}

		public boolean removeCard(Card card) {
			return cards.remove(card);
		}
		
		public Board getBoard() {
			throw new NotImplementedException();
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
		}
		
	}
}
