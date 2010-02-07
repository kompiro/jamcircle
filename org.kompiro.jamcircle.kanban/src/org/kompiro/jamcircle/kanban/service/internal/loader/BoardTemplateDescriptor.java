package org.kompiro.jamcircle.kanban.service.internal.loader;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;

public class BoardTemplateDescriptor {

	static final String ATTR_CLASS = "class";
	static final String ATTR_ICON = "icon";
	static final String ATTR_NAME = "name";
	static final String ATTR_DESCRIPTION = "description";

	public KanbanBoardTemplate createTemplate(IConfigurationElement element) throws CoreException,IllegalArgumentException {
		if(element == null) throw new IllegalArgumentException();
		KanbanBoardTemplate template = (KanbanBoardTemplate) element.createExecutableExtension(ATTR_CLASS);
		String name = element.getAttribute(ATTR_NAME);
		template.setName(name);
		String icon = element.getAttribute(ATTR_ICON);
		template.setIcon(icon);
		String description = element.getAttribute(ATTR_DESCRIPTION);
		template.setDescription(description);
		IContributor contributor = element.getContributor();
		if(contributor == null) return template;
		String contributorName = contributor.getName();
		template.setContributor(contributorName);
		return template;
	}

	
	
}
