package org.kompiro.jamcircle.kanban.service.internal.loader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.kompiro.jamcircle.kanban.service.internal.loader.KanbanBoardTemplateDescriptor.*;

import org.eclipse.core.runtime.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.KanbanActivator;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.model.Board;


public class KanbanBoardTemplateDescriptorTest {
	
	private KanbanBoardTemplateDescriptor desc;

	@Before
	public void before() throws Exception {
		desc = new KanbanBoardTemplateDescriptor();		
	}

	@Test
	public void createTemplate_null_arguments() throws Exception {
		try {
			desc.createTemplate(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Test
	public void createTemplate_illegal_class() throws Exception {
		IConfigurationElement element = mock(IConfigurationElement.class);
		IStatus status = KanbanActivator.createErrorStatus("");
		when(element.createExecutableExtension(ATTR_CLASS)).thenThrow(new CoreException(status ));
		try {
			desc.createTemplate(element);
			fail();
		} catch (CoreException e) {
		}
	}
	
	@Test
	public void createTemplate_illegal_name_and_icon() throws Exception {
		IConfigurationElement element = mock(IConfigurationElement.class);
		Object value = createTemplateImpl();
		when(element.createExecutableExtension(ATTR_CLASS)).thenReturn(value);
		KanbanBoardTemplate template = desc.createTemplate(element);
		assertThat(template.getName(),is(nullValue()));
		assertThat(template.getIcon(),is(AbstractBoardTemplate.DEFAULT_ICONS_PATH));
		assertThat(template.getDescription(),is(nullValue()));
	}
	
	@Test
	public void createTemplate() throws Exception {
		IConfigurationElement element = mock(IConfigurationElement.class);
		AbstractBoardTemplate value = createTemplateImpl();
		when(element.createExecutableExtension(ATTR_CLASS)).thenReturn(value);
		when(element.getAttribute(ATTR_ICON)).thenReturn("icon");
		when(element.getAttribute(ATTR_NAME)).thenReturn("name");
		when(element.getAttribute(ATTR_DESCRIPTION)).thenReturn("desc");
		IContributor contributor = mock(IContributor.class);
		when(contributor.getName()).thenReturn(KanbanActivator.ID);
		when(element.getContributor()).thenReturn(contributor);
		KanbanBoardTemplate template = desc.createTemplate(element);
		assertThat(template.getName(),is("name"));
		assertThat(template.getIcon(),is("icon"));
		assertThat(template.getDescription(),is("desc"));
		assertThat(((AbstractBoardTemplate)template).getContributor(),is(KanbanActivator.ID));
	}

	private AbstractBoardTemplate createTemplateImpl() {
		AbstractBoardTemplate value = new AbstractBoardTemplate() {
			public void initialize(Board board) {
			}
		};
		return value;
	}

}
