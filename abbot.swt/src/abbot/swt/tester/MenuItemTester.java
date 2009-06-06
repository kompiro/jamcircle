package abbot.swt.tester;

import java.util.Iterator;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SWTWorkarounds;
import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.WidgetHierarchyImpl;
import abbot.swt.finder.generic.MultipleFoundException;
import abbot.swt.finder.generic.NotFoundException;
import abbot.swt.finder.matchers.NameMatcher;
import abbot.swt.finder.matchers.WidgetClassMatcher;
import abbot.swt.finder.matchers.WidgetMatcher;
import abbot.swt.finder.matchers.WidgetTextMatcher;
import abbot.swt.hierarchy.Visitable.Visitor;
import abbot.swt.script.Condition;
import abbot.swt.utilities.ExtendedComparator;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link MenuItem}s.
 */
public class MenuItemTester extends ItemTester {

	public static final String ARM_LISTENER_NAME = "a4sArmListener";

	public static final String SELECTION_LISTENER_NAME = "a4sSelectionListener";

	public static final String WATCHER_NAME = "a4sWatcher";

	public static final int PATH_CLICKING_WAIT_TIME = 500000;

	public static final String DEFAULT_MENUITEM_PATH_DELIMITER = "/";

	/**
	 * Factory method.
	 */
	public static MenuItemTester getMenuItemTester() {
		return (MenuItemTester) getTester(MenuItem.class);
	}

	/**
	 * Constructs a new {@link MenuItemTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public MenuItemTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	public void actionClickItem(MenuItem item, ItemPath path) {
		checkWidget(item);
		clickItem(item, path);
	}

	public void actionClickItem(MenuItem item, String path) {
		actionClickItem(item, new ItemPath(path));
	}

	public void actionClickItem(MenuItem item, String path, String delimiter) {
		actionClickItem(item, new ItemPath(path, delimiter));
	}

	/**
	 * Clicks on a child of a {@link MenuItem} given an {@link ItemPath}.
	 * 
	 * @param item
	 *            the {@link MenuItem}
	 * @param path
	 *            the path to the desired {@link MenuItem}
	 */
	void clickItem(MenuItem item, ItemPath path) {
		clickItem(item, path.iterator());
	}

	private void clickItem(MenuItem item, Iterator<String> iterator) {

//		System.err.printf("clicking %s\n", getText(item));

		// Register an arm listener (only on some platforms).
		final ItemArmListener listener = getArmListener(item);
		if (listener != null)
			addArmListener(item, listener);

		try {

			// Move to the point on the menu item at which we'll click.
			final Point clickpoint = getGlobalClickPoint(item);
			mouseMove(clickpoint.x, clickpoint.y);
//			mouseMove(clickpoint.x+1, clickpoint.y);
//			mouseMove(clickpoint.x, clickpoint.y+1);
//			mouseMove(clickpoint.x, clickpoint.y);

			// Wait for the arm event (some platforms).
			// Wiggle the mouse a bit between intervals because some platforms
			// will not arm a menu item until more than one mouse move event
			// has been seen.
			if (listener != null) {
				wait(new Condition() {
					public boolean test() {
						if (!listener.isArmed()) {
							mouseMove(clickpoint.x - 1, clickpoint.y);
							mouseMove(clickpoint.x, clickpoint.y);
						}
						return listener.isArmed();
					}
				}, 5000L, 250L);
			}

			// Click on the menu item.
			click();

		} finally {
			if (listener != null)
				removeArmListener(item, listener);
		}

		if (iterator.hasNext()) {
			String text = iterator.next();
			Menu menu = getMenu(item);
			if (menu == null)
				throw new ActionFailedException("no menu");
			MenuTester menuTester = MenuTester.getMenuTester();
			menuTester.waitVisible(menu);
			MenuItem[] items = menuTester.getItems(menu);
			clickItem(findItem(items, text), iterator);
		}
	}

	/*
	 * On Linux/GTK a menu item will not arm unless we post extra
	 * mouse moves, so we have an ArmListener to let us know when the menu item
	 * has become armed.
	 */
	private ItemArmListener getArmListener(MenuItem item) {
		if (SWT.getPlatform().equals("gtk")) {
			Menu parent = getParent(item);
			int parentStyle = MenuTester.getMenuTester().getStyle(parent);
			if ((parentStyle & SWT.BAR) == 0)
				return new ItemArmListener();
		}
		return null;
	}

	private static class ItemArmListener implements ArmListener {

		private boolean armed = false;

		public void widgetArmed(ArmEvent e) {
			setArmed(true);
		}

		public synchronized boolean isArmed() {
			return armed;
		}

		public synchronized void setArmed(boolean armed) {
			this.armed = armed;
		}

	}

	MenuItem findItem(MenuItem[] items, String text) {

		// If the item text we're looking for is not a regular expression and does not
		// contain a tab then make it into a regex that matches the original text followed by
		// a tab plus anything. This makes the text match items that have an accelerator.
		if (!ExtendedComparator.isRegex(text) && text.indexOf('\t') == -1)
			text += "(?:\t.*)?";

		for (MenuItem item : items) {
			if (ExtendedComparator.stringsMatch(text, getText(item)))
				return item;
		}
		throw new ActionItemNotFoundException(text);
	}

	/* Proxies */

	/**
	 * @see MenuItem#addArmListener(ItemArmListener)
	 */
	public void addArmListener(final MenuItem menuItem,
			final ItemArmListener listener) {
		checkWidget(menuItem);
		if (listener == null)
			throw new RuntimeException("listener is null");
		syncExec(new Runnable() {
			public void run() {
				menuItem.addArmListener(listener);
			}
		});
	}

	/**
	 * @see MenuItem#removeArmListener(ItemArmListener)
	 */
	public void removeArmListener(final MenuItem menuItem,
			final ItemArmListener listener) {
		checkWidget(menuItem);
		if (listener == null)
			throw new RuntimeException("listener is null");
		syncExec(new Runnable() {
			public void run() {
				menuItem.removeArmListener(listener);
			}
		});
	}

	/**
	 * @see MenuItem#addSelectionListener(SelectionListener)
	 */
	public void addSelectionListener(final MenuItem menuItem,
			final SelectionListener listener) {
		checkWidget(menuItem);
		if (listener == null)
			throw new RuntimeException("listener is null");
		syncExec(new Runnable() {
			public void run() {
				menuItem.addSelectionListener(listener);
			}
		});
	}

	/**
	 * @see MenuItem#removeSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(final MenuItem menuItem,
			final SelectionListener listener) {
		checkWidget(menuItem);
		if (listener == null)
			throw new RuntimeException("listener is null");
		syncExec(new Runnable() {
			public void run() {
				menuItem.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link MenuItem#getAccelerator()}. <p/>
	 * 
	 * @param menuItem
	 *            the menuItem under test.
	 * @return the accelerator.
	 */
	public int getAccelerator(final MenuItem menuItem) {
		checkWidget(menuItem);
		return syncExec(new IntResult() {
			public int result() {
				return menuItem.getAccelerator();
			}
		});
	}

	/**
	 * Proxy for {@link MenuItem#getEnabled()}. <p/>
	 * 
	 * @param menuItem
	 *            the menuItem under test.
	 * @return the menuItem's enabled state.
	 */
	public boolean getEnabled(final MenuItem menuItem) {
		checkWidget(menuItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return menuItem.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link MenuItem#getSelection()}. <p/>
	 * 
	 * @param menuItem
	 *            the menuItem under test.
	 * @return true if the menuItem is selected.
	 */
	public boolean getSelection(final MenuItem menuItem) {
		checkWidget(menuItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return menuItem.getSelection();
			}
		});
	}

	/**
	 * Note that this method is different than
	 * {@link WidgetTester#getMenu(Widget)}.
	 * 
	 * @see MenuItem#getMenu()
	 */
	public Menu getMenu(final MenuItem menuItem) {
		checkWidget(menuItem);
		return (Menu) syncExec(new Result() {
			public Object result() {
				return menuItem.getMenu();
			}
		});
	}

	/**
	 * Gets the top-most (root) {@link Menu} of the menu hierarchy a
	 * {@link MenuItem} is in.
	 */
	public Menu getRootMenu(MenuItem item) {
		return MenuTester.getMenuTester().getRootMenu(getParent(item));
	}

	/**
	 * Proxy for {@link MenuItem#getParent()}. <p/>
	 * 
	 * @param menuItem
	 *            the menuItem under test.
	 * @return the menuItem's parent.
	 */
	public Menu getParent(final MenuItem menuItem) {
		checkWidget(menuItem);
		return (Menu) syncExec(new Result() {
			public Object result() {
				return menuItem.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link MenuItem#isEnabled()}. <p/>
	 * 
	 * @param menuItem
	 *            the menuItem under test.
	 * @return true if the menuItem is enabled.
	 */
	public boolean isEnabled(final MenuItem menuItem) {
		checkWidget(menuItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return menuItem.isEnabled();
			}
		});
	}

	public boolean isVisible(final MenuItem menuItem) {
		Menu menu = getParent(menuItem);
		return MenuTester.getMenuTester().isVisible(menu);
	}

	public boolean isVisible(Widget widget) {
		if (widget instanceof MenuItem)
			return isVisible((MenuItem) widget);
		return super.isVisible(widget);
	}

	/**
	 * Computes the bounds of the menuItem given. <p/>
	 * 
	 * @param menuItem
	 *            the menuItem under test.
	 * @return the bounds of the menuItem.
	 */
	public Rectangle getBounds(final MenuItem menuItem) {
		checkWidget(menuItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return SWTWorkarounds.getBounds(menuItem);
			}
		});
	}

	/**
	 * Gets the path for the given menu item.
	 * 
	 * @deprecated Use {@link #getPath(Item)}{@link #toString()} or similar.
	 */
	public String getPathString(MenuItem menuItem) {
		checkWidget(menuItem);
		return getPathString(menuItem, DEFAULT_MENUITEM_PATH_DELIMITER);
	}

	/**
	 * Gets the path for the given menu item.
	 * 
	 * @deprecated Use {@link #getPath(Item)}{@link #toString()} or similar.
	 */
	public String getPathString(MenuItem menuItem, String delimiter) {
		checkWidget(menuItem);
		String path = "";
		Menu parent;
		while (menuItem != null) {
			path = getText(menuItem) + delimiter + path;
			parent = getParent(menuItem);
			menuItem = ((MenuTester) getTester(Menu.class))
					.getParentItem(parent);
		}
		path = path.substring(0, path.length() - 1);
		return path;
	}

	/**
	 * @deprecated Use {@link WidgetTester#actionClickMenuItem(Widget, String)}
	 *             or {@link MenuTester#actionClickItem(Menu, String)}.
	 */
	public void actionSelectMenuItem(final String path,
			final Control parentControl, final Decorations parentDecorations,
			final int delay) {
		actionClickMenuItemByPath(path, parentControl, parentDecorations, delay);
	}

	/**
	 * @deprecated Use
	 *             {@link WidgetTester#actionClickMenuItem(Widget, String, String)}
	 *             or {@link MenuTester#actionClickItem(Menu, String)}.
	 */
	public void actionSelectMenuItem(final String path,
			final Control parentControl, final Decorations parentDecorations,
			final int delay, String delimiter) {
		clickMenuItemByPath(new ItemPath(path, delimiter), parentControl,
				parentDecorations, delay);
	}

	/**
	 * Clicks each MenuItem along a specified path. Use '/' as the path
	 * separator.
	 * 
	 * @deprecated Use {@link WidgetTester#actionClickMenuItem(Widget, String)}
	 *             or {@link MenuTester#actionClickItem(Menu, String)}.
	 * @param path
	 *            the path to the menu menuItem
	 * @param parentControl
	 *            the Control that owns the root pop-up menu, or null if not
	 *            popup
	 * @param parentDecorations
	 *            the parent Decorations
	 * @param delay
	 *            time (in ms) to wait between menuItem clicks (ignored)
	 */
	public void actionClickMenuItemByPath(final String path,
			final Control parentControl, final Decorations parentDecorations,
			final int delay) {
		checkWidget(parentControl);
		checkWidget(parentDecorations);
		clickMenuItemByPath(new ItemPath(path), parentControl,
				parentDecorations, delay);
	}

	/**
	 * @deprecated
	 */
	private void clickMenuItemByPath(final ItemPath menuItemPath,
			final Control parentControl, final Decorations parentDecorations,
			final int delay) {
		try {
			WidgetTester.getTester(parentControl).actionClickMenuItem(
					parentControl, menuItemPath);
		} catch (ActionFailedException exception) {
			final boolean[] found = { false };
			final MenuTester menuTester = MenuTester.getMenuTester();
			Visitor<Widget> visitor = new Visitor<Widget>() {
				public Result visit(Widget widget) {
					if (widget instanceof Menu) {
						menuTester.actionClickItem((Menu) widget, menuItemPath);
						found[0] = true;
						return Result.stop;
					}
					return Result.ok;
				}
			};
			WidgetHierarchyImpl.getDefault().accept(parentDecorations, visitor);
			if (!found[0])
				throw new ActionItemNotFoundException(menuItemPath.toString());
		}
	}

	// private void actionClickMenuItemByPathImp(StringTokenizer path, Control
	// parentControl,
	// Decorations parentDecorations, int delay, List watchers, StringBuffer
	// buffer,
	// boolean[] done) {
	// // private void actionClickMenuItemByPathImp(StringTokenizer path,
	// Control parentControl,
	// // Decorations parentDecorations, int delay, List<Watcher> watchers,
	// StringBuffer buffer){
	// // rt-click control if menu is a popup
	// if (parentControl != null) {
	// ControlTester tester = ControlTester.getControlTester();
	// Menu menu = tester.getMenu(parentControl);
	// if (menu != null && (tester.getStyle(menu) & SWT.POP_UP) != 0) {
	// Rectangle bounds = tester.getBounds(parentControl);
	// actionClick(parentControl, bounds.width / 2, bounds.height / 2,
	// SWT.BUTTON2);
	// }
	// }
	//
	// MenuItem lastItem = null;
	// int watcherIndex = 0;
	// try {
	// do {
	// String token = path.nextToken();
	// lastItem = resolveAndClickItem(
	// token,
	// parentControl,
	// parentDecorations,
	// ((lastItem == null) ? (Widget) parentDecorations : (Widget) lastItem),
	// (Watcher) watchers.get(watcherIndex));
	// actionDelay(delay);
	// watcherIndex++;
	// } while (path.hasMoreTokens());
	// } catch (NotFoundException e) {
	// e.printStackTrace();
	// buffer.append("\nNotFoundException:");
	// buffer.append(e.getMessage());
	// int numberOfArmedMenus = getNumberOfArmedEvents(watchers);
	// escapeArmedMenus(numberOfArmedMenus);
	// } catch (MultipleFoundException e) {
	// e.printStackTrace();
	// buffer.append("\nMultipleWidgetsFoundException:");
	// buffer.append(e.getMessage());
	// int numberOfArmedMenus = getNumberOfArmedEvents(watchers);
	// escapeArmedMenus(numberOfArmedMenus);
	// } finally {
	// done[0] = true;
	// }
	// }

	// private MenuItem resolveAndClickItem(String text, Control parentControl,
	// Decorations parentDecorations, Widget parent, Watcher watcher)
	// , MultipleFoundException {
	// checkWidget(parentControl);
	// checkWidget(parentDecorations);
	// checkWidget(parent);
	// WidgetFinder finder = WidgetFinderImpl.getDefault();
	// MenuItem menuItem = null;
	// menuItem = (MenuItem) finder
	// .find(parent, new WidgetTextMatcher(text, MenuItem.class, true));
	// addWatcher(menuItem, watcher);
	// actionClick(menuItem, 1, 1);
	// final MenuItem itemT = menuItem;
	// wait(new Condition() {
	// public boolean test() {
	// return itemT.isDisposed() || isArmedOrSelected(itemT);
	// }
	// }, PATH_CLICKING_WAIT_TIME);
	//
	// if (menuItem != null && !menuItem.isDisposed()) {
	// removeWatcher(menuItem);
	// }
	// return menuItem;
	// }

	// private void escapeArmedMenus(int numberOfArmEvents) {
	// for (int i = 0; i <= numberOfArmEvents; i++) {
	// actionDelay(200);
	// keyPress(SWT.ESC);
	// keyRelease(SWT.ESC);
	// }
	// }

	// Listener to signal when a menu menuItem has been clicked
	// private class Watcher implements Listener {
	// private volatile boolean _gotArm = false;
	//
	// private volatile boolean _gotSelection = false;
	//
	// private StringBuffer _debugBuffer;
	//
	// public Watcher(StringBuffer buffer) {
	// _debugBuffer = buffer;
	// }
	//
	// public void handleEvent(org.eclipse.swt.widgets.Event e) {
	// if (e.type == SWT.Arm) {
	// _debugBuffer.append("\n[SWT.Arm]:");
	// _debugBuffer.append(e);
	// _gotArm = true;
	// }
	// if (e.type == SWT.Selection) {
	// _debugBuffer.append("\n[SWT.Selection]:");
	// _debugBuffer.append(e);
	// _gotSelection = true;
	// }
	// }
	//
	// public boolean gotArmEvent() {
	// return _gotArm;
	// }
	//
	// public boolean gotSelectionEvent() {
	// return _gotSelection;
	// }
	// }

	// adds a watcher-listener to the given menu menuItem
	// private void addWatcher(final MenuItem menuItem, final Watcher watcher) {
	// checkWidget(menuItem);
	// Displays.syncExec(new Runnable() {
	// public void run() {
	// menuItem.setData(WATCHER_NAME, watcher);
	// menuItem.addListener(SWT.Arm, watcher);
	// menuItem.addListener(SWT.Selection, watcher);
	// }
	// });
	// }

	// removes the given menuItem's watcher-listener
	// private void removeWatcher(final MenuItem menuItem) {
	// checkWidget(menuItem);
	// Displays.syncExec(new Runnable() {
	// public void run() {
	// Watcher watcher = (Watcher) menuItem.getData(WATCHER_NAME);
	// if (watcher != null) {
	// menuItem.removeListener(SWT.Arm, watcher);
	// menuItem.removeListener(SWT.Selection, watcher);
	// }
	// }
	// });
	// }

	// private int getNumberOfArmedEvents(List watchers) {
	// // private int getNumberOfArmedEvents(List <Watcher> watchers) {
	// int numberOfArmedEvents = 0;
	// Iterator it = watchers.iterator();
	// while (it.hasNext()) {
	// // for (Watcher watcher : watchers) {
	// Watcher watcher = (Watcher) it.next();
	// if (watcher.gotArmEvent()) {
	// numberOfArmedEvents++;
	// }
	// }
	// return numberOfArmedEvents;
	// }
	//
	// // tests whether the given menuItem has received the Arm or Selection
	// event
	// private boolean isArmedOrSelected(final MenuItem menuItem) {
	// checkWidget(menuItem);
	// return syncExec(new BooleanResult() {
	// public boolean result() {
	// if (menuItem.isDisposed()) {
	// return false;
	// }
	// Watcher lWatcher = (Watcher) menuItem.getData(WATCHER_NAME);
	// return (lWatcher == null) ? false : lWatcher.gotArmEvent();
	// }
	// });
	// }

	/**
	 * Get an instrumented <code>MenuItem</code> from its <code>id</code>
	 * Because we instrumented it, we assume it not only can be found, but is
	 * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
	 * instrumented <code>MenuItem</code> must be unique and findable with
	 * param.
	 */
	public static MenuItem getInstrumentedMenuItem(String id) {
		return getInstrumentedMenuItem(id, null);
	}

	/**
	 * Get an instrumented <code>MenuItem</code> from its <code>id</code>
	 * and the <code>title</code> of its shell (e.g. of the wizard containing
	 * it). Because we instrumented it, we assume it not only can be found, but
	 * is unique, so we don't even try to catch the *Found exceptions. CONTRACT:
	 * instrumented <code>MenuItem</code> must be unique and findable with
	 * param.
	 */
	public static MenuItem getInstrumentedMenuItem(String id, String title) {
		return getInstrumentedMenuItem(id, title, null);
	}

	/**
	 * Get an instrumented <code>MenuItem</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is
	 * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
	 * instrumented <code>MenuItem</code> must be unique and findable with
	 * param.
	 */
	public static MenuItem getInstrumentedMenuItem(String id, String title,
			String text) {
		return getInstrumentedMenuItem(id, title, text, null);
	}

	/**
	 * Get an instrumented <code>MenuItem</code> from its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found, but is
	 * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
	 * instrumented <code>MenuItem</code> must be unique and findable with
	 * param.
	 */
	public static MenuItem getInstrumentedMenuItem(String id, String title,
			String text, Shell shell) {
		MenuItem ret = null;
		try {
			ret = catchInstrumentedMenuItem(id, title, text, shell);
		} catch (ActionFailedException nf) {
			Assert.fail("no instrumented MenuItem \"" + id + "\" found");
		} catch (MultipleFoundException mf) {
			Assert.fail("many instrumented MenuItems \"" + id + "\" found");
		}
		Assert.assertNotNull("ERROR: null instrumented MenuItem", ret);
		return ret;
	}

	/**
	 * Get an instrumented <code>MenuItem</code>. Look in its
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * but don't assume it can only be found!
	 */
	public static MenuItem catchInstrumentedMenuItem(String id, String title,
			String text, Shell shell) throws MultipleFoundException {
		MenuItem ret = null;
		WidgetFinder finder = WidgetFinderImpl.getDefault();
		if (shell == null) {
			try {
				/* try to find the shell */
				shell = (Shell) finder.find(new WidgetTextMatcher(title));
			} catch (NotFoundException e) {
				// Empty block intended.
			} catch (MultipleFoundException e) {
				try {
					shell = (Shell) finder.find(new WidgetClassMatcher(
							Shell.class));
				} catch (NotFoundException e1) {
					// Empty block intended.
				} catch (MultipleFoundException e1) {
					// Empty block intended.
				}
			}
		}
		/* Decide what to search on: first id, then text if id not available */
		WidgetMatcher miMatcher;
		if (id != null) {
			miMatcher = new NameMatcher(id);
		} else {
			miMatcher = new WidgetTextMatcher(text);
		}
		try {
			if (shell == null) {
				ret = (MenuItem) finder.find(miMatcher);
			} else {
				ret = (MenuItem) finder.find(shell, miMatcher);
			}
		} catch (NotFoundException nf) {
			Assert.fail("no instrumented MenuItem \"" + id + "\" found");
		} catch (MultipleFoundException mf) {
			Assert.fail("many instrumented MenuItems \"" + id + "\" found");
		}

		return ret;
	}

	/* Miscellaneous */

	/**
	 * @see ItemTester#getParentItem(Item)
	 */
	protected Item getParentItem(Item item) {
		Menu menu = getParent((MenuItem) item);
		return MenuTester.getMenuTester().getParentItem(menu);
	}

}
