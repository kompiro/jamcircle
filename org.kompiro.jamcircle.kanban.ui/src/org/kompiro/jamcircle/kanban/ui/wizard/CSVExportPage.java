package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVExportPage extends WizardPage {

	private Text fileText;

	public CSVExportPage() {
		super("CSVExportPage"); //$NON-NLS-1$
		setTitle(Messages.CSVExportWizard_title);
		setDescription(Messages.CSVExportWizard_description);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		GridLayoutFactory.swtDefaults().applyTo(composite);

		createFileGroup(composite);

		setControl(composite);
	}

	private void createFileGroup(Composite composite) {
		Group fileGroup = new Group(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileGroup);
		fileGroup.setText(Messages.Wizard_file_label);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(fileGroup);

		Label fileLabel = new Label(fileGroup, SWT.None);
		fileLabel.setText(Messages.Wizard_file_output_label);

		// TODO Select Directory or File and Model Type.
		fileText = new Text(fileGroup, SWT.BORDER);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				File file = new File(((Text) e.widget).getText());
				setPageComplete(file.exists());
			}
		});
		Button fileBrowseButton = new Button(fileGroup, SWT.None);
		fileBrowseButton.setText(Messages.Wizard_file_browse);
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
	}

	public Text getFileText() {
		return fileText;
	}

}