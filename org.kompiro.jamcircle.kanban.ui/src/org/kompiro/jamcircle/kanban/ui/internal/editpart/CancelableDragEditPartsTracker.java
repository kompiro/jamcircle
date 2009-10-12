/**
 * 
 */
package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.ui.command.CancelableCommand;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractEditPart;

public class CancelableDragEditPartsTracker extends
		DragEditPartsTracker {

	public CancelableDragEditPartsTracker(AbstractEditPart editEditPart) {
		super(editEditPart);
	}

	@Override
	protected void executeCurrentCommand() {
		Command curCommand = getCurrentCommand();
		if (curCommand != null && curCommand.canExecute()){
			if (curCommand instanceof CompoundCommand) {
				if( ! confirmExecuteCommand(curCommand)){
					setCurrentCommand(null);
					return;
				}
			}
			executeCommand(curCommand);
			setCurrentCommand(null);
		}
	}

	private boolean confirmExecuteCommand(Command curCommand) {
		List<Command> commands = expandCommands((CompoundCommand)curCommand);
		for(Command com : commands){
			if (com instanceof CancelableCommand) {
				CancelableCommand cancelableCommand = (CancelableCommand) com;
				if(!MessageDialog.openConfirm(getShell(), "Confirm", cancelableCommand.getComfirmMessage())){
					return false;
				}
			}
		}
		return true;
	}

	private List<Command> expandCommands(CompoundCommand command){
		List<Command> result = new ArrayList<Command>();
		Object[] commands = command.getChildren();
		for(Object obj :commands){
			if (obj instanceof CompoundCommand) {
				result.addAll(expandCommands((CompoundCommand)obj));
			}else if(obj instanceof Command){
				result.add((Command)obj);
			}
		}
		return result;
	}
	
	private Shell getShell() {
		return getSourceEditPart().getViewer().getControl().getShell();
	}

}