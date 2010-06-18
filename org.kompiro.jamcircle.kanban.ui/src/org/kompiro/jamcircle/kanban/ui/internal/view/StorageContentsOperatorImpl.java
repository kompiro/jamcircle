package org.kompiro.jamcircle.kanban.ui.internal.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.PlatformUI;
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
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator.MonitorRunnable;
import org.kompiro.jamcircle.kanban.ui.util.JobMonitorDelegator;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

/**
 * This class 
 */
public class StorageContentsOperatorImpl implements StorageContentsOperator{

	private static final String BEAN_NAME_BOARD_COMMAND_EXECUTER = "boardCommandExecuter";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD = "board";//$NON-NLS-1$
	private static final String BEAN_NAME_MONITOR = "monitor";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD_PART = "boardPart";//$NON-NLS-1$

	private KanbanUIEditPartFactory factory = new KanbanUIEditPartFactory();
	private IconModelFactory iconModelFactory;
	private IMonitorDelegator delegator = new JobMonitorDelegator(Messages.KanbanView_execute_script_message);
	private ScriptingService scriptingService;
	private KanbanService kanbanService;
	private InstanceScope instanceScope = new InstanceScope();
	private KanbanUIExtensionEditPartFactory extensionFactory = new KanbanUIExtensionEditPartFactory();

	public StorageContentsOperatorImpl(ScriptingService scriptingService,KanbanService kanbanService){
		this.scriptingService = scriptingService;
		this.kanbanService = kanbanService;
	}
	
	public void initialize() throws IllegalStateException{
		if(extensionFactory == null) throw new IllegalStateException(Messages.StorageContentsOperatorImpl_error_extension_factory_is_null);
		if(kanbanService == null) throw new IllegalStateException(Messages.StorageContentsOperatorImpl_error_kanban_service_is_null);
		if(scriptingService == null) throw new IllegalStateException(Messages.StorageContentsOperatorImpl_error_scripting_service_is_null);
		factory.setExtensionFactory(extensionFactory);
		iconModelFactory = new DefaultIconModelFactory(kanbanService);
	}

	public void setContents(GraphicalViewer viewer,Board board, IProgressMonitor monitor) {
		viewer.setEditPartFactory(factory);		
		String taskName = String.format(Messages.KanbanView_open_message,board.getTitle()); 
		monitor.subTask(taskName);

		BoardModel boardModel = new BoardModel(board);
		board.addPropertyChangeListener(boardModel);
		
		factory.setBoardModel(boardModel);
					
		monitor.internalWorked(1);
		initializeIcon(boardModel);
		monitor.internalWorked(1);
		
		viewer.setContents(boardModel);
		executeScript(viewer,boardModel);
		storeCurrentBoard(board.getID());
	}

	private void executeScript(final GraphicalViewer viewer ,final BoardModel boardModel) {
		
		MonitorRunnable runner = new MonitorRunnable() {
			
			public void run() {
				if(boardModel.hasScript()){
					monitor.setTaskName(Messages.KanbanView_execute_script_task_name);
					boardModel.clearMocks();
				
					Map<String, Object> beans = createBeans(viewer, boardModel);

					Board board = boardModel.getBoard();
					String script = board.getScript();
					ScriptTypes scriptType = board.getScriptType();
					String scriptName = String.format(Messages.KanbanView_target_board_message,board.getTitle());
					try {
						ScriptingService service = getScriptingService();
						service.eval(scriptType, scriptName, script,beans);
					} catch (ScriptingException e) {
						KanbanUIStatusHandler.fail(e, e.getMessage());
					}
				}
			}

			private Map<String, Object> createBeans(
					GraphicalViewer viewer, BoardModel boardModel) {
				Map<String,Object> beans= new HashMap<String, Object>();
				beans.put(BEAN_NAME_BOARD, boardModel);
				beans.put(BEAN_NAME_MONITOR, monitor);
				beans.put(BEAN_NAME_BOARD_PART, viewer.getContents());
				beans.put(BEAN_NAME_BOARD_COMMAND_EXECUTER, new BoardCommandExecuter((BoardEditPart)viewer.getContents()));
				return beans;
			}

		};
		
		delegator.run(runner);

	}

	private void initializeIcon(BoardModel boardModel) {
		Icon[] icons = kanbanService.findAllIcons();
		if(icons == null || icons.length ==0 ) return;
		for(Icon icon : icons){
			IconModel model = iconModelFactory.create(icon);
			boardModel.addIcon(model);
		}
	}
	
	private void storeCurrentBoard(int id) {
		KanbanUIStatusHandler.debugUI("KanbanView#storeCurrentBoard() id='%d'", id); //$NON-NLS-1$
		if(PlatformUI.isWorkbenchRunning()){
			getPreference().putInt(KanbanPreferenceConstants.BOARD_ID.toString(), id);
		}
	}
		
	private IEclipsePreferences getPreference() {
		return instanceScope.getNode(KanbanUIActivator.ID_PLUGIN);
	}
	
	private ScriptingService getScriptingService(){
		return scriptingService;
	}
	
	void setDelegator(IMonitorDelegator delegator) {
		this.delegator = delegator;
	}
	
	public void setExtensionFactory(
			KanbanUIExtensionEditPartFactory extensionFactory) {
		this.extensionFactory = extensionFactory;
	}
	
}
