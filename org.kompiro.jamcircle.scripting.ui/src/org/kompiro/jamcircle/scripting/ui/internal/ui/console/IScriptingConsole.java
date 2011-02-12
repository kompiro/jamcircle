package org.kompiro.jamcircle.scripting.ui.internal.ui.console;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.console.IConsole;

public interface IScriptingConsole extends IConsole {

	public ScriptingConsolePartitioner getPartitioner();

	public void streamClosed(ScriptingConsoleOutputStream outptStream);

	public void firePropertyChange(Object source, String property, Object oldValue, Object newValue);

	public void activate();

	public String getEncoding();

	public void streamClosed(ScriptingConsoleInputStream inputStream);

	public ISchedulingRule getSchedulingRule();

	public void partitionerFinished();

}
