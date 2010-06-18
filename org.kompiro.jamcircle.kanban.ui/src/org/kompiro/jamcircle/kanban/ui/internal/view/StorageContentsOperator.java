package org.kompiro.jamcircle.kanban.ui.internal.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.kompiro.jamcircle.kanban.model.Board;

public interface StorageContentsOperator {
	
	/**
	 * initialized operator
	 * @throws IllegalStateException When some fields are illegal state,it throws.
	 */
	void initialize() throws IllegalStateException;

	/**
	 * set contents to target viewer
	 * @param board
	 * @param monitor
	 */
	void setContents(GraphicalViewer viewer,Board board, IProgressMonitor monitor);


}