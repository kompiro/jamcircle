package org.kompiro.jamcircle;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

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
//		configurer.setShowFastViewBars(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setTitle("JAM Circle");
		// configurer.setShellStyle(SWT.CLOSE);
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
		shell.setMaximized(true);
		super.createWindowContents(shell);
	}
}
