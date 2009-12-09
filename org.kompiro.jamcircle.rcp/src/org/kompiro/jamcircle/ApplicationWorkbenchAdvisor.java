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
	private ToolTip tip;

	public ApplicationWorkbenchAdvisor() {
		String className = null;
		if (RCPUtils.isWindows()) {
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
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.LEFT);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OTHER_IN_PERSPECTIVE_MENU, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OPEN_ON_PERSPECTIVE_BAR, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DISABLE_NEW_FAST_VIEW, true);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
	}
	
	@Override
	public void preStartup() {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				createTray();
				initalizeManager();
			}
		});
	}
	
	@Override
	public void postStartup() {
		trayItem.setImage(getAppImage());
		showToolTip("Started JAM Circle.", "Please double click me!");
	}

	private void initalizeManager() {
		if(manager == null) return;
		manager.install();
		listener = new KeyEventListener() {
			public void fireEvent() {
				handleShowOrHideBoard(trayItem);
			}
		};
		manager.addKeyEventListener(listener);
	}

	private void createTray() {
		Display display = getDisplay();
		Tray tray = display.getSystemTray();
		trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setImage(getAppOffImage());
		trayItem.setText("JAM Circle");
		trayItem.setToolTipText("Double click if you want to open board.");
		trayItem.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				openWindowInProgress(trayItem);
			}
		});
		trayItem.addMenuDetectListener(new MenuDetectListener(){
		
			public void menuDetected(MenuDetectEvent e) {
				final Shell shell = new Shell();
				final Menu menu = new Menu(shell);
				MenuItem open = new MenuItem(menu, SWT.POP_UP);
				open.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						openWindowInProgress(trayItem);
						menu.dispose();
						shell.dispose();
					}
				});
				open.setText("&Open Board");
				open.setImage(getAppImage());
				MenuItem exit = new MenuItem(menu, SWT.POP_UP);
				exit.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						PlatformUI.getWorkbench().close();
						menu.dispose();
						shell.dispose();
					}
				});
				exit.setText("&Exit JAM Circle");
				exit.setImage(getExitImage());
				menu.setVisible(true);
			}
		});
		
		showToolTip("Welcome JAM Circle world.", "starting JAM Circle.");
	}


	private Display getDisplay() {
		Display display = Display.getDefault();
		return display;
	}
	
	@Override
	public boolean preShutdown() {
		shutdownManager();
		Tray tray = getDisplay().getSystemTray();
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
		handleShowOrHideBoard(trayItem);
	}
	
	private void handleShowOrHideBoard(TrayItem trayItem) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();
		if(shell.isVisible()){
			hideBoard(shell);
		}else{
			showBoard(shell);
		}
	}

	private void showBoard(Shell shell) {
		showToolTip("Opening Board","JAM Circle's board is open.");
		RCPUtils.modifyAlphaForSurface(shell);
//		closeToolTip();
	}

	private void hideBoard(Shell shell) {
		RCPUtils.modifyAlphaForDropout(shell);
		closeToolTip();
	}
	
	private void showToolTip(String title, String message){
		if(tip != null){
			closeToolTip();
		}
		tip = new ToolTip(new Shell(getDisplay()) , SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setText(title);
		tip.setMessage(message);
		trayItem.setToolTip(tip);
		tip.setVisible(true);					
	}
	
	private void closeToolTip(){
		if(tip == null) return;
		tip.setVisible(false);
		tip.getParent().close();
		tip.dispose();
		tip = null;
	}
	
	private ImageRegistry getImageRegistry() {
		return RCPActivator.getDefault().getImageRegistry();
	}

	private Image getAppImage() {
		return getImageRegistry().get(ImageConstants.APPLICATION_IMAGE.toString());
	}

	private Image getAppOffImage() {
		return getImageRegistry().get(ImageConstants.APPLICATION_OFF_IMAGE.toString());
	}

	private Image getExitImage() {
		return getImageRegistry().get(ImageConstants.EXIT_IMAGE.toString());
	}
		
}
