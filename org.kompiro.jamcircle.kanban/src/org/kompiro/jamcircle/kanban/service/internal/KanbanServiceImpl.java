package org.kompiro.jamcircle.kanban.service.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.*;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.*;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
import org.kompiro.jamcircle.storage.service.StorageChageListener;
import org.kompiro.jamcircle.storage.service.StorageService;


/**
 * @TestContext org.kompiro.jamcircle.kanban.service.internal.KanbanServiceImplTest
 */
public class KanbanServiceImpl implements KanbanService,StorageChageListener {
	
	private static final int ICON_SIZE_Y = 74;
	private static final String INBOX_ICON_MODEL = "org.kompiro.jamcircle.kanban.ui.model.InboxIconModel";
	private static final String BOARD_SELECTER_MODEL = "org.kompiro.jamcircle.kanban.ui.model.BoardSelecterModel";
	private static final String LANE_CREATER_MODEL = "org.kompiro.jamcircle.kanban.ui.model.LaneCreaterModel";
	private static final String MODEL_TRASH_MODEL = "org.kompiro.jamcircle.kanban.ui.model.TrashModel";

	private List<KanbanBoardTemplate> templates = new ArrayList<KanbanBoardTemplate>();
	private boolean initialized;
	private Object lock = new Object();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private StorageService storageService;
	
	public KanbanServiceImpl() {
		initializeTemplate();
	}

	private void initializeTemplate() {
		templates.add(new NoLaneBoardTemplate());
		templates.add(new TaskBoardTemplate());
		templates.add(new ColorBoardTemplate());
	}
	
	public void init(){
		if(initialized) return;
		synchronized (lock) {
			if(initialized) return;
			KanbanMigrator migrator = new KanbanMigrator(this);
			migrator.migrate();
			try {
				int boardCount = 0;
				try {
					boardCount = getEntityManager().count(Board.class);
				} catch (UndeclaredThrowableException e) {
					KanbanStatusHandler.fail(e, "KanbanServiceImpl#init",true);
					return;
				}
				if(boardCount == 0){
					Board board = createBoard();
					KanbanBoardTemplate initializer = new TaskBoardTemplate();
					initializer.initialize(board);
				}
			} catch (SQLException e) {
				KanbanStatusHandler.fail(e, "KanbanServiceImpl#init",true);
				return;
			}
			Icon[] icons = findIcons();
			if(icons.length == 0){
				addIcon(INBOX_ICON_MODEL,0,ICON_SIZE_Y * 0);
				addIcon(BOARD_SELECTER_MODEL,0,ICON_SIZE_Y * 1);
				addIcon(LANE_CREATER_MODEL,0,ICON_SIZE_Y * 2);
				addIcon(MODEL_TRASH_MODEL,0,ICON_SIZE_Y * 3);
			}
			initialized = true;
		}
	}

	private Board createBoard() {
		return createBoard("Sample Board");
	}

	public Card createClonedCard(Board board,User user,Card card,int x,int y){
		Card cloned;
		if(getEntityManager() == null){
			cloned = createMockCard(user, card,x,y);
		}else{
			String content = card.getContent();
			String subject = card.getSubject();
			String uuid = card.getUUID();
			cloned = createCard(board,subject,user, content,x,y,uuid);
		}
		return cloned;
	}
	
	private Card createCard(Board board,String subject,User user,String content,int locationX,int locationY,String uuid) {
		List<DBParam> params = new ArrayList<DBParam>();
		if(subject != null){
			params.add(new DBParam(Card.PROP_SUBJECT,subject));
		}
		if(content != null){
			params.add(new DBParam(Card.PROP_CONTENT,content));
		}
		if(user != null){
			if(user.getUserName() != null){
				params.add(new DBParam(Card.PROP_CREATED, user.getUserName()));
			}else{
				params.add(new DBParam(Card.PROP_CREATED, user.getUserId()));
			}
			params.add(new DBParam(Card.PROP_OWNER, user));
		}
		params.add(new DBParam(Card.PROP_CREATEDATE, new Date()));
		params.add(new DBParam(Card.PROP_TRASHED, false));
		params.add(new DBParam(Card.PROP_LOCATION_X,locationX));
		params.add(new DBParam(Card.PROP_LOCATION_Y,locationY));
		if(uuid != null){
			params.add(new DBParam(Card.PROP_UUID,uuid));
		}
		if(board != null){
			params.add(new DBParam(Card.PROP_BOARD,board ));
		}
		Card card = null;
		try{
			card = getEntityManager().create(Card.class,params.toArray(new DBParam[]{}));
			card = getEntityManager().find(Card.class,Card.PROP_ID + " = ?", card.getID())[0];
		} catch (SQLException e) {
			StringBuilder paramMessage = new StringBuilder(); 
			for (DBParam param : params){
				paramMessage.append(String.format("[%s:'%s'] ", param.getField(),param.getValue()));
			}
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#createCard() {%s}",paramMessage);
		}
		firePropertyChange(Card.class.getSimpleName(), null, card);
		return card;
	}

	private Card createMockCard(User user,Card card, int locationX, int locationY) {
		String content = card.getContent();
		String subject = card.getSubject();
		String created = card.getCreated();
		Card mock = new org.kompiro.jamcircle.kanban.model.mock.Card();
		mock.setSubject(subject);
		mock.setContent(content);
		mock.setCreated(created);
		mock.setCreateDate(new Date());
		mock.setDeletedVisuals(false);
		mock.setOwner(user);
		mock.setX(locationX);
		mock.setY(locationY);
		mock.setTrashed(false);
		return mock;
	}

	public void trashCard(Card card) {
		card.setTrashed(true);
		card.save();
	}
	
	
	public int countCards() {
		try {
			return getEntityManager().count(Card.class,Card.PROP_TRASHED + " = true");
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"Trash:0001:can't count cards.");
			return 0;
		}
	}
	
	public Lane createLane(Board board,String status, int x, int y,
			int width, int height) {
		DBParam[] params = new DBParam[]{
				new DBParam(Lane.PROP_STATUS,status),
				new DBParam(Lane.PROP_BOARD,board),
				new DBParam(Lane.PROP_LOCATION_X,x),
				new DBParam(Lane.PROP_LOCATION_Y,y),
				new DBParam(Lane.PROP_CREATEDATE,new Date()),
				new DBParam(Lane.PROP_WIDTH,width),
				new DBParam(Lane.PROP_HEIGHT,height)
		};
		Lane lane = null;
		try {
			lane = getEntityManager().create(Lane.class, params);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#createLane()");
		}
		firePropertyChange(Lane.class.getSimpleName(), null, lane);
		return lane;
	}

	public Card createCard(Board board,String summary, User user, int locationX, int locationY) {
		return createCard(board, summary, user, null, locationX, locationY,UUID.randomUUID().toString());
	}

	public void deleteAllCards() {
		getStorageService().deleteAllEntity(Card.class);
	}

	public Card[] findAllCards(){
		try {
			return getEntityManager().find(Card.class);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findAllCards()");
		}
		return null;
	}
	
	public Card[] findCards(String criteria, Object... parameters) {
		try {
			return getEntityManager().find(Card.class,criteria,parameters);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findCards()");
		}
		return null;
	}
	
	public Card[] findCardsOnBoard(Board board){
		Card[] cards = null;
		try {
			cards = getEntityManager().find(Card.class,
					Card.PROP_LANE + " is null and " +
					Card.PROP_BOARD + " = ? and " +
					Card.PROP_TRASHED + " = false and " +
					Card.PROP_TO + " is null",board.getID());
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"KanbanServiceImpl#findCardsOnBoard()");
		}
		return cards;
	}

	public Card[] findCardsOnLane(Lane lane){
		Card[] cards = null;
		try {
			cards = getEntityManager().find(Card.class,
					Card.PROP_LANE + " = ? and " +
					Card.PROP_BOARD + " = ? and " +
					Card.PROP_TRASHED + " = false and " +
					Card.PROP_TO + " is null", lane.getID(),lane.getBoard().getID());
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"KanbanServiceImpl#findCardsOnLane()");
		}
		return cards;
	}

	
	public Lane[] findAllLanes() {
		try {
			return getEntityManager().find(Lane.class,Lane.PROP_TRASHED + " = ?",false);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findLanesOnBoard()");
		} 
		return null;
	}

	public Lane[] findLanesInTrash() {
		try {
			return getEntityManager().find(Lane.class,Lane.PROP_TRASHED + " = ?",true);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findLanesInTrash()");
		} 
		return null;
	}

	public Lane[] findLanesOnBoard(Board board) {
		try {
			return getEntityManager().find(Lane.class,
					Lane.PROP_TRASHED + " = ? and " +
					Lane.PROP_BOARD + " = ?",
					false,board.getID());
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findLanesOnBoard() %d",board.getID());
		} 
		return null;
	}

	
	public User[] findUsersOnBoard() {
		try {
			return getEntityManager().find(User.class,User.PROP_TRASHED + " = ?" ,false);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findUsersOnBoard()");
		}
		return null;
	}
	
	public User findUser(String userId) {
		try {
			User[] users = getEntityManager().find(User.class,User.PROP_USERID + " = ? and " + 
					User.PROP_TRASHED + " = ?",userId,false);
			if(users.length != 1){
				String message = String.format("Illegal data state User '%s' length:%d",userId,users.length);
				throw new IllegalStateException(message);
			}
			return users[0];
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findFromUser() %s",userId);
		}
		return null;
	}

	public Card createReceiveCard(Board board,CardDTO dto, User current, User fromUser) {
		Card card = createCard(board,dto.getSubject(),null,dto.getX(),dto.getY());
		card.setUUID(dto.getUUID());
		card.setX(dto.getX());
		card.setY(dto.getY());
		card.setContent(dto.getContent());
		card.setSubject(dto.getSubject());
		card.setOwner(current);
		card.setCreated(dto.getCreated());
		card.setFrom(fromUser);
		card.save();
		return card;
	}

	public Card[] findCardsSentTo(User sentTo) {
		if(sentTo == null) return new Card[]{};
		try {
			return getEntityManager().find(Card.class,Card.PROP_TO + " = ?" , sentTo.getID());
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findCardSentTo() '%s'",sentTo);
		}
		return new Card[]{};
	}

	public User addUser(String userId) {
		DBParam[] params = new DBParam[]{
				new DBParam(User.PROP_USERID, userId)
		};
		User user = (User)getStorageService().createEntity(User.class, params);
		firePropertyChange(User.class.getSimpleName(), null, user);
		return user;
	}

	public void deleteUser(String userId) {
		User user,oldUser;
		user = findUser(userId);
		oldUser = user;
		user.setTrashed(true);
		user.save();
		firePropertyChange(User.class.getSimpleName(), oldUser, null);
	}

	public User[] findAllUsers() {
		try {
			return getEntityManager().find(User.class,User.PROP_TRASHED + " = ?",false);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findAllUsers() ");
			return null;
		}
	}

	public boolean hasUser(String user) {
		try {
			return getEntityManager().count(User.class, User.PROP_USERID + " = ? and " + User.PROP_TRASHED + " = ?" ,user,false) != 0;
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#hasUser() '%s'",user);
			return false;
		}
	}
	
	public Board findBoard(int id){
		try {
			return getEntityManager().find(Board.class, Board.PROP_TRASHED + " = ? and " + Board.PROP_ID + " = ?",false,id)[0];
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findBoard() id='%d'",id);
		}
		return null;		
	}

	public Board[] findAllBoard() {
		try {
			return getEntityManager().find(Board.class, Board.PROP_TRASHED + " = ?",false);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#findAllBoard()");
		}
		return null;
	}

	public void deleteAllBoards() {
		getStorageService().deleteAllEntity(Board.class);
	}

	public void deleteAllLanes() {
		getStorageService().deleteAllEntity(Lane.class);
	}

	public void deleteAllUsers() {
		try {
			User[] entities =  getEntityManager().find(User.class);
			getEntityManager().delete(entities);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#deleteAllUsers()");			
		}
	}

	public Board createBoard(String title) {
		Board board = null;
		DBParam[] params = new DBParam[]{
				new DBParam(Board.PROP_TITLE,title),
				new DBParam(Board.PROP_CREATE_DATE,new Date()),
		};
		try{
			board = getEntityManager().create(Board.class,params);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#createBoard()");			
		}
		firePropertyChange(Board.class.getSimpleName(), null, board);
		return board;
	}

	public boolean exportCards(File exportFile) {
		return getStorageService().exportEntity(exportFile, Card.class);
	}

	public boolean exportBoards(File exportFile) {
		return getStorageService().exportEntity(exportFile, Board.class);
	}

	public boolean exportLanes(File exportFile) {
		return getStorageService().exportEntity(exportFile, Lane.class);
	}

	public boolean exportUsers(File exportFile) {
		return getStorageService().exportEntity(exportFile, User.class);
	}

	public boolean importCards(File importFile) {
		return getStorageService().importEntity(importFile, Card.class);
	}

	public boolean importBoards(File importFile) {
		return getStorageService().importEntity(importFile, Board.class);
	}

	public boolean importLanes(File importFile) {
		return getStorageService().importEntity(importFile, Lane.class);
	}

	public boolean importUsers(File importFile) {
		return getStorageService().importEntity(importFile, User.class);
	}

	public Icon addIcon(String type,int x,int y) {
		Icon icon = null;
		DBParam[] params = new DBParam[]{
				new DBParam(Icon.PROP_TYPE,type),
				new DBParam(Icon.PROP_LOCATION_X,x),
				new DBParam(Icon.PROP_LOCATION_Y,y)
		};
		try {
			getEntityManager().create(Icon.class, params);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#addIcon() type:'%s' x:'%d' y:'%d' ",type,x,y);
		}
		return icon;
	}

	public void deleteAllIcons() {
		getStorageService().deleteAllEntity(Icon.class);
	}

	public Icon[] findIcons() {
		Icon[] icons = null;
		try {
			icons = getEntityManager().find(Icon.class,Icon.PROP_TRASHED + " = ?", false);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"KanbanServiceImpl#findIcons()",true);
		}
		return icons;
	}
	
	public boolean exportIcons(File exportFile) {
		return getStorageService().exportEntity(exportFile, Icon.class);
	}

	public boolean importIcons(File importFile) {
		return getStorageService().importEntity(importFile, Icon.class);
	}
	
	public Card[] cardsInTrash() {
		return null;
	}

	public int countInTrash(Class<? extends GraphicalEntity> clazz) {
		GraphicalEntity[] results = null;
		try {
			results = getEntityManager().find(clazz,GraphicalEntity.PROP_TRASHED + " = ?",true);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"KanbanServiceImpl#countInTrash()",true);
		}
		if(results == null) return 0;
		return results.length;
	}

	public KanbanBoardTemplate[] getKanbanDataInitializers() {
		return this.templates.toArray(new KanbanBoardTemplate[]{});
	}

	public void addStorageChangeListener(StorageChageListener listener) {
		getStorageService().addStorageChangeListener(listener);
	}

	public void removeStorageChangeListener(StorageChageListener listener) {
		getStorageService().removeStorageChangeListener(listener);
	}

	public void changedStorage(IProgressMonitor monitor) {
		initialized = false;
		monitor.setTaskName("Initialize Database Connection...");
		init();
		monitor.internalWorked(15);
	}

	public void dispose() {
		getStorageService().removeStorageChangeListener(this);
	}

	public Integer getPriority() {
		return 0;
	}

	void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	EntityManager getEntityManager(){
		return getStorageService().getEntityManager();
	}

	public void setStorageService(StorageService storageService) {
		this.storageService = storageService;
	}
	
	private StorageService getStorageService() {
		return this.storageService;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void firePropertyChange(String propName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

}
