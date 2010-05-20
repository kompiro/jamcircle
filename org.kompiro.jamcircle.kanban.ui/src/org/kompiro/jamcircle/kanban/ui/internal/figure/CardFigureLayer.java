package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.figure.CardToolTip;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class CardFigureLayer extends Layer {
	
	private static final String DOUBLE_CHAR = "xx"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	public static final int HEADER_SECTION_HEIGHT = 16;
	public static final int FOOTER_SECTION_HEIGHT = 16;
	public static final int CARD_CHARS_OF_LINES = 10;
	public static final int CARD_LINES = 4;
	
	private static final int ANIMATION_TIME = 100;
	private static final int LINE_WIDTH = 4;
	private static final int FRAMES = 8;
	
	public static final String COLOR_KEY_CARD_BODY = "color_key_card_body"; //$NON-NLS-1$
	public static final String COLOR_KEY_CARD_BORDER = "color_key_card_border"; //$NON-NLS-1$
	public static String KEY_OF_CARD_HEADER = "CARD_HEADER"; //$NON-NLS-1$
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
	private Figure middleSection;
	private Figure actionSection;
	private Figure statusSection;
	private Figure headerSection;
	private ImageFigure mockImage;
	private RoundedRectangle figure;
	
	public static final int CARD_WIDTH;
	public static final int CARD_HEIGHT;
	public static final Dimension CARD_SIZE;
	static {
		GC gc = new GC(getDisplay());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent(DOUBLE_CHAR).x * CARD_CHARS_OF_LINES;
		int height = (fontMetrics.getHeight() + 1) * CARD_LINES ;
		CARD_WIDTH = width + LINE_WIDTH * 2;
		CARD_HEIGHT = HEADER_SECTION_HEIGHT + height + LINE_WIDTH * 2;
		CARD_SIZE  = new Dimension(CardFigureLayer.CARD_WIDTH,CardFigureLayer.CARD_HEIGHT);
	}
	
	public CardFigureLayer(){
		this(null);
	}
	
	public CardFigureLayer(ImageRegistry imageRegisty){
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
		FlowLayout mainManager = new FlowLayout();
		mainManager.setMajorSpacing(0);
		mainManager.setMinorSpacing(0);
		setLayoutManager(mainManager);

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
		idLabel.setText(EMPTY);
		idLabel.setBorder(null);
		idLabel.setFont(getHeaderFont());
		
		mockImage = new ImageFigure();
		mockImage.setImage(KanbanImageConstants.MOCK_IMAGE.getIamge());
		
		statusSection = new Figure();
		statusSection.setLayoutManager(new ToolbarLayout(true));

		GridData idData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		identitySection.add(idLabel);
		identitySection.add(mockImage);
		headerSection.add(identitySection,idData);
		headerSection.add(statusSection,new GridData(GridData.HORIZONTAL_ALIGN_END|GridData.GRAB_HORIZONTAL));
		mockImage.setVisible(false);
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
		int width = gc.stringExtent(DOUBLE_CHAR).x * 10;
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
		idLabel.setText("#"+id); //$NON-NLS-1$
	}
	
	public void setMock(boolean isMock) {
		mockImage.setVisible(isMock);
		IFigure mockIdLabel = new Label(Messages.CardFigureLayer_script_mock_label);
		mockImage.setToolTip(mockIdLabel);
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
			subject.setText(EMPTY);
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
		KanbanUIStatusHandler.debugUI("CardFigure#paint() '%s' added:'%s' removed:'%s' alpha:'%d' visible:'%s'", //$NON-NLS-1$
				idLabel.getText(),added,removed,alpha,isVisible());
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
