package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.*;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.widget.MessageDialogHelper;
import org.kompiro.swtbot.extension.jface.WizardClass;

public class CSVExportWizardTest {

	@Rule
	public WizardClass<CSVExportWizard> rule = new WizardClass<CSVExportWizard>(CSVExportWizard.class);
	private SWTBot bot;
	private CSVExportWizard wizard;

	private SWTBotShell target;
	private IRunnableWithProgress runner;
	private MessageDialogHelper helper;

	@Before
	public void before() {
		bot = rule.getBot();
		wizard = rule.getWizard();
		target = bot.shell(Messages.CSVExportWizard_title);
		runner = mock(IExportRunnableWithProgress.class);
		helper = mock(MessageDialogHelper.class);
		wizard.setHelper(helper);
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
		setFile();
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(true));
	}

	@Test
	public void runner_is_called_when_finish_is_pushed() throws Exception {
		setFile();
		bot.button(IDialogConstants.FINISH_LABEL).click();
		verify(runner, times(1)).run((IProgressMonitor) any());
	}

	@Test
	public void open_error_dialog_when_error_is_occured_when_exporting() throws Exception {
		IllegalArgumentException toBeThrown = new IllegalArgumentException();
		doThrow(toBeThrown).when(runner).run(any(IProgressMonitor.class));
		setFile();
		bot.button(IDialogConstants.FINISH_LABEL).click();
		target.isOpen();
		verify(helper).openError(any(Shell.class), any(String.class), eq(toBeThrown));
	}

	private void setFile() throws IOException {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_output_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".txt");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
	}

	public static void main(String[] args) {
		final Shell parentShell = new Shell();
		IWizard newWizard = new CSVExportWizard();
		WizardDialog dialog = new WizardDialog(parentShell, newWizard);
		dialog.open();
	}

}
