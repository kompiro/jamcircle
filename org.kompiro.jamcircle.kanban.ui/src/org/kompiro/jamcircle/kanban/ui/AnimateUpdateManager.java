package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.draw2d.DeferredUpdateManager;
import org.eclipse.swt.widgets.Display;

public class AnimateUpdateManager extends DeferredUpdateManager {

	public static boolean animation = false;

	@Override
	protected void sendUpdateRequest() {
		if (animation) {
			Display.getCurrent().timerExec(10,new DeferredUpdateManager.UpdateRequest());
		} else {
			super.sendUpdateRequest();
		}

	}
}
