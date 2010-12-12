package org.kompiro.jamcircle.scripting.ui.internal.ruby.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.*;
import org.kompiro.jamcircle.scripting.ui.Messages;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.job.InstallGemJob;

public class GemInstallActionTest {

	private InstallGemJob job;

	private GemInstallAction action = new GemInstallAction();

	@Rule
	public ActionClass<GemInstallAction> rule = new ActionClass<GemInstallAction>(action);

	@Before
	public void before() throws Exception {
		job = mock(InstallGemJob.class);
		action.setJob(job);
	}

	@Test
	public void run() throws Exception {
		assertThat(SWTUtils.isUIThread(SWTUtils.display()), is(false));
		rule.getBot().toolbarButton().click();

		SWTBotShell shell = rule.getBot().shell(Messages.GemInstallAction_dialog_title);
		shell.activate();

		rule.getBot().text().setText("test");
		rule.getBot().button("OK").click();
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
