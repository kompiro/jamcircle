package org.kompiro.jamcircle.rcp.internal.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.kompiro.jamcircle.RCPActivator;

public class RCPPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public RCPPreferencePage() {
		super(GRID);
		setPreferenceStore(RCPActivator.getDefault().getPreferenceStore());
		setDescription("JAMCircle preference");
	}
	
	public void createFieldEditors() {
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.MINIMIZED,
				"&Minimized when launch JAMCircle",
				getFieldEditorParent()));

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}