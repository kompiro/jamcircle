package org.kompiro.jamcircle.scripting.ui.internal.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.scripting.ui.internal.eclipse.ui.console.RubyScriptingConsole;

@RunWith(ActionTestRunner.class)
@WithAction(ShutdownAction.class)
public class ShutdownActionTest {

	private SWTBot bot;
	private ShutdownAction action;
	private RubyScriptingConsole console;

	@Before
	public void before() throws Exception {
		console = mock(RubyScriptingConsole.class);
		action.setConsole(console);
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		bot.toolbarButton().click();
		verify(console).shutdown();
	}

}
