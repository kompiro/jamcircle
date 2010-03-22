package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

public class LaneFigure extends RectangleFigure {


	public static final String KEY_OF_LANE_HEADER = "LANE_HEADER";
	
	public class CardArea extends Figure {
		@Override
		protected boolean useLocalCoordinates() {
			return true;
		}

		@Override
		public void setConstraint(IFigure child, Object constraint) {
			resizeLaneFigure(child,constraint);
			super.setConstraint(child, constraint);
		}
	}
	static final int LANE_SIZE_OF_CORNER = 20;
	public static final int MARGIN = 5;
	public static final String COLOR_KEY_LANE_BODY = "color_key_lane_body";
	public static final String COLOR_KEY_LANE_BORDER = "color_key_lane_border";
	private Label statusFigure;
	private CardArea cardArea;
	private boolean creating;
	private Figure headerFigure;
	private Figure actionSection;
	private Figure statusPaneFigure;
	private Figure actionPaneFigure;
	
	public LaneFigure(){
//		corner = new Dimension(LANE_SIZE_OF_CORNER, LANE_SIZE_OF_CORNER);
		setLayoutManager(new GridLayout());
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
		if(statusFigure == null) throw new IllegalStateException("LaneFigure:statusFigure is not initialized.");
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
		GridData constraint = new GridData();
		constraint.heightHint = height;
		constraint.grabExcessHorizontalSpace = true;
		constraint.horizontalAlignment = SWT.FILL;
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
		if(cardArea == null){
			cardArea = new CardArea();
			XYLayout xyl = new XYLayout()
			{
				@Override
				public void setConstraint(IFigure figure, Object newConstraint) {
					resizeLaneFigure(figure,newConstraint);
					super.setConstraint(figure, newConstraint);
				}
			};
			cardArea.setLayoutManager(xyl);
			cardArea.setBorder(new CompoundBorder(new MarginBorder(0,MARGIN,MARGIN,MARGIN),new LineBorder(ColorConstants.gray)));
//			cardArea.addLayoutListener(new LayoutListener.Stub(){
//				@Override
//				public void setConstraint(IFigure child, Object constraint) {
//					super.setConstraint(child, constraint);
//				}
//			});
			GridData constraint = new GridData();
			constraint.grabExcessHorizontalSpace = true;
			constraint.grabExcessVerticalSpace = true;
			constraint.horizontalAlignment = SWT.FILL;
			constraint.verticalAlignment = SWT.FILL;
			add(cardArea,constraint);
		}
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
	
	private void resizeLaneFigure(IFigure child,Object constraint) {
		if (constraint instanceof Rectangle) {
			Rectangle rect = (Rectangle) constraint;
			LaneFigure parent = LaneFigure.this;
			Dimension size = parent.getSize();
			boolean changed = false;
			int x = Math.abs(rect.x);
			int reqWidth = x + rect.width + LANE_SIZE_OF_CORNER + MARGIN;
			if(rect.x < 0){
				rect.x = 0;
				changed = true;
			}
			if(size.width < reqWidth){
				size.width = reqWidth;
				changed = true;
			}
			int y = Math.abs(rect.y);
			int reqHeight = y + rect.height + LANE_SIZE_OF_CORNER + MARGIN + (int)getStatusAreaHeight();
			if(rect.y < 0){
				rect.y = 0;
				changed = true;
			}
			if(size.height < reqHeight){
				size.height = reqHeight;
				changed = true;
			}
			if(changed){
				parent.setSize(size);
				parent.repaint();
				child.revalidate();
				child.repaint();
			}
		}
	}
		
	public IFigure getActionSection(){
		return this.actionSection;
	}
}
