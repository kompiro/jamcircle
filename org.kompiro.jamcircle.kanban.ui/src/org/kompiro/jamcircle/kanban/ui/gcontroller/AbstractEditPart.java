package org.kompiro.jamcircle.kanban.ui.gcontroller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import net.java.ao.Entity;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.model.GraphicalImpl;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.model.mock.MockGraphicalEntity;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.model.AbstractModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public abstract class AbstractEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener {

	private BoardModel boardModel;

	public AbstractEditPart(BoardModel boardModel){
		this.boardModel = boardModel;
	}
	
	@Override
	public void activate() {
		super.activate();
		if (getModel() instanceof AbstractModel) {
			AbstractModel lModel = (AbstractModel) getModel();
			lModel.addPropertyChangeListener(this);
		}else if(getModel() instanceof GraphicalEntity){
			GraphicalEntity lModel = (GraphicalEntity) getModel();
			lModel.addPropertyChangeListener(this);
		}else if (getModel() instanceof GraphicalImpl){
			GraphicalImpl impl = (GraphicalImpl) getModel();
			impl.addPropertyChangeListener(this);
		}else if (getModel() instanceof Icon){
			Icon impl = (Icon) getModel();
			impl.addPropertyChangeListener(this);
		}
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		if (getModel() instanceof AbstractModel) {
			AbstractModel lModel = (AbstractModel) getModel();
			lModel.removePropertyChangeListener(this);
		}else if(getModel() instanceof GraphicalEntity){
			GraphicalEntity lModel = (GraphicalEntity) getModel();
			lModel.removePropertyChangeListener(this);
		}else if (getModel() instanceof GraphicalImpl){
			GraphicalImpl impl = (GraphicalImpl) getModel();
			impl.removePropertyChangeListener(this);
		}else if (getModel() instanceof Icon){
			Icon impl = (Icon) getModel();
			impl.removePropertyChangeListener(this);
		}
	}
	
	protected KanbanService getKanbanService(){
		return KanbanUIActivator.getDefault().getKanbanService();
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		EditPolicyIterator i = getEditPolicyIterator();
		EditPart editPart;
		while (i.hasNext()) {
			editPart = i.next()
				.getTargetEditPart(request);
			if (editPart != null)
				return editPart;
		}
		
		if (RequestConstants.REQ_SELECTION.equals(request.getType())) {
			if (isSelectable()){
				editPart = getParent();
				if (editPart instanceof AbstractEditPart) {
					AbstractEditPart part = (AbstractEditPart) editPart;
					part.reorder(this);
				}
				return this;
			}
		}
		
		if(RequestConstants.REQ_ADD.equals(request.getType())){
			return this;
		}
		return null;
	}
	
	public void reorder(EditPart child) {
		super.reorderChild(child, 0);
	}
	
	public IFigure copyFigureForDragAndDrop(){
		IFigure figure = new Layer(){			
			@Override
			public void paint(Graphics graphics) {
				graphics.setAlpha(100);
				super.paint(graphics);
			}
			@Override
			protected boolean useLocalCoordinates() {
				return true;
			}
		};
		figure.setOpaque(false);
		figure.setLayoutManager(new FreeformLayout());
		IFigure sourceFigure = createFigure();
		figure.add(sourceFigure);
		sourceFigure.setBounds(sourceFigure.getBounds().getCopy().setLocation(new Point(0,0)));
		figure.setBounds(sourceFigure.getBounds());
		return figure;
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return new CancelableDragEditPartsTracker(this);
	}

	public void propertyChange(PropertyChangeEvent evt){
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if(newValue != null && (newValue instanceof Entity || newValue instanceof AbstractModel)){
			addChild(createChild(newValue), -1);
		}else if(oldValue != null && (oldValue instanceof Entity || oldValue instanceof AbstractModel)){
			EditPart target = null;
			for(Object obj : getChildren()){
				if (obj instanceof EditPart) {
					EditPart part = (EditPart) obj;
					Object rhs = part.getModel();
					if(oldValue.getClass().equals(rhs.getClass()) && oldValue.equals(rhs)){
						target = part;
					}
				}
			}
			if(target != null){
				removeChild(target);
			}
		}
	}
	
	protected Shell getShell() {
		return getViewer().getControl().getShell();
	}
	
	protected CommandStack getCommandStack(){
		return getViewer().getEditDomain().getCommandStack();
	}

	protected ImageRegistry getImageRegistry() {
		return KanbanUIActivator.getDefault().getImageRegistry();
	}
	
	public BoardModel getBoardModel(){
		return this.boardModel;
	}

}
