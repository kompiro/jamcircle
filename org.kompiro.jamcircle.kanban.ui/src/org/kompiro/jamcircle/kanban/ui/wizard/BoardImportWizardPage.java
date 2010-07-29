package org.kompiro.jamcircle.kanban.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BoardImportWizardPage extends WizardPage {
	public BoardImportWizardPage() {
		super("BoardImport");
	}

	public void createControl(Composite parent) {
		Control composite = new Composite(parent, SWT.None);
		setControl(composite);
	}
}