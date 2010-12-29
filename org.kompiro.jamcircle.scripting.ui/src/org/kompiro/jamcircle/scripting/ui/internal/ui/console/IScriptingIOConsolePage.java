package org.kompiro.jamcircle.scripting.ui.internal.ui.console;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.console.*;
import org.kompiro.jamcircle.scripting.ui.internal.action.ScrollLockAction;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.action.*;
import org.kompiro.jamcircle.scripting.ui.internal.ruby.console.RubyScriptingConsole;

/**
 * A page for an IOConsole
 * 
 * @since 3.1
 *        Copied from org.eclipse.ui.console.IOConsole 3.4.0 by kompiro
 */
public class IScriptingIOConsolePage extends TextConsolePage {

	private ScrollLockAction fScrollLockAction;

	private boolean fReadOnly;

	private IPropertyChangeListener fPropertyChangeListener;

	private ShutdownAction fShutdownAction;

	private GemInstallAction fGemInstallAction;

	private GemListAction fGemListAction;

	private GemUninstallAction fGemUninstallAction;

	private HistoryAction fHistoryAction;

	public IScriptingIOConsolePage(TextConsole console, IConsoleView view) {
		super(console, view);

		fPropertyChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				String property = event.getProperty();
				if (property.equals(IConsoleConstants.P_CONSOLE_OUTPUT_COMPLETE)) {
					setReadOnly();
				}
			}
		};
		console.addPropertyChangeListener(fPropertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite
	 * )
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		if (fReadOnly) {
			IScriptingIOConsoleViewer viewer = (IScriptingIOConsoleViewer) getViewer();
			viewer.setReadOnly();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.console.TextConsolePage#createViewer(org.eclipse.swt.widgets
	 * .Composite,
	 * org.eclipse.ui.console.TextConsole)
	 */
	protected TextConsoleViewer createViewer(Composite parent) {
		return new IScriptingIOConsoleViewer(parent, (TextConsole) getConsole());
	}

	public void setAutoScroll(boolean scroll) {
		IScriptingIOConsoleViewer viewer = (IScriptingIOConsoleViewer) getViewer();
		if (viewer != null) {
			viewer.setAutoScroll(scroll);
			fScrollLockAction.setChecked(!scroll);
		}
	}

	/**
	 * Informs the viewer that it's text widget should not be editable.
	 */
	public void setReadOnly() {
		fReadOnly = true;
		IScriptingIOConsoleViewer viewer = (IScriptingIOConsoleViewer) getViewer();
		if (viewer != null) {
			viewer.setReadOnly();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.console.TextConsolePage#createActions()
	 */
	protected void createActions() {
		super.createActions();
		fScrollLockAction = new ScrollLockAction();
		fScrollLockAction.setConsoleView(getConsoleView());
		fShutdownAction = new ShutdownAction();
		RubyScriptingConsole console = (RubyScriptingConsole) getConsole();
		fShutdownAction.setConsole(console);
		fHistoryAction = new HistoryAction();
		fHistoryAction.setConsole(console);
		fGemInstallAction = new GemInstallAction();
		fGemUninstallAction = new GemUninstallAction();
		fGemListAction = new GemListAction();
		setAutoScroll(!fScrollLockAction.isChecked());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.console.TextConsolePage#contextMenuAboutToShow(org.eclipse
	 * .jface.action.IMenuManager)
	 */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		super.contextMenuAboutToShow(menuManager);
		menuManager.add(fScrollLockAction);
		IScriptingIOConsoleViewer viewer = (IScriptingIOConsoleViewer) getViewer();
		if (!viewer.isReadOnly()) {
			menuManager.remove(ActionFactory.CUT.getId());
			menuManager.remove(ActionFactory.PASTE.getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.console.TextConsolePage#configureToolBar(org.eclipse.jface
	 * .action.IToolBarManager)
	 */
	protected void configureToolBar(IToolBarManager mgr) {
		super.configureToolBar(mgr);
		mgr.appendToGroup(IConsoleConstants.OUTPUT_GROUP, fScrollLockAction);
		mgr.appendToGroup(IConsoleConstants.OUTPUT_GROUP, fHistoryAction);
		mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, fShutdownAction);
		mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, fGemInstallAction);
		mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, fGemUninstallAction);
		mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, fGemListAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
		fClearOutputAction = null;
		fSelectionActions.clear();
		fGlobalActions.clear();
		if (fScrollLockAction != null) {
			fScrollLockAction.dispose();
			fScrollLockAction = null;
		}
		fHistoryAction = null;
		fShutdownAction = null;
		fGemInstallAction = null;
		getConsole().removePropertyChangeListener(fPropertyChangeListener);
		TextConsoleViewer viewer = getViewer();
		if (viewer != null) {
			viewer.setEditable(false);
			super.dispose();
		}
	}

}
