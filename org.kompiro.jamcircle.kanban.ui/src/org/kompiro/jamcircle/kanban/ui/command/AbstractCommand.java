package org.kompiro.jamcircle.kanban.ui.command;

import static java.lang.String.format;

import org.eclipse.gef.commands.Command;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.*;

public abstract class AbstractCommand extends Command {

	private boolean undoable = false;
	private boolean execute = false;
	private KanbanUIActivator activator;
	private boolean initialized;
	
	public AbstractCommand(){
		setLabel(getClass().getSimpleName());
	}
	
	protected abstract void initialize() throws IllegalStateException;

	@Override
	public String getDebugLabel() {
		return this.getClass().getSimpleName();
	}
		
	@Override
	public final void execute() {
		if(!canExecute()){
			String message = format(Messages.AbstractCommand_can_not_execute_message,getDebugLabel());
			KanbanUIStatusHandler.info(message);
			return;
		}
		KanbanUIStatusHandler.info(format(Messages.AbstractCommand_execute_message,getDebugLabel()));
		try{
			doExecute();
		}catch(Exception e){
			String message = format(Messages.AbstractCommand_error_message,e.getLocalizedMessage());
			KanbanUIStatusHandler.fail(e, message , true);
			undoable = false;
		}
	}
	
	@Override
	public boolean canUndo() {
		return undoable;
	}
	
	public void setUndoable(boolean undoable){
		this.undoable = undoable;
	}

	@Override
	public boolean canExecute() throws IllegalStateException{
		if(!initialized){
			try {
				initialize();
			} catch (IllegalStateException e) {
				String message = format(Messages.AbstractCommand_error_message,getDebugLabel());
				KanbanUIStatusHandler.fail(e, message , true);
				throw e;
			}
			initialized = true;
		}
		return execute;
	}
	
	public void setExecute(boolean execute){
		this.execute = execute;
	}

	
	public abstract void doExecute();
	
	protected KanbanService getKanbanService(){
		return getActivator().getKanbanService();
	}

	private KanbanUIActivator getActivator() {
		if(this.activator == null){
			this.activator = KanbanUIActivator.getDefault();
		}
		if(activator == null) throw new IllegalStateException(Messages.AbstractCommand_activator_error_message);
		return activator;
	}
	
	public void setActivator(KanbanUIActivator activator) {
		this.activator = activator;
	}
		
}
