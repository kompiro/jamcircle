package org.kompiro.jamcircle.kanban.command;

import static java.lang.String.format;

import org.eclipse.gef.commands.Command;
import org.kompiro.jamcircle.kanban.command.internal.KanbanCommandContext;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public abstract class AbstractCommand extends Command {

	private boolean undoable = false;
	private boolean execute = false;
	private KanbanCommandContext context;
	private boolean initialized;

	public AbstractCommand() {
		setLabel(getClass().getSimpleName());
	}

	protected abstract void initialize() throws IllegalStateException;

	@Override
	public String getDebugLabel() {
		return this.getClass().getSimpleName();
	}

	@Override
	public final void execute() {
		if (!canExecute()) {
			String message = format(Messages.AbstractCommand_can_not_execute_message, getDebugLabel());
			KanbanCommandStatusHandler.info(message);
			return;
		}
		KanbanCommandStatusHandler.info(format(Messages.AbstractCommand_execute_message, getDebugLabel()));
		try {
			doExecute();
		} catch (Exception e) {
			String message = format(Messages.AbstractCommand_error_message, e.getLocalizedMessage());
			KanbanCommandStatusHandler.fail(e, message, true);
			undoable = false;
		}
	}

	@Override
	public boolean canUndo() {
		return undoable;
	}

	public void setUndoable(boolean undoable) {
		this.undoable = undoable;
	}

	@Override
	public boolean canExecute() throws IllegalStateException {
		if (!initialized) {
			try {
				initialize();
			} catch (IllegalStateException e) {
				String message = format(Messages.AbstractCommand_error_message, getDebugLabel());
				KanbanCommandStatusHandler.fail(e, message, true);
				throw e;
			}
			initialized = true;
		}
		return execute;
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	public abstract void doExecute();

	protected KanbanService getKanbanService() {
		return getContext().getKanbanService();
	}

	private KanbanCommandContext getContext() {
		if (this.context == null) {
			this.context = KanbanCommandContext.getDefault();
		}
		if (context == null)
			throw new IllegalStateException(Messages.AbstractCommand_activator_error_message);
		return context;
	}

	public void setActivator(KanbanCommandContext context) {
		this.context = context;
	}

}
