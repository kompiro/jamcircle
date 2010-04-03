package org.kompiro.jamcircle.kanban.ui.dialog;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.model.ShowdownConverter;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class BrowserPopupDialog extends PopupDialog {
	private static ShowdownConverter converter = ShowdownConverter.getInstance();
	private Browser browser;
	private String content;

	private class CloseDialogAction extends Action{
		CloseDialogAction(){
			super(Messages.BrowserPopupDialog_close,IAction.AS_PUSH_BUTTON);
		}
		
		@Override
		public void run() {
			BrowserPopupDialog.this.close();
		}
	}
	
	@Override
	protected void fillDialogMenu(IMenuManager dialogMenu) {
		super.fillDialogMenu(dialogMenu);
		dialogMenu.add(new CloseDialogAction());
	}
	
	public BrowserPopupDialog(Shell shell,String title, String info, String content) {
		super(shell,PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, true, false,
		false, true, false, title, info);
		this.content = converter.convert(content);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		browser = new Browser(parent,SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).hint(300, 200).applyTo(browser);
		browser.setText(content);
		return browser;
	}
	
	@Override
	protected Control createTitleControl(Composite parent) {
		Control target = super.createTitleControl(parent);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true,
				false).span(1, 1).hint(300,20).applyTo(target);
		return target;
	}
	
	@Override
	protected org.eclipse.swt.graphics.Point getInitialLocation(
			org.eclipse.swt.graphics.Point initialSize) {
		org.eclipse.swt.graphics.Point target = getShell().getDisplay().getCursorLocation();
		Point windowLocation = computeWindowLocation(target.x, target.y);
		return new org.eclipse.swt.graphics.Point(windowLocation.x,windowLocation.y);
	}

	private Point computeWindowLocation(int eventX, int eventY) {
		org.eclipse.swt.graphics.Rectangle clientArea = getShell().getDisplay().getClientArea();
		Point preferredLocation = new Point(eventX + 10, eventY + 10);
		
		Dimension tipSize = new Dimension(browser.getBounds().width,browser.getBounds().height);

		// Adjust location if tip is going to fall outside display
		if (preferredLocation.y + tipSize.height > clientArea.height)  
			preferredLocation.y = eventY - tipSize.height;
		
		if (preferredLocation.x + tipSize.width > clientArea.width)
			preferredLocation.x -= (preferredLocation.x + tipSize.width) - clientArea.width;
		
		return preferredLocation; 
	}

}
