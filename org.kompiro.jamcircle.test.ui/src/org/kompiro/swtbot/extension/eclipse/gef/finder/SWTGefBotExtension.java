package org.kompiro.swtbot.extension.eclipse.gef.finder;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartId;
import static org.eclipse.swtbot.eclipse.finder.waits.Conditions.waitForView;

import org.eclipse.swtbot.eclipse.finder.waits.WaitForView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefView;
import org.eclipse.ui.IViewReference;
import org.hamcrest.Matcher;

public class SWTGefBotExtension extends SWTGefBot {

	@Override
	public SWTBotGefView viewById(String id) {
		Matcher<IViewReference> matcher = withPartId(id);
		WaitForView waitForView = waitForView(matcher);
		waitUntilWidgetAppears(waitForView);
		return createView(waitForView.get(0), this);
	}

}
