package org.kompiro.jamcircle.kanban.ui.internal.view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.widgets.Control;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.BoardEditPart;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIExtensionEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;
import org.mockito.ArgumentCaptor;

public class StorageContentsOperatorImplTest {

	private StorageContentsOperatorImpl operator;
	private ScriptingService scriptingService;

	@Before
	public void before() {
		scriptingService = mock(ScriptingService.class);
		operator = createOperator(scriptingService);
		KanbanUIExtensionEditPartFactory extensionFactory = mock(KanbanUIExtensionEditPartFactory.class);
		doNothing().when(extensionFactory).initialize();
		operator.setExtensionFactory(extensionFactory);
		StorageContentsOperatorImpl.setDelegator(new IMonitorDelegator.DirectExecute());
	}

	@Test
	public void initialize_ok() throws Exception {
		operator.initialize();
	}

	@Test(expected = IllegalStateException.class)
	public void initialize_extension_factory_is_null_error() throws Exception {
		operator.setExtensionFactory(null);
		operator.initialize();
	}

	@Test(expected = IllegalStateException.class)
	public void initialize_scripting_service_is_null_error() throws Exception {
		StorageContentsOperatorImpl operator = new StorageContentsOperatorImpl(null, mock(KanbanService.class));
		operator.initialize();
	}

	@Test(expected = IllegalStateException.class)
	public void initialize_kanban_service_is_null_error() throws Exception {
		StorageContentsOperatorImpl operator = new StorageContentsOperatorImpl(mock(ScriptingService.class), null);
		operator.initialize();
	}

	@Test
	public void setContents() throws Exception {
		BoardModel board = mock(BoardModel.class);
		GraphicalViewer viewer = createMockGraphicalViewer();
		operator.setContents(viewer, board, new NullProgressMonitor());
		ArgumentCaptor<BoardEditPart> captor = ArgumentCaptor.forClass(BoardEditPart.class);

		verify(viewer).setContents(captor.capture());
		assertThat(captor.getValue().getBoardModel(), is(board));
	}

	@Test
	public void setContents_when_viewers_returns_control_is_null() throws Exception {
		BoardModel board = mock(BoardModel.class);
		GraphicalViewer viewer = mock(GraphicalViewer.class);
		operator.setContents(viewer, board, new NullProgressMonitor());
		ArgumentCaptor<BoardEditPart> captor = ArgumentCaptor.forClass(BoardEditPart.class);

		verify(viewer).setContents(captor.capture());
		assertThat(captor.getValue().getBoardModel(), is(board));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setContents_script_is_not_called() throws Exception {
		BoardModel board = mock(BoardModel.class);
		GraphicalViewer viewer = createMockGraphicalViewer();
		operator.setContents(viewer, board, new NullProgressMonitor());

		verify(scriptingService, never()).eval((ScriptTypes) any(), anyString(), anyString(),
				(Map<String, Object>) any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void setContents_script_is_called() throws Exception {
		BoardModel boardModel = mock(BoardModel.class);
		Board board = mock(Board.class);
		when(boardModel.getBoard()).thenReturn(board);
		when(boardModel.hasScript()).thenReturn(true);

		when(board.getScriptType()).thenReturn(ScriptTypes.JRuby);
		when(board.getScript()).thenReturn("p 'hello'");

		GraphicalViewer viewer = createMockGraphicalViewer();
		operator.setContents(viewer, boardModel, new NullProgressMonitor());
		verify(scriptingService).eval(eq(ScriptTypes.JRuby), anyString(), anyString(), (Map<String, Object>) any());
	}

	private GraphicalViewer createMockGraphicalViewer() {
		GraphicalViewer viewer = mock(GraphicalViewer.class);
		Control control = mock(Control.class);
		when(viewer.getControl()).thenReturn(control);
		return viewer;
	}

	private StorageContentsOperatorImpl createOperator(
			ScriptingService scriptingService) {
		KanbanService kanbanService = mock(KanbanService.class);
		StorageContentsOperatorImpl operator = new StorageContentsOperatorImpl(scriptingService, kanbanService);
		return operator;
	}

}
