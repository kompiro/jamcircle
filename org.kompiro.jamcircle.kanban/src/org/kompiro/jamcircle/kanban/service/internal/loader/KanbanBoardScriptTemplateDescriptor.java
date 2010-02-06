package org.kompiro.jamcircle.kanban.service.internal.loader;

import java.net.URL;

import org.eclipse.core.runtime.*;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.ScriptBoardTemplate;
import org.kompiro.jamcircle.kanban.internal.util.StreamUtil;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class KanbanBoardScriptTemplateDescriptor {
	
	static final String ATTR_TYPE = "type";
	static final String ATTR_ICON = "icon";
	static final String ATTR_NAME = "name";
	static final String ATTR_SOURCE = "source";
	static final String ATTR_DESCRIPTION = "description";

	public KanbanBoardTemplate createTemplate(IConfigurationElement element) {
		if(element == null) throw new IllegalArgumentException();
		ScriptBoardTemplate template = new ScriptBoardTemplate();
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
		String typeName = element.getAttribute(ATTR_TYPE);
		template.setType(ScriptTypes.valueOf(typeName));
		String sourcePath = element.getAttribute(ATTR_SOURCE);
		URL resource = null;
		if(Platform.isRunning()){
			resource = Platform.getBundle(contributorName).getResource(sourcePath);
		}else{
			resource = this.getClass().getClassLoader().getResource(sourcePath);
		}
		String source = StreamUtil.readFromResource(resource);
		template.setScript(source);
		return template;
	}


}
