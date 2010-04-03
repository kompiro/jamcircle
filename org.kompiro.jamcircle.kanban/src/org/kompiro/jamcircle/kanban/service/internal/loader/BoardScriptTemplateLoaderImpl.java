package org.kompiro.jamcircle.kanban.service.internal.loader;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.service.loader.BoardTemplateLoader;

public class BoardScriptTemplateLoaderImpl implements BoardTemplateLoader{
	
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.kanban.boardScriptTemplate"; //$NON-NLS-1$
	private IExtensionRegistry registry = RegistryFactory.getRegistry();

	public List<KanbanBoardTemplate> loadBoardTemplates() throws CoreException {
		ArrayList<KanbanBoardTemplate> result = new ArrayList<KanbanBoardTemplate>();
		IExtensionPoint extension = registry.getExtensionPoint(POINT_CALLBACK);
		if(extension == null) return result;
		IConfigurationElement[] configurationElements = extension.getConfigurationElements();
		if(configurationElements == null) return result;
		for (IConfigurationElement element : configurationElements) {
			BoardScriptTemplateDescriptor desc = new BoardScriptTemplateDescriptor();
			KanbanBoardTemplate template = desc.createTemplate(element);
			result.add(template);
		}
		return result;
	}
	
	public void setRegistry(IExtensionRegistry registry) {
		this.registry = registry;
	}

}
