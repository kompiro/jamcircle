package abbot.swt.utilities;

import org.eclipse.swt.widgets.Display;

import abbot.swt.WaitTimedOutError;
import abbot.swt.script.Condition;

public class Wait {

	/**
	 * The default maximum number of milliseconds to wait for a
	 * {@link Condition} to become <code>true</code>.<br>
	 * Value is 30000L (30 sec.).
	 */
	public static final long DEFAULT_TIMEOUT = 30000L;

	/**
	 * The minimum number of milliseconds allowed to wait for a
	 * {@link Condition} to become <code>true</code>.<br>
	 * Value is 0L (0 sec.).
	 */
	public static final long MIN_TIMEOUT = 0L;

	/**
	 * The maximum number of milliseconds allowed to wait for a
	 * {@link Condition} to become <code>true</code>.<br>
	 * Value is 300000L (5 min.).
	 */
	public static final long MAX_TIMEOUT = 300000L;

	/**
	 * The default number of milliseconds to delay between {@link Condition}
	 * tests.<br>
	 * Value is 100L (0.1 sec.).
	 */
	public static final long DEFAULT_INTERVAL = 100L;

	/**
	 * The minimum number of milliseconds allowed to delay between
	 * {@link Condition} tests.<br>
	 * Value is 10L (0.01 sec.).
	 */
	public static final long MIN_INTERVAL = 10L;

	/**
	 * The maximum number of milliseconds allowed to delay between
	 * {@link Condition} tests.<br>
	 * Value is 30000L (30 sec.).
	 */
	public static final long MAX_INTERVAL = 30000L;

	/**
	 * Wait for the specified Condition to become true.
	 * 
	 * @throws WaitTimedOutError
	 *             if the timeout is exceeded.
	 */
	public static void wait(Condition condition, long timeout, long interval) {
		checkThread();
		checkTimeout(timeout);
		checkInterval(interval);
		waitPrim(condition, timeout, interval);
	}

	/**
	 * Wait for a {@link Condition} to become true.
	 * 
	 * @throws WaitTimedOutError
	 *             if the default timeout is exceeded.
	 */
	public static void wait(Condition condition) {
		checkThread();
		waitPrim(condition, DEFAULT_TIMEOUT, DEFAULT_INTERVAL);
	}

	/**
	 * Wait for a {@link Condition} to become true.
	 * 
	 * @throws WaitTimedOutError
	 *             if the timeout is exceeded.
	 */
	public static void wait(Condition condition, long timeout) {
		checkThread();
		checkTimeout(timeout);
		waitPrim(condition, timeout, DEFAULT_INTERVAL);
	}

	/** Sleep the given duration of time (in milliseconds). */
	public static void sleep(long time) {
		checkThread();
		checkTime(time);
		sleepPrim(time);
	}

	private static void checkThread() {
		if (Display.findDisplay(Thread.currentThread()) != null)
			throw new RuntimeException("invalid thread");
	}

	private static void checkTimeout(long timeout) {
		if (timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT)
			throw new IllegalArgumentException("invalid timeout: " + timeout);
	}

	private static void checkInterval(long interval) {
		if (interval < MIN_INTERVAL || interval > MAX_INTERVAL)
			throw new IllegalArgumentException("invalid interval: " + interval);
	}

	private static void checkTime(long time) {
		if (time < MIN_TIMEOUT || time > MAX_TIMEOUT)
			throw new IllegalArgumentException("invalid time: " + time);
	}

	private static void waitPrim(Condition condition, long timeout,
			long interval) {
		long limit = System.currentTimeMillis() + timeout;
		while (!condition.test()) {
			if (System.currentTimeMillis() > limit)
				timeout(condition, timeout, interval);
			sleepPrim(interval);
		}
	}

	private static void timeout(Condition condition, long timeout, long interval) {
		String message = String.format("%d ms.: %s", timeout, condition);
		throw new WaitTimedOutError(message);
	}

	private static void sleepPrim(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException exception) {
			// Empty block intended.
		}
	}

}
