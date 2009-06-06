package abbot.swt.utilities;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.eclipse.swt.widgets.Display;

/**
 * Like {@link Assert} except that it throws a {@link RuntimeException} instead of an
 * {@link AssertionFailedError} to indicate an <i>error</i> rather than a <i>failure</i>.
 * 
 * @author gjohnsto
 */
public class Check {

	public static void assertTrue(boolean value, String format, Object... args) {
		if (!value)
			error(format, args);
	}

	public static void assertTrue(boolean value) {
		if (!value)
			error("check failed");
	}

	public static void assertFalse(boolean value, String format, Object... args) {
		if (value)
			error(format, args);
	}

	public static void assertFalse(boolean value) {
		if (value)
			error("check failed");
	}

	public static void assertNotNull(Object object, String name) {
		if (object == null)
			error("%s is null", name);
	}

	public static void assertNull(Object object, String name) {
		if (object != null)
			error("%s is not null: %s", name, object);
	}

	public static void assertOnDisplayThread() {
		if (!Displays.isOnDisplayThread())
			error("not on display thread");
	}

	public static void assertNotOnDisplayThread() {
		if (Displays.isOnDisplayThread())
			error("on display thread");
	}

	public static void assertOnDisplayThread(Display display) {
		if (!Displays.isOnDisplayThread(display))
			error("not on display thread");
	}

	public static void assertNotOnDisplayThread(Display display) {
		if (Displays.isOnDisplayThread(display))
			error("on display thread");
	}

	public static void error(String format, Object... args) {
		throw new RuntimeException(String.format(format, args));
	}

	public static void error(Throwable exception) {
		throw new RuntimeException(exception);
	}

	/* Deprecated methods */

	/**
	 * @deprecated Use {@link #assertTrue(boolean, String, Object...)}.
	 */
	public static void checkTrue(boolean value, String format, Object... args) {
		assertTrue(value, format, args);
	}

	/**
	 * @deprecated Use {@link #assertTrue(boolean)}.
	 */
	public static void checkTrue(boolean value) {
		assertTrue(value);
	}

	/**
	 * @deprecated Use {@link #assertFalse(boolean, String, Object...)}.
	 */
	public static void checkFalse(boolean value, String format, Object... args) {
		assertFalse(value, format, args);
	}

	/**
	 * @deprecated Use {@link #assertFalse(boolean)}.
	 */
	public static void checkFalse(boolean value) {
		assertFalse(value);
	}

	/**
	 * @deprecated Use {@link #assertNotNull(Object, String)}.
	 */
	public static void checkNotNull(Object object, String name) {
		assertNotNull(object, name);
	}

	/**
	 * @deprecated Use {@link #assertNull(Object, String)}.
	 */
	public static void checkNull(Object object, String name) {
		assertNull(object, name);
	}

	/**
	 * @deprecated Use {@link #assertOnDisplayThread()}.
	 */
	public static void checkDisplayThread() {
		assertOnDisplayThread();
	}

	/**
	 * @deprecated Use {@link #assertNotOnDisplayThread()}.
	 */
	public static void checkNotDisplayThread() {
		assertNotOnDisplayThread();
	}

	/**
	 * @deprecated Use {@link #assertOnDisplayThread(Display)}.
	 */
	public static void checkDisplayThread(Display display) {
		assertOnDisplayThread(display);
	}

	/**
	 * @deprecated Use {@link #assertNotOnDisplayThread(Display)}.
	 */
	public static void checkNotDisplayThread(Display display) {
		assertNotOnDisplayThread(display);
	}

}
