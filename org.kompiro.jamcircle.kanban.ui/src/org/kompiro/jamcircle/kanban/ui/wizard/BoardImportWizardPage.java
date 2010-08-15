package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BoardImportWizardPage extends WizardPage {
	private Text fileText;
	private File file;

	public BoardImportWizardPage() {
		super("import_board_wizard"); //$NON-NLS-1$
		setTitle(Messages.BoardImportWizardPage_title);
		setMessage(Messages.BoardImportWizardPage_message);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		GridLayoutFactory.swtDefaults().applyTo(composite);

		createFileGroup(composite);

		setControl(composite);
	}

	private void createFileGroup(Composite composite) {
		Group group = new Group(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		group.setText(Messages.Wizard_file_label);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(group);

		Label fileLabel = new Label(group, SWT.None);
		fileLabel.setText(Messages.BoardImportWizardPage_import_file_label);

		fileText = new Text(group, SWT.BORDER);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				BoardImportWizardPage.this.file = new File(((Text) e.widget).getText());
				setPageComplete(BoardImportWizardPage.this.file.exists());
			}
		});
		Button fileBrowseButton = new Button(group, SWT.None);
		fileBrowseButton.setText(Messages.Wizard_file_browse);
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterExtensions(new String[] { "*.zip" }); //$NON-NLS-1$
				dialog.setText(Messages.BoardImportWizardPage_import_file_dialog_message);
				String path = dialog.open();
				if (path != null && !path.equals("")) {//$NON-NLS-1$
					fileText.setText(path);
					file = new File(path);
					setPageComplete(file.exists());
				}
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileText);
		GridDataFactory.fillDefaults().applyTo(fileBrowseButton);
	}

	public File getFile() {
		return file;
	}
}