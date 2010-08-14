package org.eclipse.swtbot.jface;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class WizardDialogForTest extends WizardDialog {
	private final boolean[] finishPressed;

	public WizardDialogForTest(Shell parentShell, IWizard newWizard, boolean[] finishPressed) {
		super(parentShell, newWizard);
		this.finishPressed = finishPressed;
	}

	@Override
	protected void finishPressed() {
		super.finishPressed();
		finishPressed[0] = true;
	}
}