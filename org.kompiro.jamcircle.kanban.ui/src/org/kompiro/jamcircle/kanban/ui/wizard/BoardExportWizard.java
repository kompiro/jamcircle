package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;

public class BoardExportWizard extends Wizard implements IExportWizard {

	private BoardConverter boardConverter = KanbanUIContext.getDefault().getBoardConverter();
	private KanbanService kanbanService = KanbanUIContext.getDefault().getKanbanService();
	private BoardExportWizardPage page;

	public BoardExportWizard() {
		setNeedsProgressMonitor(true);
		page = new BoardExportWizardPage();
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		page.setBoards(kanbanService.findAllBoard());
	}

	@Override
	public boolean performFinish() {
		Board board = page.getBoard();
		boardConverter.dump(
				new File(page.getFile(), board.getTitle() + BoardConverter.BOARD_FORMAT_FILE_EXTENSION_NAME), board);
		return true;
	}

	void setBoardConverter(BoardConverter boardConverter) {
		this.boardConverter = boardConverter;
	}

	void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}

}
