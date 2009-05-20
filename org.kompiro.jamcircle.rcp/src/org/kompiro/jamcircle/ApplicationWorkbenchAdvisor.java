package org.kompiro.jamcircle;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.progress.IProgressService;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    private static final String ID_OF_PERSPECTIVE_KANBAN = "org.kompiro.jamcircle.kanban.ui.perspective.kanban";
    private static final String ID_OF_PERSPECTIVE_FRIENDS = "org.kompiro.jamcircle.xmpp.ui.perspective.friends";
	private boolean postStarted = false;
	private TrayItem trayItem;

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return ID_OF_PERSPECTIVE_KANBAN;
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setExitOnLastWindowClose(false);
		String defaultPerspective = ID_OF_PERSPECTIVE_KANBAN + "," + ID_OF_PERSPECTIVE_FRIENDS;
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, defaultPerspective);
	}

	@Override
	public boolean openWindows() {
		if(postStarted){
			return super.openWindows();
		}
		return true;
	}
	
	@Override
	public void postStartup() {
		createTray();
		postStarted = true;
	}

	private void createTray() {
		Image image = RCPActivator.getDefault().getImageRegistry().get(ImageConstants.APPLICATION_IMAGE.toString());
		Display display = PlatformUI.getWorkbench().getDisplay();
		Tray tray = display.getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setImage(image);
		trayItem.setText("JAM Circle");
		trayItem.setToolTipText("JAM Circle");
		trayItem.addMenuDetectListener(new MenuDetectListener(){
		
			public void menuDetected(MenuDetectEvent e) {
				final Shell shell = new Shell();
				Menu menu = new Menu(shell);
				MenuItem open = new MenuItem(menu, SWT.POP_UP);
				open.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						IWorkbench workbench = PlatformUI.getWorkbench();
						IProgressService service = (IProgressService) workbench.getService(IProgressService.class);
						IRunnableContext context = new ProgressMonitorDialog(shell);

						try {
							service.runInUI(context, new IRunnableWithProgress(){

								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									openWindows();
								}
								
							}, null);
						} catch (InvocationTargetException ex) {
						} catch (InterruptedException ex) {
						}

						shell.dispose();
					}
				});
				open.setText("open Boards");
				MenuItem exit = new MenuItem(menu, SWT.POP_UP);
				exit.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						PlatformUI.getWorkbench().close();
						shell.dispose();
					}
				});
				exit.setText("exit JAM Circle");
				menu.setVisible(true);
			}
		});
	}
	
	@Override
	public void postShutdown() {
		trayItem.dispose();
		PlatformUI.getWorkbench().getDisplay().getSystemTray().dispose();
		super.postShutdown();
	}
		
}
