package org.kompiro.jamcircle.kanban.service.internal.loader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.service.internal.loader.BoardScriptTemplateDescriptor.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.internal.ScriptBoardTemplate;
import org.kompiro.jamcircle.scripting.ScriptTypes;


public class BoardScriptTemplateDescriptorTest {

	private BoardScriptTemplateDescriptor desc;

	@Before
	public void before() throws Exception {
		desc = new BoardScriptTemplateDescriptor();		
	}

	@Test(expected=IllegalArgumentException.class)
	public void createTemplate_null_arguments() throws Exception {
		desc.createTemplate(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createTemplate_illegal_type_is_not_set() throws Exception {
		IConfigurationElement element = mock(IConfigurationElement.class);
		desc.createTemplate(element);
	}

	@Test
	public void createTemplate_illegal_name_and_icon() throws Exception {
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(element.getAttribute(ATTR_TYPE)).thenReturn("JRuby");

		
		KanbanBoardTemplate template = desc.createTemplate(element);
		
		
		assertThat(template.getName(),is(nullValue()));
		assertThat(template.getIcon(),is(ScriptBoardTemplate.DEFAULT_ICONS_PATH));
		assertThat(template.getDescription(),is(nullValue()));
		assertThat(template, instanceOf(ScriptBoardTemplate.class));
		ScriptBoardTemplate sbt = (ScriptBoardTemplate) template;
		assertThat(sbt.getType(),is(ScriptTypes.valueOf("JRuby")));
		assertThat(sbt.getScript(),is(nullValue()));
	}
	
	@Test
	public void createTemplate() throws Exception {
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(element.getAttribute(ATTR_ICON)).thenReturn("icon");
		when(element.getAttribute(ATTR_NAME)).thenReturn("name");
		when(element.getAttribute(ATTR_DESCRIPTION)).thenReturn("desc");
		
		when(element.getAttribute(ATTR_TYPE)).thenReturn(ScriptTypes.JRuby.name());
		when(element.getAttribute(ATTR_SOURCE)).thenReturn("test.rb");
		
		IContributor contributor = mock(IContributor.class);
		when(contributor.getName()).thenReturn(KanbanActivator.ID);
		when(element.getContributor()).thenReturn(contributor);
	
		
		KanbanBoardTemplate template = desc.createTemplate(element);
		
		
		assertThat(template.getName(),is("name"));
		assertThat(template.getIcon(),is("icon"));
		assertThat(template.getDescription(),is("desc"));
		assertThat(((AbstractBoardTemplate)template).getContributor(),is(KanbanActivator.ID));
		
		assertThat(template, instanceOf(ScriptBoardTemplate.class));
		ScriptBoardTemplate sbt = (ScriptBoardTemplate) template;
		assertThat(sbt.getType(),is(ScriptTypes.JRuby));
		assertThat(sbt.getScript(),is("p 'test'\n"));
	}
	
}
