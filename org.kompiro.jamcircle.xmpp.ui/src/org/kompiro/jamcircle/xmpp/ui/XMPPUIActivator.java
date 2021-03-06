package org.kompiro.jamcircle.xmpp.ui;


import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.osgi.framework.BundleContext;

public class XMPPUIActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.kompiro.jamcircle.xmpp.ui"; //$NON-NLS-1$
	private static XMPPUIActivator plugin;
	private IPreferenceStore preferenceStore;

	public XMPPUIActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public XMPPConnectionService getConnectionService(){
		return XMPPUIContext.getDefault().getXMPPConnectionService();
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(XMPPImageConstants.USER.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user/user.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.USER_ADD.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user/user_add.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.USER_DELETE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user/user_delete.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.USER_GO.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user/user_go.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.USER_GRAY.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user/user_gray.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.USER_ORANGE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/user/user_orange.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.CONNECT.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/connect.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.DISCONNECT.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/disconnect.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.GROUP.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/group/group.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.GROUP_ADD.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/group/group_add.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.GROUP_DELETE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/group/group_delete.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.STATUS_AWAY.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/status/status_away.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.STATUS_BUSY.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/status/status_busy.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.STATUS_ONLINE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/status/status_online.png")); //$NON-NLS-1$
		reg.put(XMPPImageConstants.STATUS_OFFLINE.toString(), imageDescriptorFromPlugin(PLUGIN_ID, "icons/status/status_offline.png")); //$NON-NLS-1$
	}
	
	@Override
	public IPreferenceStore getPreferenceStore() {
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(new ConfigurationScope(),getBundle().getSymbolicName());

        }
        return preferenceStore;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static XMPPUIActivator getDefault() {
		return plugin;
	}

}
