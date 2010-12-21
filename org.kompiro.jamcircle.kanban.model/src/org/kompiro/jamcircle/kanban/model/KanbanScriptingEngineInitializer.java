package org.kompiro.jamcircle.kanban.model;

import java.util.Map;

import org.kompiro.jamcircle.scripting.IScriptingEngineInitializer;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

/**
 * This class is a extension for Script Engine.
 * @author kompiro
 *
 */
public class KanbanScriptingEngineInitializer implements
		IScriptingEngineInitializer {

	private static final String PREFIX_NAME_OF_FLAG_TYPE = "FLAG_"; //$NON-NLS-1$

	public KanbanScriptingEngineInitializer() {
	}

	/**
	 * This implementation initialize ColorTypes and FlagTypes.
	 * @see {@link ColorTypes}
	 * @see {@link FlagTypes}
	 */
	public void init(final Map<String, Object> beans) throws ScriptingException {
		putEnum(beans,ColorTypes.RED);
		putEnum(beans,ColorTypes.YELLOW);
		putEnum(beans,ColorTypes.GREEN);
		putEnum(beans,ColorTypes.LIGHT_GREEN);
		putEnum(beans,ColorTypes.LIGHT_BLUE);
		putEnum(beans,ColorTypes.BLUE);
		putEnum(beans,ColorTypes.PURPLE);
		putEnum(beans,ColorTypes.RED_PURPLE);

		putFlagEnum(beans,FlagTypes.RED);
		putFlagEnum(beans,FlagTypes.WHITE);
		putFlagEnum(beans,FlagTypes.GREEN);
		putFlagEnum(beans,FlagTypes.BLUE);
		putFlagEnum(beans,FlagTypes.ORANGE);
	}

	private void putFlagEnum(Map<String, Object> beans, FlagTypes type) {
		beans.put(PREFIX_NAME_OF_FLAG_TYPE + type.name(), type);
	}

	private void putEnum(Map<String, Object> beans,Enum<?> type) {
		beans.put(type.name(), type);
	}

}
