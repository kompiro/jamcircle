package org.kompiro.jamcircle.kanban.ui.internal.view;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.junit.Before;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.KanbanUIExtensionEditPartFactory;
import org.kompiro.jamcircle.kanban.ui.util.IMonitorDelegator;
import org.kompiro.jamcircle.scripting.ScriptTypes;
import org.kompiro.jamcircle.scripting.ScriptingService;


public class StorageContentsOperatorImplTest {
	
	private StorageContentsOperatorImpl operator;
	private ScriptingService scriptingService;

	@Before
	public void before() {
		scriptingService = mock(ScriptingService.class);
		operator = createOperator(scriptingService);
		KanbanUIExtensionEditPartFactory extensionFactory = mock(KanbanUIExtensionEditPartFactory.class);
		operator.setExtensionFactory(extensionFactory);
		operator.setDelegator(new IMonitorDelegator.DirectExecute());
	}
	
	@Test
	public void initialize_ok() throws Exception {
		operator.initialize();
	}
	
	@Test(expected=IllegalStateException.class)
	public void initialize_extension_factory_is_null_error() throws Exception {
		operator.setExtensionFactory(null);
		operator.initialize();
	}
	
	@Test(expected=IllegalStateException.class)
	public void initialize_scripting_service_is_null_error() throws Exception {
		StorageContentsOperatorImpl operator = new StorageContentsOperatorImpl(null,mock(KanbanService.class));
		operator.initialize();
	}

	@Test(expected=IllegalStateException.class)
	public void initialize_kanban_service_is_null_error() throws Exception {
		StorageContentsOperatorImpl operator = new StorageContentsOperatorImpl(mock(ScriptingService.class),null);
		operator.initialize();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void setContents_script_is_not_called() throws Exception {
		Board board = mock(Board.class);
		GraphicalViewer viewer = mock(GraphicalViewer.class);
		operator.setContents(viewer, board , new NullProgressMonitor());
		
		verify(scriptingService,never()).eval((ScriptTypes)any(), anyString(), anyString(), (Map<String, Object>)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void setContents_script_is_called() throws Exception {
		Board board = mock(Board.class);
		when(board.getScriptType()).thenReturn(ScriptTypes.JRuby);
		when(board.getScript()).thenReturn("p 'hello'");

		GraphicalViewer viewer = mock(GraphicalViewer.class);
		operator.setContents(viewer,board , new NullProgressMonitor());
		
		verify(scriptingService).eval(eq(ScriptTypes.JRuby), anyString(), anyString(), (Map<String, Object>)any());
	}

	private StorageContentsOperatorImpl createOperator(
			ScriptingService scriptingService) {
		KanbanService kanbanService = mock(KanbanService.class);
		StorageContentsOperatorImpl operator = new StorageContentsOperatorImpl(scriptingService , kanbanService );
		return operator;
	}

	
}
