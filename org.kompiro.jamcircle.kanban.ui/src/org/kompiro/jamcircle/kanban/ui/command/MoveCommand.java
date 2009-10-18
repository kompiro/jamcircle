package org.kompiro.jamcircle.kanban.ui.command;

import org.eclipse.draw2d.geometry.Rectangle;

public abstract class MoveCommand<T> extends AbstractCommand{
	
	private T model;
	private Rectangle rect;

	public void setModel(T model){
		this.model = model;
	}
	
	protected T getModel() throws IllegalStateException{
		return model;
	}

	public void setRectangle(Rectangle rect) {
		this.rect = rect;
	}
	
	protected Rectangle getRectangle() throws IllegalStateException{
		return rect;
	}
	
	@Override
	public final void doExecute() {
		move();
		setUndoable(true);
	}
	
	protected abstract void move();

}
