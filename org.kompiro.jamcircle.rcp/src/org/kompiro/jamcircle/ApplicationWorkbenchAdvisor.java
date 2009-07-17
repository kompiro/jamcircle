package org.kompiro.jamcircle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.*;
import org.eclipse.ui.statushandlers.StatusManager;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String ID_OF_PERSPECTIVE_KANBAN = "org.kompiro.jamcircle.kanban.ui.perspective.kanban";
    private static final String ID_OF_PERSPECTIVE_FRIENDS = "org.kompiro.jamcircle.xmpp.ui.perspective.friends";
	private boolean postStarted = false;
	private IKeyStateManager manager = null;
	private TrayItem trayItem;
	private KeyEventListener listener;

	public ApplicationWorkbenchAdvisor() {
		String platform = SWT.getPlatform();
		String className = null;
		if ("win32".equals (platform) || "wpf".equals (platform)) {
			className = "org.kompiro.jamcircle.rcp.win32.internal.KeyStateManagerFowWin32";
		}
		if (className != null){
			try {
				Class<?> clazz = Class.forName(className);
				manager = (IKeyStateManager) clazz.newInstance();
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, RCPActivator.PLUGIN_ID, "Error has occured",e);
				StatusManager.getManager().handle(status);
			}
		}
	}
	
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
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, defaultPerspective);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.INITIAL_FAST_VIEW_BAR_LOCATION, IWorkbenchPreferenceConstants.LEFT);
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
		initalizeManager();
		postStarted = true;
	}

	private void initalizeManager() {
		if(manager == null) return;
		manager.install();
		listener = new KeyEventListener() {
			public void fireEvent() {
				showToolTipForWaitOpeningBoard(trayItem);
			}
		};
		manager.addKeyEventListener(listener);
	}

	private void createTray() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Tray tray = display.getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setImage(getAppImage());
		trayItem.setText("JAM Circle");
		trayItem.setToolTipText("JAM Circle");
		trayItem.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openWindowInProgress(trayItem);
			}
		});
		trayItem.addMenuDetectListener(new MenuDetectListener(){
		
			public void menuDetected(MenuDetectEvent e) {
				final Shell shell = new Shell();
				Menu menu = new Menu(shell);
				MenuItem open = new MenuItem(menu, SWT.POP_UP);
				open.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						openWindowInProgress(trayItem);
						shell.dispose();
					}
				});
				open.setText("open Boards");
				open.setImage(getAppImage());
				MenuItem exit = new MenuItem(menu, SWT.POP_UP);
				exit.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						PlatformUI.getWorkbench().close();
						shell.dispose();
					}
				});
				exit.setText("exit JAM Circle");
				exit.setImage(getExitImage());
				menu.setVisible(true);
			}
		});
	}
	
	@Override
	public boolean preShutdown() {
		shutdownManager();
		Tray tray = PlatformUI.getWorkbench().getDisplay().getSystemTray();
		TrayItem[] items = tray.getItems();
		for(TrayItem item : items){
			item.dispose();
		}
		tray.dispose();
		return super.preShutdown();
	}

	private void shutdownManager() {
		if(manager == null)return;
		manager.removeKeyEventListener(listener);
		manager.uninstall();
	}

	private void openWindowInProgress(TrayItem trayItem) {
//		IWorkbench workbench = PlatformUI.getWorkbench();
//		IProgressService service = (IProgressService) workbench.getService(IProgressService.class);
//		final Shell shell = new Shell();
//		IRunnableContext context = new ProgressMonitorDialog(shell);

//		try {
//			String jobName = "open JAM Circle";
//			UIJob job = new UIJob(jobName){
//
//				@Override
//				public IStatus runInUIThread(IProgressMonitor monitor) {
//					monitor.subTask("open Windows");
//					openWindows();
//					monitor.internalWorked(50.0);
//					return Status.OK_STATUS;
//				}
//				
//			};
//			IProgressMonitor group = Job.getJobManager().createProgressGroup();
//			group.beginTask(jobName, 100);
//			job.setProgressGroup(group, 50);
//			job.setProperty(new QualifiedName("org.kompiro.jamcircle", "rcp"), group);
//			job.schedule();
//			try {
//				job.join();
//			} catch (InterruptedException e) {
//				RCPActivator.getDefault().logError(e);
//			}
//			service.showInDialog(null, job);
//			service.runInUI(context, new IRunnableWithProgress(){
//
//				public void run(IProgressMonitor monitor)
//						throws InvocationTargetException, InterruptedException {
//					monitor.beginTask("opening board",100);
//					openWindows();
//					monitor.internalWorked(50);
//				}
//				
//			}, null);
//		} catch (InvocationTargetException ex) {
//			RCPActivator.getDefault().logError(ex);
//		} catch (InterruptedException ex) {
//			RCPActivator.getDefault().logError(ex);
//		}

//		try {
//			service.busyCursorWhile(new IRunnableWithProgress(){
//
//				public void run(IProgressMonitor monitor)
//						throws InvocationTargetException, InterruptedException {
//					monitor.setTaskName("opening board");
//					openWindows();
//					monitor.done();
//				}
//				
//			});
//		} catch (InvocationTargetException ex) {
//			RCPActivator.getDefault().logError(ex);
//		} catch (InterruptedException ex) {
//			RCPActivator.getDefault().logError(ex);
//		}
			showToolTipForWaitOpeningBoard(trayItem);
	}
	
	private void showToolTipForWaitOpeningBoard(TrayItem trayItem) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell parent = new Shell(display);
		ToolTip tip = new ToolTip(parent, SWT.ICON_INFORMATION);
		tip.setText("Opening Board");
		tip.setMessage("JAM Circle's board is opening.... Please wait...");
		trayItem.setToolTip(tip);
		tip.setVisible(true);
		openWindows();
		tip.setVisible(false);
	}

	
	private ImageRegistry getImageRegistry() {
		return RCPActivator.getDefault().getImageRegistry();
	}

	private Image getAppImage() {
		return getImageRegistry().get(ImageConstants.APPLICATION_IMAGE.toString());
	}

	private Image getExitImage() {
		return getImageRegistry().get(ImageConstants.EXIT_IMAGE.toString());
	}
		
}
