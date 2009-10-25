package org.kompiro.jamcircle.kanban.ui.command;

import static java.lang.String.format;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

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
	public boolean canExecute() throws IllegalStateException{
		if(!initialized){
			try {
				initialize();
			} catch (IllegalStateException e) {
				Shell shell = getShell();
				if(shell != null){
					String title = String.format("Why doesn't execute the command '%s'",getDebugLabel());
					MessageDialog.openInformation(shell, title, e.getLocalizedMessage());
				}else{
					throw e;
				}
			}
			initialized = true;
		}
		return execute;
	}
	
	private Shell getShell() {
		return WorkbenchUtil.getShell();
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
