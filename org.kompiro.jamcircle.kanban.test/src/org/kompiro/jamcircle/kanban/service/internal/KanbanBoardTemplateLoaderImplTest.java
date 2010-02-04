package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.service.internal.KanbanBoardTemplateDescriptor.ATTR_CLASS;
import static org.kompiro.jamcircle.kanban.service.internal.KanbanBoardTemplateDescriptor.ATTR_NAME;
import static org.kompiro.jamcircle.kanban.service.internal.KanbanBoardTemplateLoaderImpl.POINT_CALLBACK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;


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
		IExtension extension = mock(IExtension.class);
		
		when(registry.getExtension(POINT_CALLBACK)).thenReturn(extension );
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(extension.getConfigurationElements()).thenReturn(new IConfigurationElement[]{element });
		KanbanBoardTemplate template = mock(KanbanBoardTemplate.class);
		when(element.createExecutableExtension(ATTR_CLASS)).thenReturn(template);
		when(element.getAttribute(ATTR_NAME)).thenReturn("template");
		
		impl.setRegistry(registry);
		List<KanbanBoardTemplate> templates = impl.loadBoardTemplates();
		assertThat(templates,not(nullValue()));
		assertThat(templates.size(),is(1));
		verify(template).setName("template");
	}
}
