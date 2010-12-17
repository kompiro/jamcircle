package org.kompiro.jamcircle.kanban.ui.internal.editpart.policy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.Lane;

public class StickyLaneLayoutTest {

	@Test
	public void aroundNoLanes() throws Exception {
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);

		Lane lane = mock(Lane.class);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNull(actual);
	}

	@Test
	public void rideOnRightSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(10, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(109, 20, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(110, 10, 50, 50)));

	}

	@Test
	public void rideNOTOnRightSide() throws Exception {
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(10, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(111, 20, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNull(actual);
	}

	@Test
	public void rideOnLeftSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(219, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(170, 20, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(169, 10, 50, 50)));

	}

	@Test
	public void rideNOTOnLeftSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(220, 10, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(169, 20, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNull(actual);
	}

	@Test
	public void rideOnUpSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(120, 161, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(120, 160, 50, 50)));

	}

	@Test
	public void rideNOTOnUpSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(120, 150, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNull(actual);

	}

	@Test
	public void rideOnDownSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(120, 309, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(120, 310, 50, 50)));

	}

	@Test
	public void rideNOTOnDownSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(120, 311, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNull(actual);

	}

	@Test
	public void rideOnUpRightSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(180, 200, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(200, 210, 50, 50)));

	}

	@Test
	public void rideOnUpLeftSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(80, 200, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(50, 210, 50, 50)));

	}

	@Test
	public void rideOnDownRightSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(180, 280, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(200, 210, 50, 50)));

	}

	@Test
	public void rideOnDownLeftSide() throws Exception {

		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(80, 280, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
		assertNotNull(actual);
		assertThat(actual, is(new Rectangle(50, 210, 50, 50)));

	}

	@Test
	public void notRideOnIconSide() throws Exception {
		Board board = mock(Board.class);
		StickyLaneLayout layout = new StickyLaneLayout(board);
		Lane lane1 = createLaneMock(100, 210, 100, 100);
		when(lane1.isIconized()).thenReturn(true);
		when(board.getLanes()).thenReturn(new Lane[] { lane1 });

		Lane lane = createLaneMock(80, 280, 50, 50);

		Rectangle actual = layout.rideOn(lane, createRectangle(lane));
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

	private Rectangle createRectangle(Lane lane) {
		return new Rectangle(lane.getX(), lane.getY(), lane.getWidth(), lane.getHeight());
	}

}
