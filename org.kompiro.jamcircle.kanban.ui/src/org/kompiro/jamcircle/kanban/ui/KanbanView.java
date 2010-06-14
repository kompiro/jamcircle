package org.kompiro.jamcircle.kanban.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
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
import org.eclipse.swt.custom.BusyIndicator;
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
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.BoardContainer;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.IMonitorDelegator.MonitorRunnable;
import org.kompiro.jamcircle.kanban.ui.action.CaptureBoardAction;
import org.kompiro.jamcircle.kanban.ui.action.CopyAction;
import org.kompiro.jamcircle.kanban.ui.action.CutAction;
import org.kompiro.jamcircle.kanban.ui.action.EditBoardAction;
import org.kompiro.jamcircle.kanban.ui.action.OpenCardListAction;
import org.kompiro.jamcircle.kanban.ui.action.OpenCommandListAction;
import org.kompiro.jamcircle.kanban.ui.action.PasteAction;
import org.kompiro.jamcircle.kanban.ui.editpart.IBoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.command.RemoveCardCommand;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardCommandExecuter;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardDragTracker;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardCreateRequest;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.view.StorageContentsOperator;
import org.kompiro.jamcircle.kanban.ui.internal.view.StorageContentsOperatorImpl;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.GraphicalUtil;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;
import org.kompiro.jamcircle.kanban.ui.widget.CardObjectTransfer;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer.CardWrapper;
import org.kompiro.jamcircle.storage.service.StorageChageListener;

public class KanbanView extends ViewPart implements StorageChageListener,PropertyChangeListener{

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
	private IAction caputureBoardAction;
	private ScalableRootEditPart rootPart;
	private ZoomInAction zoomInAction;
	private ZoomOutAction zoomOutAction;

	private IAction editBoardAction;

	private StorageContentsOperator operator;

	private static IMonitorDelegator delegator = new IMonitorDelegator() {
		public void run(final MonitorRunnable runner) {
			UIJob job = new UIJob(Messages.KanbanView_storage_initialize_message){
			
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					runner.setMonitor(monitor);
					runner.run();
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	};


	public KanbanView() {
		if (getKanbanService() != null) {
			getKanbanService().addStorageChangeListener(this);
			getKanbanService().addPropertyChangeListener(this);
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
		operator = new StorageContentsOperatorImpl(getGraphicalViewer());
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
		if(boardModel != null){
			boardModel.getBoard().removePropertyChangeListener(boardModel);
		}

		operator.setContents(board,monitor);
	}
	
	private void storageInitialize() {
		final IProgressMonitor monitor = new NullProgressMonitor();
		MonitorRunnable runner = new MonitorRunnable(monitor){
			public void run() {
				monitor.subTask(Messages.KanbanView_storage_initialize_task_message);
				KanbanService service = getKanbanService();
				int id = getPreference().getInt(KanbanPreferenceConstants.BOARD_ID.toString(),1);
				KanbanUIStatusHandler.debugUI("KanbanView#storageInitialize() id:'%d'", id); //$NON-NLS-1$
				Board board = service.findBoard(id);
				if(board == null){
					board = service.findBoard(1);
				}
				monitor.internalWorked(30.0);
				setContents(board,monitor);				
			}
		};
		runner.setMonitor(monitor);
		runner.run();
//		delegator.run(runner);
//				KanbanService service = getKanbanService();
//				int id = getPreference().getInt(KanbanPreferenceConstants.BOARD_ID.toString(),1);
//				KanbanUIStatusHandler.debugUI("KanbanView#storageInitialize() id:'%d'", id); //$NON-NLS-1$
//				Board board = service.findBoard(id);
//				if(board == null){
//					board = service.findBoard(1);
//				}
//				setContents(board,monitor);
//				return Status.OK_STATUS;
//			}
//		};
//		job.schedule();
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
		if(BoardContainer.class.equals(adapter)){
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
	
	public static void setDelegator(IMonitorDelegator delegator) {
		KanbanView.delegator = delegator;
	}

	public static IMonitorDelegator getDelegator() {
		return KanbanView.delegator;
	}

}
