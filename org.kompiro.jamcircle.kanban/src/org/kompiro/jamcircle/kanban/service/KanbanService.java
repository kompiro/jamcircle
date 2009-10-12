package org.kompiro.jamcircle.kanban.service;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardDTO;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
import org.kompiro.jamcircle.storage.service.StorageChageListener;


public interface KanbanService {
	
	/**
	 * Initialized Kanban Service
	 */
	void init();
	
	// Card Section
	Card createCard(Board board,String summary, User user, int x, int y);
	
	Card createClonedCard(Board board,User user,Card card,int x,int y);	

	void deleteAllCards();
	
	Card[] findAllCards();
	
	Card[] findCardsOnBoard(Board board);

	Card[] findCardsOnLane(Lane lane);

	Card[] findCardsSentTo(User sentTo);
	
	Card[] findCards(String criteria, Object... parameters);
	
	Card[] findCardsInTrash();

	Card createReceiveCard(Board board,CardDTO dto,User current,User fromUser);
	
	boolean exportCards(File exportFile);

	boolean importCards(File importFile);

	
	// Lane Section
	Lane createLane(Board board,String status,int x ,int y,int width,int height);
	
	Lane[] findLanesInTrash();

	Lane[] findLanesOnBoard(Board board);

	Lane[] findAllLanes();
	
	void deleteAllLanes();

	boolean exportLanes(File exportFile);

	boolean importLanes(File importFile);

	// Board Section
	Board createBoard(String title);
	
	Board findBoard(int id);

	Board[] findAllBoard();
	
	void deleteAllBoards();

	boolean exportBoards(File exportFile);

	boolean importBoards(File importFile);
	
	// User Section
	User findUser(String user);
	
	User[] findUsersOnBoard();

	User[] findAllUsers();
	
	boolean hasUser(String user);
	
	User addUser(String user);

	void deleteUser(String user);

	void deleteAllUsers();

	boolean exportUsers(File exportFile);

	boolean importUsers(File importFile);

	User getCurrentUser();
	
	//Icon Section
	Icon[] findIcons();
	
	Icon addIcon(String type,int x,int y);
	
	void deleteAllIcons();

	boolean exportIcons(File exportFile);

	boolean importIcons(File importFile);

	// Others
	KanbanBoardTemplate[] getKanbanDataInitializers();

	void addStorageChangeListener(StorageChageListener listener);

	void removeStorageChangeListener(StorageChageListener listener);

	void addPropertyChangeListener(PropertyChangeListener boardChangeListener);

	void removePropertyChangeListener(PropertyChangeListener boardChangeListener);

	int countInTrash(Class<? extends GraphicalEntity> clazz);
	
	void discardToTrash(GraphicalEntity entity);

	void pickupFromTrash(GraphicalEntity entity);


}
