package org.kompiro.jamcircle.kanban.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.service.internal.AbstractKanbanTest;

public class UserTest  extends AbstractKanbanTest{

	@Test
	public void no_exceptions_are_occured_when_call_toString() throws Exception {
		
		User user = createUserForTest("user@example.com");
		System.out.println(user.toString());
		assertThat(user.toString(),is(not("")));
		
	}
	
}
