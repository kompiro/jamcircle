package org.kompiro.jamcircle.kanban.ui.internal.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.SubMonitor;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardCommandExecuter;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator.MonitorRunnable;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

/**
 * provides to run script on board.
 */
class BoardScriptRunnable extends MonitorRunnable {

	private static final String BEAN_NAME_BOARD_COMMAND_EXECUTER = "boardCommandExecuter";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD = "board";//$NON-NLS-1$
	private static final String BEAN_NAME_MONITOR = "monitor";//$NON-NLS-1$
	private static final String BEAN_NAME_BOARD_PART = "boardPart";//$NON-NLS-1$

	private ScriptingService scriptingService;
	private IMonitorDelegator preJob;
	private BoardEditPart boardEditPart;

	BoardScriptRunnable(BoardEditPart boardEditPart, ScriptingService scriptingService, IMonitorDelegator delegator) {
		this.boardEditPart = boardEditPart;
		this.scriptingService = scriptingService;
		this.preJob = delegator;
	}

	public void run() {
		try {
			if (preJob != null) {
				preJob.join();
			}
		} catch (InterruptedException e) {
		}
		BoardModel boardModel = boardEditPart.getBoardModel();
		if (boardModel.hasScript()) {
			SubMonitor sub = SubMonitor.convert(monitor);
			sub.setTaskName(Messages.KanbanView_execute_script_task_name);
			boardModel.clearMocks();

			Map<String, Object> beans = createBeans(boardModel, sub);

			Board board = boardModel.getBoard();
			String script = board.getScript();
			ScriptTypes scriptType = board.getScriptType();
			String scriptName = String.format(Messages.KanbanView_target_board_message, board.getTitle());
			try {
				scriptingService.eval(scriptType, scriptName, script, beans);
			} catch (ScriptingException e) {
				KanbanUIStatusHandler.fail(e, e.getMessage());
			}
		}
	}

	private Map<String, Object> createBeans(BoardModel boardModel, SubMonitor sub) {
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put(BEAN_NAME_BOARD, boardModel);
		beans.put(BEAN_NAME_MONITOR, sub);
		beans.put(BEAN_NAME_BOARD_PART, this.boardEditPart);
		beans.put(BEAN_NAME_BOARD_COMMAND_EXECUTER, new BoardCommandExecuter(this.boardEditPart));
		return beans;
	}
}