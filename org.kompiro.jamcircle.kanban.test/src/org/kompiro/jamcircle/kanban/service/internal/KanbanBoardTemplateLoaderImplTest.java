package org.kompiro.jamcircle.kanban.service.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.service.internal.KanbanBoardTemplateLoaderImpl.ATTR_CLASS;
import static org.kompiro.jamcircle.kanban.service.internal.KanbanBoardTemplateLoaderImpl.POINT_CALLBACK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		Object template = mock(KanbanBoardTemplate.class);
		when(element.createExecutableExtension(ATTR_CLASS)).thenReturn(template );
		
		impl.setRegistry(registry);
		List<KanbanBoardTemplate> templates = impl.loadBoardTemplates();
		assertThat(templates,not(nullValue()));
		assertThat(templates.size(),is(1));		
	}
}
