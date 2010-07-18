package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVExportPage extends WizardPage {

	/**
	 * 
	 */
	private CSVExportWizard exportWizard;
	private Text fileText;

	protected CSVExportPage(CSVExportWizard csvExportWizard) {
		super("CSVExportPage"); //$NON-NLS-1$
		exportWizard = csvExportWizard;
		setTitle(Messages.CSVExportWizard_title);
		exportWizard.setWindowTitle(Messages.CSVExportWizard_title);
		setDescription(Messages.CSVExportWizard_description);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout());

		Group fileGroup = new Group(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileGroup);
		fileGroup.setText(Messages.CSVExportWizard_file_label);
		GridLayout layout = new GridLayout();
		fileGroup.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label fileLabel = new Label(fileGroup, SWT.None);
		// TODO Select Directory or File and Model Type.
		fileLabel.setText(Messages.CSVExportWizard_output_label);
		fileText = new Text(fileGroup, SWT.BORDER);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				File file = new File(((Text) e.widget).getText());
				setPageComplete(file.exists());
			}
		});
		Button fileBrowseButton = new Button(fileGroup, SWT.None);
		fileBrowseButton.setText(Messages.CSVExportWizard_browse);
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage(Messages.CSVExportWizard_dialog_message);
				String path = dialog.open();
				if (path != null) {
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