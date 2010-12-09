package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.job.UninstallGemJob;

public class GemUninstallActionTest {

	private GemUninstallAction action = new GemUninstallAction();
	@Rule
	public ActionClass<GemUninstallAction> rule = new ActionClass<GemUninstallAction>(action);
	private UninstallGemJob job;

	@Before
	public void before() throws Exception {
		job = mock(UninstallGemJob.class);
		action.setJob(job);
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		SWTBot bot = rule.getBot();
		bot.toolbarButton().click();

		SWTBotShell shell = bot.shell("Uninstall gem");
		shell.activate();

		bot.text().setText("test");
		bot.button("OK").click();
		verify(job).setTarget("test");
		verify(job).schedule();
	}

}
