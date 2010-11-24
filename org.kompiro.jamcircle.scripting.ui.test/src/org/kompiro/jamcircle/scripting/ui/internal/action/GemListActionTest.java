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
import org.kompiro.jamcircle.scripting.ui.internal.job.ListGemJob;

@RunWith(ActionTestRunner.class)
@WithAction(GemListAction.class)
public class GemListActionTest {

	private SWTBot bot;
	private GemListAction action;
	private ListGemJob job;

	@Before
	public void before() throws Exception {
		job = mock(ListGemJob.class);
		action.setJob(job);
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		bot.toolbarButton().click();
		verify(job).schedule();
	}

}
