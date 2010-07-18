package org.kompiro.jamcircle.kanban.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVExportWizard extends Wizard implements IExportWizard {

	private CSVExportPage page;
	private IRunnableWithProgress runner;

	public CSVExportWizard() {
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		page = new CSVExportPage(this);
		addPage(page);
	}

	@Override
	public void addPage(IWizardPage page) {
		super.addPage(page);
	}

	@Override
	public boolean performFinish() {
		final String path = page.getFileText().getText();
		if (runner == null) {
			runner = new CSVExportRunnerWithProgress(path);
		}
		try {
			getContainer().run(true, false, runner);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), Messages.CSVExportWizard_error_title, realException.getMessage());
			return false;
		}

		return true;
	}

	public void setRunner(IRunnableWithProgress runner) {
		this.runner = runner;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
