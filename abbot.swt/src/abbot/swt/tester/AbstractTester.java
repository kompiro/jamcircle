package abbot.swt.tester;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.swt.Log;
import abbot.swt.Robot;
import abbot.swt.WaitTimedOutError;
import abbot.swt.WidgetLocator;
import abbot.swt.script.Condition;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Wait;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.CharResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;
import abbot.util.Properties;
import abbot.util.Reflector;

/**
 * The root of the SWT <code>*Tester</code> class hierarchy. Contains primarily three types of
 * methods:
 * <ol>
 * <li><code>action*</code> methods are for generating user input events that result in
 * particular action on a {@link Widget}.</li>
 * <li>Proxy methods provide convenient access to a {@link Widget}'s API. {@link Widget}.</li>
 * <li><code>assert*</code> methods are for making assertions about a {@link Widget}'s state.</li>
 * </ol>
 * 
 * @author Gary Johnston
 * @author Kevin Dale
 */
public class AbstractTester {

	/**
	 * The default {@link AbstractTester} which will be associated with a {@link Robot} on the
	 * default {@link Display}.
	 * 
	 * @see #getDefault()
	 * @see Robot#getDefault()
	 */
	private static AbstractTester Default;

	/**
	 * A mutex for getting the default {@link AbstractTester}.
	 * 
	 * @see #getDefault()
	 */
	private static final Object DefaultLock = new Object();

	/**
	 * A factory method which gets the default {@link AbstractTester}, creating it if necessary.
	 * 
	 * @see Robot#getDefault()
	 */
	public static AbstractTester getDefault() {
		synchronized (DefaultLock) {
			if (Default == null)
				Default = new AbstractTester(Robot.getDefault());
			return Default;
		}
	}

	/*
	 * Keyboard-related constants.
	 */

	/**
	 * Keystroke to expand a tree item.
	 */
	public static final int TREE_EXPAND_ACCEL;

	/**
	 * Keystroke to collapse a tree item.
	 */
	public static final int TREE_COLLAPSE_ACCEL;

	static {
		if (SWT.getPlatform().equals("gtk")) {
			TREE_EXPAND_ACCEL = SWT.SHIFT | SWT.ARROW_RIGHT;
			TREE_COLLAPSE_ACCEL = SWT.SHIFT | SWT.ARROW_LEFT;
		} else {
			TREE_EXPAND_ACCEL = SWT.ARROW_RIGHT;
			TREE_COLLAPSE_ACCEL = SWT.ARROW_LEFT;
		}
	}

	/*
	 * End of keyboard-related constants.
	 */

	/*
	 * Button-related constants.
	 */

	/**
	 * The mouse button to use to bring up a popup menu. Usually this will be {@link SWT#BUTTON3}.
	 * Using {@link SWT#BUTTON2} seems to be Mac-specific.
	 */
	public static final int BUTTON_POPUP = SWT.BUTTON3;

	/** TODO Understand & document. */
	public static final String POPUP_MODIFIER = BUTTON_POPUP == SWT.BUTTON2 ? "BUTTON2_MASK"
			: "BUTTON3_MASK";

	/** TODO Understand & document. */
	public static final boolean POPUP_ON_PRESS = !Platform.isWindows();

	/** TODO Understand & document. */
	public static final int TERTIARY_MASK = BUTTON_POPUP == SWT.BUTTON2 ? SWT.BUTTON3 : SWT.BUTTON2;

	/** TODO Understand & document. */
	public static final String TERTIARY_MODIFIER = BUTTON_POPUP == SWT.BUTTON2 ? "BUTTON3_MASK"
			: "BUTTON2_MASK";

	/**
	 * Return whether this is the tertiary button, considering primary to be button1 and secondary
	 * to be the popup trigger button.
	 * 
	 * @deprecated Nothing seems to use this so maybe it should go away.
	 */
	public static boolean isTertiaryButton(int mods) {
		return ((mods & SWT.BUTTON_MASK) != SWT.BUTTON1) && ((mods & BUTTON_POPUP) == 0);
	}

	/*
	 * End of button-related constants.
	 */

	/*
	 * Delay-related constants.
	 */

	/**
	 * A suitable delay for most cases. Tests have been run safely at this value. The value should
	 * definitely be less than the double-click threshold. The idea is to adjust this value down to
	 * as close to 0 as possible for each platform.
	 * <p>
	 * <strong>Note:</strong> The value 0 causes about half of the tests to fail on Linux.
	 * <p>
	 * FIXME Need to find a value between 0 and 100 (100 is kinda slow). 30 works (almost) for
	 * w32/linux, but OSX 10.1.5 text input lags (50 is minimum).
	 * <p>
	 * Not sure it's worth tracking down all the robot bugs and working around them.
	 * <p>
	 * The value is set in a static initializer in order to prevent the compiler from inlining the
	 * value.
	 */
	private static final long DEFAULT_DELAY;

	static {
		DEFAULT_DELAY = Platform.isOSX() || Platform.isLinux() || Platform.isWindows() ? 0L : 50L;
	}

	/**
	 * The default number of milliseconds to automatically delay after generating an input event.
	 * <p>
	 * The value is set in a static initializer in order to prevent the compiler from inlining the
	 * value.
	 */
	private static final long DEFAULT_AUTO_DELAY;

	static {
		DEFAULT_AUTO_DELAY = Properties.getProperty(
				"abbot.robot.auto_delay",
				0L,
				60000L,
				DEFAULT_DELAY);
	}

	/**
	 * The default maximum number of milliseconds to wait for a {@link Condition} to become
	 * <code>true</code>.
	 * 
	 * @see #wait(Condition)
	 */
	public static final long DEFAULT_WAIT_TIMEOUT = Properties.getProperty(
			"abbot.robot.default_wait_timeout",
			0L,
			60000L,
			30000L);

	/**
	 * The default maximum number of milliseconds to tolerate before failing to find a
	 * {@link Widget} that should be visible.
	 * 
	 * @see WidgetTester#waitForShellShowing(String, boolean)
	 * @see WidgetTester#waitForShellShowing(String)
	 */
	protected static final long DEFAULT_WIDGET_TIMEOUT = Properties.getProperty(
			"abbot.robot.widget_timeout",
			0L,
			60000L,
			DEFAULT_WAIT_TIMEOUT);

	/*
	 * End of delay-related constants.
	 */

	/*
	 * Instance variables
	 */

	/** The lower-level robot used to generate user input events. */
	protected final Robot robot;

	/**
	 * Should we automatically wait for an idle event loop after generating a user input event.
	 */
	protected boolean isAutoWaitForIdle;

	/**
	 * The number of milliseconds ti delay after generating a user input event.
	 */
	protected long autoDelay = DEFAULT_AUTO_DELAY;

	/** If we've started a drag, this is the source. */
	protected Rectangle dragSource;

	/* End of instance variables */

	/**
	 * Constructs a new {@link AbstractTester} associated with a specified {@link Robot}).
	 */
	public AbstractTester(final Robot robot) {
		if (robot == null)
			throw new IllegalArgumentException("robot is null");
		this.robot = robot;
	}

	public Robot getRobot() {
		return robot;
	}

	public Display getDisplay() {
		return robot.getDisplay();
	}

	public long getAutoDelay() {
		return autoDelay;
	}

	public long setAutoDelay(long autoDelay) {
		long oldAutoDelay = this.autoDelay;
		this.autoDelay = autoDelay;
		return oldAutoDelay;
	}

	public boolean isAutoWaitForIdle() {
		return isAutoWaitForIdle;
	}

	public boolean setAutoWaitForIdle(boolean isAutoWaitForIdle) {
		boolean oldIsAutoWaitForIdle = this.isAutoWaitForIdle;
		this.isAutoWaitForIdle = isAutoWaitForIdle;
		return oldIsAutoWaitForIdle;
	}

	protected void afterEvent() {

		if (isAutoWaitForIdle)
			waitForIdle();

		if (autoDelay > 0)
			sleep(autoDelay);
	}

	/*
	 * Utility syncExec()-like methods.
	 */

	protected void syncExec(Runnable runnable) {
		Displays.syncExec(getDisplay(), runnable);
	}

	protected Object syncExec(Result result) {
		return Displays.syncExec(getDisplay(), result);
	}

	protected int syncExec(IntResult result) {
		return Displays.syncExec(getDisplay(), result);
	}

	protected boolean syncExec(BooleanResult result) {
		return Displays.syncExec(getDisplay(), result);
	}

	protected char syncExec(CharResult result) {
		return Displays.syncExec(getDisplay(), result);
	}

	protected String syncExec(StringResult result) {
		return Displays.syncExec(getDisplay(), result);
	}

	/*
	 * End of utility syncExec()-like methods.
	 */

	/*
	 * Utility wait methods.
	 */

	/**
	 * Wait for a {@link Condition} to become true.
	 * 
	 * @throws WaitTimedOutError
	 *             if the default timeout is exceeded.
	 */
	public void wait(Condition condition) {
		Wait.wait(condition);
	}

	/**
	 * Wait for a {@link Condition} to become true.
	 * 
	 * @throws WaitTimedOutError
	 *             if the timeout is exceeded.
	 */
	public void wait(Condition condition, long timeout) {
		Wait.wait(condition, timeout);
	}

	/**
	 * Wait for the specified Condition to become true.
	 * 
	 * @throws WaitTimedOutError
	 *             if the timeout is exceeded.
	 */
	public void wait(Condition condition, long timeout, long interval) {
		Wait.wait(condition, timeout, interval);
	}

	/**
	 * Sleep the given duration of time (in milliseconds).
	 */
	public void sleep(long time) {
		Wait.sleep(time);
	}

	/** Delay the given duration of time (in milliseconds). */
	protected void delay(long time) {
		if (!isOnDisplayThread())
			throw new RuntimeException("invalid thread");
		checkTime(time);

		// Have done[0] set to true when the time has elapsed.
		final boolean[] done = new boolean[1];
		final Display display = getDisplay();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				done[0] = true;
				display.wake();
			}
		}, time);

		while (!done[0]) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		timer.cancel();
	}

	/*
	 * End: Utility wait methods.
	 */

	/*
	 * Begin: Mouse movement support.
	 */

	/**
	 * Generates a mouse move input event to move the mouse cursor to a specified display location.
	 * <p>
	 * <b>Note:</b> In robot mode, if you intend to subsequently click at the target location then
	 * you may need to invoke this method twice, including a little jitter in the first invocation.
	 * There are some conditions where a single mouse move will not actually generate the necessary
	 * enter event on a component (typically a dialog with an OK button) before a mousePress.
	 * 
	 * @see #mouseMove(Rectangle, int, int)
	 */
	public void mouseMove(final int x, final int y) {
		robot.mouseMove(x, y);
		afterEvent();
	}

	public void mouseMove(Rectangle bounds, int x, int y) {
		checkBounds(bounds);
		checkLocation(bounds, x, y);

		// Center x & y, if requested.
		if (x == -1 && y == -1) {
			x = bounds.width / 2;
			y = bounds.height / 2;
		}

		// Jitter first, if necessary.
		if (hasRobotMotionBug()) {
			Point p = getJitterPoint(bounds, x, y);
			if (p == null) {
				Log.warn(String.format("could not jitter(%s,%d,%d)\n", bounds, x, y));
			} else {
				mouseMove(bounds.x + p.x, bounds.y + p.y);
			}
		}

		// Move
		mouseMove(bounds.x + x, bounds.y + y);
	}

	private Point getJitterPoint(Rectangle bounds, int x, int y) {
		if (x > 0)
			return new Point(x - 1, y);
		if (y > 0)
			return new Point(x, y - 1);
		if (x < bounds.width - 1)
			return new Point(x + 1, y);
		if (y < bounds.height - 1)
			return new Point(x, y + 1);
		return null;
	}

	public void mouseMove(Widget widget, int x, int y) {
		checkWidget(widget);
		mouseMove(getBounds(widget), x, y);
	}

	public void mouseMove(final Widget widget) {
		mouseMove(widget, -1, -1);
	}

	/*
	 * Begin: Mouse click support.
	 */

	/**
	 * Generate a single-click user input event.
	 * <p>
	 * <b>Note:<b> This method automatically avoids generating a double-click event due to a recent
	 * prior MouseDown event. Therefore, you cannot use two calls in a row to this method for
	 * generating a double-click. Use {@link #doubleClick(int)} instead.
	 */
	public void click(int mask) {
		robot.mouseClick(mask);
		afterEvent();
	}

	public void click(int x, int y, int mask) {
		robot.mouseClick(x, y, mask);
		afterEvent();
	}

	public void click(Rectangle bounds, int x, int y, int mask, int clicks) {

		if (clicks == 1) {
			click(bounds, x, y, mask);
			return;
		}

		if (clicks == 2) {
			doubleClick(bounds, x, y, mask);
			return;
		}

		throw new IllegalArgumentException("invalid clicks: " + clicks);
	}

	public void click(Rectangle bounds, int x, int y, int mask) {
		click(bounds.x + x, bounds.y + y, mask);
	}

	public void click(Rectangle bounds, int mask) {
		click(bounds, bounds.width / 2, bounds.height / 2, mask);
	}

	public void click() {
		click(SWT.BUTTON1);
	}

	public void click(int x, int y) {
		click(x, y, SWT.BUTTON1);
	}

	public void click(Rectangle bounds, int x, int y) {
		click(bounds, x, y, SWT.BUTTON1);
	}

	public void click(Rectangle bounds) {
		click(bounds, SWT.BUTTON1);
	}

	/* Widget clicking. */

	public void click(Widget widget) {
		click(widget, SWT.BUTTON1);
	}

	public void click(Widget widget, int mask) {
		Point p = getGlobalClickPoint(widget);
		click(p.x, p.y, mask);
	}

	/**
	 * Returns the default point within (and relative to) a {@link Widget} at which it should be
	 * clicked.<br>
	 * Subclasses may redefine as needed.
	 * 
	 * @param widget
	 *            a {@link Widget}
	 * @return a {@link Point} relative to the {@link Widget}
	 */
	// protected Point getClickPoint(Widget widget) {
	// Rectangle bounds = getBounds(widget);
	// return new Point(bounds.width / 2, bounds.height / 2);
	// }
	/**
	 * @param widget
	 *            a {@link Widget}
	 * @return a display-relative {@link Rectangle} within which a mouse click will be recognized
	 */
	protected Rectangle getClickBounds(Widget widget) {
		return getBounds(widget);
	}

	protected Point getGlobalClickPoint(Widget widget) {

		// If the center of its click-bounds is a valid location, return it.
		Rectangle bounds = getClickBounds(widget);
		Point p = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
		if (robot.isValidLocation(p))
			return p;

		/*
		 * The widget's click-bounds is at least partially off-screen. Unless it is completely
		 * off-screen it will have at least one non-empty rectangle that intersects one of the
		 * robot's display's monitors. Return the center of the largest one.
		 */
		Rectangle[] intersections = robot.getIntersections(bounds);
		int largestArea = 0;
		Rectangle largestIntersection = null;
		for (int i = 0; i < intersections.length; i++) {
			Rectangle rectangle = intersections[i];
			int area = rectangle.width * rectangle.height;
			if (area > largestArea) {
				largestIntersection = rectangle;
				largestArea = area;
			}
		}
		if (largestIntersection != null)
			return new Point(largestIntersection.x + largestIntersection.width / 2,
					largestIntersection.y + largestIntersection.height / 2);

		// The widget's click-bounds are completely off-screen.
		return null;
	}

	public void click(Widget widget, int x, int y) {
		click(widget, x, y, SWT.BUTTON1);
	}

	public void click(Widget widget, int x, int y, int mask) {

		Rectangle bounds = getBounds(widget, false);
		int cx = bounds.x + x;
		int cy = bounds.y + y;
		mouseMove(cx, cy);

		/*
		 * Sanity check commented out because it doesn't work on Linux because getCursorControl()
		 * returns null for some reason.
		 */
		// Control control = (Control) syncExec(new Result<Control>() {
		// public Control result() {
		// return getDisplay().getCursorControl();
		// }
		// });
		// if (widget instanceof Control) {
		// if (widget != control)
		// throw new IllegalStateException("chaos!");
		// } else if (widget instanceof TreeItem) {
		// Tree tree = TreeItemTester.getTreeItemTester().getParent(
		// (TreeItem) widget);
		// if (tree != control)
		// throw new IllegalStateException("thrush!");
		// }
		click(mask);
	}

	public void click(Widget widget, int x, int y, int mask, int clicks) {
		click(getBounds(widget), x, y, mask, clicks);
	}

	/*
	 * End: Mouse click support.
	 */

	/*
	 * Mouse double-click support.
	 */

	public void doubleClick(int mask) {
		robot.mouseDoubleClick(mask);
		afterEvent();
	}

	public void doubleClick(int x, int y, int mask) {
		mouseMove(x, y);
		doubleClick(mask);
	}

	public void doubleClick(Rectangle bounds, int x, int y, int mask) {
		doubleClick(bounds.x + x, bounds.y + y, mask);
	}

	public void doubleClick(Rectangle bounds, int mask) {
		doubleClick(bounds, bounds.width / 2, bounds.height / 2, mask);
	}

	public void doubleClick() {
		doubleClick(SWT.BUTTON1);
	}

	public void doubleClick(int x, int y) {
		doubleClick(x, y, SWT.BUTTON1);
	}

	public void doubleClick(Rectangle bounds, int x, int y) {
		doubleClick(bounds.x + x, bounds.y + y, SWT.BUTTON1);
	}

	public void doubleClick(Rectangle bounds) {
		doubleClick(bounds, SWT.BUTTON1);
	}

	/* Widget double-clicking. */

	public void doubleClick(Widget widget) {
		doubleClick(widget, SWT.BUTTON1);
	}

	public void doubleClick(Widget widget, int mask) {
		Point p = getGlobalClickPoint(widget);
		doubleClick(p.x, p.y, mask);
	}

	public void doubleClick(Widget widget, int x, int y) {
		doubleClick(widget, x, y, SWT.BUTTON1);
	}

	public void doubleClick(Widget widget, int x, int y, int mask) {
		doubleClick(getBounds(widget), x, y, mask);
	}

	/*
	 * Keystroke support.
	 */

	/**
	 * Types (press and release) all of the keys contained in an accelerator.
	 * <p>
	 * <b>Note:</b> Uppercase characters will be typed as uppercase, which requires that the shift
	 * key be depressed.
	 * 
	 * @see Robot
	 */
	public void key(int keymask) {
		robot.key(keymask);
	}

	/**
	 * Types (press and release) a character key.
	 */
	public void key(char c) {
		robot.key((int) c);
	}

	/**
	 * Types all of the characters in a {@link CharSequence} (e.g., a {@link String}).
	 */
	public void key(CharSequence charSequence) {
		for (int i = 0; i < charSequence.length(); i++) {
			key(charSequence.charAt(i));
		}
	}

	/*
	 * Argument checking.
	 */

	protected void checkLocation(Rectangle bounds, int x, int y) {
		if (x == -1 && y == -1)
			return;
		if (x < 0 || y < 0 || x >= bounds.width - 1 || y >= bounds.height - 1)
			throw new IllegalArgumentException(String.format(
					"bad location: (%d,%d), bounds: %s\n",
					x,
					y,
					bounds));
	}

	protected void checkBounds(Rectangle bounds) {
		if (bounds.x < 0 || bounds.y < 0)
			throw new IllegalArgumentException("negative bounds: " + bounds);
	}

	protected void checkTime(long time) {
		if (time < 0L)
			throw new IllegalArgumentException("time is negative");
	}

	/*
	 * End: Argument checking utilities.
	 */

	public void waitForIdle() {
		robot.waitForIdle();
	}

	/**
	 * @deprecated Use {@link ShellTester#forceActive(Shell)}.
	 */
	public void activate(final Shell shell) {
		ShellTester.getShellTester().forceActive(shell);
	}

	/**
	 * @deprecated Use {@link #findFocusOwner()} instead
	 */
	public Widget findFocusOwner(final Display display) {
		checkDisplay(display);
		return findFocusOwner();
	}

	public Control findFocusOwner() {
		return (Control) syncExec(new Result<Control>() {
			public Control result() {
				return getDisplay().getFocusControl();
			}
		});
	}

	/** Move keyboard focus to the given component. */
	public void focus(final Control control) {
		checkWidget(control);
		syncExec(new Runnable() {
			public void run() {
				control.forceFocus();
			}
		});
		// mouseMove(control);
		// AbstractTester.syncExec(display,this, new Runnable(){
		// public void run(){
		// controlT = display.getFocusControl();
		// shellT = c.getShell();
		// if(controlT!=c){
		// Log.debug("ROBOT: Focus change");
		// activate(shellT);
		// c.forceFocus();
		// mouseMove(c);
		// }
		// }
		// });
		// if(controlT!=c){
		// Log.debug("ROBOT: Focus change");
		// activate(shellT);
		// AbstractTester.syncExec(display,this, new Runnable(){
		// public void run(){
		// c.forceFocus();
		// }
		// });
		// mouseMove(c);
		// }
	}

	// /** Sleep for a little bit, measured in UI time. */
	// public void pollingDelay() {
	// delay(DEFAULT_POLLING_DELAY);
	// }

	//	
	// -------------------------
	// TestSet1----------------------------------------
	// --------------------------------------------------------------------------
	/** Sample the color at the given point on the screen. */
	public Color sample(int x, int y) {
		return robot.capture(x, y);
	}

	/** Capture the contents of the given rectangle. */
	/*
	 * NOTE: Text components (and maybe others with a custom cursor) will capture the cursor. May
	 * want to move the cursor out of the component bounds, although this might cause issues where
	 * the component is responding visually to mouse movement. Is this an OSX bug?
	 */
	public Image capture(Rectangle bounds) {
		return robot.capture(bounds);
	}

	/**
	 * Capture the contents of the given Widget, sans any border or insets. [FROM abbot.tester.awt's
	 * implementation]: This should only be used on components that do not use a LAF UI, or the
	 * results will not be consistent across platforms.
	 */
	public Image capture(final Widget widget) {
		return capture(widget, false);
	}

	/**
	 * Capture the contents of the given Widget, optionally including the border and/or insets.
	 * [FROM abbot.tester.awt's implementation]: This should only be used on components that do not
	 * use a LAF UI, or the results will not be consistent across platforms.
	 */
	public Image capture(final Widget widget, final boolean ignoreBorder) {
		Rectangle bounds = getBounds(widget, ignoreBorder);
		return capture(bounds);
	}

	// -------------------------------------------------------------

	// /**
	// * Run the given action on the event dispatch thread.
	// */
	// public static void invokeAction(Display display, Runnable action) {
	// display.asyncExec(action);
	// }

	// /** Run the given action on the event dispatch thread, but don't return
	// until it's been run.
	// */
	// public static void invokeAndWait(Display display, Runnable action) {
	// display.syncExec(action);
	// }

	// private static final Runnable EMPTY_RUNNABLE =
	// new Runnable() { public void run() { } };

	// Bug workaround support
	// Bug workaround support
	// private void jitter(int x, int y) {
	// mouseMove((x > 0 ? x - 1 : x + 1), y);
	// }

	public void dragDrop(Widget source, Widget target, int buttons) {
		Point tp = getGlobalClickPoint(target);
		dragDrop(source, tp.x, tp.y, buttons);
	}

	public void dragDrop(Widget source, int tx, int ty, int buttons) {
		Point cp = getGlobalClickPoint(source);
		dragDrop(cp.x, cp.y, tx, ty, buttons);
	}

	public void dragDrop(int sx, int sy, int tx, int ty, int buttons) {
		robot.mouseDragDrop(sx, sy, tx, ty, buttons);
	}

	// /**
	// * Move the mouse appropriately to get from the source to the destination.
	// Enter/exit events
	// * will be generated where appropriate.
	// */
	// public void dragOver(Widget target, int x, int y) {
	// dragOver(getBounds(target), x, y);
	// }
	//
	// public void dragOver(Rectangle target, int x, int y) {
	// mouseMove(target, (x > 1) ? x - 1 : x + 1, y);
	// mouseMove(target, x, y);
	// }
	//
	// public void dragOver(int x, int y) {
	// mouseMove(x > 0 ? x - 1 : x + 1, y);
	// mouseMove(x, y);
	// }
	//
	// /** Begin a drag operation. */
	// public void drag(Widget source, int x, int y, int modifiers) {
	// drag(getBounds(source), x, y, modifiers);
	// }
	//
	// /** Begin a drag operation. */
	// public void drag(Rectangle source, int x, int y, int modifiers) {
	// // FIXME make sure it's sufficient to be recognized as a drag
	// // by the default drag gesture recognizer
	// mouseMove(source, x, y);
	// getRobot().mouseDrag(modifiers);
	// // mousePress(modifiers);
	// // mouseMove(source, x > 0 ? x - 1 : x + 1, y);
	// dragSource = source;
	// // dragLocation = new org.eclipse.swt.graphics.Point(sx, sy);
	// // inDragSource = true;
	// }
	//
	// /**
	// * End a drag operation, releasing the mouse button over the given target
	// location.
	// */
	// public void drop(Widget target, int x, int y, int modifiers) {
	// drop(getBounds(target), x, y, modifiers);
	// }
	//
	// public void drop(Rectangle target, int x, int y, int modifiers) {
	// // All motion events are relative to the drag source
	// if (dragSource == null)
	// throw new ActionFailedException("no drag source");
	// mouseMove(target, x, y);
	// getRobot().mouseDrop(modifiers);
	// // inDropTarget = dragSource == target;
	// // dragOver(target, x, y);
	// // mouseRelease(modifiers);
	// dragSource = null;
	// // dragLocation = null;
	// // inDragSource = inDropTarget = false;
	// }
	//
	// public void drop(int x, int y, int modifiers) {
	// if (dragSource == null)
	// throw new ActionFailedException("no drag source");
	// mouseMove(x, y);
	// getRobot().mouseDrop(modifiers);
	// // dragOver(x, y);
	// // mouseRelease(modifiers);
	// dragSource = null;
	// }

	protected Rectangle getBounds(final Widget widget, final boolean ignoreBorder) {
		checkWidget(widget);
		return (Rectangle) syncExec(new Result<Rectangle>() {
			public Rectangle result() {
				return WidgetLocator.getBounds(widget, ignoreBorder);
			}
		});
	}

	protected Rectangle getBounds(final Widget widget) {
		return getBounds(widget, true);
	}

	protected Point getDisplayLocation(final Widget widget) {
		checkWidget(widget);
		return (Point) syncExec(new Result<Point>() {
			public Point result() {
				return WidgetLocator.getLocation(widget);
			}
		});
	}

	/**
	 * @deprecated Cheater!
	 */
	public void selectPopupMenuItem(final MenuItem item, final int x, final int y) {
		checkWidget(item);

		MenuItemTester menuItemTester = MenuItemTester.getMenuItemTester();
		ItemPath path = menuItemTester.getPath(item);
		final Menu menu = menuItemTester.getRootMenu(item);

		MenuTester menuTester = MenuTester.getMenuTester();
		if (menuTester.isPopUp(menu)) {
			if (!menuTester.isVisible(menu)) {
				syncExec(new Runnable() {
					public void run() {
						// FIXME Cheater!
						menu.setVisible(true);
					}
				});
				menuTester.waitVisible(menu);
			}
		}

		menuTester.clickItem(menu, path);

	}

	/**
	 * Find and select a {@link MenuItem} based on its "name" property.
	 * 
	 * @deprecated Use a {@link MenuTester}.
	 * @param parent
	 *            a Composite container that contains the MenuItem
	 * @param name
	 *            the text of the MenuItem (and its name)
	 */
	public void selectMenuItemByText(Menu parent, String name) {
		checkWidget(parent);
		MenuTester menuTester = MenuTester.getMenuTester();
		MenuItem item = menuTester.findItem(parent, name);
		if (item == null)
			throw new ActionItemNotFoundException(name);
		WidgetTester.getWidgetTester().actionSelectMenuItem(item);
	}

	/**
	 * Select a {@link MenuItem}.
	 * 
	 * @deprecated Use a {@link MenuTester} or a {@link MenuItemTester}.
	 * @param item
	 *            The {@link MenuItem} to be selected
	 */
	public void selectMenuItem(final MenuItem item) {
		checkWidget(item);

		MenuTester menuTester = MenuTester.getMenuTester();
		MenuItemTester menuItemTester = MenuItemTester.getMenuItemTester();
		Menu menu = menuItemTester.getRootMenu(item);
		ItemPath path = menuItemTester.getPath(item);

		if (menuTester.isPopUp(menu)) {
			Widget widget = menuTester.getParent(menu);
			WidgetTester.getWidgetTester().actionClickMenuItem(widget, path);
		} else {
			menuTester.actionClickItem(menu, path);
		}
	}

	/**
	 * Identify the coordinates of the iconify button where we can, returning null if we can't.
	 */
	public Point getIconifyLocation(final Shell shell) {
		checkWidget(shell);
		return (Point) syncExec(new Result<Point>() {
			public Point result() {
				int style = shell.getStyle();
				if ((style & SWT.MIN) == SWT.MIN) {
					if (Platform.isWindows()) {
						int xOffset = 50 + shell.getBorderWidth();
						int yOffset = 12 + shell.getBorderWidth();
						Rectangle bounds = WidgetLocator.getBounds(shell);
						return new Point(bounds.width - xOffset, yOffset);
					}
				}
				return null;
			}
		});
	}

	/**
	 * Identify the coordinates of the maximize button where possible, returning null if not.
	 */
	public synchronized Point getMaximizeLocation(final Shell shell) {
		checkWidget(shell);
		Point loc = getIconifyLocation(shell);
		if (loc != null) {
			int style = syncExec(new IntResult() {
				public int result() {
					return shell.getStyle();
				}
			});
			if ((style & SWT.MAX) == SWT.MAX) {
				if (Platform.isWindows()) {
					return new Point(loc.x + 17, loc.y);
				}
			}
		}
		return null;
	}

	/**
	 * Iconify the given Shell. Don't support iconification of Dialogs at this point (although maybe
	 * should).
	 */
	public void iconify(final Shell shell) {
		checkWidget(shell);
		Point loc = getIconifyLocation(shell);
		if (loc != null)
			mouseMove(shell, loc.x, loc.y);
		syncExec(new Runnable() {
			public void run() {
				shell.setMinimized(true);
			}
		});
	}

	public void deiconify(Shell shell) {
		normalize(shell);
	}

	public void normalize(final Shell shell) {
		checkWidget(shell);
		syncExec(new Runnable() {
			public void run() {
				shell.setMinimized(false);
				shell.setMaximized(false);
			}
		});
	}

	/** Make the window full size */
	public void maximize(final Shell shell) {
		checkWidget(shell);
		Point loc = getMaximizeLocation(shell);
		if (loc != null)
			mouseMove(shell, loc.x, loc.y);
		syncExec(new Runnable() {
			public void run() {
				shell.setMaximized(true);

				// if maximizing failed and we can resize, resize to fit the
				// screen
				if (!shell.getMaximized() && (shell.getStyle() & SWT.RESIZE) == SWT.RESIZE) {
					Rectangle screen = shell.getDisplay().getBounds();
					shell.setLocation(screen.x, screen.y);
					shell.setSize(screen.width, screen.height);
				}

			}
		});
	}

	public static Class getCanonicalClass(Class refClass) {
		// Don't use classnames from anonymous inner classes...
		// Don't use classnames from platform LAF classes...
		while (refClass.getName().indexOf("$") != -1
				|| refClass.getName().startsWith("javax.swing.plaf")
				|| refClass.getName().startsWith("com.apple.mrj"))
			refClass = refClass.getSuperclass();
		return refClass;
	}

	/**
	 * TODO add toString methods that use the widget name property to provide a more concise
	 * representation of the Widget.
	 */

	/** Return the numeric event ID corresponding to the given string. */
	public static int getEventID(Class cls, String id) {
		return Reflector.getFieldValue(cls, id);
	}

	/**
	 * TODO MAYBE add the following methods that are useful to testers: - getModifiers(String) OK -
	 * getModifiers(int,boolean,boolean) OK - getKeyModifiers(int) OK - getMouseModifiers(int) OK -
	 * getModifiers(Event) X - getKeyCode(int) X - getKeyCode(String) X
	 */

	/**
	 * Convert the string representation into the actual modifier mask. NOTE: this ignores any
	 * character stored as a unicode char
	 */
	/** TODO Fix this so that it will parse out chars as well */
	public static int getModifiers(String mods) {
		int value = 0;
		if (mods != null && !mods.equals("")) {
			StringTokenizer st = new StringTokenizer(mods, "| ");
			while (st.hasMoreTokens()) {
				String flag = st.nextToken();
				if (POPUP_MODIFIER.equals(flag))
					value |= BUTTON_POPUP;
				else if (TERTIARY_MODIFIER.equals(flag))
					value |= TERTIARY_MASK;
				else if (!flag.equals("0") && flag.indexOf('\'') == -1)
					value |= Reflector.getFieldValue(SWT.class, flag);
			}
		}
		return value;
	}

	/**
	 * Provides a String representation of the mouse modifiers in the given accelerator.
	 */
	public String getAcceleratorMouseString(int accelerator) {
		return getAcceleratorString(accelerator, false, true);
	}

	/**
	 * Provides a String representation of a given accelerator.
	 */
	public String getAcceleratorString(int accelerator, boolean key, boolean mouse) {
		String res = "{ ";
		int count = 0;

		if (mouse) {
			if ((accelerator & SWT.BUTTON1) == SWT.BUTTON1) {
				if (count != 0)
					res += "| ";
				res += "SWT.BUTTON1 ";
				count++;
			}
			if ((accelerator & SWT.BUTTON2) == SWT.BUTTON2) {
				if (count != 0)
					res += "| ";
				res += "SWT.BUTTON2 ";
				count++;
			}
			if ((accelerator & SWT.BUTTON3) == SWT.BUTTON3) {
				if (count != 0)
					res += "| ";
				res += "SWT.BUTTON3 ";
				count++;
			}
		}

		if (key) {
			// first, check modifier keys
			if ((accelerator & SWT.ALT) == SWT.ALT) {
				if (count != 0)
					res += "| ";
				res += "SWT.ALT ";
				count++;
			}
			if ((accelerator & SWT.SHIFT) == SWT.SHIFT) {
				if (count != 0)
					res += "| ";
				res += "SWT.SHIFT ";
				count++;
			}
			if ((accelerator & SWT.CTRL) == SWT.CTRL) {
				if (count != 0)
					res += "| ";
				res += "SWT.CTRL ";
				count++;
			}
			if ((accelerator & SWT.COMMAND) == SWT.COMMAND) {
				if (count != 0)
					res += "| ";
				res += "SWT.COMMAND ";
				count++;
			}

			// now, look at the keystroke (if any)
			int keyCode = accelerator & SWT.KEY_MASK;
			if ((SWT.KEYCODE_BIT & keyCode) != 0 && keyCode != 0) { // accelerator
				// contains
				// a
				// keycode
				switch (keyCode) {
					case SWT.ARROW_UP:
						res += (count != 0) ? "| " : "";
						res += "SWT.ARROW_UP";
						count++;
						break;
					case SWT.ARROW_DOWN:
						res += (count != 0) ? "| " : "";
						res += "SWT.ARROW_DOWN";
						count++;
						break;
					case SWT.ARROW_LEFT:
						res += (count != 0) ? "| " : "";
						res += "SWT.ARROW_LEFT";
						count++;
						break;
					case SWT.ARROW_RIGHT:
						res += (count != 0) ? "| " : "";
						res += "SWT.ARROW_RIGHT";
						count++;
						break;
					case SWT.PAGE_UP:
						res += (count != 0) ? "| " : "";
						res += "SWT.PAGE_UP";
						count++;
						break;
					case SWT.PAGE_DOWN:
						res += (count != 0) ? "| " : "";
						res += "SWT.PAGE_DOWN";
						count++;
						break;
					case SWT.HOME:
						res += (count != 0) ? "| " : "";
						res += "SWT.HOME";
						count++;
						break;
					case SWT.END:
						res += (count != 0) ? "| " : "";
						res += "SWT.END";
						count++;
						break;
					case SWT.INSERT:
						res += (count != 0) ? "| " : "";
						res += "SWT.INSERT";
						count++;
						break;

					case SWT.F1:
						res += (count != 0) ? "| " : "";
						res += "SWT.F1";
						count++;
						break;
					case SWT.F2:
						res += (count != 0) ? "| " : "";
						res += "SWT.F2";
						count++;
						break;
					case SWT.F3:
						res += (count != 0) ? "| " : "";
						res += "SWT.F3";
						count++;
						break;
					case SWT.F4:
						res += (count != 0) ? "| " : "";
						res += "SWT.F4";
						count++;
						break;
					case SWT.F5:
						res += (count != 0) ? "| " : "";
						res += "SWT.F5";
						count++;
						break;
					case SWT.F6:
						res += (count != 0) ? "| " : "";
						res += "SWT.F6";
						count++;
						break;
					case SWT.F7:
						res += (count != 0) ? "| " : "";
						res += "SWT.F7";
						count++;
						break;
					case SWT.F8:
						res += (count != 0) ? "| " : "";
						res += "SWT.F8";
						count++;
						break;
					case SWT.F9:
						res += (count != 0) ? "| " : "";
						res += "SWT.F9";
						count++;
						break;
					case SWT.F10:
						res += (count != 0) ? "| " : "";
						res += "SWT.F10";
						count++;
						break;
					case SWT.F11:
						res += (count != 0) ? "| " : "";
						res += "SWT.F11";
						count++;
						break;
					case SWT.F12:
						res += (count != 0) ? "| " : "";
						res += "SWT.F12";
						count++;
						break;
					default:
						break;
				}
			} else if (keyCode != 0) {// accelerator contains a unicode
				// character
				if (count != 0)
					res += "| ";
				res += "'(char)keyCode'";
				count++;
			}
		}

		res += "}";
		return res;
	}

	/**
	 * returns a String representation of the key modifiers in the given accelerator
	 */
	public String getAcceleratorKeyString(int accelerator) {
		return getAcceleratorString(accelerator, true, false);
	}

	/** Strip the package from the class name. */
	public static String simpleClassName(Class cls) {
		String name = cls.getName();
		int dot = name.lastIndexOf(".");
		return name.substring(dot + 1, name.length());
	}

	// private static java.util.ArrayList bugList = null;
	// private static boolean gotBug1Event = false;
	/**
	 * Check for all known robot-related bugs that will affect Abbot operation. Returns a String for
	 * each bug detected on the current system.
	 */
	/*
	 * public static String[] bugCheck(final Window window) { if (bugList == null) { bugList = new
	 * java.util.ArrayList(); if (Platform.isWindows() && Platform.getJavaVersionNumber() <
	 * Platform.JAVA_1_4) { Log.debug("Checking for w32 bugs"); final int x = window.getWidth() / 2;
	 * final int y = window.getHeight() / 2; final int mask = InputEvent.BUTTON2_MASK; MouseAdapter
	 * ma = new MouseAdapter() { public void mouseClicked(MouseEvent ev) { Log.debug("Got " +
	 * AbstractTester.toString(ev)); gotBug1Event = true; // w32 acceleration bug if (ev.getSource() !=
	 * window || ev.getX() != x || ev.getY() != y) { bugList.add(Strings.get("Bug1")); } // w32
	 * mouse button mapping bug if ((ev.getModifiers() & mask) != mask) {
	 * bugList.add(Strings.get("Bug2")); } } }; window.addMouseListener(ma); AbstractTester robot =
	 * new AbstractTester(); robot.click(window, x, y, mask); robot.waitForIdle();
	 * window.removeMouseListener(ma); window.toFront(); // Bogus acceleration may mean the event
	 * goes entirely // elsewhere if (!gotBug1Event) { bugList.add(0, Strings.get("Bug1")); } } }
	 * return (String[])bugList.toArray(new String[bugList.size()]); }
	 */

	/** Place the pointer in the center of the display */
	public void resetPointer() {
		Rectangle screen = robot.getDisplay().getBounds();
		mouseMove(screen.width / 2, screen.height / 2);
		mouseMove(screen.width / 2 - 1, screen.height / 2 - 1);
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the specified {@link Display} is null or is not
	 * this {@link AbstractTester}'s.
	 * 
	 * @param display
	 *            the {@link Display}
	 */
	protected void checkDisplay(Display display) {
		if (display == null)
			throw new IllegalArgumentException("display is null");
		if (display != getDisplay())
			throw new IllegalArgumentException("display is invalid");
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the specified {@link Widget} is null or its
	 * {@link Display} is not this {@link AbstractTester}'s.
	 * 
	 * @param widget
	 *            the {@link Widget}
	 */
	protected void checkWidget(Widget widget) {
		if (widget == null)
			throw new IllegalArgumentException("widget is null");
		if (!widget.isDisposed())
			checkDisplay(widget.getDisplay());
	}

	/**
	 * Throws an {@link IllegalArgumentException} if any of the specified {@link Widget}s are null
	 * or their {@link Display} is not this {@link AbstractTester}'s.
	 * 
	 * @param widgets
	 *            the {@link Widget}s
	 */
	protected void checkWidgets(Collection<Widget> widgets) {
		for (Widget widget : widgets) {
			checkWidget(widget);
		}
	}

	/**
	 * @throws IllegalStateException
	 *             if called on this {@link AbstractTester}'s {@link Display} {@link Thread}.
	 */
	protected void checkThread() {
		if (isOnDisplayThread())
			throw new RuntimeException("invalid thread");
	}

	protected boolean isOnDisplayThread() {
		return getDisplay().getThread() == Thread.currentThread();
	}

	/**
	 * Provides a more concise representation of the component than the default
	 * Component.toString().
	 */
	// FIXME getTag has too much overhead to be calling this frequently
	public String toString(Widget widget) {

		if (widget == null)
			return "(null)";
		checkWidget(widget);

		Class cls = widget.getClass();

		WidgetTester wt = WidgetTester.getTester(widget);
		String name = (String) wt.getData(widget, "name");

		// WidgetFinder finder = DefaultWidgetFinder.getFinder();
		// String name = finder.getWidgetName(widget);

		if (name == null)
			name = WidgetTester.getTag(widget);
		cls = getCanonicalClass(cls);
		String cname = simpleClassName(widget.getClass());
		if (!cls.equals(widget.getClass()))
			cname += "/" + simpleClassName(cls);
		if (name == null)
			name = cname + " instance";
		else
			name = "'" + name + "' (" + cname + ")";
		return name;
	}

	// for setting flags inside a call to Display.asyncExec(Runnable)
	// [NOT USED CURRENTLY]
	// public static class SyncFlag {
	// static Hashtable flags = new Hashtable();
	//
	// public static synchronized void initFlag(Display display) {
	// flags.put(display, Boolean.valueOf(false));
	// }
	//
	// public static synchronized void clearFlag(Display display) {
	// flags.remove(display);
	// }
	//
	// /* To be called inside of an syncExec/asyncExec call */
	// public static synchronized void setFlag() {
	// Display display = Display.findDisplay(Thread.currentThread());
	// flags.put(display, Boolean.valueOf(true));
	// }
	//
	// public static synchronized boolean getFlag(Display display) {
	// return ((Boolean) flags.get(Thread.currentThread())).booleanValue();
	// }
	//
	// }

	// boolean locked = false;

	/**
	 * OS X (as of 1.3.1, v10.1.5), will sometimes send a click to the wrong component after a mouse
	 * move. This continues to be an issue in 1.4.1
	 * <p>
	 * Linux x86 (1.3.1) has a similar problem, although it manifests it at different times (need a
	 * bug test case for this one).
	 * <p>
	 * Solaris and HPUX probably share code with the linux VM implementation, so the bug there is
	 * probably identical.
	 * <p>
	 */
	// FIXME add tests to determine presence of bug.
	public boolean hasRobotMotionBug() {
		return (Platform.isOSX()
				|| (!Platform.isWindows() && Platform.JAVA_VERSION < Platform.JAVA_1_4) || Boolean
				.getBoolean("abbot.robot.need_jitter"));
	}
}
