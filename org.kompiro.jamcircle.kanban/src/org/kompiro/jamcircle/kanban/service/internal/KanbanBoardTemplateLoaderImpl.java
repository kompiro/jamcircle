package org.kompiro.jamcircle.kanban.service.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;

public class KanbanBoardTemplateLoaderImpl {
	
	static final String POINT_CALLBACK = "org.kompiro.jamcircle.kanban.boardTemplate";
	static final String ATTR_CLASS = "class";
	private IExtensionRegistry registry = RegistryFactory.getRegistry();

	public List<KanbanBoardTemplate> loadBoardTemplates() {
		ArrayList<KanbanBoardTemplate> result = new ArrayList<KanbanBoardTemplate>();
		IExtension extension = registry.getExtension(POINT_CALLBACK);
		IConfigurationElement[] configurationElements = extension.getConfigurationElements();
		if(configurationElements == null) return result;
		for (IConfigurationElement configurationElement : configurationElements) {
			try {
				KanbanBoardTemplate template = (KanbanBoardTemplate) configurationElement.createExecutableExtension(ATTR_CLASS);
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