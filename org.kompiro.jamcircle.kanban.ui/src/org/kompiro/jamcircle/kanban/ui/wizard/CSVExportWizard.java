package org.kompiro.jamcircle.kanban.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.widget.MessageDialogHelper;

public class CSVExportWizard extends Wizard implements IExportWizard {

	private CSVExportPage page;
	private IRunnableWithProgress runner;
	private MessageDialogHelper helper;

	public CSVExportWizard() {
		setWindowTitle(Messages.CSVExportWizard_title);
		setNeedsProgressMonitor(true);
		page = new CSVExportPage();
		addPage(page);
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
			// cancel case
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			helper.openError(getShell(), Messages.CSVExportWizard_error_title, realException);
			return false;
		}

		return true;
	}

	public void setRunner(IRunnableWithProgress runner) {
		this.runner = runner;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	public void setHelper(MessageDialogHelper helper) {
		this.helper = helper;
	}

}
