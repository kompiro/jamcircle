package org.kompiro.jamcircle.kanban.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.ao.Entity;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.model.GraphicalImpl;
import org.kompiro.jamcircle.kanban.model.Icon;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.*;
import org.kompiro.jamcircle.kanban.ui.internal.figure.AnnotationArea;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.model.AbstractModel;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

public abstract class AbstractEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener {

	public class DelegatorRunner implements Runnable {
		private final PropertyChangeEvent evt;

		private DelegatorRunner(PropertyChangeEvent evt) {
			this.evt = evt;
		}

		public void run() {
			Animation.markBegin();
			doPropertyChange(evt);
			Animation.run();
		}

		public PropertyChangeEvent getEvt() {
			return evt;
		};
	}

	private BoardModel boardModel;

	private IPropertyChangeDelegator delegator;

	public AbstractEditPart(BoardModel boardModel) {
		this.boardModel = boardModel;
	}

	@Override
	public void activate() {
		super.activate();
		if (getModel() instanceof AbstractModel) {
			AbstractModel lModel = (AbstractModel) getModel();
			lModel.addPropertyChangeListener(this);
		} else if (getModel() instanceof GraphicalEntity) {
			GraphicalEntity lModel = (GraphicalEntity) getModel();
			lModel.addPropertyChangeListener(this);
		} else if (getModel() instanceof GraphicalImpl) {
			GraphicalImpl impl = (GraphicalImpl) getModel();
			impl.addPropertyChangeListener(this);
		} else if (getModel() instanceof Icon) {
			Icon impl = (Icon) getModel();
			impl.addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		super.deactivate();
		if (getFigure() instanceof AnnotationArea<?>) {
			AnnotationArea<?> fig = (AnnotationArea<?>) getFigure();
			fig.removeAnnotation();
		}
		if (getModel() instanceof AbstractModel) {
			AbstractModel lModel = (AbstractModel) getModel();
			lModel.removePropertyChangeListener(this);
		} else if (getModel() instanceof GraphicalEntity) {
			GraphicalEntity lModel = (GraphicalEntity) getModel();
			lModel.removePropertyChangeListener(this);
		} else if (getModel() instanceof GraphicalImpl) {
			GraphicalImpl impl = (GraphicalImpl) getModel();
			impl.removePropertyChangeListener(this);
		} else if (getModel() instanceof Icon) {
			Icon impl = (Icon) getModel();
			impl.removePropertyChangeListener(this);
		}
	}

	protected KanbanService getKanbanService() {
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
			if (isSelectable()) {
				editPart = getParent();
				if (editPart instanceof AbstractEditPart) {
					AbstractEditPart part = (AbstractEditPart) editPart;
					part.reorder(this);
				}
				return this;
			}
		}

		if (RequestConstants.REQ_ADD.equals(request.getType())) {
			return this;
		}
		return null;
	}

	public void reorder(EditPart child) {
		super.reorderChild(child, 0);
	}

	public final IFigure copyFigureForDragAndDrop() {
		IFigure figure = new Layer() {
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
		final IFigure sourceFigure = copiedFigure();
		figure.add(sourceFigure);
		figure.addLayoutListener(new LayoutListener.Stub() {
			@SuppressWarnings("rawtypes")
			@Override
			public void invalidate(IFigure container) {
				AbstractEditPart part = AbstractEditPart.this;
				if (part instanceof LaneEditPart
						&& ((LaneEditPart) part).getFigure() instanceof AnnotationArea
						&& ((AnnotationArea) ((LaneEditPart) part).getFigure()).getTargetFigure() instanceof LaneFigure) {
					invalidateForAnnotationAreaForFixSize(sourceFigure, container);
				}
			}

			private void invalidateForAnnotationAreaForFixSize(final IFigure sourceFigure, IFigure container) {
				Rectangle copy = container.getBounds().getCopy();
				copy.setLocation(new Point(0, AnnotationArea.ACTION_ICON_SIZE));
				copy.setSize(copy.getSize().expand(0, -AnnotationArea.ACTION_ICON_SIZE * 2));
				sourceFigure.setBounds(copy);
			}
		});
		Rectangle copy = sourceFigure.getBounds().getCopy();
		sourceFigure.setBounds(copy.setLocation(new Point(0, 0)));
		figure.setBounds(sourceFigure.getBounds());
		return figure;
	}

	protected IFigure copiedFigure() {
		return createFigure();
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return new CancelableDragEditPartsTracker(this);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		getDelegator().run(new DelegatorRunner(evt));
	}

	private IPropertyChangeDelegator getDelegator() {
		if (delegator == null) {
			delegator = new AsyncDisplayDelegator(getDisplay());
		}
		return delegator;
	}

	@Override
	public void setFigure(IFigure figure) {
		super.setFigure(figure);
	}

	protected void doPropertyChange(final PropertyChangeEvent evt) {
		final Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (newValue != null && (newValue instanceof Entity || newValue instanceof AbstractModel)) {
			addChild(createChild(newValue), -1);
		} else if (oldValue != null && (oldValue instanceof Entity || oldValue instanceof AbstractModel)) {
			EditPart target = null;
			for (Object obj : getChildren()) {
				if (obj instanceof EditPart) {
					EditPart part = (EditPart) obj;
					Object rhs = part.getModel();
					if (oldValue.getClass().equals(rhs.getClass()) && oldValue.equals(rhs)) {
						target = part;
					}
				}
			}
			if (target != null) {
				removeChild(target);
			}
		}
	}

	protected Shell getShell() {
		return getViewer().getControl().getShell();
	}

	protected CommandStack getCommandStack() {
		return getViewer().getEditDomain().getCommandStack();
	}

	protected ImageRegistry getImageRegistry() {
		KanbanUIActivator activator = KanbanUIActivator.getDefault();
		if (activator == null) {
			WorkbenchUtil.getDisplay();
			return JFaceResources.getImageRegistry();
		}
		return activator.getImageRegistry();
	}

	public BoardModel getBoardModel() {
		return this.boardModel;
	}

	protected Display getDisplay() {
		return getViewer().getControl().getDisplay();
	}

	public void setDelegator(IPropertyChangeDelegator delegator) {
		this.delegator = delegator;
	}

	protected boolean isPropLocation(PropertyChangeEvent prop) {
		return GraphicalEntity.PROP_COMMIT_LOCATION.equals(prop.getPropertyName());
	}
}
