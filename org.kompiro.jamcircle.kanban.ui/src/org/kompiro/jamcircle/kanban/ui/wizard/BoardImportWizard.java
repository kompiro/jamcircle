package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.service.BoardConverter;
import org.kompiro.jamcircle.kanban.ui.KanbanUIContext;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardImportWizard extends Wizard implements IImportWizard {

	private BoardImportWizardPage page;
	private BoardConverter boardConverter = KanbanUIContext.getDefault().getBoardConverter();

	public BoardImportWizard() {
		setWindowTitle(Messages.BoardImportWizard_title);
		page = new BoardImportWizardPage();
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		File file = page.getFile();
		try {
			boardConverter.load(file);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "ボードをインポート中にエラーが発生しました。", e.getMessage());
			return false;
		}
		return true;
	}

	public void setConverter(BoardConverter boardConverter) {
		this.boardConverter = boardConverter;
	}

}
