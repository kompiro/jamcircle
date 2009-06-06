package abbot.swt.eclipse.utils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

public class Preferences {

	public enum Mode {
		Always, Never, Prompt
	};

	public static Mode setPerspectiveSwitchPrompt(Mode change) {
		String newMode = getModeString(change);
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		String oldMode = store.getString(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
		if (newMode.equals(oldMode))
			return change;
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, newMode);
		return getMode(newMode);
	}

	private static String getModeString(Mode mode) {
		switch (mode) {
			case Always:
				return IDEInternalPreferences.PSPM_ALWAYS;
			case Never:
				return IDEInternalPreferences.PSPM_NEVER;
			case Prompt:
				return IDEInternalPreferences.PSPM_PROMPT;
		}
		throw new IllegalArgumentException("mode is " + mode);
	}

	private static Mode getMode(String mode) {
		if (mode.equals(IDEInternalPreferences.PSPM_PROMPT))
			return Mode.Prompt;
		if (mode.equals(IDEInternalPreferences.PSPM_ALWAYS))
			return Mode.Always;
		if (mode.equals(IDEInternalPreferences.PSPM_NEVER))
			return Mode.Never;
		throw new IllegalArgumentException("mode is " + mode);
	}

}
