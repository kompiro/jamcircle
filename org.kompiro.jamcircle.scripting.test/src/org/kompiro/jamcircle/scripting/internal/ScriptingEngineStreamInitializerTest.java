package org.kompiro.jamcircle.scripting.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.OutputStream;

import org.eclipse.core.runtime.*;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.scripting.IScriptingEngineStreamLoader;

public class ScriptingEngineStreamInitializerTest {

	ScriptingEngineStreamInitializer initializer;

	@Before
	public void before() {
		initializer = new ScriptingEngineStreamInitializer();
	}

	@Test
	public void init() throws Exception {

		assertThat(initializer.getErrorStream(), is(nullValue()));
		assertThat(initializer.getOutputStream(), is(nullValue()));

	}

	@Test
	public void getOutputStream() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);

		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(ScriptingEngineStreamInitializer.POINT_CALLBACK)).thenReturn(value);

		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[] { extension });

		IConfigurationElement element = mock(IConfigurationElement.class);
		when(extension.getConfigurationElements()).thenReturn(new IConfigurationElement[] { element });
		IScriptingEngineStreamLoader loader = mock(IScriptingEngineStreamLoader.class);
		when(element.createExecutableExtension(ScriptingEngineStreamInitializer.ATTR_HANDLER_CLASS)).thenReturn(loader);

		OutputStream stream = mock(OutputStream.class);
		when(loader.getOutputStream()).thenReturn(stream);

		initializer.setRegistry(registry);
		initializer.activate();

		assertThat(initializer.getOutputStream(), is(notNullValue()));
	}

}
