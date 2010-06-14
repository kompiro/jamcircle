package org.kompiro.jamcircle.kanban.ui.internal.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.gef.GraphicalViewer;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanPreferenceConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardCommandExecuter;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIExtensionEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.DefaultIconModelFactory;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModelFactory;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class StorageContentsOperatorImpl implements StorageContentsOperator{

	private static final String BEAN_NAME_BOARD_COMMAND_EXECUTER = "boardCommandExecuter";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD = "board";//$NON-NLS-1$
	private static final String BEAN_NAME_MONITOR = "monitor";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD_PART = "boardPart";//$NON-NLS-1$
	private GraphicalViewer viewer;
	private KanbanUIEditPartFactory factory = new KanbanUIEditPartFactory();
	private IconModelFactory iconModelFactory;
	{
		KanbanUIExtensionEditPartFactory extensionFactory = new KanbanUIExtensionEditPartFactory();
		factory.setExtensionFactory(extensionFactory);
		iconModelFactory = new DefaultIconModelFactory(getKanbanService());
	}
	public StorageContentsOperatorImpl(GraphicalViewer viewer){
		this.viewer = viewer;
		viewer.setEditPartFactory(factory);
	}

	public void setContents(Board board, IProgressMonitor monitor) {
		String taskName = String.format(Messages.KanbanView_open_message,board.getTitle()); 
		monitor.subTask(taskName);

		BoardModel boardModel = new BoardModel(board);
		board.addPropertyChangeListener(boardModel);
		
		factory.setBoardModel(boardModel);
					
		monitor.internalWorked(1);
		initializeIcon(boardModel);
		monitor.internalWorked(1);
		
		viewer.setContents(boardModel);
		executeScript(boardModel);
		storeCurrentBoard(board.getID());
	}

	private void executeScript(final BoardModel boardModel) {
		new Job(Messages.KanbanView_execute_script_message){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Board board = boardModel.getBoard();
				String script = board.getScript();
				if(script != null && script.length() != 0){
					monitor.setTaskName(Messages.KanbanView_execute_script_task_name);
					boardModel.clearMocks();
				
					String scriptName = String.format(Messages.KanbanView_target_board_message,board.getTitle());
				
					Map<String,Object> beans= new HashMap<String, Object>();
					beans.put(BEAN_NAME_BOARD, boardModel);
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
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private void initializeIcon(BoardModel boardModel) {
		KanbanService service = getKanbanService();
		Icon[] icons = service.findAllIcons();
		for(Icon icon : icons){
			IconModel model = iconModelFactory.create(icon);
			boardModel.addIcon(model);
		}
	}
	
	private void storeCurrentBoard(int id) {
		KanbanUIStatusHandler.debugUI("KanbanView#storeCurrentBoard() id='%d'", id); //$NON-NLS-1$
		KanbanUIActivator activator = getActivator();
		if(activator != null){
			getPreference().putInt(KanbanPreferenceConstants.BOARD_ID.toString(), id);
		}
	}
	
	private BoardEditPart getBoardEditPart(){
		return (BoardEditPart)viewer.getContents();
	}
	
	private IEclipsePreferences getPreference() {
		return new InstanceScope().getNode(KanbanUIActivator.ID_PLUGIN);
	}

	private KanbanUIActivator getActivator() {
		return KanbanUIActivator.getDefault();
	}
	
	private ScriptingService getScriptingService() throws ScriptingException {
		return KanbanUIActivator.getDefault().getScriptingService();
	}

	private KanbanService getKanbanService() {
		return getActivator().getKanbanService();
	}

}
