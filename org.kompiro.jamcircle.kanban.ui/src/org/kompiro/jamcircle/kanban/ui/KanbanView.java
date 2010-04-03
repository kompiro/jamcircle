package org.kompiro.jamcircle.kanban.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.action.*;
import org.kompiro.jamcircle.kanban.ui.editpart.IBoardCommandExecuter;
import org.kompiro.jamcircle.kanban.ui.editpart.IBoardEditPart;
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

public class KanbanView extends ViewPart implements StorageChageListener,PropertyChangeListener{


	// TODO extract ScriptConstants
	private static final String BEAN_NAME_BOARD_COMMAND_EXECUTER = "boardCommandExecuter";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD = "board";//$NON-NLS-1$
	private static final String BEAN_NAME_MONITOR = "monitor";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD_PART = "boardPart";//$NON-NLS-1$

	public static String ID = "org.kompiro.jamcircle.kanban.KanbanView"; //$NON-NLS-1$

	private ScrollingGraphicalViewer viewer;

	private SelectionSynchronizer synchronizer;
	private OpenCardListAction openCardListAction;
	private IAction openCommandListAction;
	private BoardModel boardModel;
	
	private static final Conditional cardCond = new Conditional(){
		public boolean evaluate(EditPart editpart) {
			boolean isCardEditPart = editpart instanceof CardEditPart;
			return ! isCardEditPart;
		}
	};

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
	private IconModelFactory iconModelFactory;
	private IAction caputureBoardAction;
	private ScalableRootEditPart rootPart;
	private ZoomInAction zoomInAction;
	private ZoomOutAction zoomOutAction;

	private IAction editBoardAction;


	public KanbanView() {
		if (getKanbanService() != null) {
			getKanbanService().addStorageChangeListener(this);
			getKanbanService().addPropertyChangeListener(this);
			iconModelFactory = new DefaultIconModelFactory(getKanbanService());
		}
		KanbanJFaceResource.initialize();

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
		editBoardAction = new EditBoardAction(this);
		
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
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
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
			menuManager.add(editBoardAction);
			menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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
			menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			menuManager.add(openCardListAction);
			menuManager.add(openCommandListAction);
			menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			
		}
	}

	private void fillLocalStatusLine(User currentUser) {
		IStatusLineManager manager = getActionBars().getStatusLineManager();
		Image image;
		String message;
		if(currentUser != null){
			image = getImageRegistry().get(KanbanImageConstants.CONNECT_IMAGE.toString());
			message = currentUser.getUserId();
			manager.setErrorMessage(null);
			manager.setMessage(image,message);
		}else{
			image= getImageRegistry().get(KanbanImageConstants.DISCONNECT_IMAGE.toString());
			message = Messages.KanbanView_not_log_in_message;			
			manager.setErrorMessage(image,message);
		}
	}

	private ImageRegistry getImageRegistry() {
		return getActivator().getImageRegistry();
	}


	private void setInintialContents() {
		if(Platform.isRunning()){
			storageInitialize();
		}
	}
	
	public void setContents(Board board,final IProgressMonitor monitor) {
		BoardModel oldBoardModel = null;
		if(boardModel != null){
			oldBoardModel = boardModel;
			board.removePropertyChangeListener(boardModel);
		}
		boardModel = new BoardModel(board);
		if(oldBoardModel != null){
			List<IconModel> iconModels = oldBoardModel.getIconModels();
			for (IconModel iconModel : iconModels) {
				boardModel.addIcon(iconModel);
			}			
		}
		board.addPropertyChangeListener(boardModel);
		
		KanbanUIEditPartFactory factory = new KanbanUIEditPartFactory(this.boardModel);
		KanbanUIExtensionEditPartFactory extensionFactory = new KanbanUIExtensionEditPartFactory();
		factory.setExtensionFactory(extensionFactory);
		
		String taskName = String.format(Messages.KanbanView_open_message,board.getTitle()); 
		monitor.subTask(taskName);
		getGraphicalViewer().setEditPartFactory(factory);
		
		monitor.internalWorked(1);
		refreshIcons();
		monitor.internalWorked(1);
		
		final int id = board.getID();
		getGraphicalViewer().setContents(boardModel);
		new Job(Messages.KanbanView_execute_script_message){
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
			monitor.setTaskName(Messages.KanbanView_execute_script_task_name);
			boardModel.clearMocks();

			String scriptName = String.format(Messages.KanbanView_target_board_message,board.getTitle());

			Map<String,Object> beans= new HashMap<String, Object>();
			beans.put(BEAN_NAME_BOARD, this.boardModel);
			beans.put(BEAN_NAME_MONITOR, monitor);
			beans.put(BEAN_NAME_BOARD_PART, getBoardEditPart());
			beans.put(BEAN_NAME_BOARD_COMMAND_EXECUTER, new BoardCommandExecuter(getBoardEditPart()));
			try {
				ScriptingService service = getScriptingService();
				service.eval(board.getScriptType(), scriptName, script,beans);
			} catch (ScriptingException e) {
				KanbanUIStatusHandler.fail(e, e.getMessage());
			}
		}
	}

	private void storeCurrentBoard(int id) {
		KanbanUIStatusHandler.debugUI("KanbanView#storeCurrentBoard() id='%d'", id); //$NON-NLS-1$
		KanbanUIActivator activator = getActivator();
		if(activator != null){
			getPreference().putInt(KanbanPreferenceConstants.BOARD_ID.toString(), id);
		}
	}

	private void refreshIcons() {
		KanbanService service = getKanbanService();
		Icon[] icons = service.findAllIcons();
		for(Icon icon : icons){
			IconModel model = iconModelFactory.create(icon);
			this.boardModel.addIcon(model);
		}
	}

	private void storageInitialize() {
		UIJob job = new UIJob(Messages.KanbanView_storage_initialize_message){
		
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				monitor.subTask(Messages.KanbanView_storage_initialize_task_message);
				KanbanService service = getKanbanService();
				int id = getPreference().getInt(KanbanPreferenceConstants.BOARD_ID.toString(),1);
				KanbanUIStatusHandler.debugUI("KanbanView#storageInitialize() id:'%d'", id); //$NON-NLS-1$
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
	
	private BoardEditPart getBoardEditPart(){
		return (BoardEditPart)getGraphicalViewer().getContents();
	}

	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (synchronizer == null)
			synchronizer = new SelectionSynchronizer();
		return synchronizer;
	}

	private final class CommandStackImpl extends CommandStack {
		@Override
		public void execute(final Command command) {
			getDisplay().syncExec(new Runnable() {
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

	private final class KanbanViewDropAdapter extends DropTargetAdapter {

		public void drop(DropTargetEvent event) {
			KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop widget[%s] x:%d y:%d",event.widget,event.x,event.y); //$NON-NLS-1$
			Point location = new Point(
					getGraphicalViewer().getControl().toControl(
						Display.getCurrent().getCursorLocation()));
			Point viewLocation = getViewport().getViewLocation();
			location.translate(viewLocation);
			Object data = event.data;
			EditPart dropTarget = getGraphicalViewer().findObjectAtExcluding(location, Collections.EMPTY_SET,cardCond);
			KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop target:%s data: %s",dropTarget,data); //$NON-NLS-1$
			if (data instanceof List<?>) {
				List<?> list = (List<?>) data;
				CompoundCommand command = new CompoundCommand();
				for(Object obj : list){
					if (obj instanceof CardWrapper) {
						CardWrapper wrapper = (CardWrapper) obj;
						Card card = wrapper.getCard();
						KanbanUIStatusHandler.debugUI("KanbanViewDropAdapter#drop %s", card); //$NON-NLS-1$
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

	@Override
	public void dispose() {
		getCommandStack().removeCommandStackListener(handlerListener);
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
		getKanbanService().removePropertyChangeListener(this);
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
		if(Board.class.equals(adapter) || BoardModel.class.equals(adapter)){
			return getBoard();
		}
		if(IBoardEditPart.class.equals(adapter)){
			return getBoardEditPart();
		}
		if(IBoardCommandExecuter.class.equals(adapter)){
			return new BoardCommandExecuter(getBoardEditPart());
		}
		return super.getAdapter(adapter);
	}
	
	private CommandStack getCommandStack() {
		return getGraphicalViewer().getEditDomain().getCommandStack();
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}
	
	private KanbanService getKanbanService() {
		return getActivator().getKanbanService();
	}

	public void changedStorage(IProgressMonitor monitor) {
		monitor.setTaskName(Messages.KanbanView_refresh_board_message);
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

	public void propertyChange(final PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if(propertyName != null && propertyName.equals(KanbanService.PROP_CHANGED_CURRENT_USER)){
			Display display = getDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					fillLocalStatusLine((User)evt.getNewValue());
				}
			});
		}
	}

}
