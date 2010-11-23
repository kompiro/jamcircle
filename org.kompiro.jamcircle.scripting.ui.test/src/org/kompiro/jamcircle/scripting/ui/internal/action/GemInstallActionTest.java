package org.kompiro.jamcircle.scripting.ui.internal.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.scripting.ui.internal.job.InstallGemJob;

@RunWith(ActionTestRunner.class)
@WithAction(GemInstallAction.class)
public class GemInstallActionTest {

	private SWTBot bot;
	private GemInstallAction action;
	private InstallGemJob job;

	@Before
	public void before() throws Exception {
		job = mock(InstallGemJob.class);
		action.setJob(job);
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		bot.toolbarButton().click();

		SWTBotShell shell = bot.shell("Install gem");
		shell.activate();

		bot.text().setText("test");
		bot.button("OK").click();
		verify(job).setTarget("test");
		verify(job).schedule();
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		ApplicationWindow window = new ApplicationWindow(shell) {
			{
				addMenuBar();
				addToolBar(SWT.FLAT);
			}

			@Override
			protected void configureShell(Shell shell) {
				GemInstallAction action = new GemInstallAction();
				action.setShell(shell);
				getToolBarManager().add(action);
				super.configureShell(shell);
			}

			@Override
			protected Control createContents(Composite parent) {
				Composite comp = new Composite(parent, SWT.None);

				comp.setLayout(new FillLayout());

				return comp;
			}
		};
		window.create();
		window.open();
		Shell shell2 = window.getShell();
		while (!shell2.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
