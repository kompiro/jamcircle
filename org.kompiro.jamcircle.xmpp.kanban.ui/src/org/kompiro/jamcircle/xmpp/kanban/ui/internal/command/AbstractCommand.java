package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;


import org.eclipse.gef.commands.Command;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.BridgeXMPPActivator;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;

public abstract class AbstractCommand extends Command {

	private boolean undoable = true;
	private BridgeXMPPActivator activator;
	
	public AbstractCommand(){
		setLabel(getClass().getSimpleName());
	}
	
	@Override
	public String getDebugLabel() {
		return this.getClass().getSimpleName();
	}
		
	@Override
	public final void execute() {
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
	
	public void setCanUndo(boolean undoable){
		this.undoable = undoable;
	}
	
	public abstract void doExecute();
	
	protected KanbanService getKanbanService(){
		return getActivator().getKanbanService();
	}

	protected XMPPConnectionService getConnectionService(){
		return getActivator().getConnectionService();
	}

	private BridgeXMPPActivator getActivator() {
		if(this.activator == null){
			this.activator = BridgeXMPPActivator.getDefault();
		}
		if(activator == null) throw new IllegalStateException("Can't get BridgeXMPPActivator.");
		return activator;
	}
	
	public void setActivator(BridgeXMPPActivator activator) {
		this.activator = activator;
	}
	
	
}