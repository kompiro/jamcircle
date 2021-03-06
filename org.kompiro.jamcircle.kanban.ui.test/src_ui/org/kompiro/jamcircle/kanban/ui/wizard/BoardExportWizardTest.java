package org.kompiro.jamcircle.kanban.ui.wizard;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.syncExec;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.*;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.widget.MessageDialogHelper;
import org.kompiro.swtbot.extension.jface.WizardClass;

public class BoardExportWizardTest {
	private Board board;
	private KanbanService kanbanService;
	private MessageDialogHelper helper;
	private BoardExportRunnerWithProgress runner;
	private SWTBotShell shellBot;

	@Rule
	public WizardClass<BoardExportWizard> rule = new WizardClass<BoardExportWizard>(BoardExportWizard.class);
	private SWTBot bot;
	private BoardExportWizard wizard;

	@Before
	public void before() throws Throwable {
		wizard = rule.getWizard();
		bot = rule.getBot();
		kanbanService = mock(KanbanService.class);
		helper = mock(MessageDialogHelper.class);
		wizard.setKanbanService(kanbanService);
		runner = mock(BoardExportRunnerWithProgress.class);
		wizard.setRunnable(runner);
		wizard.setHelper(helper);
		board = mock(Board.class);
		when(board.getID()).thenReturn(1);
		when(board.getTitle()).thenReturn("test");
		Board[] boards = new Board[] { board };
		when(kanbanService.findAllBoard()).thenReturn(boards);
		syncExec(new VoidResult() {
			public void run() {
				wizard.init(null, null);
			}
		});
		shellBot = bot.shell(Messages.BoardExportWizard_title).activate();
	}

	@Test
	public void show() throws Throwable {
		shellBot.isOpen();
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
		verify(runner).run(any(IProgressMonitor.class));
	}

	@Test
	public void open_error_dialog_when_error_is_occured_when_exporting() throws Exception {
		Throwable target = new IllegalArgumentException();
		InvocationTargetException toBeThrown = new InvocationTargetException(target);
		doThrow(toBeThrown).when(runner).run(any(IProgressMonitor.class));
		setFile();
		bot.button(IDialogConstants.FINISH_LABEL).click();
		shellBot.isOpen();
		verify(helper).openError(any(Shell.class), any(String.class), eq(toBeThrown));
	}

	public static void main(String[] args) {
		final Shell parentShell = new Shell();
		BoardExportWizard wizard = new BoardExportWizard();
		BoardExportRunnerWithProgress runner = mock(BoardExportRunnerWithProgress.class);
		wizard.setRunnable(runner);
		KanbanService kanbanService = mock(KanbanService.class);
		Board board = mock(Board.class);
		when(board.getTitle()).thenReturn("test");
		when(board.getID()).thenReturn(1);
		when(kanbanService.findAllBoard()).thenReturn(new Board[] { board });
		wizard.setKanbanService(kanbanService);
		WizardDialog dialog = new WizardDialog(parentShell, wizard);
		dialog.open();
	}

	private void setFile() throws IOException {
		SWTBotText fileText = bot.textWithLabel(Messages.Wizard_file_output_label);
		assertThat(fileText.getText(), is(""));
		File tmpFile = File.createTempFile("tmp", ".txt");
		String path = tmpFile.getAbsolutePath();
		fileText.setText(path);
	}

}
