package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.ui.console.IConsoleView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.scripting.ui.internal.action.ScrollLockAction;

@RunWith(ActionTestRunner.class)
@WithAction(ScrollLockAction.class)
public class ScrollLockActionTest {
	private SWTBot bot;
	private ScrollLockAction action;
	private IConsoleView consoleView;

	@Before
	public void before() throws Exception {
		consoleView = mock(IConsoleView.class);
		action.setConsoleView(consoleView);
	}

	@Test
	public void run() throws Exception {
		when(consoleView.getScrollLock()).thenReturn(false);
		bot.toolbarToggleButton().click();
		verify(consoleView).setScrollLock(true);
	}

	@Test
	public void run_repeat() throws Exception {
		when(consoleView.getScrollLock()).thenReturn(false);
		bot.toolbarToggleButton().click();
		bot.toolbarToggleButton().click();
		verify(consoleView).setScrollLock(false);
	}

}
