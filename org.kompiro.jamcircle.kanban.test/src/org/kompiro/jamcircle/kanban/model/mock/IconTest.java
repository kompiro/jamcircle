package org.kompiro.jamcircle.kanban.model.mock;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.mock.Icon;

public class IconTest {
	
	
	@Test
	public void model() throws Exception {
		Icon icon = new Icon();
		assertThat(icon,is(not(nullValue())));

		icon.setSrc("source path");
		assertThat(icon.getSrc(),is("source path"));
		
		Board board = mock(Board.class);
		icon.setBoard(board);
		
		assertThat(icon.getBoard(),sameInstance(board));

	}

}
