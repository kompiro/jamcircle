package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.jface.WithWizard;
import org.eclipse.swtbot.jface.WizardDialogTestRunner;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.*;
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
	private CSVImportRunnerWithProgress runner;

	@Before
	public void before() {
		target = bot.shell(Messages.CSVImportWizard_title);
		runner = mock(CSVImportRunnerWithProgress.class);
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
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(false));
	}

	@Test
	public void finish_is_enabled_when_file_form_is_exists_path() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".csv");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(true));
	}

	@Test
	public void runner_is_called_when_finish_is_pushed() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".csv");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		bot.button(IDialogConstants.FINISH_LABEL).click();
		verify(runner).setPath(path);
		verify(runner).setType(CSVImportWizard.TYPE_CARD);
		verify(runner).run((IProgressMonitor) any());
	}

	@Test
	public void change_type_and_call_finish() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".csv");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		SWTBotCCombo typeCombo = bot.ccomboBoxWithLabel(Messages.CSVImportWizard_type_label);
		typeCombo.setSelection(CSVImportWizard.TYPE_USER);
		bot.button(IDialogConstants.FINISH_LABEL).click();
		verify(runner).setPath(path);
		verify(runner).setType(CSVImportWizard.TYPE_USER);
		verify(runner).run((IProgressMonitor) any());
	}

	public static void main(String[] args) {
		final Shell parentShell = new Shell();
		IWizard newWizard = new CSVImportWizard();
		WizardDialog dialog = new WizardDialog(parentShell, newWizard);
		dialog.open();
	}

}
