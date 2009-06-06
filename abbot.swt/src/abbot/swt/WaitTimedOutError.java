package abbot.swt;

import junit.framework.AssertionFailedError;
import abbot.swt.utilities.Wait;

/**
 * Thrown when a wait times out.
 * 
 * @see Wait
 */
public class WaitTimedOutError extends AssertionFailedError {

	private static final long serialVersionUID = 6286817949395706974L;

	public WaitTimedOutError() {
		super();
	}

	public WaitTimedOutError(String message) {
		super(message);
	}

}
