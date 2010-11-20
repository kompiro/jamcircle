package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

public class LaneTest extends AbstractKanbanTest {

	@Test
	public void call_handler() throws Exception {
		Board board = createBoardForTest("TEST_BOARD");
		Lane lane = createLaneForTest(board, "TEST_STATUS");

		ExecutorHandler handler = mock(ExecutorHandler.class);
		lane.setHandler(handler);

		lane.save(false);
		verify(handler).handle((Runnable) any());
	}

	@Test
	public void customIcon() throws Exception {
		Board board = createBoardForTest("TEST_BOARD");
		Lane lane = createLaneForTest(board, "TEST_STATUS");
		File file = File.createTempFile("test", ".png");

		lane.setCustomIcon(file);
		assertThat(lane.hasCustomIcon(), is(true));

		File icon = lane.getCustomIcon();

		assertThat(icon, is(notNullValue()));
		assertThat(icon.exists(), is(true));
	}

	@Test
	public void deleteOldIcon() throws Exception {

		Board board = createBoardForTest("TEST_BOARD");
		Lane lane = createLaneForTest(board, "TEST_STATUS");
		File file = File.createTempFile("test", ".png");

		lane.setCustomIcon(file);
		File file2 = File.createTempFile("test", ".png");
		lane.setCustomIcon(file2);

		File icon = lane.getCustomIcon();

		assertThat(icon, is(notNullValue()));
		assertThat(icon.exists(), is(true));
		assertThat(icon.getName(), is(file2.getName()));
	}

	@Test
	public void deleteIcon() throws Exception {

		Board board = createBoardForTest("TEST_BOARD");
		Lane lane = createLaneForTest(board, "TEST_STATUS");
		File file = File.createTempFile("test", ".png");

		lane.setCustomIcon(file);
		lane.setCustomIcon(null);

		File icon = lane.getCustomIcon();

		assertThat(icon, is(nullValue()));

	}

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {

		Board board = createBoardForTest("TEST_BOARD");
		Lane lane = createLaneForTest(board, "TEST_STATUS");
		System.out.println(lane.toString());

	}

}
