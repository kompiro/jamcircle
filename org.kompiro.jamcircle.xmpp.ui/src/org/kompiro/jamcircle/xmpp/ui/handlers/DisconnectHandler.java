package org.kompiro.jamcircle.xmpp.ui.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.kompiro.jamcircle.xmpp.ui.XMPPUIActivator;

public class DisconnectHandler extends AbstractHandler {
	
	public DisconnectHandler() {
		setBaseEnabled(isConnecting());
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ApplicationWindow window = (ApplicationWindow)HandlerUtil
		.getActiveWorkbenchWindowChecked(event);
		Job job = new Job("Disconnect"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				disconnectConnection(monitor);
				updateMenu(window);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		setBaseEnabled(isConnecting());
	}
	
	private boolean isConnecting() {
		return getService().isConnecting();
	}

	private void disconnectConnection(IProgressMonitor monitor) {
		getService().logout(monitor);
	}

	private XMPPConnectionService getService() {
		return XMPPUIActivator.getDefault().getConnectionService();
	}

	/*
	 * This method is not necessary to {@link ConnectHandler}.
	 * Because MenuManager is updated when WorkbenchWindow is modified.
	 * But this class doesn't use DialogWindow.
	 */
	private void updateMenu(final ApplicationWindow window) {
		window.getShell().getDisplay().asyncExec(new Runnable(){
			public void run() {
				window.getMenuBarManager().update(IAction.TEXT);
			}
		});
	}

	
}
