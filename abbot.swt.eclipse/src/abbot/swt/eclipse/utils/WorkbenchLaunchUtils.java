package abbot.swt.eclipse.utils;

import junit.framework.Assert;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;

/**
 * Utilities (preferably static) for use when launching workbenches.
 * @author tlroche
 * @version $Id: WorkbenchLaunchUtils.java 1596 2005-05-06 21:52:48Z tlroche $
 */
public class WorkbenchLaunchUtils {
	public static final String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 2000,2003\nUS Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	/**
	 * Workspace-relative path to the "gold folder" containing
	 * "gold files" against which we will test our generands.
	 * CONTRACT: not empty, does not begin or end with separator.
	 */
	// not in UI
	protected static final String WORKSPACE_RELATIVE_PATH_TO_GOLD = "GoldFolder";

	/**
	 * Set the preference found in UI as
	 * Windows&gt;Preferences&gt;Workbench&gt;Perspectives&gt;
	 * Switch to associated perspective when creating a new project
	 * to always switch. Note that this pref is persisted!
	 */
	public static void alwaysSwitchToPerspective(IWorkbench iw) {
		// TODO: use org.eclipse.ui.internal.util.PrefUtil
		IPreferenceStore ps = iw.getPreferenceStore();
		PreferenceUtils.setSwitchToPerspectivePreference(IDEInternalPreferences.PSPM_ALWAYS, ps);
	}

	/**
	 * Clear the preference to view welcome page at startup.
	 * Note that this pref is persisted!
	 */
	public static void preventWelcomePage(IWorkbench iw) {
		// TODO: use org.eclipse.ui.internal.util.PrefUtil
		IPreferenceStore ps = iw.getPreferenceStore();
		PreferenceUtils.setWelcomeOnStartup(false, ps);
	}

	/**
	 * Set a default, workspace-relative path to a "gold folder"
	 * containing files to which we will later compare our generands.
	 */
	public static void setGoldFolder() {
		setGoldFolder(WORKSPACE_RELATIVE_PATH_TO_GOLD);
	}

	/**
	 * Set a workspace-relative path to a "gold folder"
	 * containing files to which we will later compare our generands.
	 * CONTRACT: workspace-relative path is well-formed and valid.
	 */
	public static void setGoldFolder(String wrp) {
		FileUtils.setGoldFolder(wrp);
		Assert.assertNotNull(FileUtils.getGoldFolder());
	}

}
