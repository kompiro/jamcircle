package org.kompiro.jamcircle.rcp.win32.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.IKeyStateManager;
import org.kompiro.jamcircle.KeyEventListener;
import org.sf.feeling.swt.win32.extension.hook.Hook;
import org.sf.feeling.swt.win32.extension.hook.data.HookData;
import org.sf.feeling.swt.win32.extension.hook.data.KeyboardHookData;
import org.sf.feeling.swt.win32.extension.hook.listener.HookEventListener;

public class KeyStateManagerFowWin32 implements IKeyStateManager {
	
    private static final int KEY_CODE_OF_LEFT_WIN = 91;
	private static final int KEY_CODE_OF_K = 75;
	private List<KeyEventListener> acceptors = new ArrayList<KeyEventListener>();
	private boolean pressingWinKey = false;

	public KeyStateManagerFowWin32(){
	}
	
	/* (non-Javadoc)
	 * @see org.kompiro.jamcircle.rcp.win32.internal.IKeyStateManager#install()
	 */
	public void install() {
		Hook.KEYBOARD.install();
		Hook.KEYBOARD.addListener(new HookEventListener() {
			
			public void acceptHookData(HookData data) {
				KeyboardHookData keyData = (KeyboardHookData) data;
				if(isAcceptedPress(keyData)){
					Display display = Display.getDefault();
					display.syncExec(new Runnable() {
						public void run() {
							for(KeyEventListener acceptor:acceptors){
								acceptor.fireEvent();
							}
						}
					});
				}
			}
			
		});
	}

	private boolean isAcceptedPress(KeyboardHookData keyData) {
		if(isStartedWinKeyPressing(keyData)){
			pressingWinKey = true;
			return false;
		}
		if(pressingWinKey == false){
			return false;
		}
		if(isEndedWinKeyPressing(keyData)){
			pressingWinKey = false;
			return false;
		}
		if(isStartedAltKKeyPressing(keyData)){
			pressingWinKey = false;
			return true;
		}
		return false;
	}

	private boolean isStartedAltKKeyPressing(KeyboardHookData keyData) {
		return isPressingTransition(keyData) 
		&& isPressing(keyData,KEY_CODE_OF_K)
		&& keyData.isAltPressed();
	}

	private boolean isEndedWinKeyPressing(KeyboardHookData keyData) {
		return isPressingTransition(keyData)
		&& isPressing(keyData,KEY_CODE_OF_LEFT_WIN);
	}

	private boolean isStartedWinKeyPressing(KeyboardHookData keyData) {
		return isPressingTransition(keyData)
		&& isPressing(keyData,KEY_CODE_OF_LEFT_WIN);
	}

	private boolean isPressing(KeyboardHookData keyData,int keyCode) {
		return keyData.getWParam() == keyCode;
	}

	private boolean isPressingTransition(KeyboardHookData keyData) {
		return keyData.getTransitionState() == true;
	}

	/* (non-Javadoc)
	 * @see org.kompiro.jamcircle.rcp.win32.internal.IKeyStateManager#uninstall()
	 */
	public void uninstall() {
		Hook.KEYBOARD.uninstall();
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));

		Tray tray = display.getSystemTray();
		final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.setText("JAM Circle");
		trayItem.setToolTipText("JAM Circle");
		IKeyStateManager manager = new KeyStateManagerFowWin32();
		manager.addKeyEventListener(new KeyEventListener() {
			
			public void fireEvent() {
				Display display = Display.getDefault();
				Shell parent = new Shell(display);
				ToolTip tip = new ToolTip(parent, SWT.ICON_INFORMATION);
				tip.setText("Opening Board");
				tip.setMessage("Board is opening....");
				trayItem.setToolTip(tip);
				tip.setVisible(true);
			}

		});
		manager.install();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		manager.uninstall();
		trayItem.dispose();
		tray.dispose();
		display.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.kompiro.jamcircle.rcp.win32.internal.IKeyStateManager#addKeyEventListener(org.kompiro.jamcircle.rcp.win32.internal.KeyEventListener)
	 */
	public void addKeyEventListener(KeyEventListener listener){
		this.acceptors.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.kompiro.jamcircle.rcp.win32.internal.IKeyStateManager#removeKeyEventListener(org.kompiro.jamcircle.rcp.win32.internal.KeyEventListener)
	 */
	public void removeKeyEventListener(KeyEventListener listener){
		this.acceptors.remove(listener);
	}
	
}
