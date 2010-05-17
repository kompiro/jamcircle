package org.kompiro.jamcircle.kanban.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class KanbanScriptingEngineInitializerTest {

	@Test
	public void initializeColorTypes() throws Exception {
		KanbanScriptingEngineInitializer initializer = new KanbanScriptingEngineInitializer();
		Map<String, Object> beans = new HashMap<String, Object>();
		initializer.init(beans);

		assertEquals(beans.get("RED"),ColorTypes.RED);
	}
	
	@Test
	public void initializeFlagTypes() throws Exception {
		KanbanScriptingEngineInitializer initializer = new KanbanScriptingEngineInitializer();
		Map<String, Object> beans = new HashMap<String, Object>();
		initializer.init(beans);

		assertEquals(beans.get("FLAG_RED"),FlagTypes.RED);
	}
	
}
