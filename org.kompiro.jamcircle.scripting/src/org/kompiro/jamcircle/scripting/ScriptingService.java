package org.kompiro.jamcircle.scripting;

import java.io.PrintStream;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.kompiro.jamcircle.scripting.exception.ScriptingException;

/**
 * This service provides Scripting environment.<br>
 * Now this service supports these engines.<br>
 * <ul>
 * <li>JavaScript(Rhino)
 * <li>JRuby
 * </ul>
 */
public interface ScriptingService extends IAdaptable {

	/**
	 * evaluate script body
	 * 
	 * @param scriptTypes
	 *            Script Type
	 * @param scriptName
	 *            Script Name
	 * @param script
	 *            script body
	 * @param beans
	 *            register beans to evaluate context
	 * @return
	 * @throws ScriptingException
	 */
	Object eval(ScriptTypes scriptTypes,
			String scriptName,
			String script,
			Map<String, Object> beans) throws ScriptingException;

	/**
	 * terminate scripting engines
	 */
	void terminate();

	/**
	 * @return
	 *         registered global values
	 */
	Map<String, Object> getGlovalValues();

	/**
	 * set Scripting Engine's error stream.
	 * 
	 * @param stream
	 *            error stream
	 */
	public void setErrorStream(PrintStream stream);

	/**
	 * set Scripting Engine's output stream.
	 * 
	 * @param stream
	 *            output stream
	 */
	public void setOutputStream(PrintStream stream);

	void initialize() throws ScriptingException;
}
