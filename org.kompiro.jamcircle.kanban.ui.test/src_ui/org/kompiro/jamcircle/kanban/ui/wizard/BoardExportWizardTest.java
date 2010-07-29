package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.jface.WithWizard;
import org.eclipse.swtbot.jface.WizardDialogTestRunner;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.Messages;

@RunWith(WizardDialogTestRunner.class)
@WithWizard(BoardExportWizard.class)
public class BoardExportWizardTest {
	private SWTBot bot;
	private BoardExportWizard wizard;
	private BoardConverter boardConverter;
	private Board board;
	private KanbanService kanbanService;

	@Before
	public void before() throws Throwable {
		boardConverter = mock(BoardConverter.class);
		kanbanService = mock(KanbanService.class);
		wizard.setKanbanService(kanbanService);
		wizard.setBoardConverter(boardConverter);
		board = mock(Board.class);
		when(board.getID()).thenReturn(1);
		when(board.getTitle()).thenReturn("test");
		Board[] boards = new Board[] { board };
		when(kanbanService.findAllBoard()).thenReturn(boards);
		wizard.init(null, null);
	}

	@Test
	public void show() throws Throwable {
		bot.shell(Messages.BoardExportWizard_title).isOpen();
	}

	@Test
	public void not_close_wizard_when_click_finish_and_file_is_empty() throws Throwable {
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(false));
	}

	@Test
	public void finish_is_enabled_when_file_form_is_exists_path() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_output_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".txt");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		assertThat(bot.button(IDialogConstants.FINISH_LABEL).isEnabled(), is(true));
	}

	@Test
	public void runner_is_called_when_finish_is_pushed() throws Exception {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_output_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".txt");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
		bot.button(IDialogConstants.FINISH_LABEL).click();
		verify(boardConverter).dump(any(File.class), eq(board));
	}

	public static void main(String[] args) {
		final Shell parentShell = new Shell();
		BoardExportWizard wizard = new BoardExportWizard();
		BoardConverter boardConverter = mock(BoardConverter.class);
		wizard.setBoardConverter(boardConverter);
		KanbanService kanbanService = mock(KanbanService.class);
		Board board = mock(Board.class);
		when(board.getTitle()).thenReturn("test");
		when(board.getID()).thenReturn(1);
		when(kanbanService.findAllBoard()).thenReturn(new Board[] { board });
		wizard.setKanbanService(kanbanService);
		WizardDialog dialog = new WizardDialog(parentShell, wizard);
		dialog.open();
	}

}
