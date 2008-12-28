package abbot.swt.junit.extensions;

import java.util.Enumeration;

import junit.extensions.ActiveTestSuite;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import abbot.swt.junit.extensions.UserThread.Executable;

/**
 * Run an entire test suite on a new (non-UI) thread. Loosely patterned after
 * {@link ActiveTestSuite}.
 * 
 * @author gjohnsto
 */
public class SWTTestSuite extends TestSuite {

	private Display display;

	private Shell[] initialShells;

	public SWTTestSuite() {
		super();
	}

	public SWTTestSuite(Class theClass) {
		super(theClass);
	}

	public SWTTestSuite(String name) {
		super(name);
	}

	public SWTTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	protected synchronized final Display getDisplay() {
		if (display == null)
			display = getDefaultDisplay();
		Assert.assertNotNull(display);
		return display;
	}

	protected Display getDefaultDisplay() {
		return Display.getDefault();
	}

	protected void suiteSetUp() {
	// Default implementation does nothing.
	}

	protected void suiteTearDown() {
	// Default implementation does nothing.
	}

	/**
	 * Run super.run(TestResult) on a non-UI thread.
	 */
	public void run(TestResult testResult) {
		SWTTestCase.checkThread(getDisplay());

		// Get the initial set of Shells.
		initialShells = SWTTestCase.getShells(getDisplay());

		if (UserThread.isOnUserThread()) {

			// We're already on a non-UI thread (UserThread).
			runImpl(testResult);

		} else {

			// We're on the UI thread.

			// Create a temporary TestResult to collect exceptions when we invoke
			// runImpl(TestResult) on a UserThread.
			final TestResult tempResult = new TestResult() {
				public synchronized void addError(Test test, Throwable throwable) {
					if (throwable instanceof SWTException) {
						SWTException exception = (SWTException) throwable;
						if (exception.code == SWT.ERROR_FAILED_EXEC) {
							Throwable cause = exception.getCause();
							if (cause instanceof AssertionFailedError)
								addFailure(test, (AssertionFailedError) cause);
							else
								addError(test, cause);
							return;
						}
					}
					super.addError(test, throwable);
				}
			};

			// Start a UserThread to invoke runImple(TestResult),
			// and wait for it to finish.
			UserThread.syncExec(new Executable() {
				public void execute() throws Throwable {
					runImpl(tempResult);
				}
			});

			// Copy correct exception information to the testResult.
			fixResult(testResult, tempResult);
		}

		SWTTestCase.closeStuckMenus(getDisplay());
		SWTTestCase.closeOtherShells(getDisplay(), initialShells);
	}

	private void runImpl(TestResult testResult) {
		try {
			suiteSetUp();
			super.run(testResult);
		} finally {
			suiteTearDown();
		}
	}

	private void fixResult(TestResult testResult, TestResult tempResult) {

		// Copy errors.
		for (Enumeration testErrors = tempResult.errors(); testErrors.hasMoreElements();) {
			TestFailure testFailure = (TestFailure) testErrors.nextElement();
			Throwable error = testFailure.thrownException();
			testResult.addError(testFailure.failedTest(), error);
		}

		// Copy failures.
		for (Enumeration testFailures = tempResult.failures(); testFailures.hasMoreElements();) {
			TestFailure testFailure = (TestFailure) testFailures.nextElement();
			AssertionFailedError failure = (AssertionFailedError) testFailure.thrownException();
			testResult.addFailure(testFailure.failedTest(), failure);
		}

	}

	// private void fixResult(TestResult testResult, TestResult tempResult, Throwable exception,
	// List exceptionList) {
	//
	// // Copy errors from temp result to the one passed in.
	// // If an Exception was thrown by the user thread doing a Display.[a]synchExec(Runnable)
	// // it will have been wrapped in an SWTException with code SWT.ERROR_FAILED_EXEC.
	// // It will also have already been added to the test result as an error.
	// // Set handledErrors = new HashSet();
	// Set exceptions = new HashSet(exceptionList);
	// for (Enumeration testFailures = tempResult.errors(); testFailures.hasMoreElements();) {
	// TestFailure testFailure = (TestFailure) testFailures.nextElement();
	// Throwable throwable = testFailure.thrownException();
	//
	// Throwable cause = getSWTExceptionCause(throwable, exceptions);
	// if (cause != null)
	// throwable = cause;
	// // if (cause != null) {
	// // testResult.addError(testFailure.failedTest(), cause);
	// // handledErrors.add(testFailure);
	// // } else {
	// // testResult.addError(testFailure.failedTest(), throwable);
	// // }
	//
	// Throwable failure = getFailure(throwable);
	// if (failure != null)
	// throwable = failure;
	//
	// if (throwable instanceof AssertionFailedError)
	// testResult.addFailure(testFailure.failedTest(), (AssertionFailedError) throwable);
	// else
	// testResult.addError(testFailure.failedTest(), throwable);
	//
	// // AssertionFailedError failure = getFailure(thrownException);
	// // if (failure != null)
	// // testResult.addFailure(testFailure.failedTest(), failure);
	// // else
	// // testResult.addError(testFailure.failedTest(), thrownException);
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

	// private Throwable getSWTExceptionCause(Throwable throwable, Set causes) {
	// Set seen = new HashSet();
	// while (true) {
	// if (causes.remove(throwable))
	// return throwable;
	// throwable = throwable.getCause();
	// if (throwable == null || seen.contains(throwable))
	// return null;
	// seen.add(throwable);
	// }
	// }

	// private AssertionFailedError getFailure(Throwable throwable) {
	// Set seen = new HashSet();
	// while (true) {
	// if (throwable instanceof AssertionFailedError)
	// return (AssertionFailedError) throwable;
	// throwable = throwable.getCause();
	// if (throwable == null || seen.contains(throwable))
	// return null;
	// seen.add(throwable);
	// }
	// }

	public static void assertOnUIThread() {
		Assert.assertNotNull(Display.getCurrent());
	}

	public static void assertNotOnUIThread() {
		Assert.assertNull(Display.getCurrent());
	}

}
