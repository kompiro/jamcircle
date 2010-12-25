package org.kompiro.jamcircle.scripting.kanban.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.Test;

public class KanbanScriptingEngineInitializerTest {

	@Test
	public void init() throws Exception {
		KanbanScriptingEngineInitializer initializer = new KanbanScriptingEngineInitializer();
		HashMap<String, Object> beans = new HashMap<String, Object>();
		initializer.init(beans);
		assertThat(beans.size(), is(not(0)));
		assertThat(beans.get("RED"), is(notNullValue()));
		assertThat(beans.get("FLAG_RED"), is(notNullValue()));
	}

}
