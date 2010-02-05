package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.editpart.AbstractIconEditPart;
import org.kompiro.jamcircle.kanban.ui.model.*;
import org.kompiro.jamcircle.kanban.ui.widget.BoardListTableViewer;

public class BoardSelecterEditPart extends AbstractIconEditPart{

	public BoardSelecterEditPart(BoardModel board){
		super(board);
	}
	
	@Override
	protected Rectangle getLabelConstraint() {
		return new Rectangle(0,60,72,10);
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	public void performRequest(Request req) {
		if(RequestConstants.REQ_OPEN.equals(req.getType())){
			Shell shell = getShell();
			ApplicationWindow window = new ApplicationWindow(shell){
				private BoardListTableViewer viewer;
				@Override
				protected Control createContents(Composite parent) {
					this.viewer = new BoardListTableViewer(parent);
					viewer.setInput(getKanbanService().findAllBoard());
					viewer.setTrashModel(getBoardModel().getTrashModel());
					return parent;
				}
				
				@Override
				protected void configureShell(Shell shell) {
					super.configureShell(shell);
					shell.setText("Board List");
					Image image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.KANBANS_IMAGE.toString());
					shell.setImage(image);
				}
			};
			window.create();
			window.open();

		}
	}
	

	public void doPropertyChange(PropertyChangeEvent evt) {
		if(isPropLocation(evt)){
			figure.setLocation(getBoardSelecterModel().getLocation());
			figure.repaint();
		}
	}
	
	private BoardSelecterModel getBoardSelecterModel(){
		return (BoardSelecterModel) getModel();
	}

	@Override
	protected KanbanImageConstants getImageConstants() {
		return KanbanImageConstants.KANBANS_IMAGE;
	}

	@Override
	protected String getImageLabel() {
		return "Select";
	}

	@Override
	protected Point getLocation() {
		return getBoardSelecterModel().getLocation();
	}
	
}
