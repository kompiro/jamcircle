package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.jface.WithWizard;
import org.eclipse.swtbot.jface.WizardDialogTestRunner;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.ui.Messages;

@RunWith(WizardDialogTestRunner.class)
@WithWizard(CSVImportWizard.class)
public class CSVImportWizardTest {
	private SWTBot bot;
	private CSVImportWizard wizard;

	private SWTBotShell target;
	private IRunnableWithProgress runner;

	@Before
	public void before() {
		target = bot.shell(Messages.CSVImportWizard_title);
		runner = mock(IExportRunnableWithProgress.class);
		wizard.setRunner(runner);
		target.activate();
	}

	@Test
	public void show() throws Throwable {
		bot.button(IDialogConstants.CANCEL_LABEL).click();
		assertThat(Conditions.shellCloses(target).test(), is(true));
	}

	@Test
	public void not_close_wizard_when_click_finish_and_file_is_empty() throws Throwable {
		String finishLabel = IDialogConstants.FINISH_LABEL;
		System.out.println(finishLabel);
		assertThat(bot.button(finishLabel).isEnabled(), is(false));
	}

	public static void main(String[] args) {
		final Shell parentShell = new Shell();
		IWizard newWizard = new CSVImportWizard();
		WizardDialog dialog = new WizardDialog(parentShell, newWizard);
		dialog.open();
	}

}
