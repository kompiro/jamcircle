package org.kompiro.jamcircle.kanban.ui;

import java.util.*;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.eclipse.gef.commands.*;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.action.*;
import org.kompiro.jamcircle.kanban.ui.internal.command.RemoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.*;
import org.kompiro.jamcircle.kanban.ui.model.*;
import org.kompiro.jamcircle.kanban.ui.util.GraphicalUtil;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;
import org.kompiro.jamcircle.kanban.ui.widget.CardObjectTransfer;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer.CardWrapper;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;
import org.kompiro.jamcircle.storage.service.StorageChageListener;

public class KanbanView extends ViewPart implements StorageChageListener{

	public static String ID = "org.kompiro.jamcircle.kanban.KanbanView";
	public static final String PROP_MESSAGE_MODEL = "model";

	private ScrollingGraphicalViewer viewer;
////	private UserModifiedListener userChangeListener;

	private SelectionSynchronizer synchronizer;
	private OpenCardListAction openCardListAction;
	private IAction openCommandListAction;
	private BoardModel boardModel;

////	private ConnectionListener connectionListener = new ConnectionListener(){
////		
////		public void connectionClosed() {
////			refreshXmppConnectionStatus();
////		}
////		
////		public void connectionClosedOnError(final Exception e) {
////			getDisplay().asyncExec(new Runnable(){
////				public void run() {
////					KanbanUIStatusHandler.info("exception is occured when XMPP connection closing.",e);
////				};
////			});
////		}
////		
////		public void reconnectingIn(int seconds) {
////		}
////		
////		public void reconnectionFailed(Exception e) {
////		}
////		
////		public void reconnectionSuccessful() {
////			refreshXmppConnectionStatus();
////		}
////	};
//	
	private static final Conditional cardCond = new Conditional(){
		public boolean evaluate(EditPart editpart) {
			boolean isCardEditPart = editpart instanceof CardEditPart;
			return ! isCardEditPart;
		}
	};

////	private RosterListener rosterListener;
////	private CardReceiveListener cardSendListener = new CardReceiveListener();
	private RedoAction redoHandler;
	private UndoAction undoHandler;
	private HandlerListener handlerListener;
	private DeleteAction deleteHandler;
	private ISelectionChangedListener selectionListenerForHandler = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
			deleteHandler.update();
			copyHandler.update();
			cutHandler.update();
			openCardListAction.update();
		}
	};
	private SelectAllAction selectAllCardHandler;
	private PasteAction pasteHandler;
	private CopyAction copyHandler;
	private CutAction cutHandler;
	private CellEditorActionHandler handlers;
////	private CardReceiveFileTransferListener cardReceiveFileTransferListener;
	private IconModelFactory iconModelFactory;
	private CaptureBoardAction caputureBoardAction;
	private ScalableRootEditPart rootPart;
	private ZoomInAction zoomInAction;
	private ZoomOutAction zoomOutAction;


	public KanbanView() {
////		if(getConnectionService() != null){
////			getConnectionService().addXMPPLoginListener(this);
////		}
		if(getKanbanService() != null){
			getKanbanService().addStorageChangeListener(this);
			iconModelFactory = new DefaultIconModelFactory(getKanbanService());
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new ScrollingGraphicalViewer();
		rootPart = new ScalableRootEditPart();
		getGraphicalViewer().setRootEditPart(rootPart);
		handlers = new CellEditorActionHandler(getActionBars());
		EditDomain domain = new EditDomain();
		domain.setCommandStack(new CommandStackImpl());
		domain.addViewer(getGraphicalViewer());
		handlerListener = new HandlerListener();
		getCommandStack().addCommandStackListener(handlerListener);
		getGraphicalViewer().createControl(parent);
		makeActions();
		contributeToActionBars(); // this method is empty now.
		hookContextMenu();
		getSite().setSelectionProvider(getGraphicalViewer());
		Transfer[] types = new Transfer[] {CardObjectTransfer.getTransfer(),FileTransfer.getInstance()};
		DropTarget target = new DropTarget(getGraphicalViewer().getControl(),CardListTableViewer.OPERATIONS);
		target.setTransfer(types);
		target.addDropListener(new KanbanViewDropAdapter());
		hookGraphicalViewer();
		setInintialContents();
	}
	
		
	private void makeActions() {
		undoHandler = new UndoAction(this);
		handlers.setUndoAction(undoHandler);

		redoHandler = new RedoAction(this);
		handlers.setRedoAction(redoHandler);
		
		deleteHandler = new DeleteAction(this);
		deleteHandler.setSelectionProvider(getGraphicalViewer());
		handlers.setDeleteAction(deleteHandler);
		
		selectAllCardHandler = new SelectAllAction(this){
			@Override
			public void run() {
				if(getGraphicalViewer() != null){
					BoardEditPart boardEditPart = (BoardEditPart)(getGraphicalViewer().getContents());
					List<CardEditPart> cards = boardEditPart.getCardChildren();
					StructuredSelection selection = new StructuredSelection(cards);
					getGraphicalViewer().setSelection(selection);
				}
			}
		};
		handlers.setSelectAllAction(selectAllCardHandler);

		cutHandler = new CutAction(this);
		cutHandler.setSelectionProvider(getGraphicalViewer());
		handlers.setCutAction(cutHandler);
		
		pasteHandler = new PasteAction(this);
		handlers.setPasteAction(pasteHandler);
		
		copyHandler = new CopyAction(this);
		copyHandler.setSelectionProvider(getGraphicalViewer());
		handlers.setCopyAction(copyHandler);
		
		openCardListAction = new OpenCardListAction(this);
		openCommandListAction = new OpenCommandListAction(this);
		
		caputureBoardAction = new CaptureBoardAction(this);
		zoomInAction = new ZoomInAction(rootPart.getZoomManager());
		grobalActivateAction(zoomInAction);
		zoomOutAction = new ZoomOutAction(rootPart.getZoomManager());
		grobalActivateAction(zoomOutAction);
		
		getGraphicalViewer().addSelectionChangedListener(selectionListenerForHandler);
	}
	private void grobalActivateAction(IAction action) {
		getActionBars().setGlobalActionHandler(action.getId(), action);
	}
	
	private IActionBars getActionBars() {
		return getViewSite().getActionBars();
	}

	private void contributeToActionBars() {
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				KanbanView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getGraphicalViewer().getControl());
		getGraphicalViewer().getControl().setMenu(menu);
		getViewSite().registerContextMenu(menuMgr, getGraphicalViewer());
	}

	private void fillContextMenu(IMenuManager menuManager) {
		if(getGraphicalViewer().getProperty(BoardDragTracker.PROPERTY_DRAG_TO_MOVE_VIEWPOINT) == null){
			menuManager.add(caputureBoardAction);
			menuManager.add(zoomInAction);
			menuManager.add(zoomOutAction);
			menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			menuManager.add(selectAllCardHandler);
			menuManager.add(undoHandler);
			menuManager.add(redoHandler);
			deleteHandler.update();
			menuManager.add(deleteHandler);
			cutHandler.update();
			menuManager.add(cutHandler);
			copyHandler.update();
			menuManager.add(copyHandler);
			menuManager.add(pasteHandler);
			menuManager.add(openCardListAction);
			menuManager.add(openCommandListAction);
			menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			
		}
	}
//
//	private void fillLocalStatusLine(IStatusLineManager manager) {
//		Image image;
//		String message;
////		if(getConnectionService().isConnecting()){
////			image = getImageRegistry().get(KanbanImageConstants.CONNECT_IMAGE.toString());
////			XMPPConnection connection = getConnectionService().getConnection();
////			message = connection.getUser();
////			manager.setErrorMessage(null);
////			manager.setMessage(image,message);
////		}else{
//			image= getImageRegistry().get(KanbanImageConstants.DISCONNECT_IMAGE.toString());
//			message = "Doesn't logged in.";			
//			manager.setErrorMessage(image,message);
////		}
//	}
//
//	private ImageRegistry getImageRegistry() {
//		return getActivator().getImageRegistry();
//	}
//
////	private XMPPConnectionService getConnectionService() {
////		if( ! Platform.isRunning()) return null;
////		return getActivator().getConnectionService();
////	}
//
//
	private void setInintialContents() {
		if(Platform.isRunning()){
			storageInitialize();
		}
	}
	
	public void setContents(Board board,final IProgressMonitor monitor) {
////		if(userChangeListener != null){
////			getKanbanService().removePropertyChangeListener(userChangeListener);
////		}
		if(boardModel != null){
			board.removePropertyChangeListener(boardModel);
		}
		boardModel = new BoardModel(board);
		board.addPropertyChangeListener(boardModel);
		
////		userChangeListener = new UserModifiedListener();
////		getKanbanService().addPropertyChangeListener(userChangeListener);
		EditPartFactory factory = new KanbanControllerFactory(this.boardModel);
		
		String taskName = String.format("Openning board '%s' ...",board.getTitle()); 
		monitor.subTask(taskName);
		getGraphicalViewer().setEditPartFactory(factory);
		
		monitor.internalWorked(1);
		refreshIcons();
		monitor.internalWorked(1);
		
		final int id = board.getID();
		getGraphicalViewer().setContents(boardModel);
		new Job("execute script on board"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				executeScript(monitor);
				return Status.OK_STATUS;
			}
		}.schedule();
		storeCurrentBoard(id);
	}

	private void executeScript(final IProgressMonitor monitor) {
		Board board = boardModel.getBoard();
		String script = board.getScript();
		if(script != null && script.length() != 0){
			monitor.setTaskName("execute script");
			boardModel.clearMocks();

			String scriptName = String.format("Board '%s' Script",board.getTitle());

			Map<String,Object> beans= new HashMap<String, Object>();
			beans.put("board", this.boardModel);
			beans.put("monitor", monitor);
			try {
				ScriptingService service = getScriptingService();
				service.eval(board.getScriptType(), scriptName, script,beans);
			} catch (ScriptingException e) {
				KanbanUIStatusHandler.fail(e, e.getMessage());
			}
		}
	}

	private void storeCurrentBoard(int id) {
		KanbanUIStatusHandler.debugUI("KanbanView#storeCurrentBoard() id='%d'", id);
		KanbanUIActivator activator = getActivator();
		if(activator != null){
			getPreference().putInt(KanbanPreferenceConstants.BOARD_ID.toString(), id);
		}
	}

	private void refreshIcons() {
		KanbanService service = getKanbanService();
		Icon[] icons = service.findIcons();
		for(Icon icon : icons){
			IconModel model = iconModelFactory.create(icon);
			this.boardModel.addIcon(model);
		}
	}

	private void storageInitialize() {
		UIJob job = new UIJob("storage initialize"){
		
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				monitor.subTask("storage initializing...");
				KanbanService service = getKanbanService();
				int id = getPreference().getInt(KanbanPreferenceConstants.BOARD_ID.toString(),1);
				KanbanUIStatusHandler.debugUI("KanbanView#storageInitialize() id:'%d'", id);
				Board board = service.findBoard(id);
				if(board == null){
					board = service.findBoard(1);
				}
				setContents(board,monitor);
				monitor.internalWorked(30.0);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
////	private void refreshXmppConnectionStatus() {
////		Display display = getDisplay();
////		if(display == null) return;
////		display.asyncExec(new Runnable(){
////			public void run() {
////				fillLocalStatusLine(getActionBars().getStatusLineManager());
////			}
////		});
////
////		XMPPConnection connection = getConnectionService().getConnection();
////		if(!getConnectionService().isConnecting()){
////			display.asyncExec(new Runnable(){
////				public void run() {
////					boardModel.clearUsers();
////				}
////			});
////			return;
////		}
////		connection.addConnectionListener(connectionListener);
////		final Roster roster = connection.getRoster();
////		rosterListener = new RosterListnerForUsers(roster);
////		roster.addRosterListener(rosterListener);
////		connection.getChatManager().addChatListener(cardSendListener);
////		final Map<String,User> userMap = new HashMap<String, User>();
////		User[] userList = getKanbanService().findUsersOnBoard();
////		for(User user : userList){
////			String key = user.getUserId();
////			if(!userMap.containsKey(key)){
////				userMap.put(key, user);
////			}
////		}
////		display.asyncExec(new Runnable(){
////			public void run() {
////				if(boardModel == null) return;
////				if(boardModel.sizeUsers() != 0){
////					boardModel.clearUsers();
////				}
////				for(User user:userMap.values()){
////					Presence presence = roster.getPresence(user.getUserId());
////					UserModel userModel = new UserModel(roster.getEntry(user.getUserId()),presence,user);
////					presence.isAvailable();
////					boardModel.addUser(userModel);
////				}
////			}
////			});
////		setCardReceiveFileTransferManager();
////	}
////
////	private void setCardReceiveFileTransferManager() {
////		FileTransferManager manager = getConnectionService().getFileTransferManager();
////		manager.removeFileTransferListener(cardReceiveFileTransferListener);
////		cardReceiveFileTransferListener = new CardReceiveFileTransferListener();
////		manager.addFileTransferListener(cardReceiveFileTransferListener);
////	}

	@Override
	public void setFocus() {
	}
	
	private void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
		getSite().setSelectionProvider(getGraphicalViewer());
	}

	private ScrollingGraphicalViewer getGraphicalViewer() {
		return viewer;
	}

	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (synchronizer == null)
			synchronizer = new SelectionSynchronizer();
		return synchronizer;
	}

////	private final class UserModifiedListener implements
////			PropertyChangeListener {
////		public void propertyChange(PropertyChangeEvent evt) {
////			if(User.class.getSimpleName().equals(evt.getPropertyName())){
////				refreshXmppConnectionStatus();
////			}
////		}
////	}

	private final class CommandStackImpl extends CommandStack {
				
		@Override
		public void execute(final Command command) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					BusyIndicator.showWhile(getDisplay(), new Runnable() {
						public void run() {
							CommandStackImpl.super.execute(command);
						}
					});
				}
			});
		}
	}
//
////	private final class CardReceiveFileTransferListener implements
////			FileTransferListener {
////		public void fileTransferRequest(FileTransferRequest request) {
////			
////			String uuid = request.getDescription();
////			IncomingFileTransfer accept = request.accept();
////			try {
////				File tmpFile = new File(System.getProperty("java.io.tmpdir"),request.getFileName());
////				if(tmpFile.exists()){
////					tmpFile.delete();
////				}
////				accept.recieveFile(tmpFile);
////				while(!accept.isDone()){
////				}
////				Card tmpCard = getCard(uuid);
////				int time = 0;
////				while(tmpCard == null){
////					try {
////						if(time > 3){
////							throw new RuntimeException("can't get card data.");
////						}
////						Thread.sleep(2000);
////					} catch (InterruptedException e) {
////					}
////					tmpCard = getCard(uuid);
////					time++;
////				}
////				final Card card = tmpCard;
////				final File file = tmpFile;
////				getDisplay().asyncExec(new Runnable(){
////					public void run() {
////						card.addFile(file);
////						card.save();
////					}
////				});
////			} catch (XMPPException e) {
////				KanbanUIStatusHandler.fail(e, "error has occured.");
////				request.reject();
////			}
////		}
//
//		private Card getCard(String uuid) {
//			return getKanbanService().findCards(
//					Card.PROP_TRASHED + " = ? and" +
//					Card.PROP_UUID + " = ? and " +
//					Card.PROP_TO + " is null",false,uuid)[0];
//		}
//	}
//
	private final class KanbanViewDropAdapter extends DropTargetAdapter {

		public void drop(DropTargetEvent event) {
			KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop widget[%s] x:%d y:%d",event.widget,event.x,event.y);
			Point location = new Point(
					getGraphicalViewer().getControl().toControl(
						Display.getCurrent().getCursorLocation()));
			Point viewLocation = getViewport().getViewLocation();
			location.translate(viewLocation);
			Object data = event.data;
			EditPart dropTarget = getGraphicalViewer().findObjectAtExcluding(location, Collections.EMPTY_SET,cardCond);
			KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop target:%s data: %s",dropTarget,data);
			if (data instanceof List<?>) {
				List<?> list = (List<?>) data;
				CompoundCommand command = new CompoundCommand();
				for(Object obj : list){
					if (obj instanceof CardWrapper) {
						CardWrapper wrapper = (CardWrapper) obj;
						Card card = wrapper.getCard();
						KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop %s", card);
						command.add(removeCardFromParent(card,wrapper.getContainer()));
						command.add(addCardFromDialog(dropTarget, location, card));
					}
				}
				getCommandStack().execute(command);						
			}else if(data instanceof String[]){
				for(String fileName : (String[])data){
					CardCreateRequest request = new CardCreateRequest(getKanbanService(),KanbanView.this.boardModel.getBoard(),fileName);
					request.setType(RequestConstants.REQ_CREATE);
					request.setLocation(location);
					Command command = dropTarget.getCommand(request);
					getCommandStack().execute(command);						
				}
			}
		}

		private Command removeCardFromParent(Card card,CardContainer container) {
			return new RemoveCardCommand(card,container);
		}

		private Command addCardFromDialog(EditPart dropTargetEditPart,
				Point location, Card card) {
			Rectangle rect = new Rectangle();
			rect.setLocation(location);
			ChangeBoundsRequest request = new ChangeBoundsRequest();
			request.setType(RequestConstants.REQ_ADD);
			request.setEditParts(getGraphicalViewer().getEditPartFactory().createEditPart(null, card));
			request.setMoveDelta(location.getTranslated(GraphicalUtil.currentLocation(card).negate()));
			return dropTargetEditPart.getCommand(request);
		}

		private Viewport getViewport() {
			GraphicalEditPart rootEditPart = (GraphicalEditPart) getGraphicalViewer()
					.getRootEditPart();
			Viewport port = (Viewport) rootEditPart.getFigure();
			return port;
		}
	}

	private final class HandlerListener implements
			CommandStackListener {
		public void commandStackChanged(EventObject event) {
			undoHandler.update();
			redoHandler.update();
		}
	}

////	private final class CardReceiveListener implements
////			ChatManagerListener {
////		public void chatCreated(Chat chat, boolean createdLocally) {
////			chat.addMessageListener(new MessageListener(){
////				public void processMessage(Chat chat, Message message) {
////					Object obj = message.getProperty(KanbanView.PROP_MESSAGE_MODEL);
////					String fromUserId = chat.getParticipant();
////					if (obj instanceof CardDTO) {
////						CreateCardCommand command = new CreateCardCommand();
////						BoardModel boardModel = (BoardModel) getGraphicalViewer().getContents().getModel();
////						command.setContainer(boardModel);
////						final CardDTO dto = (CardDTO) obj;
////						User fromUser = null;
////						if(fromUserId != null){
////							fromUser = getKanbanService().findUser(fromUserId);
////						}
////						Card card = createCard(dto, fromUser);
////						card.setDeletedVisuals(false);
////						command.setModel(card);
////						getViewSite().getShell().getDisplay().asyncExec(new CreateCommandRunnable(command));
////					}
////				}
////
////				private Card createCard(CardDTO dto, User fromUser){
////					KanbanService service = getKanbanService();
////					return service.createReceiveCard(boardModel.getBoard(),dto,getUser(),fromUser);
////				}
////			});
////		}
////	}
////	
////	private final class RosterListnerForUsers implements RosterListener {
////		
////		public RosterListnerForUsers(Roster roster) {
////		}
////
////		public void entriesAdded(Collection<String> addresses) {
////		}
////
////		public void entriesDeleted(Collection<String> addresses) {
////		}
////
////		public void entriesUpdated(Collection<String> addresses) {
////		}
////
////		public void presenceChanged(final Presence presence) {
////			if(boardModel == null) return;
////			Runnable runnable = new Runnable(){
////				public void run() {
////					String from = presence.getFrom();
////					from = XMPPUtil.getRemovedResourceUser(from);
////					UserModel user = boardModel.getUser(from);
////					if(user != null) {
////						user.setPresence(presence);					
////					}
////				}
////
////			};
////			getDisplay().asyncExec(runnable);
////		}
////	}
////
////	private final class CreateCommandRunnable implements Runnable {
////		private CreateCardCommand command;
////
////		private CreateCommandRunnable(CreateCardCommand command) {
////			this.command = command;
////		}
////
////		public void run() {
////			getGraphicalViewer().getEditDomain().getCommandStack().execute(command);
////		}
////	}
////	
	@Override
	public void dispose() {
		getCommandStack().removeCommandStackListener(handlerListener);
////		getConnectionService().removeXMPPLoginListener(this);
////		XMPPConnection connection = getConnectionService().getConnection();
////		if(connection != null){
////			connection.removeConnectionListener(connectionListener);
////			Roster roster = connection.getRoster();
////			if(roster != null) roster.removeRosterListener(rosterListener);
////			ChatManager chatManager = connection.getChatManager();
////			if(chatManager != null) chatManager.removeChatListener(cardSendListener);
////		}
		getGraphicalViewer().removeSelectionChangedListener(selectionListenerForHandler);
		getKanbanService().removeStorageChangeListener(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if(GraphicalViewer.class.equals(adapter)){
			return getGraphicalViewer();
		}
		if(CommandStack.class.equals(adapter)){
			return getCommandStack();
		}
		if(CellEditorActionHandler.class.equals(adapter)){
			return handlers;
		}
		if(Board.class.equals(adapter)){
			return getBoard();
		}
		return super.getAdapter(adapter);
	}
	
	private CommandStack getCommandStack() {
		return getGraphicalViewer().getEditDomain().getCommandStack();
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}

////	public void afterLoggedIn(XMPPConnection connection) {
////		refreshXmppConnectionStatus();
////	}

////	public void beforeLoggedOut(XMPPConnection connection) {
////		refreshXmppConnectionStatus();
////	}
	
//	private User getUser() {
////		KanbanUIActivator activator = getActivator();
////		if(activator == null) return null;
////		XMPPConnectionService connectionService = activator.getConnectionService();
////		if(connectionService == null) return null;
//		return getKanbanService().getCurrentUser();
//	}
	private KanbanService getKanbanService() {
		return getActivator().getKanbanService();
	}

	public void changedStorage(IProgressMonitor monitor) {
		monitor.setTaskName("Refresh Board...");
		getDisplay().syncExec(new Runnable(){
			public void run() {
				setInintialContents();
			}
		});
		monitor.internalWorked(15);
	}

	public Integer getPriority() {
		return 10;
	}

	private BoardModel getBoard() {
		return boardModel;
	}

	private ScriptingService getScriptingService() throws ScriptingException {
		return KanbanUIActivator.getDefault().getScriptingService();
	}

	private IEclipsePreferences getPreference() {
		return new InstanceScope().getNode(KanbanUIActivator.ID_PLUGIN);
	}

	private KanbanUIActivator getActivator() {
		return KanbanUIActivator.getDefault();
	}

}
