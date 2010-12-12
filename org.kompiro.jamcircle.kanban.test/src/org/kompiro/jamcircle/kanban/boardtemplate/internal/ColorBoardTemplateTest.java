package org.kompiro.jamcircle.kanban.boardtemplate.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.mockito.ArgumentCaptor;

public class ColorBoardTemplateTest {

	private static final String[] STATUSES = new String[] { "RED",
			"YELLOW",
			"GREEN",
			"LIGHT GREEN",
			"LIGHT BLUE",
			"BLUE",
			"PURPLE",
			"RED PURPLE",
	};

	@Test
	public void initialize() throws Exception {
		Board board = mock(Board.class);
		KanbanService service = mock(KanbanService.class);
		Lane lane = mock(Lane.class);
		ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
		when(service.createLane(eq(board), statusCaptor.capture(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(
				lane);

		ColorBoardTemplate template = new ColorBoardTemplate();
		template.setKanbanService(service);
		template.initialize(board);
		assertArrayEquals(STATUSES, statusCaptor.getAllValues().toArray(new String[] {}));
	}
}
