package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.text.*;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.*;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.internal.figure.CardFigureLayer;

public class CardToolTip extends Figure {

	private TextFlow body;
	private FlowPage bodyPage;

	private static final String COLOR_KEY_CARD_TOOLTIP = "color_key_card_tooltip";
	
	static{
		int r = 255;
		int g = 246;
		int b = 143;
		RGB rgb = new RGB(r,g,b);
		JFaceResources.getColorRegistry().put(COLOR_KEY_CARD_TOOLTIP, rgb);
	}


	public CardToolTip(ImageRegistry imageRegistry){
		add(new BoldLabel("subject:"));
		body = new TextFlow();
		ParagraphTextLayout layout = new ParagraphTextLayout(body,ParagraphTextLayout.WORD_WRAP_TRUNCATE);
		body.setLayoutManager(layout);
		bodyPage = new FlowPage();
		bodyPage.add(body);
		body.setOpaque(true);
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_TOOLTIP));
		GC gc = new GC(getBackgroundColor().getDevice());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent("xx").x * CardFigureLayer.CARD_CHARS_OF_LINES * 2;
		int height = fontMetrics.getHeight() * ( CardFigureLayer.CARD_LINES * 2 + 1 ) ;
		add(bodyPage,new Rectangle(0,0,width,height));
		Image pageIconImage = imageRegistry.get(KanbanImageConstants.PAGE_IMAGE.toString());

		setLayoutManager(new FlowLayout());
		setPreferredSize(width,height);
	}
	
	public void setBody(String body){
		this.body.setText(body);
	}
	
}
