package org.kompiro.jamcircle.xmpp.internal.extension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.*;
import org.junit.Test;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;
import org.kompiro.jamcircle.xmpp.service.internal.XMPPConnectionServiceImpl;


public class XMPPLoginListenerFactoryTest {
	
	private XMPPLoginListener listener;

	@Test
	public void factoryHasIExtensionRegistryWhenNormalEnvironment() throws Exception {
		XMPPLoginListenerFactory factory = new XMPPLoginListenerFactory();
		assertThat(factory.getRegistry(),notNullValue());
		assertThat(factory.getRegistry(),is(IExtensionRegistry.class));
	}
	
	@Test
	public void provide() throws Exception {
		XMPPConnectionServiceImpl impl = spy(new XMPPConnectionServiceImpl());

		XMPPLoginListenerFactory factory = new XMPPLoginListenerFactory();
		IExtensionRegistry registry = createExtensionRegistry();
		factory.setRegistry(registry);
		
		factory.bind(impl);
		
		verify(impl).addXMPPLoginListener(listener);
	}

	@Test
	public void throwIllegalStateException() throws Exception {
		XMPPConnectionServiceImpl impl = spy(new XMPPConnectionServiceImpl());

		XMPPLoginListenerFactory factory = new XMPPLoginListenerFactory();
		IExtensionRegistry registry = createThrowExceptionExtensionRegistry();
		factory.setRegistry(registry);
		
		try {
			factory.bind(impl);
			fail();
		} catch (Exception e) {
		}
		
	}

	
	private IExtensionRegistry createThrowExceptionExtensionRegistry() throws CoreException {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		IExtensionPoint point = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(XMPPLoginListenerFactory.POINT_CALLBACK)).thenReturn(point);
		IConfigurationElement elem1 = createThrowExceptionElement();
		IConfigurationElement[] elements = new IConfigurationElement[]{
				elem1,
		};
		IExtension extension = mock(IExtension.class);
		when(extension.getConfigurationElements()).thenReturn(elements);
		IExtension[] extensions = new IExtension[]{
				extension
		};
		when(point.getExtensions()).thenReturn(extensions);
		return registry;
	}

	private IConfigurationElement createThrowExceptionElement() throws CoreException {
		IConfigurationElement elem = mock(IConfigurationElement.class);
		IStatus status = mock(IStatus.class);
		CoreException coreException = new CoreException(status);
		when(elem.createExecutableExtension(XMPPLoginListenerFactory.ATTR_CLASS)).thenThrow(coreException);
		return elem;
	}

	
	private IExtensionRegistry createExtensionRegistry() throws CoreException {
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		IExtensionPoint point = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(XMPPLoginListenerFactory.POINT_CALLBACK)).thenReturn(point);
		IConfigurationElement elem1 = createElement1();
		IConfigurationElement[] elements = new IConfigurationElement[]{
				elem1,
		};
		IExtension extension = mock(IExtension.class);
		when(extension.getConfigurationElements()).thenReturn(elements);
		IExtension[] extensions = new IExtension[]{
				extension
		};
		when(point.getExtensions()).thenReturn(extensions);
		return registry;
	}

	private IConfigurationElement createElement1() throws CoreException {
		IConfigurationElement elem = mock(IConfigurationElement.class);
		listener = mock(XMPPLoginListener.class);
		when(elem.createExecutableExtension(XMPPLoginListenerFactory.ATTR_CLASS)).thenReturn(listener);
		return elem;
	}

}
