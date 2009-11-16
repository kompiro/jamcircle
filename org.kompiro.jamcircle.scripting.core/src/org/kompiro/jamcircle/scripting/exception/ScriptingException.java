package org.kompiro.jamcircle.scripting.exception;

public class ScriptingException extends Exception {

	private static final long serialVersionUID = 7592416859093587894L;

    public ScriptingException(String message, Throwable cause) {
        super(message, cause);
    }

	public ScriptingException(String message) {
		super(message);
	}

}
