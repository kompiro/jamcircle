package org.kompiro.jamcircle.kanban.ui.internal.editpart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.*;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.mock.*;
import org.kompiro.jamcircle.kanban.ui.editpart.ExtendedEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.editpart.SupportedClassPair;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;


public class KanbanUIExtensionEditPartFactoryTest {
	
	private static final String EXPECTED_TOSTRING_1 = "Mocked by createElement1";
	private static final String EXPECTED_TOSTRING_2 = "Mocked by createElement2";
	private KanbanUIExtensionEditPartFactory factory;

	@Before
	public void before() throws Exception{
		createFactory();
	}
	
	@Test
	public void factoryHasNoIExtensionRegistryWhenNormalEnvironment() throws Exception {
		if(PlatformUI.isWorkbenchRunning() == false) return; 
		KanbanUIExtensionEditPartFactory factory = new KanbanUIExtensionEditPartFactory();
		assertThat(factory.getRegistry(),notNullValue());
		assertThat(factory.getRegistry(),instanceOf(IExtensionRegistry.class));
	}
	
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
	public void createEditPartWhenSupportedOnBoard() throws Exception {
		BoardModel board = new BoardModel(new Board());
		EditPart part = new BoardEditPart(board);
		EditPart editPart = factory.createEditPart(part , new User());
		assertThat(editPart,not(nullValue()));
		assertThat(editPart.toString(),is(EXPECTED_TOSTRING_1));
	}

	@Test
	public void createEditPartWhenSupportedChildClassOnBoard() throws Exception {
		BoardModel board = new BoardModel(new Board());
		EditPart part = new BoardEditPart(board);
		EditPart editPart = factory.createEditPart(part , new org.kompiro.jamcircle.kanban.model.mock.User());
		assertThat(editPart,not(nullValue()));
		assertThat(editPart.toString(),is(EXPECTED_TOSTRING_1));
	}

	
	@Test
	public void createEditPartWhenSupportedOnLane() throws Exception {
		BoardModel board = new BoardModel(new Board());
		EditPart part = new LaneEditPart(board);
		EditPart editPart = factory.createEditPart(part , new User());
		assertThat(editPart,not(nullValue()));
		assertThat(editPart.toString(),is(EXPECTED_TOSTRING_2));
	}
	
	@Test
	public void createEditPart() throws Exception {
		BoardModel board = new BoardModel(new Board());
		EditPart part = new BoardEditPart(board);
		factory.createEditPart(part , new User());
		part = new LaneEditPart(board);
		EditPart editPart = factory.createEditPart(part , new User());
		
		assertThat(editPart,not(nullValue()));
		assertThat(editPart.toString(),is(EXPECTED_TOSTRING_2));		
	}
	
	@Test
	public void createEditPartWhenNotSupported() throws Exception {
		BoardModel board = new BoardModel(new Board());
		EditPart part = new BoardEditPart(board);
		try {
			factory.createEditPart(part , new Card());
			fail();
		} catch (IllegalArgumentException e) {
		}
		part = new LaneEditPart(board);
		try {
			factory.createEditPart(part , new Card());
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	private void createFactory() throws CoreException {
		factory = new KanbanUIExtensionEditPartFactory();
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
	}

	private IConfigurationElement createElement1() throws CoreException {
		IConfigurationElement elem = mock(IConfigurationElement.class);
		ExtendedEditPartFactory extendedfactory = mock(ExtendedEditPartFactory.class);
		when(extendedfactory.supportedClasses()).thenReturn(new SupportedClassPair[]{new SupportedClassPair(BoardEditPart.class,User.class)});
		EditPart part = mock(EditPart.class);
		when(part.toString()).thenReturn(EXPECTED_TOSTRING_1);
		when(extendedfactory.createEditPart(isA(BoardEditPart.class), isA(User.class))).thenReturn(part);
		when(elem.createExecutableExtension(KanbanUIExtensionEditPartFactory.ATTR_HANDLER_CLASS)).thenReturn(extendedfactory);
		return elem;
	}

	private IConfigurationElement createElement2() throws CoreException {
		IConfigurationElement elem = mock(IConfigurationElement.class);
		ExtendedEditPartFactory extendedfactory = mock(ExtendedEditPartFactory.class);
		SupportedClassPair[] pair1 = new SupportedClassPair[]{new SupportedClassPair(LaneEditPart.class,User.class)};
		when(extendedfactory.supportedClasses()).thenReturn(pair1);
		EditPart part = mock(EditPart.class);
		when(part.toString()).thenReturn(EXPECTED_TOSTRING_2);
		when(extendedfactory.createEditPart(isA(LaneEditPart.class), isA(User.class))).thenReturn(part);
		when(elem.createExecutableExtension(KanbanUIExtensionEditPartFactory.ATTR_HANDLER_CLASS)).thenReturn(extendedfactory);
		return elem;
	}

}
