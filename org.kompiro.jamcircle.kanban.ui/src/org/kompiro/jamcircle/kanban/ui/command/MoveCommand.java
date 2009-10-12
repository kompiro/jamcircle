package org.kompiro.jamcircle.kanban.ui.command;

import org.eclipse.draw2d.geometry.Rectangle;

public abstract class MoveCommand extends AbstractCommand{
	
	private Object model;
	private Rectangle rect;

	public void setModel(Object model){
		this.model = model;
	}
	
	protected Object getModel() throws IllegalStateException{
		if(model == null) throw new IllegalStateException("This command needs to initialize models.");
		return model;
	}

	public void setRectangle(Rectangle rect) {
		this.rect = rect;
	}
	
	protected Rectangle getRectangle() throws IllegalStateException{
		if(rect == null) throw new IllegalStateException("This command needs to initialize rectangle.");
		return rect;
	}
	
	protected abstract void initialize();

}
