package org.kompiro.jamcircle.kanban.ui.internal.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.*;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIExtensionEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.model.*;
import org.kompiro.jamcircle.kanban.ui.util.*;
import org.kompiro.jamcircle.scripting.ScriptingService;

/**
 * Operate for storage instance.
 */
public class StorageContentsOperatorImpl implements StorageContentsOperator {

	private KanbanUIEditPartFactory factory = new KanbanUIEditPartFactory();
	private IconModelFactory iconModelFactory;
	private IMonitorDelegator uiDelegator = new UIJobMonitorDelegator(Messages.KanbanView_open_message);
	private IMonitorDelegator delegator = new JobMonitorDelegator(Messages.KanbanView_execute_script_message);
	private ScriptingService scriptingService;
	private KanbanService kanbanService;
	private InstanceScope instanceScope = new InstanceScope();
	private KanbanUIExtensionEditPartFactory extensionFactory = new KanbanUIExtensionEditPartFactory();

	public StorageContentsOperatorImpl(ScriptingService scriptingService, KanbanService kanbanService) {
		this.scriptingService = scriptingService;
		this.kanbanService = kanbanService;
	}

	public void initialize() throws IllegalStateException {
		if (extensionFactory == null)
			throw new IllegalStateException(Messages.StorageContentsOperatorImpl_error_extension_factory_is_null);
		if (kanbanService == null)
			throw new IllegalStateException(Messages.StorageContentsOperatorImpl_error_kanban_service_is_null);
		if (scriptingService == null)
			throw new IllegalStateException(Messages.StorageContentsOperatorImpl_error_scripting_service_is_null);
		factory.setExtensionFactory(extensionFactory);
		iconModelFactory = new DefaultIconModelFactory(kanbanService);
	}

	public void setContents(GraphicalViewer viewer, BoardModel boardModel, IProgressMonitor monitor) {
		viewer.setEditPartFactory(factory);
		String taskName = String.format(Messages.KanbanView_open_message, boardModel.getTitle());
		monitor.subTask(taskName);

		factory.setBoardModel(boardModel);

		monitor.internalWorked(1);
		initializeIcon(boardModel);
		monitor.internalWorked(1);
		SetContentRunnable runner = new SetContentRunnable(boardModel, viewer);
		uiDelegator.run(runner);
		executeScript(viewer, boardModel);
		storeCurrentBoard(boardModel.getID());
	}

	private void executeScript(GraphicalViewer viewer, BoardModel boardModel) {
		delegator.run(new BoardScriptRunnable(boardModel, viewer, getScriptingService(), uiDelegator));

	}

	private void initializeIcon(BoardModel boardModel) {
		Icon[] icons = kanbanService.findAllIcons();
		if (icons == null || icons.length == 0)
			return;
		for (Icon icon : icons) {
			IconModel model = iconModelFactory.create(icon);
			boardModel.addIcon(model);
		}
	}

	private void storeCurrentBoard(int id) {
		KanbanUIStatusHandler.debugUI("KanbanView#storeCurrentBoard() id='%d'", id); //$NON-NLS-1$
		if (PlatformUI.isWorkbenchRunning()) {
			getPreference().putInt(KanbanPreferenceConstants.BOARD_ID.toString(), id);
		}
	}

	private IEclipsePreferences getPreference() {
		return instanceScope.getNode(KanbanUIActivator.ID_PLUGIN);
	}

	private ScriptingService getScriptingService() {
		return scriptingService;
	}

	void setDummyDelegator(IMonitorDelegator delegator) {
		this.uiDelegator = delegator;
		this.delegator = delegator;
	}

	public void setExtensionFactory(
			KanbanUIExtensionEditPartFactory extensionFactory) {
		this.extensionFactory = extensionFactory;
	}

}
