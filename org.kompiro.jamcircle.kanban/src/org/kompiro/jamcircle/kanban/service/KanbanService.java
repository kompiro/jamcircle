package org.kompiro.jamcircle.kanban.service;

import java.beans.PropertyChangeListener;
import java.io.File;

import net.java.ao.Entity;

import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
import org.kompiro.jamcircle.storage.service.StorageChageListener;

/**
 * This class provides Kanban feature's based services.<br>
 * If you'd like to use this service, you can get from bundle context or
 * Declarative Service.
 * TODO: Export and Import methods will be separated because these
 * responsibility aren't base services.
 * TODO: Separated model methods to each service provider class.
 */
public interface KanbanService {

	public static final String PROP_CHANGED_CURRENT_USER = "changed_current_user";

	/**
	 * Initialize Kanban Service.<br>
	 * If Kanban Service isn't initialized, the services aren't able to correct
	 * results.
	 */
	void init();

	// Card Section

	/**
	 * create card to the target board on storage.
	 * 
	 * @param board
	 *            [required] target board
	 * @param summary
	 *            card's default summary
	 * @param user
	 *            related user
	 * @param x
	 *            [required] location x
	 * @param y
	 *            [required] location y
	 * @return created card
	 */
	Card createCard(Board board, String summary, User user, int x, int y);

	/**
	 * cloned by param card to the target board.
	 * 
	 * @param board
	 *            [required] target board
	 * @param user
	 *            related user
	 * @param card
	 *            [required] target card
	 * @param x
	 *            [required] location x
	 * @param y
	 *            [required] location y
	 * @return cloned card
	 */
	Card createClonedCard(Board board, User user, Card card, int x, int y);

	/**
	 * delete all cards from storage.<br>
	 * This method is TESTING PURPOSE only.
	 */
	void deleteAllCards();

	/**
	 * find all cards from storage.
	 * 
	 * @return found all cards (include cards in trash)
	 */
	Card[] findAllCards();

	/**
	 * find cards from target board
	 * 
	 * @param board
	 *            [required] target board
	 * @return found cards
	 */
	Card[] findCardsOnBoard(Board board);

	/**
	 * find cards from target lane
	 * 
	 * @param lane
	 *            [required] target lane
	 * @return found cards
	 */
	Card[] findCardsOnLane(Lane lane);

	/**
	 * find cards from sent to user
	 * 
	 * @param sentTo
	 *            [required] Sent to user
	 * @return found cards
	 */
	Card[] findCardsSentTo(User sentTo);

	/**
	 * find cards using search condition
	 * 
	 * @param criteria
	 *            [required] condition string
	 * @param parameters
	 *            parameter
	 * @return found cards
	 */
	Card[] findCards(String criteria, Object... parameters);

	/**
	 * find cards in trash
	 * 
	 * @return found cards.
	 */
	Card[] findCardsInTrash();

	/**
	 * create card from received card dto object on board
	 * 
	 * @param board
	 *            [required] target board
	 * @param dto
	 *            [required] translated card data object
	 * @param fromUser
	 *            [required] sent the card from user
	 * @return created card
	 */
	Card createReceiveCard(Board board, CardDTO dto, User fromUser);

	/**
	 * export cards [CSV format] from storage
	 * 
	 * @param exportFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean exportCards(File exportFile);

	/**
	 * import cards [CSV format] to storage
	 * 
	 * @param importFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean importCards(File importFile);

	// Lane Section
	/**
	 * create lane to target board
	 * 
	 * @param board
	 *            [required] target board
	 * @param status
	 *            lane's default status
	 * @param x
	 *            [required] location x
	 * @param y
	 *            [required] location y
	 * @param width
	 *            [required] size width
	 * @param height
	 *            [required] size height
	 * @return created lane
	 */
	Lane createLane(Board board, String status, int x, int y, int width, int height);

	/**
	 * find lanes in trash
	 * 
	 * @return found lanes
	 */
	Lane[] findLanesInTrash();

	/**
	 * find lanes on board
	 * 
	 * @param board
	 *            [required] target board
	 * @return found lanes
	 */
	Lane[] findLanesOnBoard(Board board);

	/**
	 * find all lanes in storage
	 * 
	 * @return found lanes
	 */
	Lane[] findAllLanes();

	/**
	 * delete all lanes from storage.<br>
	 * This method is TESTING PURPOSE only.
	 */
	void deleteAllLanes();

	/**
	 * export lanes [CSV format] from storage
	 * 
	 * @param exportFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean exportLanes(File exportFile);

	/**
	 * import lanes [CSV format] to storage
	 * 
	 * @param importFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean importLanes(File importFile);

	// Board Section
	/**
	 * create board to storage
	 * 
	 * @param title
	 *            board's default title
	 * @return created board
	 */
	Board createBoard(String title);

	/**
	 * find board from storage
	 * 
	 * @param id
	 *            [required] board's id
	 * @return found board
	 */
	Board findBoard(int id);

	/**
	 * find all board from storage
	 * 
	 * @return all board
	 */
	Board[] findAllBoard();

	/**
	 * delete all boards from storage.<br>
	 * This method is TESTING PURPOSE only.
	 */
	void deleteAllBoards();

	/**
	 * export boards [CSV format] from storage
	 * 
	 * @param exportFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean exportBoards(File exportFile);

	/**
	 * export boards [CSV format] from storage
	 * 
	 * @param exportFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean importBoards(File importFile);

	// User Section

	/**
	 * find user from storage
	 * 
	 * @param userId
	 *            target userId
	 * @return found user
	 */
	User findUser(String userId);

	/**
	 * find users from storage exclude trashed data
	 * 
	 * @return found users
	 */
	User[] findUsersOnBoard();

	/**
	 * find all users from storage
	 * 
	 * @return found users
	 */
	User[] findAllUsers();

	/**
	 * search target userId from storage
	 * 
	 * @param userId
	 *            [required] target user
	 * @return true:exist false:not exist
	 */
	boolean hasUser(String userId);

	/**
	 * add user to storage
	 * 
	 * @param userId
	 *            [required] target userId
	 * @return added user
	 */
	User addUser(String userId);

	/**
	 * delete user from storage
	 * 
	 * @param userId
	 *            [required] target userId
	 */
	void deleteUser(String userId);

	/**
	 * delete all users from storage.<br>
	 * This method is TESTING PURPOSE only.
	 */
	void deleteAllUsers();

	/**
	 * export users [CSV format] from storage
	 * 
	 * @param exportFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean exportUsers(File exportFile);

	/**
	 * import users [CSV format] to storage
	 * 
	 * @param importFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean importUsers(File importFile);

	/**
	 * get using user information.
	 * 
	 * @return current user
	 */
	User getCurrentUser();

	/**
	 * change using user information.
	 * 
	 * @param user
	 *            [required] change user
	 */
	void changeCurrentUser(User user);

	// Icon Section
	/**
	 * find all icons from storage
	 * 
	 * @return found icons
	 */
	Icon[] findAllIcons();

	/**
	 * add icon to storage
	 * 
	 * @param type
	 *            [required] icon type ex. class name
	 * @param x
	 *            [required] location x
	 * @param y
	 *            [required] location y
	 * @return added icon
	 */
	Icon addIcon(String type, int x, int y);

	/**
	 * delete all icons from storage.<br>
	 * This method is TESTING PURPOSE only.
	 */
	void deleteAllIcons();

	/**
	 * export icons [CSV format] from storage
	 * 
	 * @param exportFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
	boolean exportIcons(File exportFile);

	/**
	 * import icons [CSV format] to storage
	 * 
	 * @param importFile
	 *            target file
	 * @return operation is success:true failed:false
	 */
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

	void delete(Entity entity);

	boolean isTestMode();

}
