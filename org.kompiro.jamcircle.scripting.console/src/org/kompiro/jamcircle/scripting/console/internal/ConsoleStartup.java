package org.kompiro.jamcircle.scripting.console.internal;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

public class ConsoleStartup implements IStartup {

	public void earlyStartup() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				for (ScriptingColorEnum e : ScriptingColorEnum.values()) {
					e.initialize();
				}
				ScriptingStreamInitializer.outputStream.setColor(ScriptingColorEnum.OUTPUT_STREAM_COLOR.getColor());
				ScriptingStreamInitializer.errorStream.setColor(ScriptingColorEnum.ERROR_STREAM_COLOR.getColor());
			}
		});
	}

}
