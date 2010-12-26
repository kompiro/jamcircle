package org.kompiro.jamcircle.storage;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.h2.tools.Server;

public class H2ServerLauncher implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		new Server().run();
		return EXIT_OK;
	}

	public void stop() {
	}

}
