package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.text.*;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.*;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigure;

public class CardToolTip extends Figure {

	private static final String DOUBLE_CHAR_LENGTH_TARGET = "xx";//$NON-NLS-1$
	private TextFlow body;
	private FlowPage bodyPage;

	private static final String COLOR_KEY_CARD_TOOLTIP = "color_key_card_tooltip"; //$NON-NLS-1$
	
	static{
		int r = 255;
		int g = 246;
		int b = 143;
		RGB rgb = new RGB(r,g,b);
		JFaceResources.getColorRegistry().put(COLOR_KEY_CARD_TOOLTIP, rgb);
	}


	public CardToolTip(ImageRegistry imageRegistry){
		add(new BoldLabel(Messages.CardToolTip_subject_label));
		body = new TextFlow();
		ParagraphTextLayout layout = new ParagraphTextLayout(body,ParagraphTextLayout.WORD_WRAP_TRUNCATE);
		body.setLayoutManager(layout);
		bodyPage = new FlowPage();
		bodyPage.add(body);
		body.setOpaque(true);
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_TOOLTIP));
		GC gc = new GC(getBackgroundColor().getDevice());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent(DOUBLE_CHAR_LENGTH_TARGET).x * CardFigure.CARD_CHARS_OF_LINES * 2;
		int height = fontMetrics.getHeight() * ( CardFigure.CARD_LINES * 2 + 1 ) ;
		add(bodyPage,new Rectangle(0,0,width,height));
		setLayoutManager(new FlowLayout());
		setPreferredSize(width,height);
	}
	
	public void setBody(String body){
		this.body.setText(body);
	}
	
}
