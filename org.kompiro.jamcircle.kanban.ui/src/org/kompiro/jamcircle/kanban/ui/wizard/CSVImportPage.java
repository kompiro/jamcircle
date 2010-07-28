package org.kompiro.jamcircle.kanban.ui.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class CSVImportPage extends WizardPage {

	private Text fileText;
	private CCombo typeCombo;

	public CSVImportPage() {
		super("CSVImportPage"); //$NON-NLS-1$
		setTitle(Messages.CSVImportWizard_title);
		setDescription(Messages.CSVImportWizard_description);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout());

		Group fileGroup = new Group(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileGroup);
		fileGroup.setText(Messages.CSVImportWizard_file_text);
		GridLayout layout = new GridLayout();
		fileGroup.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label fileLabel = new Label(fileGroup, SWT.None);
		fileLabel.setText(Messages.Wizard_file_label);
		fileText = new Text(fileGroup, SWT.BORDER);
		Button fileBrowseButton = new Button(fileGroup, SWT.None);
		fileBrowseButton.setText(Messages.Wizard_file_browse);
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				String path = dialog.open();
				if (path != null) {
					fileText.setText(path);
				}
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileText);
		GridDataFactory.fillDefaults().applyTo(fileBrowseButton);

		Label typeLabel = new Label(fileGroup, SWT.None);
		GridDataFactory.fillDefaults().applyTo(typeLabel);
		typeLabel.setText(Messages.CSVImportWizard_type_label);
		typeCombo = new CCombo(fileGroup, SWT.BORDER);
		typeCombo.add(CSVImportWizard.TYPE_CARD);
		typeCombo.add(CSVImportWizard.TYPE_BOARD);
		typeCombo.add(CSVImportWizard.TYPE_LANE);
		typeCombo.add(CSVImportWizard.TYPE_USER);
		typeCombo.select(0);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(typeCombo);
		setControl(composite);
	}

	public Text getFileText() {
		return fileText;
	}

	public CCombo getTypeCombo() {
		return typeCombo;
	}
}