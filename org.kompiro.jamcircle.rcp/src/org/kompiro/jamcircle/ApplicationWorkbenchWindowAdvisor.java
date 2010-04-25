package org.kompiro.jamcircle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.*;
import org.kompiro.jamcircle.rcp.internal.preferences.PreferenceConstants;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static final String APP_NAME = "JAM Circle"; //$NON-NLS-1$
	private static final String CONVERST_LINE_DELIMITERS_TO = "converstLineDelimitersTo";//$NON-NLS-1$
	private static final String ORG_ECLIPSE_UI_OPEN_LOCAL_FILE = "org.eclipse.ui.openLocalFile"; //$NON-NLS-1$

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowPerspectiveBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(false);
		configurer.setShowFastViewBars(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setTitle(APP_NAME);
	}
	
	
	@Override
	public void createWindowContents(Shell shell) {
		Image image = RCPActivator.getDefault().getImageRegistry().get(ImageConstants.APPLICATION_IMAGE.toString());
		//		PaintListener listener = new PaintListener(){
//
//			public void paintControl(PaintEvent e) {
//				System.out.println(".paintControl()");
//				ImageDescriptor desc = RCPActivator.getImageDescriptor("icons/user.png");
//				Image image = desc.createImage();
//				e.gc.drawImage(image,0,-50);
//				e.gc.drawLine(0, 0, 400, 400);
//				image.dispose();
//			}
//			
//		};
//		shell.addPaintListener(listener);
		shell.setImage(image);
		super.createWindowContents(shell);
	}
		
	@Override
	public void postWindowCreate() {
		hideMenus();
		// for MacOSX, it doesn't set setMinimized(false) because it shows nothing.
		if(isMinimized()) getShell().setMinimized(true);
	}
	
	@Override
	public void postWindowOpen() {
		if(isMinimized()){
			getShell().setAlpha(0);
			getShell().setVisible(false);
		}
	}

	private boolean isMinimized() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		if(preferenceStore == null) return false;
		return preferenceStore.getBoolean(PreferenceConstants.MINIMIZED);
	}

	private IPreferenceStore getPreferenceStore() {
		RCPActivator activator = RCPActivator.getDefault();
		if(activator == null ) return null;
		IPreferenceStore preferenceStore = activator.getPreferenceStore();
		return preferenceStore;
	}

	
	private void hideMenus() {
		IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		IMenuManager fileMenu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_FILE);
		fileMenu.remove(ORG_ECLIPSE_UI_OPEN_LOCAL_FILE);
		fileMenu.remove(CONVERST_LINE_DELIMITERS_TO);
		
	}

	@Override
	public boolean preWindowShellClose() {
		Shell shell = getShell();
		RCPUtils.modifyAlphaForDropout(shell);
		return false;
	}
	
	private Shell getShell() {
		return getWindow().getShell();
	}
	
	private IWorkbenchWindow getWindow() {
		return getWindowConfigurer().getWindow();
	}

}
