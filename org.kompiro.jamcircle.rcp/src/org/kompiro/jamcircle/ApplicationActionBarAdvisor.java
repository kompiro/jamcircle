package org.kompiro.jamcircle;

import org.eclipse.jface.action.*;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction preferenceAction;
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;

	private IWorkbenchAction newAction;
	private IWorkbenchAction exportAction;
	private IWorkbenchAction importAction;
	
	private IWorkbenchAction selectAllAction;
	private IWorkbenchAction undoAction;
	private IWorkbenchAction redoAction;
	private IWorkbenchAction cutAction;
	private IWorkbenchAction copyAction;
	private IWorkbenchAction pasteAction;
	private IWorkbenchAction deleteAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
		preferenceAction = ActionFactory.PREFERENCES.create(window);
		register(preferenceAction);
		undoAction = ActionFactory.UNDO.create(window);
		register(undoAction);
		redoAction = ActionFactory.REDO.create(window);
		register(redoAction);
		cutAction = ActionFactory.CUT.create(window);
		register(cutAction);
		copyAction = ActionFactory.COPY.create(window);
		register(copyAction);
		pasteAction = ActionFactory.PASTE.create(window);
		register(pasteAction);
		deleteAction = ActionFactory.DELETE.create(window);
		register(deleteAction);
		selectAllAction = ActionFactory.SELECT_ALL.create(window);
		register(selectAllAction);
		newAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);
		register(newAction);
		exportAction = ActionFactory.EXPORT.create(window);
		register(exportAction);
		importAction = ActionFactory.IMPORT.create(window);
		register(importAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		createFileMenu(menuBar);
		createEditMenu(menuBar);
		createWindowMenu(menuBar);
		createHelpMenu(menuBar);
	}

	private void createHelpMenu(IMenuManager menuBar) {
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_Help, IWorkbenchActionConstants.M_HELP);
		helpMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		helpMenu.add(aboutAction);
		menuBar.add(helpMenu);
	}

	private void createWindowMenu(IMenuManager menuBar) {
		MenuManager windowMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_Window, IWorkbenchActionConstants.M_WINDOW);
		windowMenu.add(new Separator(IWorkbenchActionConstants.ADD_EXT));
		windowMenu.add(preferenceAction);
		menuBar.add(windowMenu);
	}

	private void createEditMenu(IMenuManager menuBar) {
		MenuManager editMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_Edit, IWorkbenchActionConstants.M_EDIT);
		editMenu.add(undoAction);
		editMenu.add(redoAction);
		editMenu.add(new Separator(IWorkbenchActionConstants.UNDO_EXT));
		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.add(new Separator(IWorkbenchActionConstants.CUT_EXT));
		editMenu.add(deleteAction);
		editMenu.add(selectAllAction);
		editMenu.add(new Separator(IWorkbenchActionConstants.EDIT_END));
		editMenu.add(new Separator(IWorkbenchActionConstants.ADD_EXT));
		menuBar.add(editMenu);
	}

	private void createFileMenu(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_File, IWorkbenchActionConstants.M_FILE);
		fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		fileMenu.add(newAction);
		fileMenu.add(new Separator(IWorkbenchActionConstants.LAUNCH_EXT));
		fileMenu.add(importAction);
		fileMenu.add(exportAction);
		fileMenu.add(new Separator(IWorkbenchActionConstants.GROUP_FILE));
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);
	}

}
