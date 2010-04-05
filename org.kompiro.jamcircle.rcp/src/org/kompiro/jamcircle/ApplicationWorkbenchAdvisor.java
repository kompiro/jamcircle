package org.kompiro.jamcircle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;
import org.eclipse.ui.statushandlers.StatusManager;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String WIN32_KEY_STATE_MANAGER = "org.kompiro.jamcircle.rcp.win32.internal.KeyStateManagerFowWin32"; //$NON-NLS-1$
	private static final String ID_SEPARATOR = ","; //$NON-NLS-1$
	private static final String ID_OF_PERSPECTIVE_KANBAN = "org.kompiro.jamcircle.kanban.ui.perspective.kanban"; //$NON-NLS-1$
    private static final String ID_OF_PERSPECTIVE_FRIENDS = "org.kompiro.jamcircle.xmpp.ui.perspective.friends"; //$NON-NLS-1$
	private IKeyStateManager manager = null;
	private TrayItem trayItem;
	private KeyEventListener listener;
	private ToolTip tip;

	public ApplicationWorkbenchAdvisor() {
		String className = null;
		if (RCPUtils.isWindows()) {
			className = WIN32_KEY_STATE_MANAGER;
		}
		if (className != null){
			try {
				Class<?> clazz = Class.forName(className);
				manager = (IKeyStateManager) clazz.newInstance();
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, RCPActivator.PLUGIN_ID, Messages.ApplicationWorkbenchAdvisor_error,e);
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
		String defaultPerspective = ID_OF_PERSPECTIVE_KANBAN + ID_SEPARATOR + ID_OF_PERSPECTIVE_FRIENDS;
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, defaultPerspective);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.INITIAL_FAST_VIEW_BAR_LOCATION, IWorkbenchPreferenceConstants.LEFT);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.LEFT);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OTHER_IN_PERSPECTIVE_MENU, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OPEN_ON_PERSPECTIVE_BAR, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DISABLE_NEW_FAST_VIEW, true);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_INTRO, false);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DEFAULT_PERSPECTIVE_ID, ID_OF_PERSPECTIVE_KANBAN);
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
		trayItem.setText(ApplicationWorkbenchWindowAdvisor.APP_NAME);
		trayItem.setToolTipText(Messages.ApplicationWorkbenchAdvisor_icon_tooltip);
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
				openItem(shell, menu);
				extensionItem(menu);
				exitItem(shell, menu);
				menu.setVisible(true);
			}

			private void extensionItem(Menu menu) {
				separator(menu);
			}

			private void openItem(final Shell shell, final Menu menu) {
				MenuItem open = new MenuItem(menu, SWT.POP_UP);
				open.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						openWindowInProgress(trayItem);
						menu.dispose();
						shell.dispose();
					}
				});
				open.setText(Messages.ApplicationWorkbenchAdvisor_open_menu);
				open.setImage(getAppImage());
			}

			private void separator(final Menu menu) {
				new MenuItem(menu, SWT.SEPARATOR);
			}
			
			private void exitItem(final Shell shell, final Menu menu) {
				MenuItem exit = new MenuItem(menu, SWT.POP_UP);
				exit.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						PlatformUI.getWorkbench().close();
						menu.dispose();
						shell.dispose();
					}
				});
				exit.setText(Messages.ApplicationWorkbenchAdvisor_exit_menu);
				exit.setImage(getExitImage());
			}

		});
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
		if(shell.isVisible() == false || shell.getMinimized()){
			showBoard(shell);
		}else{
			hideBoard(shell);
		}
	}

	private void showBoard(Shell shell) {
		RCPUtils.modifyAlphaForSurface(shell);
		shell.setMinimized(false);
	}

	private void hideBoard(Shell shell) {
		RCPUtils.modifyAlphaForDropout(shell);
		closeToolTip();
	}
		
	public void showToolTip(String title){
		showToolTip(title, null);
	}
	
	public void showToolTip(String title, String message){
		if(tip != null){
			closeToolTip();
		}
		tip = new ToolTip(new Shell(getDisplay()) , SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setText(title);
		if(message != null)	tip.setMessage(message);
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
