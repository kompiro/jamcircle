package abbot.swt.eclipse.utils;

import junit.framework.Assert;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import abbot.swt.Log;

/**
 * Utilities (preferably static) for accessing workbench preferences.
 * @author tlroche
 * @version $Id: PreferenceUtils.java 2247 2007-03-21 15:08:55Z gjohnsto $
 */
public class PreferenceUtils {
	public static final String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 2000,2003\nUS Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	/**
	 * No instances  
	 */
	private PreferenceUtils() {
	}

	/**
	 * Return the value of the preference named by the passed key
	 * as a <code>Boolean</code>, which is <code>null</code> on error.
	 */
	public static Boolean getBooleanPreference(String key) {
		Boolean ret = null;
		IPreferenceStore ps = WorkbenchPlugin.getDefault().getPreferenceStore();
		if (ps == null) {
			Log.log("ERROR: could not get PreferenceStore");
		} else {
			ret = getBooleanPreference(key, ps);
		}
		return ret;
	}

	/**
	 * Return the value of the preference named by the passed key
	 * from the passed <code>IPreferenceStore</code>
	 * as a <code>Boolean</code>, which is <code>null</code> on error.
	 */
	public static Boolean getBooleanPreference(String key, IPreferenceStore ps) {
		Boolean value = null;
		Assert.assertNotNull("ERROR: null PreferenceStore", ps);
		if (Utils.isEmpty(key)) { // TODO_Tom: can validate more?
			Log.log("ERROR: bad key");
		} else {
			value = Boolean.valueOf(ps.getBoolean(key));
			if (value == null) {
				if (ps.contains(key)) {
					Log.log("ERROR: PreferenceStore returned null Boolean");
				} else {
					Log.log("ERROR: PreferenceStore does not contain desired pref key");
				}
			}
		}
		return value;
	}

	/**
	 * Set the value of the preference named by the passed key.
	 */
	public static void setBooleanPreference(String key, Boolean val) {
		setBooleanPreference(key, val.booleanValue());
	}

	/**
	 * Set the value of the preference named by the passed key.
	 */
	public static void setBooleanPreference(String key, boolean val) {
		IPreferenceStore ps = WorkbenchPlugin.getDefault().getPreferenceStore();
		setBooleanPreference(key, val, ps);
	}

	/**
	 * Set the value of the preference named by the passed key
	 * into the passed <code>IPreferenceStore</code>.
	 */
	public static void setBooleanPreference(
		String key, boolean val, IPreferenceStore ps) {
		Assert.assertNotNull("ERROR: null PreferenceStore", ps);
		if (Utils.isEmpty(key)) { // TODO_Tom: can validate more?
			Log.log("ERROR: bad key");
		} else {
			if (ps.contains(key)) {
				ps.setValue(key, val);
			} else {
				Log.log(
					"ERROR: PreferenceStore does not contain desired pref key=\"" +
					key + "\""
				);
			}
		}
	}

	/**
	 * Return the value of the preference named by the passed key.
	 */
	public static String getStringPreference(String key) {
		String ret = null;
		// TODO: use org.eclipse.ui.internal.util.PrefUtil
		IPreferenceStore ps = WorkbenchPlugin.getDefault().getPreferenceStore();
		if (ps == null) {
			Log.log("ERROR: could not get PreferenceStore");
		} else {
			ret = getStringPreference(key, ps);
		}
		return ret;
	}

	/**
	 * Return the value of the preference named by the passed key
	 * from the passed <code>IPreferenceStore</code>
	 */
	public static String getStringPreference(String key, IPreferenceStore ps) {
		String value = null;
		Assert.assertNotNull("ERROR: null PreferenceStore", ps);
		if (Utils.isEmpty(key)) { // TODO_Tom: can validate more?
			Log.log("ERROR: bad key");
		} else {
			value = ps.getString(key);
			if (Utils.isEmpty(value)) {
				if (ps.contains(key)) {
					Log.log("ERROR: PreferenceStore returned empty pref value");
				} else {
					Log.log("ERROR: PreferenceStore does not contain desired pref key");
				}
			}
		}
		return value;
	}

	/**
	 * Set the value of the preference named by the passed key.
	 */
	public static void setStringPreference(String key, String val) {
		// TODO: use org.eclipse.ui.internal.util.PrefUtil
		IPreferenceStore ps = WorkbenchPlugin.getDefault().getPreferenceStore();
		setStringPreference(key, val, ps);
	}

	/**
	 * Set the value of the preference named by the passed key
	 * into the passed <code>IPreferenceStore</code>.
	 */
	public static void setStringPreference(
		String key, String val, IPreferenceStore ps) {
		Assert.assertNotNull("ERROR: null PreferenceStore", ps);
		if (Utils.isEmpty(key)) { // TODO_Tom: can validate more?
			Log.log("ERROR: bad key");
		} else {
			if (ps.contains(key)) {
				ps.setValue(key, val);
			} else {
				Log.log(
					"ERROR: PreferenceStore does not contain desired pref key=\"" +
					key + "\""
				);
			}
		}
	}

	/**
	 * Get the preference found in UI as
	 * Windows&gt;Preferences&gt;Workbench&gt;Perspectives&gt;
	 * Switch to associated perspective when creating a new project
	 */
	public static String getSwitchToPerspectivePreference() {
		return getStringPreference(
			IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
	}

	/**
	 * Set the preference found in UI as
	 * Windows&gt;Preferences&gt;Workbench&gt;Perspectives&gt;
	 * Switch to associated perspective when creating a new project
	 * Note that this pref is persisted!
	 */
	public static void setSwitchToPerspectivePreference(String val) {
		setStringPreference(
			IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, val);
	}

	/**
	 * Set the preference found in UI as
	 * Windows&gt;Preferences&gt;Workbench&gt;Perspectives&gt;
	 * Switch to associated perspective when creating a new project
	 * in the workbench associated with the passed <code>IPreferenceStore</code>
	 * Note that this pref is persisted!
	 */
	public static void setSwitchToPerspectivePreference(
		String val, IPreferenceStore ps) {
		setStringPreference(
			IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, val, ps);
	}

	/**
	 * Get the preference to view welcome page on startup
	 * as a <code>Boolean</code>, which is <code>null</code> on error.
	 */
	public static Boolean isWelcomeOnStartup() {
		return getBooleanPreference(IDEInternalPreferences.WELCOME_DIALOG);
	}

	/**
	 * Get the preference to view welcome page on startup
	 * as a <code>Boolean</code>, which is <code>null</code> on error.
	 */
	public static Boolean isWelcomeOnStartup(IPreferenceStore ps) {
		return getBooleanPreference(IDEInternalPreferences.WELCOME_DIALOG, ps);
	}

	/**
	 * Set the preference to view welcome page on startup.
	 */
	public static void setWelcomeOnStartup(boolean trueToShow, IPreferenceStore ps) {
		setBooleanPreference(IDEInternalPreferences.WELCOME_DIALOG, trueToShow, ps);
	}

	/** 
	 * Set the preference to ask whether to switch the perspective.
	 */
	public static void setConfirmPerspectiveSwitch(boolean trueToAsk, IPreferenceStore ps) {
		// TODO: use org.eclipse.ui.internal.util.PrefUtil
		if (trueToAsk) {
			// User chose Yes/Don't ask again, so always switch
			ps.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
			// leave PROJECT_OPEN_NEW_PERSPECTIVE as is
		} else {
			// User chose No/Don't ask again, so never switch
			IDEWorkbenchPlugin.getDefault().getPreferenceStore().setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_NEVER);
			// update PROJECT_OPEN_NEW_PERSPECTIVE to correspond
			ps.setValue(
					IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE,
					IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE);
		}
		
		
	}
	
	/** 
	 * Set the preference to ask whether to confirm enablement.
	 */
	public static void setConfirmEnablement(boolean trueToAsk, IPreferenceStore ps) {
		// TODO: use org.eclipse.ui.internal.util.PrefUtil
    	//PlatformUI.getWorkbench().getPreferenceStore()
		ps.setValue(IPreferenceConstants.SHOULD_PROMPT_FOR_ENABLEMENT, trueToAsk);
    	WorkbenchPlugin.getDefault().savePluginPreferences();
	}
}
