package org.kompiro.jamcircle.kanban.ui.model;

import org.eclipse.core.runtime.Platform;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.model.internal.KanbanUIModelContext;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public class TrashModel extends AbstractIconModel implements CardContainer, LaneContainer {
	public static final String NAME = Messages.TrashModel_name;

	public static String PROP_CARD = "trash card"; //$NON-NLS-1$
	public static String PROP_LANE = "trash lane"; //$NON-NLS-1$
	private KanbanService kanbanService;
	private ConfirmStrategy confirmStrategy = KanbanUIModelContext.getDefault().getConfirmStrategy();
	private static final long serialVersionUID = 33231939397478655L;

	public TrashModel(Icon icon, KanbanService kanbanService) {
		super(icon);
		this.kanbanService = kanbanService;
	}

	public boolean addCard(Card card) {
		kanbanService.discardToTrash(card);
		firePropertyChange(PROP_CARD, null, card);
		return false;
	}

	public boolean removeCard(Card card) {
		kanbanService.pickupFromTrash(card);
		firePropertyChange(PROP_CARD, card, null);
		return false;
	}

	public boolean containCard(Card card) {
		return card.isTrashed();
	}

	public Card[] getCards() {
		return getKanbanService().findCardsInTrash();
	}

	public int countTrashedCard() {
		return countTrashed(Card.class);
	}

	public boolean isEmpty() {
		if (Platform.isRunning()) {
			return countTrashedCard() == 0;
		}
		return true;
	}

	public boolean addLane(Lane lane) {
		kanbanService.discardToTrash(lane);
		firePropertyChange(PROP_LANE, lane, null);
		return false;
	}

	public boolean removeLane(Lane lane) {
		kanbanService.pickupFromTrash(lane);
		firePropertyChange(PROP_LANE, null, lane);
		return false;
	}

	public boolean containLane(Lane lane) {
		return lane.isTrashed();
	}

	public Board gainBoard() {
		return null;
	}

	public int countTrashedLane() {
		return countTrashed(Lane.class);
	}

	public Lane[] getLanes() {
		return getKanbanService().findLanesInTrash();
	}

	public String getContainerName() {
		return Messages.TrashModel_container_name;
	}

	private KanbanService getKanbanService() {
		return kanbanService;
	}

	public int countTrashed(Class<? extends GraphicalEntity> clazz) {
		return getKanbanService().countInTrash(clazz);
	}

	/**
	 * Delete cards and lanes on board and itself.
	 * 
	 * @param board
	 *            target board
	 */
	public void addBoard(Board board) {
		String message = String.format(Messages.TrashModel_confirm_message, board.getTitle());
		if (confirm(message)) {
			Lane[] lanes = board.getLanes();
			if (lanes != null) {
				for (Lane lane : lanes) {
					Card[] cards = lane.getCards();
					addCards(cards);
					addLane(lane);
				}
			}
			Card[] cards = board.getCards();
			addCards(cards);
			board.setTrashed(true);
			board.save(false);
		}
	}

	private void addCards(Card[] cards) {
		if (cards != null) {
			for (Card card : cards) {
				addCard(card);
			}
		}
	}

	private boolean confirm(String message) {
		return confirmStrategy.confirm(message);
	}

	public void setConfirmStrategy(ConfirmStrategy confirmStrategy) {
		this.confirmStrategy = confirmStrategy;
	}

	public String getName() {
		return NAME;
	}

}