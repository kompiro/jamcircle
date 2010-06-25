package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.draw2d.geometry.Rectangle;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;

public class StickyLaneLayoutTest {

	@Test
	public void aroundNoLanes() throws Exception {
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);

		Lane lane = mock(Lane.class);

		Rectangle actual = layout.around(lane);
		assertNull(actual);
	}
	
	@Test
	public void rideOnRightSide() throws Exception {
		
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(10, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[]{lane1});

		Lane lane = createLaneMock(109,20,50,50);

		Rectangle actual = layout.around(lane);
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(110,20,50,50)));
		
	}

	@Test
	public void rideNOTOnRightSide() throws Exception {
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(10, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[]{lane1});

		Lane lane = createLaneMock(111,20,50,50);

		Rectangle actual = layout.around(lane);
		assertNull(actual);
	}

	@Test
	public void rideOnLeftSide() throws Exception {
		
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(219, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[]{lane1});

		Lane lane = createLaneMock(170,20,50,50);

		Rectangle actual = layout.around(lane);
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(169,20,50,50)));
		
	}
	
	@Test
	public void rideNOTOnLeftSide() throws Exception {
		
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(220, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[]{lane1});

		Lane lane = createLaneMock(169,20,50,50);

		Rectangle actual = layout.around(lane);
		assertNull(actual);		
	}

	
	private Lane createLaneMock(int x, int y, int width, int height) {
		Lane lane = mock(Lane.class);
		when(lane.getX()).thenReturn(x);
		when(lane.getY()).thenReturn(y);
		when(lane.getWidth()).thenReturn(width);
		when(lane.getHeight()).thenReturn(height);
		return lane;
	}
		
}
