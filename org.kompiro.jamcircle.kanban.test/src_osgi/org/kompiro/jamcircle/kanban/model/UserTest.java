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

public class UserTest extends AbstractKanbanTest {

	@Test
	public void call_handler() throws Exception {
		User user = createUserForTest("user@example.com");

		ExecutorHandler handler = mock(ExecutorHandler.class);
		user.setHandler(handler);

		user.save(false);
		verify(handler).handle((Runnable) any());
	}

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {

		User user = createUserForTest("user@example.com");
		System.out.println(user.toString());
		assertThat(user.toString(), is(not("")));

	}

}
