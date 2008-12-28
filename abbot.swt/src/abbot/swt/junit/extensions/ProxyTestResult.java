package abbot.swt.junit.extensions;

import java.util.Enumeration;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;

public class ProxyTestResult extends TestResult {

	private final TestResult result;

	public ProxyTestResult(TestResult result) {
		this.result = result;
	}

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
		result.addError(test, throwable);
	}

	public synchronized void addFailure(Test test, AssertionFailedError t) {
		result.addFailure(test, t);
	}

	public synchronized void addListener(TestListener listener) {
		result.addListener(listener);
	}

	public void endTest(Test test) {
		result.endTest(test);
	}

	public synchronized int errorCount() {
		return result.errorCount();
	}

	public synchronized Enumeration errors() {
		return result.errors();
	}

	public synchronized int failureCount() {
		return result.failureCount();
	}

	public synchronized Enumeration failures() {
		return result.failures();
	}

	public synchronized void removeListener(TestListener listener) {
		result.removeListener(listener);
	}

	public synchronized int runCount() {
		return result.runCount();
	}

//	public void runProtected(Test test, Protectable p) {
//		result.runProtected(test, p);
//	}

	public synchronized boolean shouldStop() {
		return result.shouldStop();
	}

	public void startTest(Test test) {
		result.startTest(test);
	}

	public synchronized void stop() {
		result.stop();
	}

	public synchronized boolean wasSuccessful() {
		return result.wasSuccessful();
	}

}
