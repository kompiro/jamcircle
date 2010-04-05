package org.kompiro.jamcircle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.kompiro.jamcircle.messages"; //$NON-NLS-1$
	public static String ApplicationActionBarAdvisor_Edit;
	public static String ApplicationActionBarAdvisor_File;
	public static String ApplicationActionBarAdvisor_Help;
	public static String ApplicationActionBarAdvisor_Window;
	public static String ApplicationWorkbenchAdvisor_error;
	public static String ApplicationWorkbenchAdvisor_exit_menu;
	public static String ApplicationWorkbenchAdvisor_icon_tooltip;
	public static String ApplicationWorkbenchAdvisor_open_menu;
	public static String ApplicationWorkbenchAdvisor_open_message;
	public static String ApplicationWorkbenchAdvisor_started_message;
	public static String ApplicationWorkbenchAdvisor_started_title;
	public static String RCPActivator_error;
	public static String SiteAction_text;
	public static String RCPPreferencePage_description;
	public static String RCPPreferencePage_minimized_message;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
