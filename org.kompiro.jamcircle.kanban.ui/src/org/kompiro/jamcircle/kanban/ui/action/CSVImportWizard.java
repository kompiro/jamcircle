package org.kompiro.jamcircle.kanban.ui.action;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVImportWizard extends Wizard implements IExportWizard {
	
	private static final String TYPE_CARD = "Card"; //$NON-NLS-1$
	private static final String TYPE_BOARD = "Board"; //$NON-NLS-1$
	private static final String TYPE_LANE = "Lane"; //$NON-NLS-1$
	private static final String TYPE_USER = "User"; //$NON-NLS-1$

	public class CSVImportPage  extends WizardPage{

		private Text fileText;
		private CCombo typeCombo;

		protected CSVImportPage() {
			super("CSVImportPage"); //$NON-NLS-1$
			setTitle(Messages.CSVImportWizard_title);
			setDescription(Messages.CSVImportWizard_description);
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent,SWT.None);
			composite.setLayout(new GridLayout());
			
			Group fileGroup = new Group(composite,SWT.None);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(fileGroup);
			fileGroup.setText(Messages.CSVImportWizard_file_text);
			GridLayout layout = new GridLayout();
			fileGroup.setLayout(layout);
			layout.numColumns = 3;
			layout.verticalSpacing = 9;
			Label fileLabel = new Label(fileGroup,SWT.None);
			fileLabel.setText(Messages.CSVImportWizard_file_label);
			fileText = new Text(fileGroup,SWT.BORDER);
			Button fileBrowseButton = new Button(fileGroup,SWT.None);
			fileBrowseButton.setText(Messages.CSVImportWizard_browse_label);
			fileBrowseButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell());
					String path = dialog.open();
					if(path != null){
						fileText.setText(path);
					}
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(fileText);
			GridDataFactory.fillDefaults().applyTo(fileBrowseButton);
			
			Label typeLabel = new Label(fileGroup,SWT.None);
			GridDataFactory.fillDefaults().applyTo(typeLabel);
			typeLabel.setText(Messages.CSVImportWizard_type_label);
			typeCombo = new CCombo(fileGroup,SWT.BORDER);
			typeCombo.add(TYPE_CARD);
			typeCombo.add(TYPE_BOARD);
			typeCombo.add(TYPE_LANE);
			typeCombo.add(TYPE_USER);
			typeCombo.select(0);
			GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(typeCombo);
			setControl(composite);
		}
		
		public Text getFileText() {
			return fileText;
		}

		public CCombo getTypeCombo(){
			return typeCombo;
		}
	}

	private CSVImportPage page;
	
	public CSVImportWizard() {
		setNeedsProgressMonitor(true);
	}
	
	public void addPages() {
		page = new CSVImportPage();
		addPage(page);
	}
	
	@Override
	public void addPage(IWizardPage page) {
		super.addPage(page);
	}

	@Override
	public boolean performFinish() {
		final String path =  page.getFileText().getText();
		final String type =  page.getTypeCombo().getText();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask(Messages.CSVImportWizard_import_task_name, 10);
				File importFile = new File(path);
				if(TYPE_CARD.equals(type)){
					KanbanUIActivator.getDefault().getKanbanService().importCards(importFile);
				}else if(TYPE_BOARD.equals(type)){
					KanbanUIActivator.getDefault().getKanbanService().importBoards(importFile);					
				}else if(TYPE_LANE.equals(type)){
					KanbanUIActivator.getDefault().getKanbanService().importLanes(importFile);					
				}else if(TYPE_USER.equals(type)){
					KanbanUIActivator.getDefault().getKanbanService().importUsers(importFile);
				}
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
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

}
