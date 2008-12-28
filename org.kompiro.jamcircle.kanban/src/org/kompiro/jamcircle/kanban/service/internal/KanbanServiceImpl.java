package org.kompiro.jamcircle.kanban.service.internal;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.KanbanStatusHandler;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.ColorBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.NoLaneBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.TaskBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardDTO;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.storage.service.StorageChageListener;
import org.kompiro.jamcircle.storage.service.StorageService;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;


public class KanbanServiceImpl implements KanbanService,StorageChageListener {
	
	private KanbanActivator activator;
	private List<KanbanBoardTemplate> templates = new ArrayList<KanbanBoardTemplate>();
	private boolean initialized;
	private Object lock = new Object();
	
	public KanbanServiceImpl(KanbanActivator activator) {
		this.activator = activator; 
		initializeTemplate();
		assert getStorageService() == null;
		getStorageService().addStorageChangeListener(this);
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
				if(getEntityManager().count(Board.class) == 0){
					Board board = createBoard();
					KanbanBoardTemplate initializer = new TaskBoardTemplate();
					initializer.initialize(board);
				}
			} catch (SQLException e) {
				KanbanStatusHandler.fail(e, "KanbanServiceImpl#init",true);
			}
			Icon[] icons = findIcons();
			if(icons.length == 0){
				addIcon("org.kompiro.jamcircle.kanban.ui.model.InboxIconModel",0,74 * 0);
				addIcon("org.kompiro.jamcircle.kanban.ui.model.BoardSelecterModel",0,74 * 1);
				addIcon("org.kompiro.jamcircle.kanban.ui.model.LaneCreaterModel",0,74 * 2);
				addIcon("org.kompiro.jamcircle.kanban.ui.model.TrashModel",0,74 * 3);
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
			params.add(new DBParam(Card.PROP_CREATED, user));
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


	public int countCards() {
		try {
			return getEntityManager().count(Card.class,Card.PROP_TRASHED + " = true");
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e,"Trash:0001:can't count cards.");
			return 0;
		}
	}
	
	EntityManager getEntityManager(){
		return getStorageService().getEntityManager();
	}

	private StorageService getStorageService() {
		return activator.getStorageService();
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
		DBParam param = new DBParam(User.PROP_USERID, userId);
		User user = null;
		try {
			user = getEntityManager().create(User.class, param);
		} catch (SQLException e) {
			KanbanStatusHandler.fail(e, "KanbanServiceImpl#addUser() '%s'",userId);
		}
		return user;
	}

	public void deleteUser(String userId) {
		User user = findUser(userId);
		user.setTrashed(true);
		user.save();
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

}
