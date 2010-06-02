package org.kompiro.jamcircle.storage.exception;

public class FileException extends RuntimeException {

	private static final long serialVersionUID = -4575578088182878225L;

	public FileException() {
		super();
	}

	public FileException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileException(String message) {
		super(message);
	}

	public FileException(Throwable cause) {
		super(cause);
	}


}
