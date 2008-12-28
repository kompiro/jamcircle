package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class KanbanUIPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		KanbanUIActivator activator = KanbanUIActivator.getDefault();
		if(activator == null) return;
		activator.getPluginPreferences().setDefault(KanbanPreferenceConstants.BOARD_ID.toString(),1);
	}

}
