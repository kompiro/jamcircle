package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class SyncCommandStack extends CommandStack {
	@Override
	public void execute(final Command command) {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				SyncCommandStack.super.execute(command);
			}
		});
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}

}
