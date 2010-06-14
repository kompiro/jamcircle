package org.kompiro.jamcircle.kanban.ui.internal.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kompiro.jamcircle.kanban.model.Board;

public interface StorageContentsOperator {

	/**
	 * set contents
	 * @param board
	 * @param monitor
	 */
	void setContents(Board board, IProgressMonitor monitor);

}