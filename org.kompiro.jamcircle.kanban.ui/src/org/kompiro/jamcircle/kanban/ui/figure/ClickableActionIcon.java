package org.kompiro.jamcircle.kanban.ui.figure;

import org.eclipse.draw2d.*;
import org.eclipse.swt.graphics.Image;

public abstract class ClickableActionIcon extends Clickable implements ActionListener{
	
	public ClickableActionIcon(Image image){
		super(new Label(image));
		setSize(16,16);
	}
	
	@Override
	public void addNotify() {
		addActionListener(this);
		super.addNotify();
	}
	
	public void setTooltipText(String text){
		setToolTip(new Label(text));
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		removeActionListener(this);
	}

	public abstract void actionPerformed(ActionEvent event);

}
