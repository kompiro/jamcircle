package org.kompiro.jamcircle.kanban.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVImportWizard extends Wizard implements IImportWizard {

	static final String TYPE_CARD = "Card"; //$NON-NLS-1$
	static final String TYPE_BOARD = "Board"; //$NON-NLS-1$
	static final String TYPE_LANE = "Lane"; //$NON-NLS-1$
	static final String TYPE_USER = "User"; //$NON-NLS-1$

	private CSVImportPage page;
	private CSVImportRunnerWithProgress runner = new CSVImportRunnerWithProgress();

	public CSVImportWizard() {
		setWindowTitle(Messages.CSVImportWizard_title);
		setNeedsProgressMonitor(true);
		page = new CSVImportPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		String path = page.getFilePath();
		String type = page.getType();
		runner.setPath(path);
		runner.setType(type);
		try {
			getContainer().run(true, false, runner);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), Messages.CSVImportWizard_error_title, realException.getMessage());
			return false;
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	public void setRunner(CSVImportRunnerWithProgress runner) {
		this.runner = runner;
	}

}
