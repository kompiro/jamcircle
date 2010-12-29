package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.*;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.job.ListGemJob;
import org.kompiro.swtbot.extension.jface.ActionClass;

public class GemListActionTest {

	private GemListAction action = new GemListAction();
	@Rule
	public ActionClass<GemListAction> rule = new ActionClass<GemListAction>(action);
	private ListGemJob job;

	@Before
	public void before() throws Exception {
		job = mock(ListGemJob.class);
		action.setJob(job);
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		rule.getBot().toolbarButton().click();
		verify(job).schedule();
	}

}
