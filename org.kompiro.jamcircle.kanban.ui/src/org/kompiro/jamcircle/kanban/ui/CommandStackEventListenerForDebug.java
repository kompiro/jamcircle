package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.gef.commands.*;

public class CommandStackEventListenerForDebug implements
		CommandStackEventListener {
	public void stackChanged(CommandStackEvent event) {
		Command command = event.getCommand();
		StringBuilder builder = new StringBuilder("CommandStack is changed. ");
		if(event.isPostChangeEvent()){
			builder.append("POST EVENT \n");
		}else{
			builder.append("PRE EVENT \n");
		}
		extractDebugLabel(command, builder);
		KanbanUIStatusHandler.debug(builder.toString());			
	}

	private void extractDebugLabel(Command command, StringBuilder builder) {
		if (command instanceof CompoundCommand) {
			CompoundCommand comm = (CompoundCommand) command;
			for(Object o : comm.getChildren()){
				extractDebugLabel((Command)o, builder);
			}
		}else{
			builder.append(command.getClass().getSimpleName());
			builder.append("\n");				
		}
	}
}