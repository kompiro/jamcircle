package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.kompiro.jamcircle.kanban.ui.Messages;

public class LaneFigure extends RectangleFigure {

	public static final String KEY_OF_LANE_HEADER = "LANE_HEADER"; //$NON-NLS-1$
	
	public class CardArea extends Figure {
		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}
	}
	
	static final int LANE_SIZE_OF_CORNER = 20;
	public static final int MARGIN = 5;
	public static final String COLOR_KEY_LANE_BODY = "color_key_lane_body"; //$NON-NLS-1$
	public static final String COLOR_KEY_LANE_BORDER = "color_key_lane_border"; //$NON-NLS-1$
	private Label statusFigure;
	private CardArea cardArea;
	private boolean creating;
	private Figure headerFigure;
	private Figure actionSection;
	private Figure statusPaneFigure;
	private Figure actionPaneFigure;
	
	public LaneFigure(){
//		corner = new Dimension(LANE_SIZE_OF_CORNER, LANE_SIZE_OF_CORNER);
		setLayoutManager(new GridLayout(1,true));
		
		SchemeBorder outer = new ShadowRectangleBoarder();
		setBorder(outer);
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_LANE_BODY));
		setLineWidth(4);
		createHeader();
		createCardArea();
	}

	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		Rectangle r = getBounds();
		f.x = r.x + lineWidth / 2;
		f.y = r.y + lineWidth / 2;
		f.width = r.width - lineWidth;
		f.height = r.height - lineWidth;
		graphics.setForegroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_LANE_BORDER));
		graphics.drawRectangle(f);
	}
		
	
	public void setStatus(String status) {
		if(statusFigure == null) throw new IllegalStateException(Messages.LaneFigure_initialized_error_message);
		statusFigure.setText(status);
	}

	private void createHeader() {
		createHeaderFigure();
		createStatusLabel();
		createActionSection();
	}

	private void createHeaderFigure() {
		int height = getStatusAreaHeight();
		headerFigure = new Figure();
		headerFigure.setLayoutManager(new StackLayout());

		statusPaneFigure = new Figure();
		statusPaneFigure.setLayoutManager(new GridLayout());
		headerFigure.add(statusPaneFigure);

		actionPaneFigure = new Figure();
		actionPaneFigure.setLayoutManager(new GridLayout());
		headerFigure.add(actionPaneFigure);
		GridData constraint = new GridData(GridData.FILL_HORIZONTAL);
		constraint.heightHint = height;
		add(headerFigure,constraint);
	}

	private void createStatusLabel() {
		int height = (int)getStatusAreaHeight();
		statusFigure = new Label();
		Font statusFont = getFontRegistry().get(KEY_OF_LANE_HEADER);
		statusFigure.setFont(statusFont);
		GridData constraint;
		constraint = new GridData();
		constraint.heightHint = (int)height;
		constraint.grabExcessHorizontalSpace = true;
		constraint.grabExcessVerticalSpace = true;
		constraint.horizontalAlignment = SWT.FILL;
		constraint.verticalAlignment = SWT.TOP;
		statusPaneFigure.add(statusFigure,constraint);
	}

	private void createActionSection() {
		actionSection = new Figure();
		actionSection.setLayoutManager(new ToolbarLayout(true));
		int height = (int)getStatusAreaHeight();
		GridData iconizeConstraint = new GridData();
		iconizeConstraint.heightHint = (int)height;
		iconizeConstraint.grabExcessHorizontalSpace = true;
		iconizeConstraint.grabExcessVerticalSpace = true;
		iconizeConstraint.horizontalAlignment = SWT.RIGHT;
		iconizeConstraint.verticalAlignment = SWT.CENTER;
		actionPaneFigure.add(actionSection,iconizeConstraint);
	}


	private int getStatusAreaHeight() {
		FontRegistry registry = getFontRegistry();
		Font statusFont = registry.get(KEY_OF_LANE_HEADER);
		GC gc = new GC(statusFont.getDevice());
		gc.setFont(statusFont);
		FontMetrics fontMetrics = gc.getFontMetrics();
		int height = (int)(fontMetrics.getHeight() * 1.5);
		return height;
	}

	private FontRegistry getFontRegistry() {
		return JFaceResources.getFontRegistry();
	}
	
	private void createCardArea() {
		IFigure cardAreaSection = new Figure(); 
		cardAreaSection.setLayoutManager(new GridLayout());
		cardArea = new CardArea();
		cardArea.setLayoutManager(new XYLayout());
		cardArea.setBorder(new LineBorder(ColorConstants.lightGray));
		cardArea.setSize(CardFigureLayer.CARD_SIZE);

		GridData constraint = new GridData(GridData.FILL_BOTH);
		cardAreaSection.add(cardArea,constraint);
		constraint = new GridData(GridData.FILL_BOTH);
		constraint.widthHint = CardFigureLayer.CARD_WIDTH;
		constraint.heightHint = CardFigureLayer.CARD_HEIGHT;
		add(cardAreaSection,constraint);
	}

	public CardArea getCardArea() {
		return cardArea;
	}

	@Override
	public void paint(Graphics graphics) {
		if(isCreating()){
			graphics.setAlpha(128);			
		}
		super.paint(graphics);
	}

	private boolean isCreating() {
		return creating;
	}
	
	public void setCreating(boolean creating){
		this.creating = creating;
	}
	
	public int getMaxCardLocationX(Dimension sourceSize,Dimension targetSize){
		return sourceSize.width - (targetSize.width + LANE_SIZE_OF_CORNER + MARGIN);
	}
	public int getMaxCardLocationY(Dimension sourceSize,Dimension targetSize){
		return sourceSize.height - (targetSize.height + LANE_SIZE_OF_CORNER + MARGIN + (int)getStatusAreaHeight());
	}
			
	public IFigure getActionSection(){
		return this.actionSection;
	}
}
