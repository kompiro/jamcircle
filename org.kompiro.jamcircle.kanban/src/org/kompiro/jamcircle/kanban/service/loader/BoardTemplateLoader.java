package org.kompiro.jamcircle.kanban.service.loader;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;

public interface BoardTemplateLoader {

	public abstract List<KanbanBoardTemplate> loadBoardTemplates()
			throws CoreException;

}