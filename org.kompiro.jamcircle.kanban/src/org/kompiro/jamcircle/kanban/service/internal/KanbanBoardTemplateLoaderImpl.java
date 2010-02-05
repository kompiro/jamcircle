package org.kompiro.jamcircle.kanban.service.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;

public class KanbanBoardTemplateLoaderImpl {
	
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.kanban.boardTemplate";
	private IExtensionRegistry registry = RegistryFactory.getRegistry();

	public List<KanbanBoardTemplate> loadBoardTemplates() {
		ArrayList<KanbanBoardTemplate> result = new ArrayList<KanbanBoardTemplate>();
		IExtensionPoint extension = registry.getExtensionPoint(POINT_CALLBACK);
		if(extension == null) return result;
		IConfigurationElement[] configurationElements = extension.getConfigurationElements();
		if(configurationElements == null) return result;
		for (IConfigurationElement configurationElement : configurationElements) {
			try {
				KanbanBoardTemplateDescriptor desc = new KanbanBoardTemplateDescriptor(configurationElement);
				KanbanBoardTemplate template = desc.createTemplate();
				result.add(template);
			} catch (CoreException e) {
			}
		}
		return result;
	}
	
	public void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;
	}

}
