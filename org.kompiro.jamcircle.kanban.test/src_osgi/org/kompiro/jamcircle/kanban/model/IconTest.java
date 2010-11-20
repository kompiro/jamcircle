package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

public class IconTest extends AbstractKanbanTest {

	@Test
	public void call_handler() throws Exception {
		Icon icon = createIconForTest("test_type");

		ExecutorHandler handler = mock(ExecutorHandler.class);
		icon.setHandler(handler);

		icon.save(false);
		verify(handler).handle((Runnable) any());
	}

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {

		Icon icon = createIconForTest("test_type");
		System.out.println(icon.toString());
		assertThat(icon.toString(), is(not("")));

	}

}
