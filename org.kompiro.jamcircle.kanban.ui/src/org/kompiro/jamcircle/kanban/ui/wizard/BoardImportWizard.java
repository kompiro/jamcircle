package org.kompiro.jamcircle.kanban.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardImportWizard extends Wizard implements IImportWizard {

	public BoardImportWizard() {
		setWindowTitle(Messages.BoardImportWizard_title);
		addPage(new BoardImportWizardPage());
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
