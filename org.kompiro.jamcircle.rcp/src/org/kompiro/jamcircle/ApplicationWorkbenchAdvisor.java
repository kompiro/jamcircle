package org.kompiro.jamcircle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.*;
import org.eclipse.ui.statushandlers.StatusManager;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String ID_OF_PERSPECTIVE_KANBAN = "org.kompiro.jamcircle.kanban.ui.perspective.kanban";
    private static final String ID_OF_PERSPECTIVE_FRIENDS = "org.kompiro.jamcircle.xmpp.ui.perspective.friends";
	private IKeyStateManager manager = null;
	private TrayItem trayItem;
	private KeyEventListener listener;

	public ApplicationWorkbenchAdvisor() {
		String className = null;
		if (isWindows()) {
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

	private boolean isWindows() {
		String platform = SWT.getPlatform();
		return "win32".equals (platform) || "wpf".equals (platform);
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
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.LEFT);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OTHER_IN_PERSPECTIVE_MENU, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OPEN_ON_PERSPECTIVE_BAR, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DISABLE_NEW_FAST_VIEW, true);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
	}
	
	@Override
	public void postStartup() {
		createTray();
		initalizeManager();
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
		showToolTipForWaitOpeningBoard(trayItem);
	}
	
	private void showToolTipForWaitOpeningBoard(TrayItem trayItem) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();
		ToolTip tip = null;
		if(isWindows()){
			tip = new ToolTip(shell , SWT.ICON_INFORMATION);
			tip.setText("Opening Board");
			tip.setMessage("JAM Circle's board is open.");
			trayItem.setToolTip(tip);
			tip.setVisible(true);			
		}
		
		shell.setVisible(true);
		shell.setActive();
		shell.setFocus();
		modifyAlpha(shell, 128);
		
		if(isWindows()){
			tip.setVisible(false);
			tip.dispose();
		}
	}

	private void modifyAlpha(final Shell shell, final int alpha) {
		if(alpha == 0){ 
			shell.setAlpha(255);
			return;
		}
		shell.getDisplay().timerExec(50, new Runnable() {
			
			public void run() {
				shell.setAlpha(255 - alpha);
				modifyAlpha(shell, alpha / 2);
			}
		});
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
