package org.kompiro.jamcircle.kanban.model;

import java.util.Map;

import org.kompiro.jamcircle.scripting.IScriptingEngineInitializer;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

public class KanbanScriptingEngineInitializer implements
		IScriptingEngineInitializer {

	public KanbanScriptingEngineInitializer() {
	}

	public void init(final Map<String, Object> beans) throws ScriptingException {
		beans.put("RED", ColorTypes.RED);
		beans.put("YELLOW",ColorTypes.YELLOW);
		beans.put("GREEN",ColorTypes.GREEN);
		beans.put("LIGHT_GREEN",ColorTypes.LIGHT_GREEN);
		beans.put("LIGHT_BLUE",ColorTypes.LIGHT_BLUE);
		beans.put("BLUE",ColorTypes.BLUE);
		beans.put("PURPLE",ColorTypes.PURPLE);
		beans.put("RED_PURPLE",ColorTypes.RED_PURPLE);

		beans.put("FLAG_RED", FlagTypes.RED);
		beans.put("FLAG_WHITE",FlagTypes.WHITE);
		beans.put("FLAG_GREEN",FlagTypes.GREEN);
		beans.put("FLAG_BLUE",FlagTypes.BLUE);
		beans.put("FLAG_ORANGE",FlagTypes.ORANGE);
	}

}
