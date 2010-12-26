package org.kompiro.jamcircle.storage.exception;

public class DBMigrationNeededException extends StorageConnectException {

	private static final long serialVersionUID = 5812833781329343855L;

	public DBMigrationNeededException() {
		super();
	}

	public DBMigrationNeededException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBMigrationNeededException(String message) {
		super(message);
	}

	public DBMigrationNeededException(Throwable cause) {
		super(cause);
	}

}
