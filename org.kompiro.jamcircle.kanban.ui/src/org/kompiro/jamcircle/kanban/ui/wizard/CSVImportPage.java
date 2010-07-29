package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVImportPage extends WizardPage {

	private String filePath;
	protected String type;

	public CSVImportPage() {
		super("CSVImportPage"); //$NON-NLS-1$
		setTitle(Messages.CSVImportWizard_title);
		setDescription(Messages.CSVImportWizard_description);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
		createFileGroup(composite);
		createTypeGroup(composite);
		setControl(composite);
	}

	private void createTypeGroup(Composite composite) {

		Label typeLabel = new Label(composite, SWT.None);
		typeLabel.setText(Messages.CSVImportWizard_type_label);
		GridDataFactory.fillDefaults().applyTo(typeLabel);

		CCombo typeCombo = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		typeCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				type = ((CCombo) e.widget).getText();
			}
		});
		typeCombo.add(CSVImportWizard.TYPE_CARD);
		typeCombo.add(CSVImportWizard.TYPE_BOARD);
		typeCombo.add(CSVImportWizard.TYPE_LANE);
		typeCombo.add(CSVImportWizard.TYPE_USER);
		typeCombo.select(0);
		GridDataFactory.fillDefaults().applyTo(typeCombo);

		Label label = new Label(typeCombo, SWT.NONE);
		label.setText("test");
		GridDataFactory.swtDefaults().applyTo(label);
	}

	private void createFileGroup(Composite composite) {
		Label fileLabel = new Label(composite, SWT.None);
		fileLabel.setText(Messages.Wizard_file_label);

		final Text fileTextWidget = new Text(composite, SWT.BORDER);
		fileTextWidget.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				File file = new File(((Text) e.widget).getText());
				setPageComplete(file.exists());
				filePath = file.getAbsolutePath();
			}
		});

		Button fileBrowseButton = new Button(composite, SWT.None);
		fileBrowseButton.setText(Messages.Wizard_file_browse);
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				String path = dialog.open();
				if (path != null) {
					fileTextWidget.setText(path);
				}

			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileTextWidget);
		GridDataFactory.fillDefaults().applyTo(fileBrowseButton);
	}

	public String getFilePath() {
		return filePath;
	}

	public String getType() {
		return type;
	}
}