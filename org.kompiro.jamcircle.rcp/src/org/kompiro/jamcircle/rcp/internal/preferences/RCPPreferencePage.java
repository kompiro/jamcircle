package org.kompiro.jamcircle.rcp.internal.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.Messages;
import org.kompiro.jamcircle.RCPActivator;

public class RCPPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public RCPPreferencePage() {
		super(GRID);
		setPreferenceStore(RCPActivator.getDefault().getPreferenceStore());
		setDescription(Messages.RCPPreferencePage_description);
	}
	
	public void createFieldEditors() {
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.MINIMIZED,
				Messages.RCPPreferencePage_minimized_message,
				getFieldEditorParent()));
		addField(
				new BooleanFieldEditor(
					PreferenceConstants.BLUR_ANIMATION,
					"閉じる際にアニメーションを行う。",
					getFieldEditorParent()));

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}