package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.jface.WithWizard;
import org.eclipse.swtbot.jface.WizardDialogTestRunner;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.wizard.BoardImportWizard.WizardContainerDelegator;

@RunWith(WizardDialogTestRunner.class)
@WithWizard(BoardImportWizard.class)
public class BoardImportWizardTest {

	private SWTBot bot;
	private BoardImportWizard wizard;

	private SWTBotShell target;
	private BoardConverter boardConverter;
	private WizardContainerDelegator delegator;

	@Before
	public void before() {
		boardConverter = mock(BoardConverter.class);
		wizard.setConverter(boardConverter);
		delegator = mock(WizardContainerDelegator.class);
		wizard.setDelegator(delegator);
		target = bot.shell(Messages.BoardImportWizard_title);
		target.activate();
	}

	@Test
	public void show() throws Throwable {
		target.isOpen();

		bot.button(IDialogConstants.CANCEL_LABEL).click();
		assertThat(Conditions.shellCloses(target).test(), is(true));
	}

	@Test
	public void not_close_wizard_when_click_finish_and_file_is_empty() throws Throwable {
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(false));
	}

	@Test
	public void finish_is_enabled_when_file_form_is_exists_path() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.BoardImportWizardPage_import_file_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".txt");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(true));
	}

	@Test
	public void runner_is_called_when_finish_is_pushed() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.BoardImportWizardPage_import_file_label);
		File tmpFile = File.createTempFile("tmp", ".txt");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		bot.button(IDialogConstants.FINISH_LABEL).click();
		verify(boardConverter).load(any(File.class));
		verify(delegator).run(any(Board.class));
	}

}
