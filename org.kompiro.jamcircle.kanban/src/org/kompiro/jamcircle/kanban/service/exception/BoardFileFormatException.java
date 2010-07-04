package org.kompiro.jamcircle.kanban.service.exception;

public class BoardFileFormatException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BoardFileFormatException() {
		super();
	}

	public BoardFileFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public BoardFileFormatException(String message) {
		super(message);
	}

	public BoardFileFormatException(Throwable cause) {
		super(cause);
	}

}
