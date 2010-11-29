package org.kompiro.jamcircle.web;

import java.util.*;

import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

import com.vaadin.data.*;
import com.vaadin.data.util.ObjectProperty;

public class BoardHierarchy implements Container.Hierarchical {
	private static final long serialVersionUID = 1L;
	private Board[] boards;

	public BoardHierarchy(Board[] boards) {
		this.boards = boards;
	}

	@Override
	public int size() {
		return boards.length;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		return false;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		return null;
	}

	@Override
	public Collection<?> getItemIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item getItem(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return Arrays.asList(new String[] { "caption" });
	}

	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		if ("caption".equals(propertyId)) {
			if (itemId instanceof Board) {
				Board board = (Board) itemId;
				return new ObjectProperty(board.getTitle());
			}
			if (itemId instanceof Lane) {
				Lane card = (Lane) itemId;
				return new ObjectProperty(card.getStatus());
			}

			if (itemId instanceof Card) {
				Card card = (Card) itemId;
				return new ObjectProperty(card.getSubject());
			}
		} else if ("icon".equals(propertyId)) {

		}
		return null;
	}

	@Override
	public boolean containsId(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
			throws UnsupportedOperationException {
		return false;
	}

	@Override
	public Collection<?> rootItemIds() {
		return Arrays.asList(boards);
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean isRoot(Object itemId) {
		if (itemId instanceof Board) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasChildren(Object itemId) {
		return getChildren(itemId).size() != 0;
	}

	@Override
	public Object getParent(Object itemId) {
		if (itemId instanceof Card) {
			Card card = (Card) itemId;
			if (card.getLane() != null)
				return card.getLane();
			return card.getBoard();
		}
		if (itemId instanceof Lane) {
			Lane lane = (Lane) itemId;
			return lane.getBoard();
		}

		return null;
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		if (itemId instanceof Board) {
			Board board = (Board) itemId;
			List<GraphicalEntity> entities = new ArrayList<GraphicalEntity>();
			entities.addAll(Arrays.asList(board.getLanes()));
			entities.addAll(Arrays.asList(board.getCards()));
			return entities;
		}
		if (itemId instanceof Lane) {
			Lane lane = (Lane) itemId;
			return Arrays.asList(lane.getCards());
		}
		return null;
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		if (itemId instanceof Board) {
			return true;
		}
		if (itemId instanceof Lane) {
			return true;
		}
		return false;
	}
}
