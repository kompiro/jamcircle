package org.kompiro.jamcircle.kanban.ui;

import org.eclipse.core.runtime.preferences.*;

public class KanbanUIPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		KanbanUIActivator activator = KanbanUIActivator.getDefault();
		if(activator == null) return;
		new DefaultScope().getNode(KanbanUIActivator.ID_PLUGIN).putInt(KanbanPreferenceConstants.BOARD_ID.toString(),1);
	}

}
