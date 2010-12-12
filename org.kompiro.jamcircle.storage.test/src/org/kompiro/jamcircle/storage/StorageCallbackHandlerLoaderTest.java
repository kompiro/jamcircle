package org.kompiro.jamcircle.storage;

import static org.kompiro.jamcircle.storage.StorageCallbackHandlerLoader.ATTR_HANDLER_CLASS;
import static org.kompiro.jamcircle.storage.StorageCallbackHandlerLoader.POINT_CALLBACK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.*;
import org.junit.Before;
import org.junit.Test;

public class StorageCallbackHandlerLoaderTest {

	private StorageCallbackHandlerLoader loader;

	@Before
	public void before() throws Exception {
		loader = new StorageCallbackHandlerLoader();

	}

	@Test
	public void setupStorageSetting() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);

		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);

		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[] { extension });
		IConfigurationElement element = mock(IConfigurationElement.class);
		when(extension.getConfigurationElements()).thenReturn(new IConfigurationElement[] { element });
		IUIStorageCallbackHandler initializer = mock(IUIStorageCallbackHandler.class);
		when(element.createExecutableExtension(ATTR_HANDLER_CLASS)).thenReturn(initializer);
		loader.setRegistry(registry);

		loader.setupStorageSetting();
		verify(initializer).setupStorageSetting();

	}

	@Test
	public void no_extension_point() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);

		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);
		loader.setRegistry(registry);
		loader.setupStorageSetting();

	}

	@Test
	public void no_config_element() throws Exception {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);

		IExtensionPoint value = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(POINT_CALLBACK)).thenReturn(value);
		IExtension extension = mock(IExtension.class);
		when(value.getExtensions()).thenReturn(new IExtension[] { extension });
		loader.setRegistry(registry);
		loader.setupStorageSetting();

	}
}
