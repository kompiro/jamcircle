package org.kompiro.jamcircle.kanban.ui;


import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.ui.KanbanView;

public class FigureViewer {

//	private ScrollingGraphicalViewer viewer;
	
	private KanbanView viewer;
	
	public FigureViewer(Composite comp) {
		viewer = new KanbanView();
		viewer.createPartControl(comp);
//		viewer = new ScrollingGraphicalViewer();
//		viewer.createControl(comp);
//		viewer.setEditPartFactory(new KanbanControllerFactory());
//		EditDomain domain = new EditDomain();
//		domain.addViewer(viewer);
	}
	
	public void setContents(Object object){
		viewer.getGraphicalViewer().setContents(object);
	}
	
	
	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		FigureViewer v = new FigureViewer(shell);
		shell.open();
//		BoardModel board = new BoardModel();
//		v.setContents(board);
		while(!shell.isDisposed()){
			shell.getDisplay().readAndDispatch();
		}
	}
	
}
