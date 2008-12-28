package org.kompiro.jamcircle.storage;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.h2.tools.Server;

public class H2ServerLauncher implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
        int exitCode = new Server().run((String[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS), System.out);
        while (exitCode == 0) {
        }
        return EXIT_OK;
	}

	public void stop() {
	}

}
