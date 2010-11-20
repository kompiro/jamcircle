package org.kompiro.jamcircle.kanban.model.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LaneTest {

	@Test
	public void isMock() throws Exception {
		Lane lane = new Lane();
		assertThat(lane.isMock(), is(true));
	}

}
