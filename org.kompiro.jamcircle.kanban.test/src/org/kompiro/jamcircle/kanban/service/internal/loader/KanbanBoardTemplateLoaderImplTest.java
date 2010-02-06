package org.kompiro.jamcircle.kanban.service.internal.loader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.service.internal.loader.KanbanBoardTemplateDescriptor.*;
import static org.kompiro.jamcircle.kanban.service.internal.loader.KanbanBoardTemplateLoaderImpl.POINT_CALLBACK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.core.runtime.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.service.internal.loader.KanbanBoardTemplateLoaderImpl;


public class KanbanBoardTemplateLoaderImplTest {
	
	private KanbanBoardTemplateLoaderImpl impl;

	@Before
	public void before() throws Exception{
		impl = new KanbanBoardTemplateLoaderImpl();
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
		KanbanBoardTemplate template = mock(KanbanBoardTemplate.class);
		when(element.createExecutableExtension(ATTR_CLASS)).thenReturn(template);
		when(element.getAttribute(ATTR_NAME)).thenReturn("template");
		when(element.getAttribute(ATTR_ICON)).thenReturn("template.png");
		
		impl.setRegistry(registry);
		List<KanbanBoardTemplate> templates = impl.loadBoardTemplates();
		assertThat(templates,not(nullValue()));
		assertThat(templates.size(),is(1));
		verify(template).setName("template");
		verify(template).setIcon("template.png");
	}
}
