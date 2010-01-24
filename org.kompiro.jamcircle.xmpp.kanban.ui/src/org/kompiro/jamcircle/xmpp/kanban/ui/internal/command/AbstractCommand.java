package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;


import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.BridgeXMPPActivator;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.XMPPKanbanUIContext;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;

public abstract class AbstractCommand extends org.kompiro.jamcircle.kanban.ui.command.AbstractCommand {

	private boolean undoable = true;
	
	public AbstractCommand(){
		setLabel(getClass().getSimpleName());
	}
	
	@Override
	public boolean canUndo() {
		return undoable;
	}
	
	public void setCanUndo(boolean undoable){
		this.undoable = undoable;
	}
	
	protected KanbanService getKanbanService(){
		return getContext().getKanbanService();
	}

	protected XMPPConnectionService getConnectionService(){
		return getContext().getXMPPConnectionService();
	}
	
	private XMPPKanbanUIContext getContext(){
		return XMPPKanbanUIContext.getDefault();
	}
		
}
