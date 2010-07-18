package org.kompiro.jamcircle.kanban.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class BoardExportWizard extends Wizard implements IExportWizard {

	public BoardExportWizard() {
		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		addPage(new BoardExportWizardPage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
