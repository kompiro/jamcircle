package org.kompiro.jamcircle.storage.exception;

public class FileStorageException extends RuntimeException {

	private static final long serialVersionUID = -4575578088182878225L;

	public FileStorageException() {
		super();
	}

	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileStorageException(String message) {
		super(message);
	}

	public FileStorageException(Throwable cause) {
		super(cause);
	}


}
