package org.kompiro.jamcircle.kanban.service.internal;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;

public class KanbanBoardTemplateDescriptor {

	private IConfigurationElement element;
	static final String ATTR_CLASS = "class";
	static final String ATTR_ICON = "icon";
	static final String ATTR_NAME = "name";

	public KanbanBoardTemplateDescriptor(
			IConfigurationElement element) {
		this.element = element;
	}

	public KanbanBoardTemplate createTemplate() throws CoreException {
		KanbanBoardTemplate template = (KanbanBoardTemplate) element.createExecutableExtension(ATTR_CLASS);
		String name = element.getAttribute(ATTR_NAME);
		template.setName(name);
		String icon = element.getAttribute(ATTR_ICON);
		template.setIcon(icon);
		IContributor contributor = element.getContributor();
		if(contributor == null) return template;
		String contributorName = contributor.getName();
		template.setContributor(contributorName);
		return template;
	}

	
	
}
