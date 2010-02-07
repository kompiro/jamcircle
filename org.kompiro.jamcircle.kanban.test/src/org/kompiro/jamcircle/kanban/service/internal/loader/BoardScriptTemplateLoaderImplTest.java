package org.kompiro.jamcircle.kanban.service.internal.loader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.kompiro.jamcircle.kanban.service.internal.loader.BoardScriptTemplateLoaderImpl.*;
import static org.kompiro.jamcircle.kanban.service.internal.loader.BoardScriptTemplateDescriptor.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.runtime.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.ScriptBoardTemplate;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class BoardScriptTemplateLoaderImplTest {

	private BoardScriptTemplateLoaderImpl impl;

	@Before
	public void before() throws Exception{
		impl = new BoardScriptTemplateLoaderImpl();
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		impl.setRegistry(registry);
	}

	@Test
	public void loadBoardTemplates_when_no_registered() throws Exception {
		List<KanbanBoardTemplate> templates = impl.loadBoardTemplates();
		assertThat(templates,not(nullValue()));
		assertThat(templates.size(),is(0));
	}
	
	@Test
	public void loadBoardTemplates() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		
		IExtensionPoint point = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(point);
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(point.getConfigurationElements()).thenReturn(new IConfigurationElement[]{element });
		when(element.getAttribute(ATTR_NAME)).thenReturn("template");
		when(element.getAttribute(ATTR_TYPE)).thenReturn("JRuby");
		
		impl.setRegistry(registry);
		List<KanbanBoardTemplate> templates = impl.loadBoardTemplates();
		assertThat(templates,not(nullValue()));
		assertThat(templates.size(),is(1));
		KanbanBoardTemplate template = templates.get(0);
		assertThat(template.getName(),is("template"));
		assertThat(template,instanceOf(ScriptBoardTemplate.class));
		ScriptBoardTemplate sTemplate = (ScriptBoardTemplate) template;
		assertThat(sTemplate.getType(),is(ScriptTypes.JRuby));
	}	
}
