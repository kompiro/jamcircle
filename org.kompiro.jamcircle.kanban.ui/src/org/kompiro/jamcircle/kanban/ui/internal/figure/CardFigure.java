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
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.figure.CardToolTip;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class CardFigure extends RoundedRectangle {

	private static final String DOUBLE_CHAR = "xx"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	public static final int HEADER_SECTION_HEIGHT = 16;
	public static final int CARD_CHARS_OF_LINES = 10;
	public static final int CARD_LINES = 4;

	private static final int LINE_WIDTH = 4;

	public static final String COLOR_KEY_CARD_BODY = "color_key_card_body"; //$NON-NLS-1$
	public static final String COLOR_KEY_CARD_BORDER = "color_key_card_border"; //$NON-NLS-1$
	public static String KEY_OF_CARD_HEADER = "CARD_HEADER"; //$NON-NLS-1$
	public static int theta = 45;
	public static int maxColorCount = 360 / theta;

	private TextFlow subject;

	private Label idLabel;

	private FlowPage subjectPage;

	private Dimension subjectSize;
	private CardToolTip toolTipFigure;
	private ColorTypes colorType;
	private Figure middleSection;
	private Figure statusSection;
	private Figure headerSection;
	private ImageFigure mockImage;

	public static final int CARD_WIDTH;
	public static final int CARD_HEIGHT;
	public static final Dimension CARD_SIZE;
	static {
		GC gc = new GC(getDisplay());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent(DOUBLE_CHAR).x * CARD_CHARS_OF_LINES;
		int height = (fontMetrics.getHeight() + 1) * CARD_LINES;
		CARD_WIDTH = width + LINE_WIDTH * 2;
		CARD_HEIGHT = HEADER_SECTION_HEIGHT + height + LINE_WIDTH * 2;
		CARD_SIZE = new Dimension(CardFigure.CARD_WIDTH, CardFigure.CARD_HEIGHT);
	}

	public CardFigure() {
		this(null);
	}

	public CardFigure(ImageRegistry imageRegisty) {
		setSize(CARD_WIDTH, CARD_HEIGHT);
		setLineWidth(LINE_WIDTH);
		FlowLayout manager = new FlowLayout();
		manager.setMajorSpacing(0);
		manager.setMinorSpacing(0);
		setLayoutManager(manager);

		CompoundBorder compoundBorder = new CompoundBorder(new ShadowBoarder(new Dimension(8, 8)), new MarginBorder(
				LINE_WIDTH - 1));
		setBorder(compoundBorder);

		createHeaderSection();
		createSubjectPage();
		FlowLayout mainManager = new FlowLayout();
		mainManager.setMajorSpacing(0);
		mainManager.setMinorSpacing(0);
		setLayoutManager(mainManager);

		toolTipFigure = new CardToolTip(imageRegisty);
		setToolTip(toolTipFigure);
	}

	protected void outlineShape(Graphics graphics) {
		Rectangle f = Rectangle.SINGLETON;
		Rectangle r = getBounds();
		f.x = r.x + getLineWidth() / 2;
		f.y = r.y + getLineWidth() / 2;
		f.width = r.width - getLineWidth();
		f.height = r.height - getLineWidth();

		graphics.setForegroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_BORDER + colorType.ordinal()));
		graphics.drawRoundRectangle(f, 8, 8);
	}

	private void createHeaderSection() {
		headerSection = new Figure();
		GridLayout manager = new GridLayout(3, false);
		manager.marginWidth = 0;
		manager.marginHeight = 0;
		manager.horizontalSpacing = 0;
		manager.verticalSpacing = 0;
		headerSection.setLayoutManager(manager);
		add(headerSection, new Rectangle(0, 0, getSize().width, 16));

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
		headerSection.add(identitySection, idData);
		headerSection.add(statusSection, new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
		mockImage.setVisible(false);
	}

	private Font getHeaderFont() {
		return JFaceResources.getFontRegistry().get(KEY_OF_CARD_HEADER);
	}

	private void createSubjectPage() {
		middleSection = new Figure();
		middleSection.setLayoutManager(new XYLayout());
		subject = new TextFlow();
		ParagraphTextLayout layout = new ParagraphTextLayout(subject, ParagraphTextLayout.WORD_WRAP_SOFT);
		subject.setLayoutManager(layout);
		subjectPage = new FlowPage();
		subjectPage.add(subject);
		GC gc = new GC(getDisplay());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent(DOUBLE_CHAR).x * 10;
		int height = fontMetrics.getHeight() * 4;
		subjectSize = new Dimension(width, height);
		middleSection.add(subjectPage, new Rectangle(new Point(0, 0), subjectSize));
		add(middleSection);
	}

	public IFigure getMiddleSection() {
		return middleSection;
	}

	public void setId(int id) {
		idLabel.setText("#" + id); //$NON-NLS-1$
	}

	public void setMock(boolean isMock) {
		mockImage.setVisible(isMock);
		IFigure mockIdLabel = new Label(Messages.CardFigureLayer_script_mock_label);
		mockImage.setToolTip(mockIdLabel);
		idLabel.setVisible(!isMock);
	}

	public void setColorType(ColorTypes colorType) {
		colorType = setDefaultColorType(colorType);
		this.colorType = colorType;
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_BODY + colorType.ordinal()));
	}

	private ColorTypes setDefaultColorType(ColorTypes colorType) {
		if (colorType == null) {
			colorType = ColorTypes.YELLOW;
		}
		return colorType;
	}

	public void setSubject(String subjectText) {
		if (subjectText != null) {
			subject.setText(subjectText);
			toolTipFigure.setBody(subjectText);
		} else {
			subject.setText(EMPTY);
			toolTipFigure.setBody(null);
		}
	}

	@Override
	protected void fireFigureMoved() {
		invalidate();
		super.fireFigureMoved();
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

}
