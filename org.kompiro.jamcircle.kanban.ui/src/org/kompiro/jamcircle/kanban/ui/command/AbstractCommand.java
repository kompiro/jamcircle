package org.kompiro.jamcircle.kanban.ui.command;

import static java.lang.String.format;

import org.eclipse.gef.commands.Command;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;

public abstract class AbstractCommand extends Command {

	private boolean undoable = false;
	private boolean execute = false;
	private KanbanUIActivator activator;
	
	public AbstractCommand(){
		setLabel(getClass().getSimpleName());
	}
	
	protected abstract void initialize();

	@Override
	public String getDebugLabel() {
		return this.getClass().getSimpleName();
	}
		
	@Override
	public final void execute() {
		initialize();
		if(!canExecute()){
			String message = format("can't execute:'%s'",getDebugLabel());
			KanbanUIStatusHandler.info(message);
			return;
		}
		KanbanUIStatusHandler.info("execute:'" + getDebugLabel() + "'");
		try{
			doExecute();
		}catch(Exception e){
			String message = String.format("An Exception has occured. '%s'",e.getLocalizedMessage());
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
	public boolean canExecute() {
		initialize();
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
		if(activator == null) throw new IllegalStateException("Can't get KanbanUIActivator.");
		return activator;
	}
	
	public void setActivator(KanbanUIActivator activator) {
		this.activator = activator;
	}
		
}
