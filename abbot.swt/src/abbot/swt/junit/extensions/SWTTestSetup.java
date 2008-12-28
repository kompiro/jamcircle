package abbot.swt.junit.extensions;

import junit.extensions.TestSetup;
import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestResult;
import abbot.swt.junit.extensions.UserThread.Executable;

/**
 * A {@link TestSetup} extension that ensures that {@link #setUp()} & {@link #tearDown()} run on a
 * non-UI thread.
 * 
 * @author gjohnsto
 */
public class SWTTestSetup extends TestSetup {

	public SWTTestSetup(Test test) {
		super(test);
	}

	public void run(final TestResult result) {
		// Invoke super.runProtected() on a non-UI thread.
		if (UserThread.isOnUserThread()) {
			// We're on a non-UI thread (UserThread) already so no need to start another.
			super.run(result);
		} else {
			// Causes problems if no Display has been created yet. So for now just
			// carry on as if things were ok (which they might be).
			// if (Display.getCurrent() == null)
			// throw new IllegalStateException("invalid thread");

			final ProxyTestResult proxyResult = new ProxyTestResult(result);
			Protectable protectable = new Protectable() {
				public void protect() throws Throwable {
					UserThread userThread = UserThread.syncExec(new Executable() {
						public void execute() throws Throwable {
							setUp();
							basicRun(proxyResult);
							tearDown();
						}
					});
					Throwable throwable = userThread.getException();
					if (throwable != null)
						throw throwable;
				}
			};
			proxyResult.runProtected(this, protectable);
		}
	}

	// public void run(final TestResult result) {
	// final TestResult tempResult = new TestResult() {
	// public synchronized void addError(Test test, Throwable throwable) {
	// if (throwable instanceof SWTException) {
	// SWTException exception = (SWTException) throwable;
	// if (exception.code == SWT.ERROR_FAILED_EXEC) {
	// Throwable cause = exception.getCause();
	// if (cause instanceof AssertionFailedError)
	// addFailure(test, (AssertionFailedError) cause);
	// else
	// addError(test, cause);
	// return;
	// }
	// }
	// super.addError(test, throwable);
	// }
	// };
	// UserThread userThread = UserThread.syncExec(new Executable() {
	// public void execute() throws Throwable {
	// SWTTestSetup.super.run(tempResult);
	// }
	// });
	//
	// Throwable throwable = userThread.getException();
	// if (throwable != null)
	// tempResult.addError(fTest, throwable);
	//
	// fixResult(result, tempResult);
	//
	// }

	// private void fixResult(TestResult testResult, TestResult tempResult) {
	//
	// // Copy errors.
	// for (Enumeration testErrors = tempResult.errors(); testErrors.hasMoreElements();) {
	// TestFailure testFailure = (TestFailure) testErrors.nextElement();
	// Throwable error = testFailure.thrownException();
	// testResult.addError(testFailure.failedTest(), error);
	// }
	//
	// // Copy failures.
	// for (Enumeration testFailures = tempResult.failures(); testFailures.hasMoreElements();) {
	// TestFailure testFailure = (TestFailure) testFailures.nextElement();
	// AssertionFailedError failure = (AssertionFailedError) testFailure.thrownException();
	// testResult.addFailure(testFailure.failedTest(), failure);
	// }
	//
	// }

	// public void run(final TestResult result) {
	// List exceptions = UserThread.syncExec(new Executable() {
	// public void execute() throws Throwable {
	// SWTTestSetup.super.run(result);
	// }
	// });
	// assertTrue(exceptions.isEmpty());
	// }
}
