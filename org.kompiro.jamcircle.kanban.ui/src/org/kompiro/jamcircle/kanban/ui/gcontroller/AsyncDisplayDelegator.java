package org.kompiro.jamcircle.kanban.ui.gcontroller;

import org.eclipse.swt.widgets.Display;

public class AsyncDisplayDelegator implements IPropertyChangeDelegator {
	
	private Display display;

	public AsyncDisplayDelegator(Display display){
		this.display = display;
	}

	public void run(Runnable runnable){
		display.asyncExec(runnable);
	}

}
