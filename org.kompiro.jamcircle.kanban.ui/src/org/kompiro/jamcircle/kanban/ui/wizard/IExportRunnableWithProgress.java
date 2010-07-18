package org.kompiro.jamcircle.kanban.ui.wizard;

import org.eclipse.jface.operation.IRunnableWithProgress;

public interface IExportRunnableWithProgress extends IRunnableWithProgress {

	public void setPath(String path);

}