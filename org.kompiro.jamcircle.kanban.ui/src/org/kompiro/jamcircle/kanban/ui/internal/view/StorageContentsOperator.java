package org.kompiro.jamcircle.kanban.ui.internal.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;

public interface StorageContentsOperator {

	/**
	 * initialized operator
	 * 
	 * @throws IllegalStateException
	 *             is threw when a field's state are illegal.
	 */
	void initialize() throws IllegalStateException;

	/**
	 * set contents to target viewer
	 * 
	 * @param viewer
	 *            target viewer
	 * @param boardModel
	 *            contents
	 * @param monitor
	 *            monitor
	 */
	void setContents(GraphicalViewer viewer, BoardModel boardModel, IProgressMonitor monitor);

}