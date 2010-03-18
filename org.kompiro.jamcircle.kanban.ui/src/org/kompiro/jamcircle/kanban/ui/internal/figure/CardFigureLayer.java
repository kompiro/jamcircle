package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.*;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.figure.CardToolTip;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class CardFigureLayer extends Layer {
	
	public static final int HEADER_SECTION_HEIGHT = 16;
	public static final int FOOTER_SECTION_HEIGHT = 16;
	public static final int CARD_CHARS_OF_LINES = 10;
	public static final int CARD_LINES = 4;
	
	private static final int ANIMATION_TIME = 100;
	private static final int LINE_WIDTH = 4;
	private static final int FRAMES = 8;
	
	public static final String COLOR_KEY_CARD_BODY = "color_key_card_body";
	public static final String COLOR_KEY_CARD_BORDER = "color_key_card_border";
	public static String KEY_OF_CARD_HEADER = "CARD_HEADER";
	public static int theta = 45;
	public static int maxColorCount = 360 / theta;

	private int alpha = 50;
	private boolean added = false;
	private boolean removed = false;
	private TextFlow subject;
	
	private Label idLabel;

	private FlowPage subjectPage;

	private Dimension subjectSize;
	private CardToolTip toolTipFigure;
	private Figure footerSection;
	private ColorTypes colorType;
//	private FlagTypes flagType;
	private Figure middleSection;
	private Figure actionSection;
	private Figure statusSection;
	private Figure headerSection;
	private ImageFigure mockImage;
	private ImageRegistry imageRegistry;
	private RoundedRectangle figure;
	
	public static final int CARD_WIDTH;
	public static final int CARD_HEIGHT;
	public static final Dimension CARD_SIZE;
	static {
		GC gc = new GC(getDisplay());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent("xx").x * CARD_CHARS_OF_LINES;
		int height = (fontMetrics.getHeight() + 1) * CARD_LINES ;
		CARD_WIDTH = width + LINE_WIDTH * 2;
		CARD_HEIGHT = HEADER_SECTION_HEIGHT + height + LINE_WIDTH * 2;
		CARD_SIZE  = new Dimension(CardFigureLayer.CARD_WIDTH,CardFigureLayer.CARD_HEIGHT);
	}
	public CardFigureLayer(){
		this(null);
	}
	
	public CardFigureLayer(ImageRegistry imageRegisty){
		this.imageRegistry = imageRegisty;
		this.figure = new RoundedRectangle(){
			protected void outlineShape(Graphics graphics) {
				Rectangle f = Rectangle.SINGLETON;
				Rectangle r = getBounds();
				f.x = r.x + figure.getLineWidth() / 2;
				f.y = r.y + figure.getLineWidth() / 2;
				f.width = r.width - figure.getLineWidth();
				f.height = r.height - figure.getLineWidth();
				
				graphics.setForegroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_BORDER + colorType.ordinal()));
				graphics.drawRoundRectangle(f, 8, 8);
			}
		};
		setSize(CARD_WIDTH,	CARD_HEIGHT + FOOTER_SECTION_HEIGHT);
		figure.setLineWidth(LINE_WIDTH);
		FlowLayout manager = new FlowLayout();
		manager.setMajorSpacing(0);
		manager.setMinorSpacing(0);
		figure.setLayoutManager(manager);

		CompoundBorder compoundBorder = new CompoundBorder(new ShadowBoarder(new Dimension(8,8)),new MarginBorder(LINE_WIDTH - 1));
		figure.setBorder(compoundBorder);

		createHeaderSection();
		createSubjectPage();
		manager = new FlowLayout();
		manager.setMajorSpacing(0);
		manager.setMinorSpacing(0);
		setLayoutManager(manager);

		add(figure);

		createFooterSection();
		toolTipFigure = new CardToolTip(imageRegisty);
		setToolTip(toolTipFigure);
	}
	
	
	private void createHeaderSection(){
		headerSection = new Figure();
		GridLayout manager = new GridLayout(3,false);
		manager.marginWidth = 0;
		manager.marginHeight = 0;
		manager.horizontalSpacing = 0;
		manager.verticalSpacing = 0;
		headerSection.setLayoutManager(manager);
		figure.add(headerSection,new Rectangle(0,0,getSize().width, 16));

		IFigure identitySection = new Figure();
		identitySection.setLayoutManager(new StackLayout());
		
		idLabel = new Label();
		idLabel.setText("");
		idLabel.setBorder(null);
		idLabel.setFont(getHeaderFont());
		
		mockImage = new ImageFigure();
		mockImage.setImage(getImageRegistry().get(KanbanImageConstants.MOCK_IMAGE.toString()));
		
		statusSection = new Figure();
		statusSection.setLayoutManager(new ToolbarLayout(true));

		GridData idData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		identitySection.add(idLabel);
		identitySection.add(mockImage);
		headerSection.add(identitySection,idData);
		headerSection.add(statusSection,new GridData(GridData.HORIZONTAL_ALIGN_END|GridData.GRAB_HORIZONTAL));
		mockImage.setVisible(false);
	}

	private ImageRegistry getImageRegistry() {
		if(this.imageRegistry == null) this.imageRegistry = JFaceResources.getImageRegistry();
		return this.imageRegistry;
	}

	private Font getHeaderFont() {
		return JFaceResources.getFontRegistry().get(KEY_OF_CARD_HEADER);
	}

	private void createSubjectPage() {
		middleSection = new Figure();
		middleSection.setLayoutManager(new XYLayout());
		subject = new TextFlow();
		ParagraphTextLayout layout = new ParagraphTextLayout(subject,ParagraphTextLayout.WORD_WRAP_SOFT);
		subject.setLayoutManager(layout);
		subjectPage = new FlowPage();
		subjectPage.add(subject);
		GC gc = new GC(getDisplay());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent("xx").x * 10;
		int height = fontMetrics.getHeight() * 4;
		subjectSize = new Dimension(width,height);
		middleSection.add(subjectPage,new Rectangle(new Point(0,0),subjectSize));
		figure.add(middleSection);
	}
	
	
	public IFigure getMiddleSection() {
		return middleSection;
	}

	private void createFooterSection() {
		actionSection = new Layer();
		actionSection.setLayoutManager(new ToolbarLayout(true));
		footerSection = new Layer();
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		footerSection.setLayoutManager(gridLayout);
		GridData actionLayoutData = new GridData();
		actionLayoutData.horizontalAlignment = GridData.FILL_HORIZONTAL;
		footerSection.add(actionSection,actionLayoutData);
		footerSection.setBackgroundColor(null);
		footerSection.setOpaque(false);
		actionSection.setBackgroundColor(null);
		actionSection.setOpaque(false);
		add(footerSection);
	}

	public void setId(int id){
		idLabel.setText("#"+id);
		IFigure mockIdLabel = new Label("" + id);
		mockImage.setToolTip(mockIdLabel);
	}
	
	public void setMock(boolean isMock) {
		mockImage.setVisible(isMock);
		idLabel.setVisible( ! isMock);
	}
	

	
	public void setColorType(ColorTypes colorType){
		colorType = setDefaultColorType(colorType);
		this.colorType = colorType;
		figure.setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_BODY + colorType.ordinal()));
	}

	private ColorTypes setDefaultColorType(ColorTypes colorType) {
		if(colorType == null){
			colorType = ColorTypes.YELLOW;
		}
		return colorType;
	}
	
	public void setSubject(String subjectText){
		if(subjectText != null){
			subject.setText(subjectText);
			toolTipFigure.setBody(subjectText);
		}else{
			subject.setText("");
			toolTipFigure.setBody(null);
		}
	}
			
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	@Override
	protected void fireFigureMoved() {
		invalidate();
		super.fireFigureMoved();
	}
	
	@Override
	public void paint(Graphics graphics) {
		KanbanUIStatusHandler.debugUI("CardFigure#paint() '%s' " +
				"added:'%s' removed:'%s' alpha:'%d' visible:'%s'",idLabel.getText(),added,removed,alpha,isVisible());
		if(!added){
			doAddedAnimation(graphics);
		}
		if(removed){
			doRemovedAnimation(graphics);
		}
		super.paint(graphics);
	}

	private void doAddedAnimation(Graphics graphics) {
		if(alpha < 255){
			alpha += 255/FRAMES;
			alpha = alpha > 255 ? 255 : alpha; 
			repaintAnimation();
		}else{
			added = true;
		}
		graphics.setAlpha(alpha);
	}

	private void doRemovedAnimation(Graphics graphics) {
		if(alpha > 0){
			alpha -= 255/FRAMES;
			alpha = alpha < 0 ? 0 : alpha; 
			repaintAnimation();
		}else{
			// for Animation Tip
			setVisible(false);
		}
		graphics.setAlpha(alpha);
	}

	private void repaintAnimation() {
		Display display = getDisplay();
		if(display == null) return;
		Runnable runnable = new Runnable() {
			public void run() {
				repaint();
			}
		};
		display.timerExec(ANIMATION_TIME / FRAMES,runnable);
	}

	private static Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Figure getStatusSection() {
		return statusSection;
	}
	
	public Figure getActionSection() {
		return actionSection;
	}
	
	public void setAdded(boolean added) {
		this.added = added;
		if(added){
			alpha = 255;
		}
	}

	public IFigure getCardFigure() {
		return this.figure;
	}

}
