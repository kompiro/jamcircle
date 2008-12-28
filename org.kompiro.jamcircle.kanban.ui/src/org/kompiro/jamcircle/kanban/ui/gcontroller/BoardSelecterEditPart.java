package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.beans.PropertyChangeEvent;


import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.model.AbstractModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardSelecterModel;
import org.kompiro.jamcircle.kanban.ui.widget.BoardListTableViewer;

public class BoardSelecterEditPart extends AbstractEditPart implements IconEditPart{

	public BoardSelecterEditPart(BoardModel board){
		super(board);
	}
	
	@Override
	protected IFigure createFigure() {
		Image kanbans = getImageRegistry().get(KanbanImageConstants.KANBANS_IMAGE.toString());
		ImageFigure boardImage = new ImageFigure(kanbans);
		boardImage.setSize(72,72);
		boardImage.setLayoutManager(new XYLayout());
		Label labelFigure = new Label();
		labelFigure.setTextAlignment(PositionConstants.CENTER);
		labelFigure.setText("Select");
		boardImage.add(labelFigure,new Rectangle(0,60,72,10));
		boardImage.setLocation(getBoardSelecterModel().getLocation());
		return boardImage;
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
	

	public void propertyChange(PropertyChangeEvent evt) {
		if(isPropLocation(evt)){
			figure.setLocation(getBoardSelecterModel().getLocation());
			figure.repaint();
		}
	}
	
	private boolean isPropLocation(PropertyChangeEvent prop) {
		return AbstractModel.PROP_LOCATION.equals(prop.getPropertyName());
	}
	
	private BoardSelecterModel getBoardSelecterModel(){
		return (BoardSelecterModel) getModel();
	}

}
