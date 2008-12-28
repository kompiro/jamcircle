package org.kompiro.jamcircle.kanban.ui.action;

import java.io.File;
import java.lang.reflect.InvocationTargetException;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class CSVExportWizard extends Wizard implements IExportWizard {
	
	public class CSVExportPage  extends WizardPage{

		private Text fileText;

		protected CSVExportPage() {
			super("wizardPage");
			setTitle("Export CSV");
			setDescription("This wizard export some types to CSV.");
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent,SWT.None);
			composite.setLayout(new GridLayout());
			
			Group fileGroup = new Group(composite,SWT.None);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(fileGroup);
			fileGroup.setText("File");
			GridLayout layout = new GridLayout();
			fileGroup.setLayout(layout);
			layout.numColumns = 3;
			layout.verticalSpacing = 9;
			Label fileLabel = new Label(fileGroup,SWT.None);
			// TODO Select Directory or File and Model Type.
			fileLabel.setText("&Output Directory:");
			fileText = new Text(fileGroup,SWT.BORDER);
			Button fileBrowseButton = new Button(fileGroup,SWT.None);
			fileBrowseButton.setText("Browse");
			fileBrowseButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					dialog.setMessage("Select CSV output directory.");
					String path = dialog.open();
					if(path != null){
						fileText.setText(path);
					}
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(fileText);
			GridDataFactory.fillDefaults().applyTo(fileBrowseButton);
			setControl(composite);
		}
		
		public Text getFileText() {
			return fileText;
		}

	}

	private CSVExportPage page;
	
	public CSVExportWizard() {
		setNeedsProgressMonitor(true);
	}
	
	public void addPages() {
		page = new CSVExportPage();
		addPage(page);
	}
	
	@Override
	public void addPage(IWizardPage page) {
		super.addPage(page);
	}

	@Override
	public boolean performFinish() {
		final String path =  page.getFileText().getText();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask("Export CSV Data...", 10);
				File exportFile = new File(path);
				File exportCardFile = new File(exportFile,"cards.csv");
				File exportLaneFile = new File(exportFile,"lanes.csv");
				File exportUserFile = new File(exportFile,"users.csv");
				File exportBoardFile = new File(exportFile,"boards.csv");
				monitor.internalWorked(2);
				KanbanUIActivator.getDefault().getKanbanService().exportCards(exportCardFile);
				monitor.internalWorked(2);
				KanbanUIActivator.getDefault().getKanbanService().exportLanes(exportLaneFile);
				monitor.internalWorked(2);
				KanbanUIActivator.getDefault().getKanbanService().exportUsers(exportUserFile);
				monitor.internalWorked(2);
				KanbanUIActivator.getDefault().getKanbanService().exportBoards(exportBoardFile);
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
