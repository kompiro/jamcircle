package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.gef.commands.*;

public class CommandStackEventListenerForDebug implements
		CommandStackEventListener {
	private static final String LINE_BREAK = System.getProperty("line.separator"); //$NON-NLS-1$

	public void stackChanged(CommandStackEvent event) {
		Command command = event.getCommand();
		StringBuilder builder = new StringBuilder("CommandStack is changed."); //$NON-NLS-1$
		if(event.isPostChangeEvent()){
			builder.append("POST EVENT" + LINE_BREAK); //$NON-NLS-1$
		}else{
			builder.append("PRE EVENT" + LINE_BREAK); //$NON-NLS-1$
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
			builder.append(LINE_BREAK);				
		}
	}
}