package org.eclipse.swtbot.eclipse.gef.view.finder.widgets;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.ui.IViewReference;

public class SWTGefViewBot extends SWTGefBot {
	
	public SWTBotGefView createView(IViewReference reference,
			SWTWorkbenchBot bot) {
		return new SWTBotGefView(reference, bot);
	}

}
