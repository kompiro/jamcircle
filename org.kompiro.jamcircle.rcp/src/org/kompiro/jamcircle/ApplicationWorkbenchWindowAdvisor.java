package org.kompiro.jamcircle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.*;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

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
//		configurer.setInitialSize(new Point(400, 400));
		configurer.setShowPerspectiveBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(false);
		configurer.setShowFastViewBars(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setTitle("JAM Circle");
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
		getShell().setMinimized(true);
	}
	
	@Override
	public void postWindowOpen() {
		getShell().setAlpha(0);
		getShell().setVisible(false);
	}
	
	private void hideMenus() {
		IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		IMenuManager fileMenu = menuManager.findMenuUsingPath("file");
		fileMenu.remove("org.eclipse.ui.openLocalFile");
		fileMenu.remove("converstLineDelimitersTo");
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
