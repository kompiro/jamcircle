package org.kompiro.jamcircle.kanban.service.internal.loader;

import org.eclipse.core.runtime.IConfigurationElement;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.ScriptBoardTemplate;

public class KanbanBoardScriptTemplateDescriptor {

	public KanbanBoardTemplate createTemplate(IConfigurationElement element) {
		ScriptBoardTemplate template = new ScriptBoardTemplate();
		return template;
	}


}
