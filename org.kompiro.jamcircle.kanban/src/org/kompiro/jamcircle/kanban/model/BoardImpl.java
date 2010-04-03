package org.kompiro.jamcircle.kanban.model;

import java.beans.PropertyChangeEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.model.mock.MockGraphicalEntity;


/**
 * This implementation describes board implmentation wrapper.
 * @author kompiro
 */
public class BoardImpl extends EntityImpl{
	
	private static final String ERROR_HAS_OCCURED = "SQLException has occured.";//$NON-NLS-1$

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private Board board;
	
	private List<Card> mockCards = new ArrayList<Card>();

	private List<Lane> mockLanes = new ArrayList<Lane>();

	
	public BoardImpl(Board board){
		super(board);
		this.board = board;
	}
	
	public boolean addCard(Card card){
		Board board2 = card.getBoard();
		if(board2 == null || board.getID() != board2.getID()){
			card.setBoard(board);
		}
		card.setLane(null);
		card.setDeletedVisuals(false);
		if(card.isMock()){
			mockCards.add(card);
		}else{
			card.save(false);
			board.getEntityManager().flush(card);
			try {
				board.getEntityManager().find(Card.class,Card.PROP_ID + QUERY, card.getID());
			} catch (SQLException e) {
				KanbanStatusHandler.fail(e, ERROR_HAS_OCCURED);
			}
		}
		PropertyChangeEvent event = new PropertyChangeEvent(board,Board.PROP_CARD,null,card);
		fireEvent(event);
		return true;
	}

	public boolean removeCard(Card card){
		card.setBoard(null);
		card.setDeletedVisuals(true);
		if(card.isMock()){
			mockCards.remove(card);
		}else{
			card.save(false);
			board.getEntityManager().flush(card,board);
		}
		PropertyChangeEvent event = new PropertyChangeEvent(board,Board.PROP_CARD,card,null);
		fireEvent(event);
		return true;
	}
	
	public boolean containCard(Card card){
		return board.equals(card.getBoard()) || mockCards.contains(card);
	}
	
	public Card[] getCards(){
		Collection<Card> allCards = new ArrayList<Card>();
		allCards.addAll(Arrays.asList(board.getCardsFromDB()));
		allCards.addAll(mockCards);
		return allCards.toArray(new Card[]{});
	}
	
	public void clearMocks(){
		for(Card card : mockCards){
			card.setDeletedVisuals(true);
			PropertyChangeEvent event = new PropertyChangeEvent(board,Board.PROP_CARD,card,null);
			fireEvent(event);
		}
		mockCards.clear();
		for(Lane lane : mockLanes){
			lane.setDeletedVisuals(true);
			PropertyChangeEvent event = new PropertyChangeEvent(board,Board.PROP_LANE,lane,null);
			fireEvent(event);
		}
		mockLanes.clear();
	}
	
	public  boolean addLane(Lane lane){
		lane.setBoard(board);
		if(lane instanceof MockGraphicalEntity){
			mockLanes.add(lane);
		}else{
			lane.save(false);
			board.getEntityManager().flush(lane,board);
		}
		PropertyChangeEvent event = new PropertyChangeEvent(board,Board.PROP_LANE,null,lane);
		fireEvent(event);
		return true;
	}

	public  boolean removeLane(Lane lane){
		lane.setBoard(null);
		if(lane.isMock()){
			mockLanes.remove(lane);
		}else{
			lane.save(false);
			board.getEntityManager().flush(lane,board);
		}
		PropertyChangeEvent event = new PropertyChangeEvent(board,Board.PROP_LANE,lane,null);
		fireEvent(event);
		return true;		
	}
	
	public Lane[] getLanes(){
		Collection<Lane> allLanes = new ArrayList<Lane>();
		allLanes.addAll(Arrays.asList(board.getLanesFromDB()));
		allLanes.addAll(mockLanes);
		return allLanes.toArray(new Lane[]{});
	}
	
	public void save(boolean directExecution){
		if(directExecution){
			board.save();
		}
		Runnable runnable = new Runnable() {
			public void run() {
				board.save();
			}
		};
		executor.execute(runnable);
	}
	
	public Board getBoard(){
		return board;
	}

	public Board gainBoard(){
		return board;
	}
	
	@Override
	public String toString() {
		return String.format(TO_STRING_FORMAT,board.getID(),board.getTitle(),board.isTrashed());
	}
	
}
