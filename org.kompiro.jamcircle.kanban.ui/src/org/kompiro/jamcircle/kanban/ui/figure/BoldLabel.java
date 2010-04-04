package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.Label;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class BoldLabel extends Label{
	private final static String BOLD_FONT = "BOLD_FONT"; //$NON-NLS-1$
	static{
		Font fo = JFaceResources.getDefaultFont();
		FontData[] fontData = fo.getFontData();
		for(int i = 0; i < fontData.length; i++){
			fontData[i].setStyle(SWT.BOLD);
		}
		JFaceResources.getFontRegistry().put(BOLD_FONT,fontData);				
	}
	
	public BoldLabel(){
		super();
		setFont(JFaceResources.getFontRegistry().get(BOLD_FONT));
	}
	
	public BoldLabel(String label){
		this();
		setText(label);
	}
	
}
