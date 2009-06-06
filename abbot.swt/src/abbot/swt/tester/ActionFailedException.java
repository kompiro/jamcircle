package abbot.swt.tester;

/**
 * A {@link RuntimeException} that indicates that a {@link WidgetTester} action failed to execute
 * properly.
 * 
 * @see RuntimeException
 */
public class ActionFailedException extends RuntimeException {

	private static final long serialVersionUID = -376315229365184203L;

	/** Constructs a new {@link ActionFailedException} with no message or cause. */
	public ActionFailedException() {
		super();
	}

	/** Constructs a new {@link ActionFailedException} with the specified message and no cause. */
	public ActionFailedException(String message) {
		super(message);
	}

	/** Constructs a new {@link ActionFailedException} with the specified message and cause. */
	public ActionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/** Constructs a new {@link ActionFailedException} with no message and the specified cause. */
	public ActionFailedException(Throwable cause) {
		super(cause);
	}
}
