package org.kompiro.jamcircle.scripting.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.scripting.internal.ScriptingEngineInitializerLoaderImpl.POINT_CALLBACK;
import static org.kompiro.jamcircle.scripting.internal.ScriptingInitializerLoaderDescriptor.ATTR_HANDLER_CLASS;
import static org.kompiro.jamcircle.scripting.internal.ScriptingInitializerLoaderDescriptor.ScriptExtendDescriptor.FILE;
import static org.kompiro.jamcircle.scripting.internal.ScriptingInitializerLoaderDescriptor.ScriptExtendDescriptor.TYPE;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.eclipse.core.runtime.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.scripting.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.service.component.ComponentContext;

@SuppressWarnings("unchecked")
public class ScriptingEngineInitializerLoaderImplTest{
	private ScriptingEngineInitializerLoaderImpl loader;
	private ScriptingServiceImpl service;

	@Before
	public void before() throws Exception{
		loader = new ScriptingEngineInitializerLoaderImpl();
		service = mock(ScriptingServiceImpl.class);
	}

	@Test
	public void initGlobalValues() throws Exception {
		Map<String, Object> values = loader.getGrobalValues();
		assertThat(values,not(nullValue()));
		assertThat(values.isEmpty(),is(true));
	}
	
	@Test
	public void getGrobalValues() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		
		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);

		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[]{extension});
		
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(extension.getConfigurationElements()).thenReturn(new IConfigurationElement[]{element });	IScriptingEngineInitializer initializer = mock(IScriptingEngineInitializer.class);
		when(element.createExecutableExtension(ATTR_HANDLER_CLASS)).thenReturn(initializer );
		loader.setRegistry(registry);
		
		doAnswer(new Answer<Void>(){
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object obj = invocation.getArguments()[0];
				Map<String,Object> map = (Map<String, Object>) obj;
				map.put("test", new Object());
				return null;
			}
		}).when(initializer).init((Map<String, Object>)anyMap());
		ComponentContext context = mock(ComponentContext.class);
		loader.activate(context);
		
		Map<String, Object> values = loader.getGrobalValues();
		assertThat(values,not(nullValue()));
		assertThat(values.isEmpty(),is(false));
		assertThat(values.size(),is(2));
		assertThat(values.containsKey("test"),is(true));
	}
	
	@Test
	public void noExtensionPoint() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		
		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);
		loader.setRegistry(registry);
		ScriptingService service = mock(ScriptingService.class);
		loader.loadExtendScript(service);
		verify(service,never()).eval((ScriptTypes)anyObject(), (String)anyObject(), (String)anyObject(), (Map<String,Object>)anyObject());

		Map<String, Object> values = loader.getGrobalValues();
		assertThat(values,not(nullValue()));
		assertThat(values.isEmpty(),is(true));
	}

	@Test
	public void noConfigElement() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		
		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);
		
		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[]{extension});
		
		loader.setRegistry(registry);

		loader.loadExtendScript(service);
		verify(service,never()).eval((ScriptTypes)anyObject(), (String)anyObject(), (String)anyObject(), (Map<String,Object>)anyObject());
	}

	@Test
	public void noScript() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		
		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);
		
		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[]{extension});
		
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(extension.getConfigurationElements()).thenReturn(new IConfigurationElement[]{element });
		
		loader.setRegistry(registry);

		loader.loadExtendScript(service);
		verify(service,never()).eval((ScriptTypes)anyObject(), (String)anyObject(), (String)anyObject(), (Map<String,Object>)anyObject());
	}
	
	@Test
	public void loadExtendScript() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		
		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);
		
		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[]{extension});
		
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(extension.getConfigurationElements()).thenReturn(new IConfigurationElement[]{element });
		
		IConfigurationElement script = mock(IConfigurationElement.class);
		when(script.getAttribute(TYPE)).thenReturn("JRuby");
		when(script.getAttribute(FILE)).thenReturn("JRuby");
		when(element.getChildren()).thenReturn(new IConfigurationElement[]{script });
		
		loader.setRegistry(registry);
		loader.loadExtendScript(service);
		verify(service,times(1)).executeScript(eq(ScriptTypes.valueOf("JRuby")), (String)anyObject(), (String)anyObject(), eq(0));
	}

}
