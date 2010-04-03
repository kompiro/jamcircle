package org.kompiro.jamcircle.kanban.ui;


import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;
import org.kompiro.jamcircle.kanban.ui.internal.figure.LaneFigure;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;

public class KanbanJFaceResource {
	public static final String DARK_COLOR_KEY = "org.kompiro.jamcircle.DARK_COLOR_KEY"; //$NON-NLS-1$
	
	private KanbanJFaceResource(){
		
	}
	
	public static void initialize(){
		tableInitialize();
		boardInitialize();
		laneInitialize();
		cardInitialize();
	}

	private static void boardInitialize() {
		Font fo = JFaceResources.getFontRegistry().defaultFont();
		FontData[] defaultFontData = fo.getFontData();
		FontData[] boardFontData = new FontData[defaultFontData.length];
		for(int i = 0; i < defaultFontData.length; i++){
			FontData fontData = defaultFontData[i];
			boardFontData[i] = new FontData(fontData.getName(),24,SWT.BOLD|SWT.ITALIC);
		}
		JFaceResources.getFontRegistry().put(BoardEditPart.TITLE_FONT_KEY, boardFontData);
	}

	private static void tableInitialize() {
		int shift = "carbon".equals(SWT.getPlatform()) ? -25 : -10;//$NON-NLS-1$ 

		Color lightColor = WorkbenchUtil.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

		// Determine a dark color by shifting the list color
		RGB darkRGB = new RGB(Math.max(0, lightColor.getRed() + shift), Math
				.max(0, lightColor.getGreen() + shift), Math.max(0, lightColor
				.getBlue()
				+ shift));
		JFaceResources.getColorRegistry().put(DARK_COLOR_KEY, darkRGB);
	}

	private static void laneInitialize() {
		int r = 255;
		int g = 255;
		int b = 180;
		RGB rgb = new RGB(r,g,b);
		JFaceResources.getColorRegistry().put(LaneFigure.COLOR_KEY_LANE_BODY, rgb);
		r = 255;
		g = 100;
		b = 230;
		rgb = new RGB(r,g,b);
		JFaceResources.getColorRegistry().put(LaneFigure.COLOR_KEY_LANE_BORDER, rgb);
		Font fo = JFaceResources.getDefaultFont();
		FontData[] defaultFontData = fo.getFontData();
		FontData[] laneFontData = new FontData[defaultFontData.length];
		for(int i = 0; i < defaultFontData.length; i++){
			laneFontData[i] = new FontData(defaultFontData[i].getName(),16,defaultFontData[i].getStyle()|SWT.BOLD);
		}
		JFaceResources.getFontRegistry().put(LaneFigure.KEY_OF_LANE_HEADER, laneFontData);
	}

	private static void cardInitialize() {
		for(int i = 0; i < CardFigureLayer.maxColorCount; i++){
			float h = CardFigureLayer.theta * i;
			float s = 0.1f;
			float b = 1f;
			RGB rgb = new RGB(h,s,b);
			JFaceResources.getColorRegistry().put(CardFigureLayer.COLOR_KEY_CARD_BODY + i, rgb);
			s = 0.8f;
			rgb = new RGB(h,s,b);
			JFaceResources.getColorRegistry().put(CardFigureLayer.COLOR_KEY_CARD_BORDER + i, rgb);			
		}
		Font fo = JFaceResources.getFontRegistry().defaultFont();
		FontData[] defaultFontData = fo.getFontData();
		FontData[] cardFontData = new FontData[defaultFontData.length];
		for(int i = 0; i < defaultFontData.length; i++){
			FontData fontData = defaultFontData[i];
			cardFontData[i] = new FontData(fontData.getName(),10,SWT.BOLD);
		}
		JFaceResources.getFontRegistry().put(CardFigureLayer.KEY_OF_CARD_HEADER, cardFontData);
	}

}
