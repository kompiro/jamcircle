package org.kompiro.jamcircle;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
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
		shell.setAlpha(0);
		super.createWindowContents(shell);
	}
		
	@Override
	public void postWindowOpen() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setVisible(false);
	}
	
	@Override
	public boolean preWindowShellClose() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		shell.setAlpha(0);
		shell.setVisible(false);
		return false;
	}
}
