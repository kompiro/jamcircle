package org.kompiro.jamcircle.kanban;

import org.eclipse.core.runtime.Platform;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class OSGiEnvironment implements MethodRule {

	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		if (Platform.isRunning()) {
			return base;
		}
		throw new IllegalStateException("Please launch on PDE Environment");
	}

}
