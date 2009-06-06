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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;

public class CSVImportWizard extends Wizard implements IExportWizard {
	
	private static final String TYPE_CARD = "Card";
	private static final String TYPE_BOARD = "Board";
	private static final String TYPE_LANE = "Lane";
	private static final String TYPE_USER = "User";

	public class CSVImportPage  extends WizardPage{

		private Text fileText;
		private CCombo typeCombo;

		protected CSVImportPage() {
			super("wizardPage");
			setTitle("Import CSV");
			setDescription("This wizard import a type from CSV.This wizard is only used for insert batch." +
					"(Experimental: this wizard doesn't care about data relations.)");
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
			fileLabel.setText("&File:");
			fileText = new Text(fileGroup,SWT.BORDER);
			Button fileBrowseButton = new Button(fileGroup,SWT.None);
			fileBrowseButton.setText("Browse");
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
			typeLabel.setText("&Type:");
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
				monitor.beginTask("Import CSV Data...", 10);
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
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
