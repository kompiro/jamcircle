package org.kompiro.jamcircle.web;import org.kompiro.jamcircle.kanban.model.Board;import com.vaadin.Application;import com.vaadin.ui.Label;import com.vaadin.ui.Window;public class MyprojectApplication extends Application {	private static final long serialVersionUID = 1L;	@Override	public void init() {		Window mainWindow = new Window("Myproject Application");		Board[] boards = WebContext.getDefault().getKanbanService().findAllBoard();		for (Board board : boards) {			Label label = new Label(board.getTitle());			mainWindow.addComponent(label);		}		setMainWindow(mainWindow);	}}