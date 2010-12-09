package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.*;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.console.RubyScriptingConsole;

public class ShutdownActionTest {

	private SWTBot bot;
	private ShutdownAction action = new ShutdownAction();
	@Rule
	public ActionClass<ShutdownAction> rule = new ActionClass<ShutdownAction>(action);

	private RubyScriptingConsole console;

	@Before
	public void before() throws Exception {
		console = mock(RubyScriptingConsole.class);
		action.setConsole(console);
		bot = rule.getBot();
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		bot.toolbarButton().click();
		verify(console).shutdown();
	}

}
