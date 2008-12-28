package org.kompiro.jamcircle.xmpp.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

public class XMPPPreferenceRegistry {
	private static IPreferenceStore store;
	public static final String PREFERENCE_HOST_NAME = "HOST";
	public static final String PREFERENCE_PORT = "PORT";
	public static final String PREFERENCE_USER_NAME = "USER_NAME";
	public static final String PREFERENCE_USER_ICON = "USER_ICON_IMAGE";

	public static void initialize(){
		XMPPUIActivator activator = XMPPUIActivator.getDefault();
		if(activator == null){
			store = new PreferenceStore();
		}else{
			store = activator.getPreferenceStore();
		}
	}
	
	public static IPreferenceStore getPreferenceStore(){
		return store;
	}

}
