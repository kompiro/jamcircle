package org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console;

import org.eclipse.ui.console.*;
import org.eclipse.ui.part.IPageBookViewPage;

public class ScriptingConsolePageParticipant implements IConsolePageParticipant {

	private TextConsolePage page;

	public void activated() {
		page.getViewer().setEditable(true);
	}

	public void deactivated() {
		page.getViewer().setEditable(false);
	}

	public void dispose() {
	}

	public void init(IPageBookViewPage page, IConsole console) {
		this.page = (TextConsolePage) page;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

}
