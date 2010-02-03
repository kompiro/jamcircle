package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import org.kompiro.jamcircle.kanban.ui.editpart.IPropertyChangeDelegator;

public class IPropertyChangeDelegatorForTest implements
		IPropertyChangeDelegator {
	public void run(Runnable runner) {
		runner.run();
	}
}
