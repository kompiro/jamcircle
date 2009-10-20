package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.*;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.*;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.ExtendedEditPartFactory.SupportedClassPair;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class KanbanUIExtensionEditPartFactoryTest {
	
	private static final String EXPECTED_TOSTRING_1 = "Mocked by createElement1";

	@Test
	public void createEditPartWhenNoPointsFound() throws Exception {
		KanbanUIExtensionEditPartFactory factory = new KanbanUIExtensionEditPartFactory();
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		factory.setRegistry(registry);
		factory.initialize();
		try {
			factory.createEditPart(null, new Object());
			fail();
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Test
	public void initialize() throws Exception {
		KanbanUIExtensionEditPartFactory factory = new KanbanUIExtensionEditPartFactory();
		IExtensionRegistry registry = mock(IExtensionRegistry.class);
		factory.setRegistry(registry);
		IExtensionPoint point = mock(IExtensionPoint.class);
		when(registry.getExtensionPoint(KanbanUIExtensionEditPartFactory.POINT_CALLBACK)).thenReturn(point);
		IConfigurationElement elem1 = createElement1();
		IConfigurationElement elem2 = createElement2();
		IConfigurationElement[] elements = new IConfigurationElement[]{
				elem1,
				elem2
		};
		IExtension extension = mock(IExtension.class);
		when(extension.getConfigurationElements()).thenReturn(elements);
		IExtension[] extensions = new IExtension[]{
				extension
		};
		when(point.getExtensions()).thenReturn(extensions);
		factory.initialize();
		BoardModel board = new BoardModel(new Board());
		EditPart part = new BoardEditPart(board);
		EditPart editPart = factory.createEditPart(part , new User());
		assertThat(editPart,not(nullValue()));
		assertThat(editPart.toString(),is(EXPECTED_TOSTRING_1));
		try {
			editPart = factory.createEditPart(part , new Card());
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	private IConfigurationElement createElement1() throws CoreException {
		IConfigurationElement elem1 = mock(IConfigurationElement.class);
		ExtendedEditPartFactory extendedfactory = mock(ExtendedEditPartFactory.class);
		when(extendedfactory.supportedClasses()).thenReturn(new SupportedClassPair[]{new SupportedClassPair(BoardEditPart.class,User.class)});
		EditPart part = mock(EditPart.class);
		when(part.toString()).thenReturn(EXPECTED_TOSTRING_1);
		when(extendedfactory.createEditPart(isA(BoardEditPart.class), isA(User.class))).thenReturn(part);
		when(elem1.createExecutableExtension(KanbanUIExtensionEditPartFactory.ATTR_HANDLER_CLASS)).thenReturn(extendedfactory);
		return elem1;
	}

	private IConfigurationElement createElement2() throws CoreException {
		IConfigurationElement elem = mock(IConfigurationElement.class);
		ExtendedEditPartFactory extendedfactory = mock(ExtendedEditPartFactory.class);
		SupportedClassPair[] pair1 = new SupportedClassPair[]{new SupportedClassPair(LaneEditPart.class,User.class)};
		when(extendedfactory.supportedClasses()).thenReturn(pair1);
		when(elem.createExecutableExtension(KanbanUIExtensionEditPartFactory.ATTR_HANDLER_CLASS)).thenReturn(extendedfactory);
		return elem;
	}

}
