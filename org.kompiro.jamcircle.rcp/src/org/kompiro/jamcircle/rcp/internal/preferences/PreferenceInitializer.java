package org.kompiro.jamcircle.rcp.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.kompiro.jamcircle.RCPActivator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = RCPActivator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.MINIMIZED, false);
		store.setDefault(PreferenceConstants.BLUR_ANIMATION, false);
	}

}
