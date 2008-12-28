package abbot.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;

import abbot.swt.script.Condition;
import abbot.swt.utilities.Bresenham;
import abbot.swt.utilities.Wait;

/**
 * Provides API for mouse & keyboard input event generation and for querying the state of them and
 * the display.
 * 
 * @author Kevin Dale
 * @author Gary Johnston
 */
public class Robot {

	/**
	 * The number of pixels in each dimension we need to move the mouse cursor in order to trigger a
	 * DragDetect event. There is not API for this so we use a relatively large value to try to make
	 * sure we'll trigger it.
	 */
	private static final int DRAG_THRESHOLD = 10;

	/**
	 * The default {@link Robot} instance, based on the default {@link Display}.
	 * 
	 * @see #getDefault()
	 */
	private static Robot Default;

	/**
	 * Gets the default {@link Robot}, based on the default {@link Display}, creating a new one if
	 * necessary.
	 * 
	 * @return the default {@link Robot}
	 */
	public static synchronized Robot getDefault() {
		if (Default == null)
			Default = new Robot(Display.getDefault());
		return Default;
	}

	/**
	 * This {@link Robot}'s {@link Display}.
	 */
	private final Display display;

	/**
	 * The bounds of this {@link Robot}'s {@link Display}.
	 */
	private Rectangle displayBounds;

	/**
	 * The bounds of this {@link Robot}'s {@link Display}'s {@link Monitor}s. These are cached
	 * for performance and to avoid having to call {@link Display#syncExec(Runnable)} during
	 * {@link #checkLocation(int, int)}, which can sometimes cause deadlock.
	 */
	private Rectangle[] monitorBounds;

	/**
	 * The index in monitorBounds of the primary monitor.
	 */
	private int primaryMonitorIndex;

	/**
	 * The maximum time between MouseDown events that will cause a MouseDoubleClick event.
	 */
	private long doubleClickTime;

	/**
	 * The system time (in milliseconds) of the last mouse button press. Used to avoid unintentional
	 * double-clicks.
	 * 
	 * @see #mousePressPrim(int, boolean)
	 */
	private volatile long clickstamp;

	/**
	 * A {@link Listener} to update this {@link Robot}'s cached and {@link #monitorBounds} if/when
	 * necessary.
	 */
	private Listener listener;

	/**
	 * Constructs a new {@link Robot} for the specified {@link Display}.
	 * <p>
	 * <b>Note:</b> You should probably call the factory method {@link #getDefault()} instead of
	 * explicitly constructing a new {@link Robot}.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>display</code> is null
	 */
	public Robot(Display display) {
		if (display == null)
			throw new IllegalArgumentException("display is null");
		this.display = display;
		clickstamp = System.currentTimeMillis();
		init();
	}

	private void init() {
		listener = new Listener() {
			public void handleEvent(Event event) {
				updateSettings();
			}
		};
		display.syncExec(new Runnable() {
			public void run() {
				display.addListener(SWT.Settings, listener);
				updateSettings();
			}
		});
	}

	/**
	 * Update the cached bounds of our {@link Display}'s {@link Monitor}s. Must be called on the
	 * display thread.
	 */
	private void updateSettings() {

		// Cache the display's bounds.
		displayBounds = display.getBounds();

		// Cache the monitors' bounds and the index of the primary monitor.
		Monitor[] monitors = display.getMonitors();
		Monitor primaryMonitor = display.getPrimaryMonitor();
		monitorBounds = new Rectangle[monitors.length];
		primaryMonitorIndex = -1;
		for (int i = 0; i < monitors.length; i++) {
			Monitor monitor = monitors[i];
			monitorBounds[i] = monitor.getBounds();
			if (monitor.equals(primaryMonitor))
				primaryMonitorIndex = i;
		}

		// Cache the double-click time.
		doubleClickTime = display.getDoubleClickTime();

		// System.err.printf("Robot: %d monitors:\n", monitorBounds.length);
		// for (int i = 0; i < monitorBounds.length; i++) {
		// System.err.printf(" %d: %s\n", i, monitorBounds[i]);
		// }
		// System.err.printf("doubleClickTime: %d\n", doubleClickTime);
		// System.err.printf("clickstamp: %d\n", clickstamp);
	}

	public void dispose() {
		if (display != null && !display.isDisposed()) {
			display.syncExec(new Runnable() {
				public void run() {
					display.removeFilter(SWT.MouseDown, listener);
					display.removeListener(SWT.Settings, listener);
				}
			});
		}
		listener = null;
	}

	/**
	 * @return the receiver's {@link Display}
	 */
	public Display getDisplay() {
		return display;
	}

	public String toString() {

		Formatter formatter = new Formatter();
		formatter.format(
				"Robot{%s%s@%s %dx%d@%d,%d",
				display == Display.getDefault() ? "*" : "",
				display.getClass().getSimpleName(),
				Integer.toHexString(display.hashCode()),
				displayBounds.width,
				displayBounds.height,
				displayBounds.x,
				displayBounds.y);

		formatter.format(" [");
		for (int i = 0; i < monitorBounds.length; i++) {
			Rectangle bounds = monitorBounds[i];
			formatter.format("%s%s%dx%d@%d,%d", i == 0 ? "" : " ", i == primaryMonitorIndex ? "*"
					: "", bounds.width, bounds.height, bounds.x, bounds.y);
		}
		formatter.format("] double-click: %dms}", doubleClickTime);

		return formatter.toString();
	}

	/**
	 * Generates a user input event to move the mouse cursor to a specified location on the
	 * receiver's {@link Display}.
	 * 
	 * @param x
	 *            the x coordinate of the location
	 * @param y
	 *            the y coordinate of the location
	 * @exception IllegalArgumentException
	 *                if called on the {@link Thread} that created the receiver's {@link Display} or
	 *                if the location is not within the bounds of the receiver's {@link Display}.
	 */
	public synchronized void mouseMove(int x, int y) {
		checkThread();
		checkLocation(x, y);
		mouseMovePrim(x, y);
	}

	/**
	 * Generates input events to click (press and release) one or more mouse buttons, optionally
	 * including one or more keyboard modifiers.
	 * 
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display} or if
	 *             the buttonmask is invalid
	 */
	public synchronized void mouseClick(int buttonmask) {
		checkThread();
		checkButtons(buttonmask);

		avoidDoubleClick(0);
		keyPressModifiers(buttonmask);
		mousePressButtons(buttonmask);
		mouseReleaseButtons(buttonmask);
		keyReleaseModifiers(buttonmask);
	}

	public synchronized void mouseClick(int x, int y, int buttonmask) {
		checkThread();
		checkLocation(x, y);
		checkButtons(buttonmask);

		mouseMovePrim(x, y);

		avoidDoubleClick(0);
		keyPressModifiers(buttonmask);
		mousePressButtons(buttonmask);
		mouseReleaseButtons(buttonmask);
		keyReleaseModifiers(buttonmask);
	}

	/**
	 * Generates input events to double-click (press and release) one or more mouse buttons,
	 * optionally including one or more keyboard modifiers.
	 * 
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display} or if
	 *             the buttonmask is invalid
	 */
	public synchronized void mouseDoubleClick(int buttonmask) {
		checkThread();
		checkButtons(buttonmask);

		avoidDoubleClick(doubleClickTime);
		keyPressModifiers(buttonmask);
		mousePressButtons(buttonmask);
		mouseReleaseButtons(buttonmask);
		mousePressButtons(buttonmask);
		mouseReleaseButtons(buttonmask);
		keyReleaseModifiers(buttonmask);
	}

	/**
	 * Generates input events to move the cursor to a specified display coordinate and double-click
	 * (press and release) one or more mouse buttons, optionally including one or more keyboard
	 * modifiers.
	 * 
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display} or if
	 *             the buttonmask is invalid
	 */
	public synchronized void mouseDoubleClick(int x, int y, int buttonmask) {
		checkThread();
		checkLocation(x, y);
		checkButtons(buttonmask);

		mouseMovePrim(x, y);

		avoidDoubleClick(doubleClickTime);
		keyPressModifiers(buttonmask);
		mousePressButtons(buttonmask);
		mouseReleaseButtons(buttonmask);
		mousePressButtons(buttonmask);
		mouseReleaseButtons(buttonmask);
		keyReleaseModifiers(buttonmask);
	}

	/**
	 * Generates a user input event to move the mouse's scroll wheel.
	 * 
	 * @param count
	 *            the number of "notches" to move the mouse wheel. Negative values indicate movement
	 *            up/away from the user. Positive values indicate movement down/towards the user.
	 * @param page
	 *            <code>true</code> or <code>false</code> indicating page or line scrolling,
	 *            respectively
	 * @throws IllegalArgumentException -
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 * @throws UnsupportedOperationException -
	 *             if called on a platform on which {@link Display#post(Event)} does not support
	 *             generating the {@link SWT#MouseWheel} event (GTK, for example)
	 */
	public synchronized void mouseWheel(int count, boolean page) {
		checkThread();
		if (SWT.getPlatform().equals("gtk"))
			throw new UnsupportedOperationException("unsupported on GTK");
		postMouseWheel(count, page);
	}

	/**
	 * Generates user input events to cause a drag and drop.
	 * 
	 * @param sx
	 *            the x coordinate of the drag starting location
	 * @param sy
	 *            the y coordinate of the drag starting location
	 * @param tx
	 *            the x coordinate of the drop target location
	 * @param ty
	 *            the y coordinate of the drop target location
	 * @param buttonMask
	 *            the mouse buttons, optionally including one or more keyboard modifiers, to be
	 *            pressed at the drag starting location and released at the drop target location
	 * @throws IllegalArgumentException
	 *             <ul>
	 *             <li>if called on the {@link Thread} that created the receiver's {@link Display};
	 *             or</li>
	 *             <li>if either the starting location or target location are invalid; or</li>
	 *             <li>the buttonMask is invalid</li>
	 *             </ul>
	 */
	public synchronized void mouseDragDrop(int sx, int sy, int tx, int ty, final int buttonmask) {
		checkThread();
		checkLocation(sx, sy);
		checkLocation(tx, ty);
		checkButtons(buttonmask);

		/*
		 * Generate mouse press and then enough move events to cause a drag to be initiated (i.e.,
		 * drag detect).
		 */
		/*
		 * Feature in SWT on GTK: When the display thread gets a MousePress event it will hold onto
		 * some internal lock until it gets enough MouseMove events to cause drag detect (or until
		 * it gets some other user input event that rules out a drag). If we then attempt to
		 * generate the MouseMove events, we will deadlock in Display.post(Event) because it tries
		 * to acquire the same lock. See Eclipse bugzilla 204333. The workaround is to generate the
		 * MousePress and MouseMove events within a single syncExec() on the display thread so that
		 * all of the input events have been generated before the display thread gets a chance to
		 * process the MousePress event.
		 */
		/*
		 * Note that in the following, b.next() will throw a NoSuchElementException if there are
		 * fewer than DRAG_THRESHOLD points available after we move to the starting point.
		 */
		final Bresenham points = new Bresenham(sx, sy, tx, ty);
		Point p = points.next();
		mouseMovePrim(p.x, p.y); // Move to the drag starting location.
		avoidDoubleClick(0);
		for (int i = 0; i < DRAG_THRESHOLD - 1; i++) {
			points.next();
		}
		getDisplay().syncExec(new Runnable() {
			public void run() {
				Point q = points.next();
				keyPressModifiers(buttonmask);
				mousePressButtons(buttonmask);
				postMouseMove(q.x, q.y);
			}
		});

		/*
		 * Move to the drop target and release the mouse buttons (& any modifiers).
		 */
		for (Point point : points) {
			mouseMovePrim(point.x, point.y);
		}
		mouseReleaseButtons(buttonmask);
		keyReleaseModifiers(buttonmask);
	}

	/**
	 * Generates a user input event to move the cursor to a specified location (and waits for the it
	 * to get there).
	 * 
	 * @param x
	 *            the x coordinate of the location
	 * @param y
	 *            the y coordinate of the location
	 * @throws WaitTimedOutError
	 *             if the cursor doesn't get to the target location within a reasonable amount of
	 *             time
	 */
	private void mouseMovePrim(final int x, final int y) {

		// First try.
		try {
			postMouseMove(x, y);
			waitMouseLocation(x, y);
			return;
		} catch (WaitTimedOutError exception) {
			Log.warn("Strike one: %s\n", exception);
		}

		// Second try.
		try {
			postMouseMove(x, y);
			waitMouseLocation(x, y);
			return;
		} catch (WaitTimedOutError exception) {
			Log.warn("Strike two: %s\n", exception);
		}

		// Third and final try.
		postMouseMove(x, y);
		waitMouseLocation(x, y);
	}

	private void waitMouseLocation(final int x, final int y) {
		Wait.wait(new Condition() {
			Point p = null;

			public boolean test() {
				p = getCursorLocationPrim();
				return p.x == x && p.y == y;
			}

			public String toString() {
				return String.format("move to %d,%d (got %s)", x, y, p);
			}
		}, 3000);
	}

	private void mousePressButtons(int buttons) {

		// Press the buttons.
		if ((buttons & SWT.BUTTON1) != 0)
			postMousePress(1);
		if ((buttons & SWT.BUTTON2) != 0)
			postMousePress(2);
		if ((buttons & SWT.BUTTON3) != 0)
			postMousePress(3);
		if ((buttons & SWT.BUTTON4) != 0)
			postMousePress(4);
		if ((buttons & SWT.BUTTON5) != 0)
			postMousePress(5);

		clickstamp = System.currentTimeMillis();
	}

	private void mouseReleaseButtons(int buttons) {
		if ((buttons & SWT.BUTTON5) != 0)
			postMouseRelease(5);
		if ((buttons & SWT.BUTTON4) != 0)
			postMouseRelease(4);
		if ((buttons & SWT.BUTTON3) != 0)
			postMouseRelease(3);
		if ((buttons & SWT.BUTTON2) != 0)
			postMouseRelease(2);
		if ((buttons & SWT.BUTTON1) != 0)
			postMouseRelease(1);
	}

	private static final long DoubleClickPadding = 200L;

	private void avoidDoubleClick(long extra) {
		long time = clickstamp + doubleClickTime + DoubleClickPadding + extra
				- System.currentTimeMillis();
		if (time > 0)
			sleep(time);
	}

	private void sleep(long time) {
		// System.err.printf("sleep %d\n", time);
		try {
			Thread.sleep(time);
		} catch (InterruptedException exception) {
			// Ignore exception.
		}
	}

	/**
	 * Generates user input events to cause a keystroke (key press and release), optionally
	 * including one or more modifiers.
	 * <p>
	 * They keystroke is specified as an integer keymask which can contain exactly one character or
	 * keycode and, optionally, one or more modifier flags ({@link SWT#CTRL}, {@link SWT#ALT},
	 * {@link SWT#SHIFT} and {@link SWT#COMMAND}).
	 * <p>
	 * For a Unicode character use the Unicode character value (e.g., <code>'k'</code>,
	 * <code>'K'</code>, <code>'5'</code>, etc.). For non-character keys use the keycodes
	 * defined in {@link SWT}. For example, use {@link SWT#F1} for F1.
	 * <p>
	 * <strong>Note:</strong>
	 * <ul>
	 * <li>Character processing is case-sensitive, so {@link SWT#SHIFT} is implied if the character
	 * is uppercase or otherwise requires the shift key (e.g., '&').</li>
	 * <li>Characters that do not appear on a US keyboard are currently ignored.</li>
	 * </ul>
	 * 
	 * @param keymask
	 *            they key mask consiting of a character or keycode plus, optionally, one or more
	 *            modifiers
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	public synchronized void key(int keymask) {
		checkThread();
		// checkKeyMask(keymask);

		/*
		 * "Normalize" they keymask. If the keymask contains a virtual key code or the null
		 * character then the keymask is left unchanged. Otherwise, if the character requires the
		 * shift key then we add SWT.SHIFT and replace the character with its unshifted counterpart.
		 * For example, 'A' would become SWT.SHIFT|'a'.
		 */
		if ((keymask & SWT.KEYCODE_BIT) == 0) {
			char ch = (char) (keymask & SWT.KEY_MASK);
			if (ch != '\0') {
				char chUnshifted = unshifted(ch);
				if (chUnshifted != ch) {
					keymask &= ~SWT.KEY_MASK;
					keymask |= SWT.SHIFT | chUnshifted;
				}
			}
		}

		keyPressPrim(keymask);
		keyReleasePrim(keymask);
	}

	private void keyPressPrim(int keymask) {

		keyPressModifiers(keymask);

		if ((keymask & SWT.KEYCODE_BIT) != 0) {
			postKeyPress(keymask & SWT.KEY_MASK, '\0');
		} else {
			char c = (char) (keymask & SWT.KEY_MASK);
			if (c != '\0')
				postKeyPress(0, c);
		}
	}

	private void keyReleasePrim(int keymask) {

		if ((keymask & SWT.KEYCODE_BIT) != 0) {
			postKeyRelease(keymask & SWT.KEY_MASK, '\0');
		} else {
			char c = (char) (keymask & SWT.KEY_MASK);
			if (c != '\0')
				postKeyRelease(0, c);
		}

		keyReleaseModifiers(keymask);
	}

	private void keyPressModifiers(int modifiers) {
		if ((modifiers & SWT.CTRL) != 0)
			postKeyPress(SWT.CTRL, '\0');
		if ((modifiers & SWT.ALT) != 0)
			postKeyPress(SWT.ALT, '\0');
		if ((modifiers & SWT.SHIFT) != 0)
			postKeyPress(SWT.SHIFT, '\0');
		if ((modifiers & SWT.COMMAND) != 0)
			postKeyPress(SWT.COMMAND, '\0');
	}

	private void keyReleaseModifiers(int modifiers) {
		if ((modifiers & SWT.COMMAND) != 0)
			postKeyRelease(SWT.COMMAND, '\0');
		if ((modifiers & SWT.SHIFT) != 0)
			postKeyRelease(SWT.SHIFT, '\0');
		if ((modifiers & SWT.ALT) != 0)
			postKeyRelease(SWT.ALT, '\0');
		if ((modifiers & SWT.CTRL) != 0)
			postKeyRelease(SWT.CTRL, '\0');
	}

	/**
	 * Gets the unshifted version of a character.
	 * 
	 * @param c
	 *            the character key
	 * @return the unshifted value of <code>key</code>
	 */
	private char unshifted(char c) {
		if (c >= 'A' && c <= 'Z')
			return (char) ((c - 'A') + 'a');
		if (c >= 'a' && c <= 'z')
			return c;

		switch (c) {
			case '~':
				return '`';
			case '!':
				return '1';
			case '@':
				return '2';
			case '#':
				return '3';
			case '$':
				return '4';
			case '%':
				return '5';
			case '^':
				return '6';
			case '&':
				return '7';
			case '*':
				return '8';
			case '(':
				return '9';
			case ')':
				return '0';
			case '_':
				return '-';
			case '+':
				return '=';
			case '{':
				return '[';
			case '}':
				return ']';
			case '|':
				return '\\';
			case ':':
				return ';';
			case '"':
				return '\'';
			case '<':
				return ',';
			case '>':
				return '.';
			case '?':
				return '/';
			case SWT.ESC:
			case SWT.DEL:
			case ' ':
			case '\b':
			case '\t':
			case '\n':
			case '\r':
			case '`':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '0':
			case '-':
			case '=':
			case '[':
			case ']':
			case '\\':
			case ';':
			case '\'':
			case ',':
			case '.':
			case '/':
				return c;
		}
		throw new IllegalArgumentException(String.format("invalid key character: 0x%04x", c));
	}

	/**
	 * Creates an {@link Image} containing pixels read from a rectangular area on this {@link Robot}'s
	 * {@link Display}.
	 * <p>
	 * <b>Note:</b> The <b><i>caller</i></b> is responsible for disposing the returned
	 * {@link Image}.
	 * 
	 * @param bounds
	 *            the {@link Rectangle} area to capture in display coordinates
	 * @return the captured {@link Image}
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	public synchronized Image capture(final Rectangle bounds) {
		checkThread();
		checkBounds(bounds);

		return capturePrim(bounds);
	}

	/**
	 * Creates an {@link Image} containing pixels read from this {@link Robot}'s entire
	 * {@link Display}.
	 * <p>
	 * <b>Note:</b> The <b><i>caller</i></b> is responsible for disposing the returned
	 * {@link Image}.
	 * 
	 * @return the captured {@link Image}
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	public synchronized Image capture() {
		checkThread();
		return capturePrim(displayBounds);
	}

	/**
	 * Returns the {@link Color} of a pixel at specified display coordinates.
	 * <p>
	 * <strong>Note:</strong> The <b><i>caller</i></b> is responsible for disposing the
	 * {@link Color}.
	 * 
	 * @param x
	 *            the x position of the pixel
	 * @param y
	 *            the y position of the pixel
	 * @return the {@link Color} of the pixel
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	public synchronized Color capture(int x, int y) {
		checkThread();
		checkLocation(x, y);

		/*
		 * Seems like there should be a more efficient way to get the color of a single pixel.
		 * Currently, we're essentially doing a single pixel screen area capture, which seems
		 * excessively heavyweight.
		 */
		Image image = capturePrim(new Rectangle(x, y, 1, 1));
		try {
			ImageData data = image.getImageData();
			int pixel = data.getPixel(0, 0);
			RGB rgb = data.palette.getRGB(pixel);
			return new Color(display, rgb);
		} finally {
			if (image != null)
				image.dispose();
		}
	}

	private Image capturePrim(final Rectangle rectangle) {
		final Image image = new Image(display, rectangle.width, rectangle.height);
		final GC[] gc = new GC[1];
		try {
			display.syncExec(new Runnable() {
				public void run() {
					gc[0] = new GC(display);
					gc[0].copyArea(image, rectangle.x, rectangle.y);
				}
			});
		} finally {
			if (gc[0] != null)
				gc[0].dispose();
		}
		return image;
	}

	/*
	 * Low-level event posting.
	 */

	private void postMouseMove(int x, int y) {
		Event event = new Event();
		event.type = SWT.MouseMove;
		event.x = x;
		event.y = y;
		post(event);
	}

	/**
	 * Generates a user input event to press a mouse button.
	 * 
	 * @param button
	 *            The number of the button to be pressed (i.e., 1, 2, 3, etc.)
	 */
	private void postMousePress(int button) {
		Event event = new Event();
		event.type = SWT.MouseDown;
		event.button = button;
		post(event);
	}

	/**
	 * Generates a user input event to release a mouse button.
	 * 
	 * @param button
	 *            The number of the button to be pressed (i.e., 1, 2, 3, etc.)
	 */
	private void postMouseRelease(int button) {
		Event event = new Event();
		event.type = SWT.MouseUp;
		event.button = button;
		post(event);
	}

	private void postKeyPress(int keyCode, char character) {
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.keyCode = keyCode;
		event.character = character;
		post(event);
	}

	private void postKeyRelease(int keyCode, char character) {
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.keyCode = keyCode;
		event.character = character;
		post(event);
	}

	private void postMouseWheel(int count, boolean page) {
		Event event = new Event();
		event.type = SWT.MouseWheel;
		event.detail = page ? SWT.SCROLL_PAGE : SWT.SCROLL_LINE;
		event.count = count;
		post(event);
	}

	/**
	 * Posts an {@link Event} on the UI input queue.
	 * 
	 * @param event
	 *            the {@link Event} to post
	 * @throws RuntimeException
	 *             if it fails
	 * @see Display#post(Event)
	 */
	private void post(Event event) {

		if (!display.post(event))
			throw new RuntimeException("post failed: " + event);

		/*
		 * On some platforms (Linux GTK, at least) if the display thread is sleeping at this point
		 * it is not always awakened reliably. So we poke it here. If it finds that it has nothing
		 * to do, it will go back to sleep, so no harm done.
		 */
		display.wake();
	}

	/*
	 * Argument checking.
	 */

	/**
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	private void checkThread() {
		if (isOnDisplayThread())
			throw new IllegalArgumentException("invalid thread");
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the location is outside the receiver's Display's bounds.
	 */
	private void checkLocation(int x, int y) {
		if (!isValidLocation(x, y))
			throw new IllegalArgumentException(String.format(
					"invalid location: %d,%d not contained in %s",
					x,
					y,
					Arrays.toString(monitorBounds)));
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the bounds is not completely within the receiver's Display's bounds.
	 */
	private void checkBounds(Rectangle bounds) {
		checkLocation(bounds.x, bounds.y);
		checkLocation(bounds.x + bounds.width - 1, bounds.y + bounds.height - 1);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the <code>buttonmask</code> is not valid
	 */
	private void checkButtons(int buttonmask) {
		if ((buttonmask & ~(SWT.BUTTON_MASK | SWT.MODIFIER_MASK)) != 0)
			throw new IllegalArgumentException(String.format(
					"invalid button mask: 0x%08x",
					buttonmask));
	}

	/*
	 * Other utilities.
	 */

	/**
	 * @return <code>true</code> if the location specified by {@link Point} <code>p</code> is
	 *         within any of the receiver's {@link Monitor}s, <code>false</code> otherwise
	 */
	public boolean isValidLocation(Point p) {
		return isValidLocation(p.x, p.y);
	}

	/**
	 * @return <code>true</code> if the location specified by <code>x,y</code> is within any of
	 *         the receiver's {@link Monitor}s, <code>false</code> otherwise
	 */
	public boolean isValidLocation(int x, int y) {
		for (int i = 0; i < monitorBounds.length; i++) {
			if (monitorBounds[i].contains(x, y))
				return true;
		}
		return false;
	}

	/**
	 * Gets the {@link Rectangle}s representing the non-empty intersections of a specified
	 * {@link Rectangle} with each of the receiver's {@link Monitor}'s.
	 * 
	 * @param bounds
	 *            an area in display coordinates
	 * @return a possibly empty array of intersecting {@link Rectangle}s.
	 */
	public Rectangle[] getIntersections(Rectangle bounds) {
		List<Rectangle> intersections = new ArrayList<Rectangle>(monitorBounds.length);
		for (int i = 0; i < monitorBounds.length; i++) {
			Rectangle intersection = monitorBounds[i].intersection(bounds);
			if (!intersection.isEmpty())
				intersections.add(intersection);
		}
		return intersections.toArray(new Rectangle[intersections.size()]);
	}

	/**
	 * @return <code>true</code> if called on the {@link Thread} that created the receiver's
	 *         {@link Display}, <code>false</code> otherwise
	 */
	public boolean isOnDisplayThread() {
		return display.getThread() == Thread.currentThread();
	}

	private static final long WAIT_IDLE_TIMEOUT = 60000L;

	private static final long WAIT_IDLE_INTERVAL = 100L;

	public void waitForIdle() {
		checkThread();

		// Queue up an asyncExec to set a flag when it runs. Because the Display
		// runs them in FIFO
		// order, ours will not run until everything that was previously in the
		// queue has run and
		// completed.
		final boolean[] done = new boolean[1];
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				done[0] = true;
			}
		});

		// Wait for the asyncExec to complete. If it times out we will assume it
		// means that the
		// display thread is hung so we will do a System.exit().
		final long limit = System.currentTimeMillis() + WAIT_IDLE_TIMEOUT;
		while (!done[0]) {
			try {
				Thread.sleep(WAIT_IDLE_INTERVAL);
			} catch (InterruptedException exception) {
				// Empty block intended.
			}
			if (System.currentTimeMillis() > limit)
				throw new WaitTimedOutError();
		}

	}

	/**
	 * @return the current cursor location
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	public Point getCursorLocation() {
		checkThread();
		return getCursorLocationPrim();
	}

	private Point getCursorLocationPrim() {
		final Point[] point = new Point[1];
		display.syncExec(new Runnable() {
			public void run() {
				point[0] = display.getCursorLocation();
			}
		});
		return point[0];
	}

	/**
	 * @return the {@link Control} that the cursor is currently over, or <code>null</code> if
	 *         there isn't one
	 * @throws IllegalArgumentException
	 *             if called on the {@link Thread} that created the receiver's {@link Display}
	 */
	public Control getCursorControl() {
		checkThread();
		final Control[] control = new Control[1];
		display.syncExec(new Runnable() {
			public void run() {
				control[0] = display.getCursorControl();
			}
		});
		return control[0];
	}

}
