package org.kompiro.jamcircle.kanban.ui.wizard;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.widget.BoardListTableWidget;

public class BoardExportWizardPage extends WizardPage {

	private Text fileText;
	private File file;
	private Board[] boards;
	private BoardListTableWidget list;

	public BoardExportWizardPage() {
		super("export_board_wizard"); //$NON-NLS-1$
		setTitle(Messages.BoardExportWizardPage_title);
		setMessage(Messages.BoardExportWizardPage_message);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		GridLayoutFactory.swtDefaults().applyTo(composite);

		createFileGroup(composite);
		createBoardSelectionGroup(composite);

		setControl(composite);
	}

	private void createFileGroup(Composite composite) {
		Group group = new Group(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		group.setText(Messages.Wizard_file_label);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(group);

		Label fileLabel = new Label(group, SWT.None);
		fileLabel.setText(Messages.Wizard_file_output_label);

		fileText = new Text(group, SWT.BORDER);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				BoardExportWizardPage.this.file = new File(((Text) e.widget).getText());
				setPageComplete(BoardExportWizardPage.this.file.exists());
			}
		});
		Button fileBrowseButton = new Button(group, SWT.None);
		fileBrowseButton.setText(Messages.Wizard_file_browse);
		fileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage(Messages.BoardExportWizardPage_export_file_selection_dialog_message);
				String path = dialog.open();
				if (path != null) {
					fileText.setText(path);
				}
				File file = new File(path);
				setPageComplete(file.exists());
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileText);
		GridDataFactory.fillDefaults().applyTo(fileBrowseButton);
	}

	private void createBoardSelectionGroup(Composite composite) {
		Group group = new Group(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		group.setText(Messages.BoardExportWizardPage_selection_group);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(group);
		list = new BoardListTableWidget(group);
		list.setInput(boards);
		group.pack(true);
	}

	public File getFile() {
		return file;
	}

	public Board getBoard() {
		return list.getBoard();
	}

	public void setBoards(Board[] boards) {
		this.boards = boards;
		if (list != null) {
			list.setInput(boards);
		}
	}

}
