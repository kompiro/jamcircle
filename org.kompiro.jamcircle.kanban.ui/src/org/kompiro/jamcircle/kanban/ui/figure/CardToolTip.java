package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

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


	public CardToolTip(){
		FlowLayout manager = new FlowLayout();
		manager.setMajorSpacing(0);
		manager.setMinorSpacing(0);
		setLayoutManager(manager);
		
		body = new TextFlow();
		ParagraphTextLayout layout = new ParagraphTextLayout(body,ParagraphTextLayout.WORD_WRAP_SOFT);
		body.setLayoutManager(layout);
		bodyPage = new FlowPage();
		bodyPage.add(body);
		body.setOpaque(true);
		add(bodyPage);
		setBackgroundColor(JFaceResources.getColorRegistry().get(COLOR_KEY_CARD_TOOLTIP));
		GC gc = new GC(getBackgroundColor().getDevice());
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = gc.stringExtent("xx").x * 10;
		int height = fontMetrics.getHeight() * 4;
		setSize(width, height);

	}
	
	public void setBody(String body){
		this.body.setText(body);
	}
	
}
