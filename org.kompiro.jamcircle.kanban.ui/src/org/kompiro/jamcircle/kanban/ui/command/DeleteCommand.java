package org.kompiro.jamcircle.kanban.ui.command;


public abstract class DeleteCommand<T,U> extends AbstractCommand{
	
	private T model;
	private U container;

	public void setModel(T model){
		this.model = model;
	}
	
	protected T getModel() throws IllegalStateException{
		return model;
	}

	public void setContainer(U container) {
		this.container = container;
	}
	
	protected U getContainer() throws IllegalStateException{
		return container;
	}
	
	@Override
	public final void doExecute() {
		delete();
		setUndoable(true);
	}
	
	protected abstract void delete();

}
