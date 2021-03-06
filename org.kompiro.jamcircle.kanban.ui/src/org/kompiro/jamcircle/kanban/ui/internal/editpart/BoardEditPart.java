package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.command.CardCloneCommand;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.editpart.*;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.policy.BoardXYLayoutEditPolicy;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;

/**
 * @TestContext BoardEditPartTest
 */
public class BoardEditPart extends AbstractEditPart implements CardContainerEditPart, IBoardEditPart {
	private static final String MESSAGE_OF_PROPERTY_CHANGE_EVENT = "BoardEditPart propertyChange %s newValue:'%s' oldValue:'%s'"; //$NON-NLS-1$

	public static final String TITLE_FONT_KEY = "org.kompiro.jamcircle.TITLE_FONT_KEY"; //$NON-NLS-1$

	private static final String LAYER_KEY_CARD = "card"; //$NON-NLS-1$
	private static final String LAYER_KEY_ICON = "icon"; //$NON-NLS-1$
	private static final String LAYER_KEY_LANE = "lane"; //$NON-NLS-1$
	private static final String LAYER_KEY_BOARD = "background"; //$NON-NLS-1$
	private static final String LAYER_KEY_BOARD_TITLE = "title"; //$NON-NLS-1$

	private static final class CardLayer extends Layer {

		public CardLayer() {
			addLayoutListener(LayoutAnimator.getDefault());
			setLayoutManager(new FreeformLayout());
			setOpaque(false);
		}

		@Override
		public IFigure findFigureAt(int x, int y, TreeSearch search) {
			return super.findFigureAt(x, y, search);
		}

		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}
	}

	private static final class LaneLayer extends Layer {

		public LaneLayer() {
			// addLayoutListener(LayoutAnimator.getDefault());
			setLayoutManager(new FreeformLayout());
			setOpaque(false);
		}

		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}
	}

	private static final class IconLayer extends Layer {

		public IconLayer() {
			// addLayoutListener(LayoutAnimator.getDefault());
			setLayoutManager(new FreeformLayout());
			setOpaque(false);
		}

		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}

	}

	private Layer boardLayer;
	private Layer iconLayer;
	private Layer laneLayer;
	private Layer cardLayer;
	private LayeredPane wallboard;
	private ImageFigure backImageFigure;
	private Layer boardStatus;

	private Label boardTitle;

	public BoardEditPart(BoardModel board) {
		super(board);
		setModel(board);
		createFigure();
	}

	@Override
	public void activate() {
		super.activate();
		getBoardModel().setAnimated(true);
	}

	@Override
	protected IFigure createFigure() {
		wallboard = new LayeredPane();
		wallboard.setOpaque(false);
		createBoardBackground();
		createStatusLayer();
		createLaneLayer();
		createIconLayer();
		createCardLayer();
		setPaintBackgroundListener();
		return wallboard;
	}

	private void setPaintBackgroundListener() {
		boardLayer.addLayoutListener(new LayoutListener.Stub() {
			public void postLayout(IFigure container) {
				backImageFigure.setBounds(container.getBounds());
			}
		});
	}

	private void createStatusLayer() {
		boardStatus = new Layer();
		GridLayout manager = new GridLayout();
		boardStatus.setLayoutManager(manager);
		boardTitle = new Label() {
			@Override
			public void paint(Graphics graphics) {
				graphics.setAlpha(128);
				super.paint(graphics);
			}
		};
		Font font = JFaceResources.getFontRegistry().get(TITLE_FONT_KEY);
		boardTitle.setFont(font);
		Board board = getBoard();
		if (board != null)
			boardTitle.setText(board.getTitle());
		GridData constraint = new GridData();
		constraint.grabExcessHorizontalSpace = true;
		constraint.grabExcessVerticalSpace = true;
		constraint.horizontalAlignment = SWT.END;
		constraint.verticalAlignment = SWT.END;
		boardStatus.add(boardTitle, constraint);
		wallboard.add(boardStatus, LAYER_KEY_BOARD_TITLE, 1);
	}

	private Board getBoard() {
		if (getBoardModel() == null)
			return null;
		return getBoardModel().getBoard();
	}

	private void createBoardBackground() {
		boardLayer = new Layer();
		boardLayer.setLayoutManager(new FreeformLayout());
		boardLayer.setBorder(new LineBorder(ColorConstants.lightGray, 1));
		Image backImage = getImageRegistry().get(KanbanImageConstants.BACKGROUND_IMAGE.toString());
		backImageFigure = new ImageFigure(backImage) {
			@Override
			protected void paintFigure(Graphics graphics) {
				Image image = getImage();
				if (image == null)
					return;
				org.eclipse.swt.graphics.Rectangle rect = image.getBounds();
				for (int x = 0; x < getBounds().width; x = x + rect.width) {
					for (int y = 0; y < getBounds().height; y = y + rect.height) {
						graphics.drawImage(image, x, y);
					}
				}
			}
		};
		boardLayer.add(backImageFigure);
		wallboard.add(boardLayer, LAYER_KEY_BOARD, 0);
	}

	private void createLaneLayer() {
		laneLayer = new LaneLayer();
		wallboard.addLayerAfter(laneLayer, LAYER_KEY_LANE, LAYER_KEY_BOARD_TITLE);
	}

	private void createIconLayer() {
		iconLayer = new IconLayer();
		wallboard.addLayerAfter(iconLayer, LAYER_KEY_ICON, LAYER_KEY_LANE);
	}

	private void createCardLayer() {
		cardLayer = new CardLayer();
		wallboard.addLayerAfter(cardLayer, LAYER_KEY_CARD, LAYER_KEY_ICON);
	}

	@Override
	public IFigure getContentPane() {
		return boardLayer;
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return new BoardDragTracker(getKanbanService());
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List getModelChildren() {
		BoardModel model = (BoardModel) getModel();
		return model.getChildren();
	}

	public List<CardEditPart> getCardChildren() {
		List<CardEditPart> results = new ArrayList<CardEditPart>();
		for (Object o : getChildren()) {
			if (o instanceof CardEditPart)
				results.add((CardEditPart) o);
		}
		return results;
	}

	public List<LaneEditPart> getLaneChildren() {
		List<LaneEditPart> results = new ArrayList<LaneEditPart>();
		for (Object o : getChildren()) {
			if (o instanceof LaneEditPart)
				results.add((LaneEditPart) o);
		}
		return results;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new BoardXYLayoutEditPolicy(this));
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy() {

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}

			@Override
			protected Command getCloneCommand(ChangeBoundsRequest request) {
				return new CardCloneCommand(request, getBoardModel());
			}

		});
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		String message = String.format(MESSAGE_OF_PROPERTY_CHANGE_EVENT, evt.getPropertyName(), newValue, oldValue);
		KanbanUIStatusHandler.debug(message);
		super.doPropertyChange(evt);
		if (isPropTitle(evt)) {
			if (newValue != null) {
				boardTitle.setText(newValue.toString());
			}
		}
	}

	private boolean isPropTitle(PropertyChangeEvent evt) {
		return Board.PROP_TITLE.equals(evt.getPropertyName());
	}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		if (childEditPart instanceof IconEditPart) {
			IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
			iconLayer.add(child);
		} else if (childEditPart instanceof LaneEditPart) {
			IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
			laneLayer.add(child, child.getBounds(), -1);
		} else {
			IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
			cardLayer.add(child, child.getBounds(), -1);
		}
	}

	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
		// Tips : for Animation
		Layer layer = wallboard.getLayer(LAYER_KEY_CARD);
		if (childEditPart instanceof CardEditPart) {
			CardEditPart cardEditPart = (CardEditPart) childEditPart;
			GraphicalEntity model = cardEditPart.getCardModel();
			CardFigure cardFigure = cardEditPart.getCardFigure();
			if (model.isDeletedVisuals()) {
				cardFigure.setVisible(false);
			}
			if (!cardFigure.isVisible()) {
				layer.remove(child);
			}
		} else if (childEditPart instanceof IconEditPart) {
			iconLayer.remove(child);
		} else if (childEditPart instanceof LaneEditPart) {
			laneLayer.remove(child);
		} else {
			layer.remove(child);
		}
	}

	public BoardModel getBoardModel() {
		return (BoardModel) getModel();
	}

	public CardContainer getCardContainer() {
		return getBoardModel();
	}

	protected CommandStack getCommandStack() {
		return super.getCommandStack();
	}

}
