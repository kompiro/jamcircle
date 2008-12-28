package abbot.swt.tester;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.Robot;
import abbot.swt.WaitTimedOutError;
import abbot.swt.WidgetLocator;
import abbot.swt.finder.WidgetHierarchy;
import abbot.swt.finder.WidgetHierarchyImpl;
import abbot.swt.hierarchy.Visitable.Visitor;
import abbot.swt.script.Condition;
import abbot.swt.script.WidgetReference;
import abbot.swt.utilities.ImageComparator;
import abbot.swt.utilities.Wait;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * {@link WidgetTester} is the root of the SWT {@link Widget} tester hierarchy. A
 * {@link WidgetTester} primarily contains three types of methods:
 * <ol>
 * <li><code>action*</code> methods are for executing a particular action on a {@link Widget}
 * (from any thread).</li>
 * <li><code>get*</code> methods are for obtaining information from a {@link Widget}.</li>
 * <li><code>assert*</code> methods are for making assertions about a {@link Widget}'s state.</li>
 * </ol>
 * 
 * @author Kevin Dale
 * @author Gary Johnston
 */
public class WidgetTester extends AbstractTester {

	/*
	 * TODO: Need to decide how to support extensions.
	 */
	/*
	 * TODO: Get rid of waitForIdle() calls here. They should be done by the Robot if they're
	 * needed. But for now we continue to include them where they've been before.
	 */

	/**
	 * Factory interface.
	 */
	public interface Factory {

		void addPackage(String packageName, ClassLoader classLoader);

		void removePackage(String packageName);

		WidgetTester getTester(Class widgetClass);

		WidgetTester getTester(Widget widget);

		void setTester(Class widgetClass, WidgetTester tester);
	}

	/**
	 * Our default Factory.
	 */
	private static final Factory Factory;
	static {
		Factory = new WidgetTesterFactory(abbot.swt.Robot.getDefault());
		Factory.addPackage(WidgetTester.class.getPackage().getName(), WidgetTester.class
				.getClassLoader());
	}

	/**
	 * Gets the current {@link Factory}.
	 * 
	 * @return the current {@link Factory}
	 */
	public static Factory getFactory() {
		return Factory;
	}

	/**
	 * Establish the given WidgetTester as the one to use for the given class. This may be used to
	 * override the default tester for a given core class. Note that this will only work with
	 * widgets loaded by the framework class loader, not those loaded by the class loader for the
	 * code under test.
	 */
	public static void setTester(Class<? extends Widget> widgetClass, WidgetTester tester) {
		Factory.setTester(widgetClass, tester);
	}

	/**
	 * Factory method (generic). Gets a tester for the specified {@link Widget}.
	 */
	public static WidgetTester getTester(Widget widget) {
		return Factory.getTester(widget);
	}

	/**
	 * Factory method (generic). Gets a tester for the specified {@link Widget} class.
	 */
	public static WidgetTester getTester(Class widgetClass) {
		return Factory.getTester(widgetClass);
	}

	/**
	 * Factory method. Gets a {@link WidgetTester}.
	 */
	public static WidgetTester getWidgetTester() {
		return getTester(Widget.class);
	}

	/**
	 * Constructs a new {@link WidgetTester} on a specified {@link Robot}.
	 * <p>
	 * <b>Note:</b> You should probably not use this constructor in your tests. Instead, use the
	 * factory methods {@link #getTester(Class)} et al. because they do caching for you.
	 * 
	 * @param robot
	 *            the {@link Robot} to be associated with the new {@link WidgetTester}
	 */
	public WidgetTester(Robot robot) {
		super(robot);
	}

	private WidgetHierarchy hierarchy;

	/**
	 * Convenience method to get a {@link WidgetHierarchy} (for our {@link Display}). In general,
	 * {@link WidgetTester}s shouldn't need to use {@link WidgetHierarchy}s, but a few do.
	 */
	protected synchronized WidgetHierarchy getHierarchy() {
		if (hierarchy == null)
			hierarchy = new WidgetHierarchyImpl(getDisplay());
		return hierarchy;
	}

	/**
	 * Return the {@link Widget} class that corresponds to this {@link WidgetTester} class.
	 */
	public Class getTestedClass(Class widgetClass) {
		while (getTester(widgetClass.getSuperclass()) == this) {
			widgetClass = widgetClass.getSuperclass();
		}
		return widgetClass;
	}

	/*
	 * Proxies.
	 */

	/**
	 * See {@link Widget#addDisposeListener(DisposeListener)}.
	 */
	public void addDisposeListener(final Widget widget, final DisposeListener disposeListener) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.addDisposeListener(disposeListener);
			}
		});
	}

	/**
	 * See {@link Widget#removeDisposeListener(DisposeListener)}.
	 */
	public void removeDisposeListener(final Widget widget, final DisposeListener disposeListener) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.removeDisposeListener(disposeListener);
			}
		});
	}

	/**
	 * See {@link Widget#addListener(int, Listener)}.
	 */
	public void addListener(final Widget widget, final int eventType, final Listener listener) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.addListener(eventType, listener);
			}
		});
	}

	/**
	 * See {@link Widget#removeListener(int, Listener)}.
	 */
	public void removeListener(final Widget widget, final int eventType, final Listener listener) {
		syncExec(new Runnable() {
			public void run() {
				widget.removeListener(eventType, listener);
			}
		});
	}

	/**
	 * See {@link Widget#isListening(int)}.
	 */
	public boolean isListening(final Widget widget, final int eventType) {
		checkWidget(widget);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return widget.isListening(eventType);
			}
		});
	}

	/**
	 * @See {@link Widget#notifyListeners(int, Event)}.
	 */
	public void notifyListeners(final Widget widget, final int eventType, final Event event) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.notifyListeners(eventType, event);
			}
		});
	}

	/**
	 * @see Widget#dispose()
	 */
	public void dispose(final Widget widget) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.dispose();
			}
		});
	}

	/**
	 * See {@link Widget#isDisposed()}.
	 */
	public boolean isDisposed(Widget widget) {
		checkWidget(widget);
		return widget.isDisposed();
	}

	/**
	 * See {@link Widget#getData()}.
	 */
	public Object getData(final Widget widget) {
		checkWidget(widget);
		return syncExec(new Result() {
			public Object result() {
				return widget.getData();
			}
		});
	}

	/**
	 * See {@link Widget#getData(String)}.
	 */
	public Object getData(final Widget widget, final String key) {
		checkWidget(widget);
		return syncExec(new Result() {
			public Object result() {
				return widget.getData(key);
			}
		});
	}

	/**
	 * Proxy for {@link Widget#setData(java.lang.Object)}. <p/>
	 * 
	 * @param widget
	 *            the Widget whichs data should be set.
	 * @param data
	 *            the data to set.
	 */
	public void setData(final Widget widget, final Object data) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.setData(data);
			}
		});
	}

	/**
	 * Proxy for {@link Widget#setData(java.lang.String, java.lang.Object)}. <p/>
	 * 
	 * @param widget
	 *            the widget whichs data to set.
	 * @param key
	 *            the key under shich the data should be stored.
	 * @param data
	 *            the data to store.
	 */
	public void setData(final Widget widget, final String key, final Object data) {
		checkWidget(widget);
		syncExec(new Runnable() {
			public void run() {
				widget.setData(key, data);
			}
		});
	}

	/**
	 * Proxy for {@link Widget#getDisplay()}.
	 */
	public Display getDisplay(final Widget widget) {
		checkWidget(widget);
		return widget.getDisplay();
	}

	/**
	 * See {@link Widget#getStyle()}.
	 */
	public int getStyle(final Widget widget) {
		checkWidget(widget);
		return syncExec(new IntResult() {
			public int result() {
				return widget.getStyle();
			}
		});
	}

	/**
	 * See {@link Widget#toString()}.
	 */
	public String toString(final Widget widget) {
		checkWidget(widget);
		return syncExec(new StringResult() {
			public String result() {
				return widget.toString();
			}
		});
	}

	/* Actions */

	/* Actions for invoking pop-up menus. */

	/**
	 * Clicks a menu item in a {@link Widget}'s context (pop-up) menu.
	 */
	public void actionClickMenuItem(Widget widget, ItemPath menuPath) {
		checkWidget(widget);
		clickMenuItem(widget, menuPath);
	}

	/**
	 * Clicks a menu item in a {@link Widget}'s context (pop-up) menu.
	 */
	public void actionClickMenuItem(Widget widget, String menuPath) {
		actionClickMenuItem(widget, new ItemPath(menuPath));
	}

	/**
	 * Clicks a menu item in a {@link Widget}'s context (pop-up) menu.
	 */
	public void actionClickMenuItem(Widget widget, String menuPath, String delimiter) {
		actionClickMenuItem(widget, new ItemPath(menuPath, delimiter));
	}

	void clickMenuItem(Widget widget, ItemPath menuPath) {

		// Get the pop-up Menu.
		// We get a specialized tester for the actual class of the widget in
		// order to
		// invoke the appropriate getMenu(Widget) implementation.
		WidgetTester tester = getTester(widget);
		Menu menu = tester.getMenu(widget);
		if (menu == null)
			throw new ActionFailedException("no menu");

		// Bring up the pop-up Menu and wait for it to become visible.
		MenuTester menuTester = MenuTester.getMenuTester();
		if (!menuTester.isVisible(menu)) {
			click(widget, BUTTON_POPUP);
			menuTester.waitVisible(menu);
		}

		// Find and click the MenuItem speified by the menuPath.
		menuTester.clickItem(menu, menuPath);

	}

	/**
	 * @deprecated Use {@link MenuTester#actionClickItem(Menu, String)} or similar.
	 */
	public void actionSelectMenuItemByText(Menu menu, String text) {
		checkWidget(menu);
		selectMenuItemByText(menu, text);
	}

	/**
	 * Select the given menu item.
	 * 
	 * @deprecated Use {@link MenuTester#actionClickMenuItem(Widget, ItemPath)} or similar.
	 */
	public void actionSelectMenuItem(MenuItem item) {
		checkWidget(item);

		MenuItemTester menuItemTester = MenuItemTester.getMenuItemTester();
		MenuTester menuTester = MenuTester.getMenuTester();

		Menu menu = menuItemTester.getRootMenu(item);
		ItemPath path = menuItemTester.getPath(item);
		if ((menuTester.getStyle(menu) & SWT.POP_UP) != 0) {
			Control control = menuTester.getParent(menu);
			actionClickMenuItem(control, path);
		} else {
			menuTester.clickItem(menu, path);
		}
	}

	/**
	 * Open the item's popup menu at the given coordinates of its parent control, and select the
	 * given item.
	 * 
	 * @deprecated Use {@link #actionClickMenuItem(Widget, ItemPath)} or similar. @
	 */
	public void actionSelectPopupMenuItem(MenuItem item, int x, int y) {
		checkWidget(item);

		// Get the root Menu.
		MenuItemTester itemTester = MenuItemTester.getMenuItemTester();
		Menu menu = itemTester.getRootMenu(item);
		if (menu == null)
			throw new ActionFailedException("no menu");

		// Bring up the pop-up Menu and wait for it to become visible.
		MenuTester menuTester = MenuTester.getMenuTester();
		Shell parent = (Shell) menuTester.getParent(menu);
		if (!menuTester.isVisible(menu)) {
			click(parent, x, y, BUTTON_POPUP);
			menuTester.waitVisible(menu);
		}

		// Find and click the MenuItem speified by the menuPath.
		menuTester.clickItem(menu, itemTester.getPath(item));
	}

	/**
	 * Subclasses that have a pop-up menu should redefine this method. This default implementation
	 * simply returns <code>null</code>.
	 * 
	 * @see #clickMenuItem(Widget, ItemPath)
	 * @see ControlTester#getMenu(Widget)
	 * @see TabItemTester#getMenu(Widget)
	 * @see TableItemTester#getMenu(Widget)
	 * @see ToolItemTester#getMenu(Widget)
	 * @see TreeColumnTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		return null;
	}

	/* Proxies */

	/**
	 * @return <code>true</code> if the {@link Widget} is visible (showing), <code>false</code>
	 *         otherwise
	 * @throws UnsupportedOperationException -
	 *             Subclasses that can be visible must redefine this method.
	 */
	public boolean isVisible(Widget widget) {
		throw new UnsupportedOperationException();
	}

	public void waitVisible(final Widget widget) {
		waitVisible(widget, 60000);
	}

	public void waitVisible(final Widget widget, final long timeout) {
		checkWidget(widget);
		checkTime(timeout);
		Wait.wait(new Condition() {
			public boolean test() {
				return isVisible(widget);
			}

			public String toString() {
				return String.format("widget visible: %s", WidgetTester.this.toString(widget));
			}
		}, timeout);
	}

	/**
	 * Wait for the specified Widget to be disposed. Times out after the default timeout.
	 * 
	 * @param widget
	 *            the Widget
	 * @see AbstractTester#wait(Condition)
	 */
	public void waitForDispose(final Widget widget) throws WaitTimedOutError {
		checkWidget(widget);
		wait(new Condition() {
			public boolean test() {
				return widget.isDisposed();
			}
		});
	}

	/**
	 * Wait for the specified Widget to be disposed. Times out after the specified timeout
	 * (milliseconds).
	 * 
	 * @param widget
	 *            the Widget
	 * @param timeout
	 *            the timeout in milliseconds
	 * @see AbstractTester#wait(Condition, long)
	 */
	public void waitForDispose(final Widget widget, long timeout) throws WaitTimedOutError {
		checkWidget(widget);
		wait(new Condition() {
			public boolean test() {
				return widget.isDisposed();
			}
		}, timeout);
	}

	public interface Textable {

		String getText(Widget widget);

		boolean isTextEditable(Widget widget);
	}

	/**
	 * TODO Is this the right place for this? Should there instead be a static
	 * getTextableTester(Widget) factory method?
	 */
	public String getWidgetText(final Widget widget) {
		WidgetTester tester = getTester(widget);
		if (tester instanceof Textable)
			return ((Textable) tester).getText(widget);
		return null;
	}

	/**
	 * Get the location of the widget in global screen coordinates
	 */
	public Point getGlobalLocation(Widget widget) {
		return getGlobalLocation(widget, true);
	}

	/**
	 * Get the location of the widget in global screen coordinates, optionally ignoring the
	 * 'trimmings'.
	 */
	public Point getGlobalLocation(final Widget widget, final boolean ignoreBorder) {
		checkWidget(widget);
		return (Point) syncExec(new Result() {
			public Object result() {
				return WidgetLocator.getLocation(widget, ignoreBorder);
			}
		});
	}

	/**
	 * Get the bounding rectangle for the given Widget in global screen coordinates.
	 */
	public Rectangle getGlobalBounds(Widget widget) {
		return getGlobalBounds(widget, true);
	}

	/**
	 * Get the bounding rectangle for the given Widget in global screen coordinates, optionally
	 * ignoring the 'trimmings'.
	 */
	public Rectangle getGlobalBounds(final Widget widget, final boolean ignoreBorder) {
		checkWidget(widget);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return WidgetLocator.getBounds(widget, ignoreBorder);
			}
		});
	}

	/**
	 * Derive a tag from the given accessible context if possible, or return null.
	 */
	protected String deriveAccessibleTag(AccessibleContext context) {
		String tag = deriveRawAccessibleTag(context);
		if (tag != null) {
			tag = tag.substring(tag.lastIndexOf("/") + 1);
			tag = tag.substring(tag.lastIndexOf("\\") + 1);
			return tag;
		}
		return null;
	}

	private String deriveRawAccessibleTag(AccessibleContext context) {
		if (context != null) {
			String name = context.getAccessibleName();
			if (name != null && name.length() > 0)
				return name;
			AccessibleIcon[] icons = context.getAccessibleIcon();
			if (icons != null && icons.length > 0) {
				String description = icons[0].getAccessibleIconDescription();
				if (description != null && description.length() > 0)
					return description;
			}
		}
		return null;
	}

	/** Return a reasonable identifier for the given widget. */
	public static String getTag(Widget widget) {
		return getTester(widget.getClass()).deriveTag(widget);
	}

	/**
	 * Provide a String that is fairly distinct for the given widget. For a generic widget, attempt
	 * to look up some common patterns such as a title or label. Derived classes should absolutely
	 * override this method if such a String exists.
	 * <p>
	 * Don't use widget names as tags.
	 * <p>
	 */
	public String deriveTag(Widget widget) {
		checkWidget(widget);

		// Try text.
		WidgetTester tester = getTester(widget);
		if (tester instanceof Textable) {
			Textable textable = (Textable) tester;
			if (!textable.isTextEditable(widget)) {
				String text = textable.getText(widget);
				if (text != null && text.length() > 0)
					return text;
			}
		}

		// Try data ("name").
		Object object = tester.getData(widget, "name");
		if (object instanceof String) {
			String name = (String) object;
			if (name.length() > 0)
				return name;
		}

		// In the absence of any other tag, try to derive one from something
		// recognizable on one of its ancestors.
		Widget parent = getHierarchy().getParent(widget);
		if (parent != null) {
			String parentTag = getTag(parent);
			if (parentTag != null && parentTag.length() > 0) {
				// Don't use the tag if it's simply the window title; that
				// doesn't provide any extra information.
				if (!parentTag.equals(getWidgetText(parent))) {
					StringBuffer buffer = new StringBuffer(parentTag);
					String under = " under ";
					int underIndex = parentTag.indexOf(under);
					if (underIndex != -1)
						buffer = buffer.delete(0, underIndex + under.length());
					buffer.insert(0, under);
					buffer.insert(0, simpleClassName(widget.getClass()));
					return buffer.toString();
				}
			}
		}

		return null;
	}

	/**
	 * Wait for an idle AWT event queue. Will return when there are no more events on the event
	 * queue.
	 */
	public void actionWaitForIdle() {
		waitForIdle();
	}

	/** Delay the given number of ms. */
	public void actionDelay(final long time) {
		checkThread();
		checkTime(time);

		syncExec(new Runnable() {
			public void run() {
				delay(time);
			}
		});
	}

	/** Click on the center of the widget. */
	public void actionClick(Widget widget) {
		checkWidget(widget);
		click(widget);
		if (!widget.isDisposed())
			waitForIdle();
	}

	/** Click on a widget. */
	public void actionClick(Widget widget, int mask) {
		checkWidget(widget);
		click(widget, mask);
		if (!widget.isDisposed())
			waitForIdle();
	}

	/** Click on the widget at the given location. */
	public void actionClick(Widget widget, int x, int y) {
		checkWidget(widget);
		click(widget, x, y, SWT.BUTTON1);
		if (!widget.isDisposed())
			waitForIdle();
	}

	/** Click on the widget at the given location. */
	public void actionClick(Widget widget, int x, int y, int mask) {
		checkWidget(widget);
		click(widget, x, y, mask);
		if (!widget.isDisposed())
			waitForIdle();
	}

	/** Click on the widget at the given location. */
	public void actionClick(Widget widget, int x, int y, int mask, int count) {
		checkWidget(widget);
		click(widget, x, y, mask, count);
		if (!widget.isDisposed())
			waitForIdle();
	}

	/**
	 * Click on the widget at the given location. The buttons string should be the
	 * org.eclipse.swt.SWT field name for the mask.
	 */
	public void actionClick(Widget widget, int x, int y, String buttons) {
		checkWidget(widget);
		click(widget, x, y, getModifiers(buttons));
		if (!widget.isDisposed())
			waitForIdle();
	}

	/**
	 * Click on the widget at the given location. The buttons string should be the
	 * org.eclipse.swt.SWT field name for the mask. This variation provides for multiple clicks.
	 */
	public void actionClick(Widget widget, int x, int y, String buttons, int count) {
		checkWidget(widget);
		click(widget, x, y, getModifiers(buttons), count);
		waitForIdle();
	}

	public void actionDoubleClick(Widget widget) {
		checkWidget(widget);
		doubleClick(widget);
	}

	public void actionDoubleClick(Widget widget, int mask) {
		checkWidget(widget);
		doubleClick(widget, mask);
	}

	/**
	 * Press, and release, the keys contained in the given accelerator
	 */
	public void actionKey(int accelerator) {
		key(accelerator);
		waitForIdle();
	}

	/**
	 * Type the given character. Note that this sends the key to whatever component currently has
	 * the focus.
	 */
	public void actionKeyChar(char c) {
		key((int) c);
		waitForIdle();
	}

	/**
	 * Type the given string.
	 */
	public void actionKeyString(String string) {
		key(string);
		waitForIdle();
	}

	/**
	 * Set the focus on to the given component.
	 * <p>
	 * TODO MAY NEED TO CHECK THAT THE CONTROL DOES INDEED HAVE FOCUS
	 */
	public void actionFocus(Widget widget) {
		checkWidget(widget);
		while (!(widget instanceof Control))
			widget = getHierarchy().getParent(widget);
		focus((Control) widget);
		waitForIdle();
	}

	public void actionDragDrop(int sx, int sy, int tx, int ty, int buttons) {
		checkThread();
		dragDrop(sx, sy, tx, ty, buttons);
	}

	public void actionDragDrop(Widget source, int tx, int ty, int buttons) {
		checkThread();
		checkWidget(source);
		dragDrop(source, tx, ty, buttons);
	}

	public void actionDragDrop(Widget source, Widget target, int buttons) {
		checkThread();
		checkWidget(source);
		checkWidget(target);
		dragDrop(source, target, buttons);
	}

	/** Return whether the widget's contents matches the given image. */
	public boolean assertImage(Widget widget, Image image, boolean ignoreBorder) {
		checkWidget(widget);
		Image widgetImage = capture(widget, ignoreBorder);
		try {
			return new ImageComparator().compare(image, widgetImage) == 0;
		} finally {
			widgetImage.dispose();
		}
	}

	/**
	 * Returns whether Decorations corresponding to the given String is showing. The string may be a
	 * plain String or regular expression and may match either the Decoration's title or name
	 */
	public boolean assertDecorationsShowing(String title) {
		return assertDecorationsShowing(title, true);
	}

	// public boolean assertDecorationsShowing(String title, boolean topOnly) {
	// int status = STATUS_NOT_FOUND;
	// if (topOnly) {
	// Collection shells = Displays.getShells(getDisplay());
	// status = getShellStatus(shells, title);
	// } else {
	// status = getShellStatus(title);
	// }
	// switch (status) {
	// case STATUS_VISIBLE:
	// return true;
	// case STATUS_NOT_VISIBLE:
	// case STATUS_NOT_FOUND:
	// return false;
	// default:
	// error("invalid status: " + status);
	// }
	// error("unreachable");
	// return false;
	// }

	public boolean assertDecorationsShowing(String title, boolean topOnly) {
		int status = getShellStatus(title, topOnly);
		switch (status) {
			case STATUS_VISIBLE:
				return true;
			case STATUS_NOT_VISIBLE:
			case STATUS_NOT_FOUND:
				return false;
			default:
				throw new RuntimeException("invalid status: " + status);
		}
	}

	private static final int STATUS_NOT_FOUND = 0;

	private static final int STATUS_NOT_VISIBLE = 1;

	private static final int STATUS_VISIBLE = 2;

	private int getShellStatus(final String title, final boolean topOnly) {
		final int[] status = new int[] { STATUS_NOT_FOUND };
		syncExec(new Runnable() {
			public void run() {
				getHierarchy().accept(new Visitor<Widget>() {
					public Result visit(Widget widget) {
						if (!widget.isDisposed()) {
							if (widget instanceof Shell) {
								Shell shell = (Shell) widget;
								if (shell.getText().equals(title)) {
									status[0] = shell.isVisible() ? STATUS_VISIBLE
											: STATUS_NOT_VISIBLE;
									return Result.stop;
								}
								if (topOnly)
									return Result.prune;
							}
							return Result.ok;
						}
						return Result.prune;
					}
				});
			}
		});
		return status[0];
	}

	// private int getShellStatus(final String title) {
	//
	// return syncExec(new IntResult() {
	//
	// public int result() {
	// Collection roots = getHierarchy().getRoots();
	// return getStatus(roots);
	// }
	//
	// private int getStatus(Collection widgets) {
	//
	// // Check the widgets themselves first (i.e., breadth-first search).
	// for (Iterator iterator = widgets.iterator(); iterator.hasNext();) {
	// Widget widget = (Widget) iterator.next();
	// if (!widget.isDisposed() && widget instanceof Shell) {
	// Shell shell = (Shell) widget;
	// if (shell.getText().equals(title))
	// return shell.isVisible() ? STATUS_VISIBLE : STATUS_NOT_VISIBLE;
	// }
	// }
	//
	// // Check the widgets' children.
	// Hierarchy hierarchy = getHierarchy();
	// for (Iterator iterator = widgets.iterator(); iterator.hasNext();) {
	// Widget widget = (Widget) iterator.next();
	// if (!widget.isDisposed()) {
	// Collection children = hierarchy.getWidgets(widget);
	// int status = getStatus(children);
	// if (status != STATUS_NOT_FOUND)
	// return status;
	// }
	// }
	//
	// return STATUS_NOT_FOUND;
	// }
	// });
	// }

	// private int getShellStatus(final Collection shells, final String title) {
	// checkWidgets(shells);
	// if (!shells.isEmpty()) {
	// return syncExec(new IntResult() {
	// public int result() {
	// for (Iterator iterator = shells.iterator(); iterator.hasNext();) {
	// Shell shell = (Shell) iterator.next();
	// if (!shell.isDisposed() && shell.getText().equals(title))
	// return shell.isVisible() ? STATUS_VISIBLE : STATUS_NOT_VISIBLE;
	// }
	// return STATUS_NOT_FOUND;
	// }
	// });
	// }
	// return STATUS_NOT_FOUND;
	// }

	/**
	 * <b>Note:</b> This method ignores the value of <code>topOnly</code> and always behaves as
	 * if it were <code>false</code>.
	 * 
	 * @deprecated Use {@link ShellTester#waitVisible(String, Shell, long)}.
	 */
	public static void waitForShellShowing(final String title, final boolean topOnly,
			final long delay) {
		ShellTester.waitVisible(title, null, delay);
	}

	/**
	 * @deprecated Use {@link ShellTester#waitVisible(String, Shell, long)}.
	 */
	public static void waitForShellShowing(final String title, final long delay) {
		ShellTester.waitVisible(title, null, delay);
	}

	/**
	 * <b>Note:</b> This method ignores the value of <code>topOnly</code> and always behaves as
	 * if it were <code>false</code>.
	 * 
	 * @deprecated Use {@link ShellTester#waitVisible(String, Shell)}.
	 */
	public static void waitForShellShowing(final String title, final boolean topOnly) {
		ShellTester.waitVisible(title, null);
	}

	/**
	 * @deprecated Use {@link ShellTester#waitVisible(String, Shell)}.
	 */
	public static void waitForShellShowing(final String title) {
		ShellTester.waitVisible(title, null);
	}

	/**
	 * Return whether the Widget represented by the given WidgetReference is available.
	 */
	public boolean assertWidgetShowing(WidgetReference reference) {
		try {
			reference.getWidget();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Wait for the Widget represented by the given WidgetReference to become available. The timeout
	 * is affected by abbot.robot.component_delay, which defaults to 30s.
	 * 
	 * @deprecated Use the new matcher API in abbot.finder.swt.
	 */
	public static void waitForWidgetShowing(final WidgetReference ref) {
		getWidgetTester().wait(new Condition() {
			public boolean test() {
				return getTester(Widget.class).assertWidgetShowing(ref);
			}

			public String toString() {
				return ref + " to show";
			}
		}, DEFAULT_WIDGET_TIMEOUT);
	}

	/**
	 * Gets the {@link Point} at the center of a {@link Rectangle}.
	 * 
	 * @param rectangle
	 *            a Rectangle
	 * @return the {@link Point} at the center
	 */
	protected static Point getCenter(Rectangle rectangle) {
		return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
	}

	/*
	 * Cached method support. Currently unused. Would be used by ScriptEditor if we had one. Some
	 * day we may, so don't delete this code. It's harmless because it doesn't really do anything
	 * unless it's called, which it isn't. Currently.
	 */

	/*
	 * TODO No need to have a cache for each WidgetTester instance. A cache per class would suffice.
	 */

	private Method[] cachedMethods = null;

	/** Look up methods with the given prefix. */
	private synchronized Method[] getMethods(String prefix, Class returnType, boolean onWidget) {

		if (cachedMethods == null)
			cachedMethods = getClass().getMethods();

		List methods = new ArrayList();
		Set names = new HashSet();
		for (int i = 0; i < cachedMethods.length; i++) {
			String name = cachedMethods[i].getName();
			if (!names.contains(name)) {
				Class[] parameterTypes = cachedMethods[i].getParameterTypes();
				if (name.startsWith(prefix)
						&& (returnType == null || returnType.equals(cachedMethods[i]
								.getReturnType()))
						&& ((parameterTypes.length == 0 && !onWidget) || (parameterTypes.length > 0 && (Widget.class
								.isAssignableFrom(parameterTypes[0]) == onWidget)))) {
					methods.add(cachedMethods[i]);
					names.add(name);
				}
			}
		}
		return (Method[]) methods.toArray(new Method[methods.size()]);
	}

	private Method[] cachedActions = null;

	/**
	 * Return a list of all actions defined by this class that don't depend on a widget argument.
	 */
	public synchronized Method[] getActions() {
		if (cachedActions == null)
			cachedActions = getMethods("action", void.class, false);
		return cachedActions;
	}

	private Method[] cachedWidgetActions = null;

	/**
	 * Return a list of all actions defined by this class that require a widget argument.
	 */
	public synchronized Method[] getWidgetActions() {
		if (cachedWidgetActions == null)
			cachedWidgetActions = getMethods("action", void.class, true);
		return cachedWidgetActions;
	}

	private Method[] cachedPropertyMethods = null;

	/**
	 * Return an array of all property check methods defined by this class. The first argument
	 * <b>must</b> be a Widget.
	 */
	public synchronized Method[] getPropertyMethods() {
		if (cachedPropertyMethods == null) {
			List methods = new ArrayList();
			Collections.addAll(methods, (Object[]) getMethods("is", boolean.class, true));
			Collections.addAll(methods, (Object[]) getMethods("get", null, true));
			// Remove getXXX or isXXX methods which aren't property checks
			Class[] parameterTypes = { Widget.class };
			try {
				methods.remove(getClass().getMethod("getTag", parameterTypes));
				methods.remove(getClass().getMethod("getTester", parameterTypes));
				methods.remove(getClass().getMethod("isOnPopup", parameterTypes));
			} catch (NoSuchMethodException e) {
			}
			cachedPropertyMethods = (Method[]) methods.toArray(new Method[methods.size()]);
		}
		return cachedPropertyMethods;
	}

	private Method[] cachedAssertMethods = null;

	/**
	 * Return a list of all assertions defined by this class that don't depend on a widget argument.
	 */
	public synchronized Method[] getAssertMethods() {
		if (cachedAssertMethods == null)
			cachedAssertMethods = getMethods("assert", boolean.class, false);
		return cachedAssertMethods;
	}

	private Method[] cachedWidgetAssertMethods = null;

	/**
	 * Return a list of all assertions defined by this class that require a widget argument.
	 */
	public synchronized Method[] getComponentAssertMethods() {
		if (cachedWidgetAssertMethods == null)
			cachedWidgetAssertMethods = getMethods("assert", boolean.class, true);
		return cachedWidgetAssertMethods;
	}

	/**
	 * Quick and dirty strip raw text from html, for getting the basic text from html-formatted
	 * labels and buttons. Behavior is undefined for badly formatted html.
	 */
	public static String stripHTML(String str) {
		if (str != null && (str.startsWith("<html>") || str.startsWith("<HTML>"))) {
			while (str.startsWith("<")) {
				int right = str.indexOf(">");
				if (right == -1)
					break;
				str = str.substring(right + 1);
			}
			while (str.endsWith(">")) {
				int right = str.lastIndexOf("<");
				if (right == -1)
					break;
				str = str.substring(0, right);
			}
		}
		return str;
	}
}
