package org.kompiro.jamcircle.kanban.ui.figure;

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
import org.eclipse.ui.*;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;

public class CardFigure extends RoundedRectangle {
	
	private static final int HEADER_SECTION_HEIGHT = 16;
	private static final int FOOTER_SECTION_HEIGHT = 16;
	private static final int ANIMATION_TIME = 500;
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
//	private CardToolTip toolTipFigure;
	private Figure footerSection;
	private ColorTypes colorType;
//	private FlagTypes flagType;
	private Figure middleSection;
	private Figure actionSection;
	private Figure statusSection;
	private Figure headerSection;
	private ImageFigure mockImage;
	private ImageRegistry imageRegistry;
	
	public CardFigure(){
		this(null);
	}
	
	public CardFigure(ImageRegistry imageRegisty){
		this.imageRegistry = imageRegisty;
		FlowLayout manager = new FlowLayout();
		manager.setMajorSpacing(0);
		manager.setMinorSpacing(0);
		setLayoutManager(manager);

		CompoundBorder compoundBorder = new CompoundBorder(new ShadowBoarder(corner),new MarginBorder(LINE_WIDTH - 1));
		setBorder(compoundBorder);

		createHeaderSection();
		createSubjectPage();
		createFooterSection();
		GC gc = new GC(getDisplay());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent("xx").x * 10;
		int height = fontMetrics.getHeight() * 3;

		setSize(width + LINE_WIDTH * 2,
				HEADER_SECTION_HEIGHT + height + LINE_WIDTH * 2 + FOOTER_SECTION_HEIGHT);

		setLineWidth(LINE_WIDTH);
//		toolTipFigure = new CardToolTip();
//		setToolTip(toolTipFigure);
	}
	

	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		Rectangle r = getBounds();
		f.x = r.x + lineWidth / 2;
		f.y = r.y + lineWidth / 2;
		f.width = r.width - lineWidth;
		f.height = r.height - lineWidth;
		
		graphics.setForegroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_BORDER + colorType.ordinal()));
		graphics.drawRoundRectangle(f, corner.width, corner.height);
	}
	
	
	private void createHeaderSection(){
		headerSection = new Figure();
		GridLayout manager = new GridLayout(3,false);
		manager.marginWidth = 0;
		manager.marginHeight = 0;
		manager.horizontalSpacing = 0;
		manager.verticalSpacing = 0;
		headerSection.setLayoutManager(manager);
		add(headerSection,new Rectangle(0,0,getSize().width, 16));

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
		int height = fontMetrics.getHeight() * 3;
		subjectSize = new Dimension(width,height);
		middleSection.add(subjectPage,new Rectangle(new Point(0,0),subjectSize));
		add(middleSection);
	}
	
	public IFigure getMiddleSection() {
		return middleSection;
	}

	private void createFooterSection() {
		actionSection = new Figure();
		actionSection.setLayoutManager(new ToolbarLayout(true));
		footerSection = new Figure();
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		footerSection.setLayoutManager(gridLayout);
		GridData actionLayoutData = new GridData();
		actionLayoutData.horizontalAlignment = GridData.FILL_HORIZONTAL;
		footerSection.add(actionSection,actionLayoutData);
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
		this.colorType = colorType;
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_BODY + colorType.ordinal()));
	}
	
	public void setSubject(String subjectText){
		if(subjectText != null){
			subject.setText(subjectText);
		}else{
			subject.setText("");
		}
	}
			
	public void setRemoved(boolean removed) {
		this.removed = removed;
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

	private Display getDisplay() {
		Display defDisplay = Display.getDefault();
		try{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if(workbench == null) return defDisplay;
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			if(activeWorkbenchWindow == null) return defDisplay;
			Display display = activeWorkbenchWindow.getShell().getDisplay();
			return display;
		}catch(IllegalStateException e){
			return defDisplay;
		}
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



}
