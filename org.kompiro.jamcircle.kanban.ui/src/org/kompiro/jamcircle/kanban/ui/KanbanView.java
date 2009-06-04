package org.kompiro.jamcircle.kanban.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jruby.exceptions.RaiseException;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.action.CaptureBoardAction;
import org.kompiro.jamcircle.kanban.ui.action.CopyAction;
import org.kompiro.jamcircle.kanban.ui.action.CutAction;
import org.kompiro.jamcircle.kanban.ui.action.OpenCardListAction;
import org.kompiro.jamcircle.kanban.ui.action.OpenCommandListAction;
import org.kompiro.jamcircle.kanban.ui.action.PasteAction;
import org.kompiro.jamcircle.kanban.ui.command.CreateCardCommand;
import org.kompiro.jamcircle.kanban.ui.command.RemoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.gcontroller.BoardDragTracker;
import org.kompiro.jamcircle.kanban.ui.gcontroller.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.gcontroller.CardCreateRequest;
import org.kompiro.jamcircle.kanban.ui.gcontroller.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.DefaultIconModelFactory;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModelFactory;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;
import org.kompiro.jamcircle.kanban.ui.util.GraphicalUtil;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.kanban.ui.util.XMPPUtil;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;
import org.kompiro.jamcircle.kanban.ui.widget.CardObjectTransfer;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer.CardWrapper;
import org.kompiro.jamcircle.storage.service.StorageChageListener;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;


public class KanbanView extends ViewPart implements XMPPLoginListener,StorageChageListener {

	public static String ID = "org.kompiro.jamcircle.kanban.KanbanView";
	public static final String PROP_MESSAGE_MODEL = "model";

	private ScrollingGraphicalViewer viewer;
	private SelectionSynchronizer synchronizer;
//	private CommandStackEventListener commandStackDebuglistener = new CommandStackEventListenerForDebug();
	private OpenCardListAction openCardListAction;
	private IAction openCommandListAction;
	private BoardModel boardModel;

	private ConnectionListener connectionListener = new ConnectionListener(){
		
		public void connectionClosed() {
			refreshXmppConnectionStatus();
		}
		
		public void connectionClosedOnError(final Exception e) {
			getDisplay().asyncExec(new Runnable(){
				public void run() {
					KanbanUIStatusHandler.fail(e, "exception is occured when XMPP connection closing.");
				};
			});
		}
		
		public void reconnectingIn(int seconds) {
		}
		
		public void reconnectionFailed(Exception e) {
		}
		
		public void reconnectionSuccessful() {
			refreshXmppConnectionStatus();
		}
	};
	
	private static final Conditional cardCond = new Conditional(){
		public boolean evaluate(EditPart editpart) {
			boolean isCardEditPart = editpart instanceof CardEditPart;
			return ! isCardEditPart;
		}
	};

	private RosterListener rosterListener;
	private CardReceiveListener cardSendListener = new CardReceiveListener();
	private RedoAction redoHandler;
	private UndoAction undoHandler;
	private HandlerListener handlerListener;
	private DeleteAction deleteHandler;
	private ISelectionChangedListener selectionListenerForHandler = new ISelectionChangedListener(){

		public void selectionChanged(SelectionChangedEvent event) {
			deleteHandler.update();
//			pasteHandler.update();
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
	private CardReceiveFileTransferListener cardReceiveFileTransferListener;
	private IconModelFactory iconModelFactory = new DefaultIconModelFactory();
	private CaptureBoardAction caputureBoardAction;
	private ScalableRootEditPart rootPart;
	private ZoomInAction zoomInAction;
	private ZoomOutAction zoomOutAction;


	public KanbanView() {
		if(getConnectionService() != null){
			getConnectionService().addXMPPLoginListener(this);
		}
		if(getKanbanService() != null){
			getKanbanService().addStorageChangeListener(this);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new ScrollingGraphicalViewer();
		rootPart = new ScalableRootEditPart();
		viewer.setRootEditPart(rootPart);
		handlers = new CellEditorActionHandler(getActionBars());
		EditDomain domain = new EditDomain();
		domain.setCommandStack(new CommandStackImpl());
		domain.addViewer(viewer);
		handlerListener = new HandlerListener();
		getCommandStack().addCommandStackListener(handlerListener);
		viewer.createControl(parent);
		makeActions();
		contributeToActionBars();
		hookContextMenu();
		getSite().setSelectionProvider(viewer);
		Transfer[] types = new Transfer[] {CardObjectTransfer.getTransfer(),FileTransfer.getInstance()};
		DropTarget target = new DropTarget(viewer.getControl(),CardListTableViewer.OPERATIONS);
		target.setTransfer(types);
		target.addDropListener(new KanbanViewDropAdapter());

		setInintialContents();
	}
	
		
	private void makeActions() {
		undoHandler = new UndoAction(this);
		handlers.setUndoAction(undoHandler);

		redoHandler = new RedoAction(this);
		handlers.setRedoAction(redoHandler);
		
		deleteHandler = new DeleteAction(this);
		deleteHandler.setSelectionProvider(viewer);
		handlers.setDeleteAction(deleteHandler);
		
		selectAllCardHandler = new SelectAllAction(this){
			@Override
			public void run() {
				if(viewer != null){
					BoardEditPart boardEditPart = (BoardEditPart)(viewer.getContents());
					List<CardEditPart> cards = boardEditPart.getCardChildren();
					StructuredSelection selection = new StructuredSelection(cards);
					viewer.setSelection(selection);
				}
			}
		};
		handlers.setSelectAllAction(selectAllCardHandler);

		cutHandler = new CutAction(this);
		cutHandler.setSelectionProvider(viewer);
		handlers.setCutAction(cutHandler);
		
		pasteHandler = new PasteAction(this);
		handlers.setPasteAction(pasteHandler);
		
		copyHandler = new CopyAction(this);
		copyHandler.setSelectionProvider(viewer);
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
//		IActionBars bars = getViewSite().getActionBars();
//		fillToolbarMenu(bars.getToolBarManager());
	}
	
//	private void fillToolbarMenu(IToolBarManager toolBarManager) {
////		MenuManager editMenu = new MenuManager("Edit", IWorkbenchActionConstants.M_EDIT);
////		toolBarManager.add(editMenu);
//		toolBarManager.add(deleteHandler);
//		toolBarManager.add(undoHandler);
//		toolBarManager.add(redoHandler);
//	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				KanbanView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager menuManager) {
		if(viewer.getProperty(BoardDragTracker.PROPERTY_DRAG_TO_MOVE_VIEWPOINT) == null){
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

	private void fillLocalStatusLine(IStatusLineManager manager) {
		Image image;
		String message;
		if(getConnectionService().isConnecting()){
			image = getImageRegistry().get(KanbanImageConstants.CONNECT_IMAGE.toString());
			XMPPConnection connection = getConnectionService().getConnection();
			message = connection.getUser();
			manager.setErrorMessage(null);
			manager.setMessage(image,message);
		}else{
			image= getImageRegistry().get(KanbanImageConstants.DISCONNECT_IMAGE.toString());
			message = "Doesn't logged in.";			
			manager.setErrorMessage(image,message);
		}
	}

	private ImageRegistry getImageRegistry() {
		return getActivator().getImageRegistry();
	}

	private XMPPConnectionService getConnectionService() {
		if( ! Platform.isRunning()) return null;
		return getActivator().getConnectionService();
	}


	private void setInintialContents() {
		if(Platform.isRunning()){
			storageInitialize();
		}
	}
	
	public void setContents(Board board,final IProgressMonitor monitor) {
		if(boardChangeListener != null){
			getKanbanService().removePropertyChangeListener(boardChangeListener);
		}
		boardModel = new BoardModel(board);
		boardChangeListener = new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				if(BoardModel.PROP_USER.equals(evt.getPropertyName())){
					refreshXmppConnectionStatus();
				}
			}
		};
		getKanbanService().addPropertyChangeListener(boardChangeListener);
		monitor.internalWorked(1);
		refreshIcons();
		monitor.internalWorked(1);
		EditPartFactory factory = new KanbanControllerFactory(this.boardModel);
		viewer.setEditPartFactory(factory);
		String taskName = String.format("Openning board '%s' ...",board.getTitle()); 
		monitor.subTask(taskName);
		final int id = board.getID();
		executeScript(monitor);
		getDisplay().asyncExec(new Runnable(){
			public void run() {
				viewer.setContents(KanbanView.this.boardModel);
			}
		});
		storeCurrentBoard(id);
	}

	private void executeScript(final IProgressMonitor monitor) {
		Board board = boardModel.getBoard();
		String script = board.getScript();
		if(script != null && script.length() != 0){
			boardModel.clearMocks();
			BSFManager manager = new BSFManager();
			try {
//				Board mock = new org.kompiro.jamcircle.kanban.model.mock.Board();
				monitor.setTaskName("execute script");

				manager.registerBean("board", this.boardModel);
				manager.registerBean("monitor", monitor);
				manager.registerBean("JRubyType",ScriptTypes.JRuby);
				manager.registerBean("JavaScriptType",ScriptTypes.JavaScript);

				manager.registerBean("RED", ColorTypes.RED);
				manager.registerBean("YELLOW",ColorTypes.YELLOW);
				manager.registerBean("GREEN",ColorTypes.GREEN);
				manager.registerBean("LIGHT_GREEN",ColorTypes.LIGHT_GREEN);
				manager.registerBean("LIGHT_BLUE",ColorTypes.LIGHT_BLUE);
				manager.registerBean("BLUE",ColorTypes.BLUE);
				manager.registerBean("PURPLE",ColorTypes.PURPLE);
				manager.registerBean("RED_PURPLE",ColorTypes.RED_PURPLE);

				manager.registerBean("FLAG_RED", FlagTypes.RED);
				manager.registerBean("FLAG_WHITE",FlagTypes.WHITE);
				manager.registerBean("FLAG_GREEN",FlagTypes.GREEN);
				manager.registerBean("FLAG_BLUE",FlagTypes.BLUE);
				manager.registerBean("FLAG_ORANGE",FlagTypes.ORANGE);

				
				String scriptName = String.format("Board '%s' Script",board.getTitle());
				String templateName = null;
				int initLines = 0;
				switch (board.getScriptType()){
				case JavaScript:
					templateName = "init.js";
					initLines = 7;
					break;
				case JRuby:
					templateName = "init.rb";
					initLines = 4;
					break;
				default:
					String message = String.format("Board's script type is null.Please check the data. id='%d'",board.getID());
					throw new IllegalStateException(message);
				}
				InputStreamReader reader = new InputStreamReader(getClass().getResource(templateName).openStream());
				script = IOUtils.getStringFromReader(reader) + script;
				manager.exec(board.getScriptType().getType(), scriptName, -initLines, 0, script);
//				mock.setTitle("(Scripting)" + board.getTitle());
//				board = mock;
			} catch (BSFException e) {
				Throwable targetException = e.getTargetException();
				if(targetException instanceof RaiseException){
					RaiseException ex = (RaiseException) targetException;
					KanbanUIStatusHandler.fail(targetException, ex.getException().asJavaString());
					return;
				}
				KanbanUIStatusHandler.fail(targetException, "Scripting Exception");
			} catch (IOException e) {
				KanbanUIStatusHandler.fail(e, "An Error is occured when reading template script.");
			}
		}
	}

	private void storeCurrentBoard(int id) {
		KanbanUIStatusHandler.debugUI("KanbanView#storeCurrentBoard() id='%d'", id);
		KanbanUIActivator activator = getActivator();
		if(activator != null){
			activator.getPluginPreferences().setValue(KanbanPreferenceConstants.BOARD_ID.toString(), id);
		}
	}

	private KanbanUIActivator getActivator() {
		return KanbanUIActivator.getDefault();
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
					KanbanUIActivator activator = getActivator();
					int id = activator.getPluginPreferences().getInt(KanbanPreferenceConstants.BOARD_ID.toString());
					KanbanUIStatusHandler.debugUI("KanbanView#storageInitialize() id:'%d'", id);
					final Board board = service.findBoard(id);
					setContents(board,monitor);
					monitor.internalWorked(30.0);
					return Status.OK_STATUS;
				}
			};
			QualifiedName key = new QualifiedName("org.kompiro.jamcircle", "rcp");
			IJobManager manager = Job.getJobManager();
			Object obj = manager.currentJob().getProperty(key);
			if(obj != null && obj instanceof IProgressMonitor){
				IProgressMonitor monitor = (IProgressMonitor) obj;
				job.setProgressGroup(monitor, 50);
			}
			job.schedule();
	}
	
	private static Object lock = new Object();
	private PropertyChangeListener boardChangeListener;

	private void refreshXmppConnectionStatus() {
		Display display = getDisplay();
		if(display == null) return;
		display.asyncExec(new Runnable(){
			public void run() {
				fillLocalStatusLine(getActionBars().getStatusLineManager());
			}
		});

		XMPPConnection connection = getConnectionService().getConnection();
		if(!getConnectionService().isConnecting()){
			display.asyncExec(new Runnable(){
				public void run() {
					boardModel.clearUsers();
				}
			});
			return;
		}
		connection.addConnectionListener(connectionListener);
		final Roster roster = connection.getRoster();
		rosterListener = new RosterListnerForUsers(roster);
		roster.addRosterListener(rosterListener);
		connection.getChatManager().addChatListener(cardSendListener);
		final Map<String,User> userMap = new HashMap<String, User>();
		User[] userList = getKanbanService().findUsersOnBoard();
		for(User user : userList){
			String key = user.getUserId();
			if(!userMap.containsKey(key)){
				userMap.put(key, user);
			}
		}
		display.asyncExec(new Runnable(){
			public void run() {
				if(boardModel.sizeUsers() != 0){
					boardModel.clearUsers();
				}
				for(User user:userMap.values()){
					System.out.println(String.format("%s %s",user ,user.getUserId()));
					Presence presence = roster.getPresence(user.getUserId());
					UserModel userModel = new UserModel(roster.getEntry(user.getUserId()),presence,user);
					presence.isAvailable();
					boardModel.addUser(userModel);
				}
			}
			});
		setCardReceiveFileTransferManager();
	}

	private void setCardReceiveFileTransferManager() {
		FileTransferManager manager = getConnectionService().getFileTransferManager();
		manager.removeFileTransferListener(cardReceiveFileTransferListener);
		cardReceiveFileTransferListener = new CardReceiveFileTransferListener();
		manager.addFileTransferListener(cardReceiveFileTransferListener);
	}
	

	@Override
	public void setFocus() {
//		refreshXmppConnectionStatus();
	}
	
	protected void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
		getSite().setSelectionProvider(getGraphicalViewer());
	}

	GraphicalViewer getGraphicalViewer() {
		return viewer;
	}

	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (synchronizer == null)
			synchronizer = new SelectionSynchronizer();
		return synchronizer;
	}

	private final class CommandStackImpl extends CommandStack {
		@Override
		public void execute(final Command command) {
			getSite().getShell().setCursor(Cursors.WAIT);
//			if(Platform.isRunning()){
//				IProgressService service = (IProgressService) getSite().getService(IProgressService.class);
//				IRunnableContext context = new ProgressMonitorDialog(getSite()
//						.getShell());
//				try {
//					service.runInUI(context, new IRunnableWithProgress(){
//						public void run(IProgressMonitor monitor)
//								throws InvocationTargetException, InterruptedException {
//							CommandStackImpl.super.execute(command);
//						}
//					}, null);
//				} catch (InvocationTargetException e) {
//					KanbanUIStatusHandler.fail(e, "Opening Kanban Board is failed.");
//				} catch (InterruptedException e) {
//					KanbanUIStatusHandler.fail(e, "Opening Kanban Board is failed.");
//				}
//			}else{
				super.execute(command);
//			}
				getSite().getShell().setCursor(null);
		}
	}

	private final class CardReceiveFileTransferListener implements
			FileTransferListener {
		public void fileTransferRequest(FileTransferRequest request) {
			
			String uuid = request.getDescription();
			IncomingFileTransfer accept = request.accept();
			try {
				File tmpFile = new File(System.getProperty("java.io.tmpdir"),request.getFileName());
				if(tmpFile.exists()){
					tmpFile.delete();
				}
				accept.recieveFile(tmpFile);
				while(!accept.isDone()){
				}
				Card tmpCard = getCard(uuid);
				int time = 0;
				while(tmpCard == null){
					try {
						if(time > 3){
							throw new RuntimeException("can't get card data.");
						}
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					tmpCard = getCard(uuid);
					time++;
				}
				final Card card = tmpCard;
				final File file = tmpFile;
				getDisplay().asyncExec(new Runnable(){
					public void run() {
						card.addFile(file);
						card.save();
					}
				});
			} catch (XMPPException e) {
				KanbanUIStatusHandler.fail(e, "error has occured.");
				request.reject();
			}
		}

		private Card getCard(String uuid) {
			return getKanbanService().findCards(
					Card.PROP_TRASHED + " = ? and" +
					Card.PROP_UUID + " = ? and " +
					Card.PROP_TO + " is null",false,uuid)[0];
		}
	}

	private final class KanbanViewDropAdapter extends DropTargetAdapter {

		public void drop(DropTargetEvent event) {
			KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop widget[%s] x:%d y:%d",event.widget,event.x,event.y);
			Point location = new Point(
					viewer.getControl().toControl(
						Display.getCurrent().getCursorLocation()));
			Point viewLocation = getViewport().getViewLocation();
			location.translate(viewLocation);
			Object data = event.data;
			EditPart dropTarget = viewer.findObjectAtExcluding(location, Collections.EMPTY_SET,cardCond);
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
					CardCreateRequest request = new CardCreateRequest(getKanbanService(),boardModel.getBoard(),fileName);
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
			request.setEditParts(viewer.getEditPartFactory().createEditPart(null, card));
			request.setMoveDelta(location.getTranslated(GraphicalUtil.currentLocation(card).negate()));
			return dropTargetEditPart.getCommand(request);
		}

		private Viewport getViewport() {
			GraphicalEditPart rootEditPart = (GraphicalEditPart) viewer
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

	private final class CardReceiveListener implements
			ChatManagerListener {
		public void chatCreated(Chat chat, boolean createdLocally) {
			chat.addMessageListener(new MessageListener(){
				public void processMessage(Chat chat, Message message) {
					Object obj = message.getProperty(KanbanView.PROP_MESSAGE_MODEL);
					String fromUserId = chat.getParticipant();
					if (obj instanceof CardDTO) {
						CreateCardCommand command = new CreateCardCommand();
						BoardModel boardModel = (BoardModel) viewer.getContents().getModel();
						command.setContainer(boardModel);
						final CardDTO dto = (CardDTO) obj;
						User fromUser = null;
						if(fromUserId != null){
							fromUser = getKanbanService().findUser(fromUserId);
						}
						Card card = createCard(dto, fromUser);
						card.setDeletedVisuals(false);
						command.setModel(card);
						getViewSite().getShell().getDisplay().asyncExec(new CreateCommandRunnable(command));
					}
				}

				private Card createCard(CardDTO dto, User fromUser){
					KanbanService service = getKanbanService();
					return service.createReceiveCard(boardModel.getBoard(),dto,getUser(),fromUser);
				}
			});
		}
	}
	
	private final class RosterListnerForUsers implements RosterListener {
		
		public RosterListnerForUsers(Roster roster) {
		}

		public void entriesAdded(Collection<String> addresses) {
		}

		public void entriesDeleted(Collection<String> addresses) {
		}

		public void entriesUpdated(Collection<String> addresses) {
		}

		public void presenceChanged(final Presence presence) {
			Runnable runnable = new Runnable(){
				public void run() {
					String from = presence.getFrom();
					from = XMPPUtil.getRemovedResourceUser(from);
					UserModel user = boardModel.getUser(from);
					if(user != null) {
						user.setPresence(presence);					
					}
				}

			};
			getViewSite().getShell().getDisplay().asyncExec(runnable);
		}
	}

	private final class CreateCommandRunnable implements Runnable {
		private CreateCardCommand command;

		private CreateCommandRunnable(CreateCardCommand command) {
			this.command = command;
		}

		public void run() {
			viewer.getEditDomain().getCommandStack().execute(command);
		}
	}
	
	@Override
	public void dispose() {
//		getCommandStack().removeCommandStackEventListener(commandStackDebuglistener);
		getCommandStack().removeCommandStackListener(handlerListener);
		getConnectionService().removeXMPPLoginListener(this);
		XMPPConnection connection = getConnectionService().getConnection();
		if(connection != null){
			connection.removeConnectionListener(connectionListener);
			Roster roster = connection.getRoster();
			if(roster != null) roster.removeRosterListener(rosterListener);
			ChatManager chatManager = connection.getChatManager();
			if(chatManager != null) chatManager.removeChatListener(cardSendListener);
		}
		getGraphicalViewer().removeSelectionChangedListener(selectionListenerForHandler);
		getKanbanService().removeStorageChangeListener(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if(GraphicalViewer.class.equals(adapter)){
			return viewer;
		}
		if(CommandStack.class.equals(adapter)){
			return getCommandStack();
		}
		if(CellEditorActionHandler.class.equals(adapter)){
			return handlers;
		}
		return super.getAdapter(adapter);
	}
	
	private CommandStack getCommandStack() {
		return viewer.getEditDomain().getCommandStack();
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}

	public void afterLoggedIn(XMPPConnection connection) {
		refreshXmppConnectionStatus();
	}

	public void beforeLoggedOut(XMPPConnection connection) {
		refreshXmppConnectionStatus();
	}
	
	private User getUser() {
		KanbanUIActivator activator = getActivator();
		if(activator == null) return null;
		XMPPConnectionService connectionService = activator.getConnectionService();
		if(connectionService == null) return null;
		return connectionService.getCurrentUser();
	}
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

}
