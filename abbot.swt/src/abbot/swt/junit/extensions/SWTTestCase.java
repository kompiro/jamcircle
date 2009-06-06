package abbot.swt.junit.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.WidgetLocator;
import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.WidgetHierarchy;
import abbot.swt.finder.WidgetHierarchyImpl;
import abbot.swt.hierarchy.Visitable.Visitor;
import abbot.swt.junit.extensions.UserThread.Executable;
import abbot.swt.tester.ShellTester;
import abbot.swt.tester.WidgetTester;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.WidgetHierarchyPrinter;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

public class SWTTestCase extends TestCase {

	/**
	 * Key of system property which, if set to true when a test fails or errors,
	 * will cause the entire current Widget hierarchy to be printed to the
	 * standard error output stream.
	 */
	public static final String WIDGET_HIERARCHY_ON_ERROR_KEY = "abbot.swt.error.WidgetHierarchy";

	/**
	 * Key of system property which, if set to true when a test fails or errors,
	 * will cause stack traces of all threads to be printed to the standard
	 * error output stream.
	 */
	public static final String STACK_TRACES_ON_ERROR_KEY = "abbot.swt.error.StackTraces";

	static void checkThread(Display display) {
		if (!Displays.isOnDisplayThread(display)
				&& !UserThread.isOnUserThread())
			throw new IllegalStateException("invalid thread");
	}

	static Shell[] getShells(final Display display) {
		if (Displays.isOnDisplayThread(display))
			return display.getShells();
		return Displays.syncExec(display, new Result<Shell[]>() {
			public Shell[] result() {
				return display.getShells();
			}
		});
	}

	static void closeOtherShells(final Display display,
			final Shell[] initialShells) {
		if (display != null && !display.isDisposed()) {

			// Close any shells that weren't open to begin with.
			if (Displays.isOnDisplayThread(display)) {
				closeShells(display, initialShells);
			} else {
				display.syncExec(new Runnable() {
					public void run() {
						closeShells(display, initialShells);
					}
				});
			}
		}
	}

	/**
	 * Attempts to close any pop-up or drop-down Menus that are "stuck" open.
	 */
	static void closeStuckMenus(final Display display) {
		if (display != null && !display.isDisposed()) {
			Map<Menu, MenuInfo> infos = getStuckMenuInfos(display);
			for (Entry<Menu, MenuInfo> entry : infos.entrySet()) {
				MenuInfo info = entry.getValue();
				System.err.printf("Stuck menu (bar: %s): %s\n", info.isBar,
						info.string);
				esc(display);
				if (info.isBar)
					esc(display);
			}
		}
	}

	/**
	 * Generate ESC key press and release.
	 * 
	 * @see #closeStuckMenus(Display)
	 */
	private static void esc(Display display) {
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.character = SWT.ESC;
		display.post(event);
		event.type = SWT.KeyUp;
		display.post(event);
	}

	/**
	 * Holds some information about {@link Menu}s that were stuck open.
	 * 
	 * @see #closeStuckMenus(Display)
	 * @see #getStuckMenuInfos(Display)
	 * 
	 */
	private static class MenuInfo {
		public String string;
		public boolean isBar;
	}

	/**
	 * Gets a {@link Map} the keys of which are all drop-down and pop-up
	 * {@link Menu}s that are "stuck" open. The values are corresponding
	 * {@link MenuInfo}'s.
	 * 
	 * @see #closeStuckMenus(Display)
	 */
	private static Map<Menu, MenuInfo> getStuckMenuInfos(final Display display) {
		final Map<Menu, MenuInfo> infos = new HashMap<Menu, MenuInfo>();
		display.syncExec(new Runnable() {
			public void run() {
				new WidgetHierarchyImpl(display).accept(new Visitor<Widget>() {
					public Result visit(Widget widget) {
						if (!widget.isDisposed() && widget instanceof Menu) {
							int style = widget.getStyle();
							if ((style & (SWT.POP_UP | SWT.DROP_DOWN)) != 0) {
								Rectangle bounds = WidgetLocator
										.getBounds(widget);
								if (bounds != null && !bounds.isEmpty()
										& !infos.containsKey(widget)) {
									Menu menu = (Menu) widget;
									MenuInfo info = new MenuInfo();
									info.string = menu.toString();
									info.isBar = isBar(menu);
									infos.put(menu, info);
								}
							}
						}
						return Result.ok;
					}
				});
			}
		});
		return infos;
	}

	/**
	 * @return true iff the {@link Menu} is, or is a descendent of, a menu bar.
	 * @see #getStuckMenuInfos(Display)
	 */
	private static boolean isBar(Menu menu) {
		while (menu != null) {
			if ((menu.getStyle() & SWT.BAR) != 0)
				return true;
			menu = menu.getParentMenu();
		}
		return false;
	}

	/*
	 * Must called on display thread.
	 */
	private static void closeShells(Display display, Shell[] initialShells) {
		Shell[] remainingShells = display.getShells();
		for (int i = 0; i < remainingShells.length; i++) {
			Shell remainingShell = remainingShells[i];
			if (shouldClose(remainingShell, initialShells))
				remainingShell.close();
		}
	}

	private static boolean shouldClose(Shell remainingShell,
			Shell[] initialShells) {
		if (remainingShell.isDisposed())
			return false;
		for (int j = 0; j < initialShells.length; j++) {
			if (remainingShell == initialShells[j])
				return false;
		}
		return true;
	}

	private Display display;

	private Shell[] initialShells;

	public SWTTestCase(String name) {
		super(name);
	}

	public SWTTestCase() {
		super();
	}

	protected synchronized final Display getDisplay() {
		if (display == null)
			display = getDefaultDisplay();
		assertNotNull(display);
		return display;
	}

	protected Display getDefaultDisplay() {
		return Display.getDefault();
	}

	protected synchronized final Display setDisplay(Display display) {
		Display oldDisplay = this.display;
		this.display = display;
		return oldDisplay;
	}

	/**
	 * Execute super.runBare() in a non-UI Thread.
	 * 
	 * @see junit.framework.TestCase#runBare()
	 */
	public void runBare() throws Throwable {
		checkThread(getDisplay());

		// Invoke runBareWrapper() on a non-UI thread.
		if (UserThread.isOnUserThread()) {

			/*
			 * We're on a non-UI thread (UserThread) already so no need to start
			 * another one.
			 */
			runBareWrapper();

		} else {

			/*
			 * We're on a UI thread so invoke it on a UserThread.
			 */
			UserThread userThread = UserThread.syncExec(new Executable() {
				public void execute() throws Throwable {
					runBareWrapper();
				}
			});

			// If the UserThread threw an exception, rethrow it.
			Throwable throwable = userThread.getException();
			if (throwable != null) {
				if (throwable instanceof SWTException) {
					SWTException exception = (SWTException) throwable;
					if (exception.code == SWT.ERROR_FAILED_EXEC)
						throw exception.getCause();
				}
				throw throwable;
			}
		}
	}

	/**
	 * Call super.runBare() wrapped by calls to preSetUp() & postTearDown(),
	 * also calling caughtThrowable() if a Throwable is caught.
	 * 
	 * @see #runBare()
	 */
	private void runBareWrapper() throws Throwable {
		try {
			preSetUp();
			super.runBare();
		} catch (Throwable throwable) {
			caughtThrowable(throwable);
			throw throwable;
		} finally {
			postTearDown();
		}
	}

	private void preSetUp() {

		// Get the initial set of Shells.
		initialShells = getShells(getDisplay());

		// Try to make sure any root Shells are in front.
		ShellTester tester = ShellTester.getShellTester();
		for (Shell shell : initialShells) {
			Control parent = tester.getParent(shell);
			if (parent == null) {
				System.out.printf("%s: Activating %s\n", getName(), tester
						.toString(shell));
				tester.setActive(shell);
				tester.forceActive(shell);
			}
		}

		// Record starting time.
		startTime = System.currentTimeMillis();
	}

	private void postTearDown() {

		// Report ending time.
		long endTime = System.currentTimeMillis();
		double time = (double) (endTime - startTime) / 1000.0;
		System.out.printf("%s: %1.3f sec.\n", getName(), time);
		startTime = 0L;

		// Close any drop-down or pop-up menus that are open.
		closeStuckMenus(getDisplay());

		// Close any created Shells that are still open.
		closeOtherShells(getDisplay(), initialShells);
	}

	private void caughtThrowable(Throwable throwable) {
		checkDumpWidgets();
		checkDumpStacks();
	}

	private void checkDumpWidgets() {
		if (Boolean.getBoolean(WIDGET_HIERARCHY_ON_ERROR_KEY))
			dumpWidgets();
	}

	private void checkDumpStacks() {
		if (Boolean.getBoolean(STACK_TRACES_ON_ERROR_KEY))
			dumpStacks();
	}

	private volatile long startTime = 0L;

	protected void setUp() throws Exception {
		super.setUp();
		Displays.syncExec(getDisplay(), new Runnable() {
			public void run() {
				setUpDisplay();
			}
		});
	}

	protected void setUpDisplay() {
	}

	protected void tearDown() throws Exception {

		super.tearDown();
		Displays.syncExec(getDisplay(), new Runnable() {
			public void run() {
				tearDownDisplay();
			}
		});

	}

	protected void tearDownDisplay() {
	}

	/**
	 * A convenience method to get a {@link WidgetTester} that is appropriate
	 * for the specified {@link Widget} {@link Class} for our {@link Display}.
	 * 
	 * @param widgetClass
	 *            the {@link Widget} {@link Class}
	 * @return a {@link WidgetTester}
	 */
	protected WidgetTester getTester(Class<?> widgetClass) {
		return WidgetTester.getTester(widgetClass);
	}

	protected WidgetFinder getFinder() {
		return WidgetFinderImpl.getDefault();
	}

	public static void assertOnUIThread() {
		assertNotNull(Display.getCurrent());
	}

	public static void assertNotOnUIThread() {
		assertNull(Display.getCurrent());
	}

	/**
	 * Sleeps the calling {@link Thread} for a specified number of milliseconds.
	 * 
	 * @return <code>true</code> if not interrupted, <code>false</code>
	 *         otherwise
	 * @param time
	 *            the number of milliseconds to sleep
	 */
	public static boolean sleep(long time) {
		assertNotOnUIThread();
		try {
			Thread.sleep(time);
			return true;
		} catch (InterruptedException e) {
			// Empty block intended.
		}
		return false;
	}

	public void syncExec(Runnable runnable) {
		assertNotOnUIThread();
		Displays.syncExec(getDisplay(), runnable);
	}

	public <T> T syncExec(Result<T> result) {
		assertNotOnUIThread();
		return Displays.syncExec(getDisplay(), result);
	}

	public int syncExec(IntResult result) {
		assertNotOnUIThread();
		return Displays.syncExec(getDisplay(), result);
	}

	public boolean syncExec(BooleanResult result) {
		assertNotOnUIThread();
		return Displays.syncExec(getDisplay(), result);
	}

	public String syncExec(StringResult result) {
		assertNotOnUIThread();
		return Displays.syncExec(getDisplay(), result);
	}

	protected void dumpWidgets() {
		System.err.printf("\n\nWidget hierarchy:\n");
		WidgetHierarchy hierarchy = new WidgetHierarchyImpl(getDisplay());
		final WidgetHierarchyPrinter printer = new WidgetHierarchyPrinter(
				hierarchy, System.err);
		printer.print();
	}

	protected void dumpStacks() {
		System.err.printf("\n\nThreads:\n");
		for (Map.Entry<Thread, StackTraceElement[]> entry : Thread
				.getAllStackTraces().entrySet()) {
			Thread thread = entry.getKey();
			StackTraceElement[] stack = entry.getValue();
			System.err.printf("\n%s:", thread);
			for (int i = 0; i < stack.length; i++)
				System.err.printf("\n\t" + stack[i]);
		}
	}

}
