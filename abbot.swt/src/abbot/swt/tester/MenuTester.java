package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.SWTWorkarounds;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

public class MenuTester extends WidgetTester {

	public static final String POPUP_ROOT_FLAG = "POPUP_ROOT";

	public static final String ROOT_FLAG = "MENU_ROOT";

	// TODO Put methods from WidgetTester into MenuTester and add getters

	/**
	 * Factory method.
	 */
	public static MenuTester getMenuTester() {
		return (MenuTester) WidgetTester.getTester(Menu.class);
	}

	/**
	 * Constructs a new {@link MenuTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public MenuTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	public void actionClickItem(Menu menu, String path) {
		actionClickItem(menu, new ItemPath(path));
	}

	public void actionClickItem(Menu menu, String path, String delimiter) {
		actionClickItem(menu, new ItemPath(path, delimiter));
	}

	public void actionClickItem(Menu menu, ItemPath path) {
		checkWidget(menu);
		if (!isVisible(menu))
			throw new IllegalStateException("menu is not visible");
		clickItem(menu, path);
	}

	void clickItem(Menu menu, ItemPath path) {
		MenuItem item = getItem(menu, path.getSegment(0));
		MenuItemTester.getMenuItemTester().actionClickItem(item, path.subPath(1));
	}

	MenuItem getItem(Menu menu, String text) {
		MenuItem[] items = getItems(menu);
		return MenuItemTester.getMenuItemTester().findItem(items, text);
	}

	/**
	 * @deprecated Do not use this.
	 */
	MenuItem findItem(Menu menu, String text) {
		MenuItemTester menuItemTester = MenuItemTester.getMenuItemTester();
		MenuItem[] items = getItems(menu);
		for (MenuItem item : items) {
			if (text.equals(menuItemTester.getText(item)))
				return item;
		}

		for (MenuItem item : items) {
			Menu submenu = menuItemTester.getMenu(item);
			if (submenu != null) {
				MenuItem subitem = findItem(submenu, text);
				if (subitem != null)
					return subitem;
			}
		}

		return null;
	}

	/* Proxies */

	public Rectangle getBounds(final Menu menu) {
		checkWidget(menu);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return SWTWorkarounds.getBounds(menu);
			}
		});
	}

	/* Use the Menu's parentItem to getText */
	public String getText(final Menu menu) {
		return Displays.syncExec(menu.getDisplay(), new StringResult() {
			public String result() {
				String s = "";
				MenuItem parentItem = menu.getParentItem();
				if (parentItem != null) {
					s = parentItem.getText();
				}
				if (s == null || s.equals("")) {
					if ((menu.getStyle() & SWT.POP_UP) > 0) {
						s = POPUP_ROOT_FLAG;
					} else {
						s = ROOT_FLAG;
					}
				}
				return s;
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getParentItem()}.{@link MenuItem#toString()}.
	 */
	public String toString(final Menu m) {
		return Displays.syncExec(m.getDisplay(), new StringResult() {
			public String result() {
				String s = "";
				MenuItem parentItem = m.getParentItem();
				if (parentItem != null) {
					s = parentItem.toString();
				}
				return s;
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getDefaultItem()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the default item.
	 */
	public MenuItem getDefaultItem(final Menu menu) {
		MenuItem result = (MenuItem) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getDefaultItem();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Menu#getEnabled()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return true if the menu is enabled.
	 */
	public boolean getEnabled(final Menu menu) {
		return Displays.syncExec(menu.getDisplay(), new BooleanResult() {
			public boolean result() {
				return menu.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getItem(int)}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @param index
	 *            the index of the item to get.
	 * @return the item at the index given.
	 */
	public MenuItem getItem(final Menu menu, final int index) {
		return (MenuItem) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getItem(index);
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getParentItem()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the parent item.
	 */
	public MenuItem getParentItem(final Menu menu) {
		return (MenuItem) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getParentItem();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getItemCount()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the number of items under this menu.
	 */
	public int getItemCount(final Menu menu) {
		return Displays.syncExec(menu.getDisplay(), new IntResult() {
			public int result() {
				return menu.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getItems()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the children.
	 */
	public MenuItem[] getItems(final Menu menu) {
		return (MenuItem[]) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getParent()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the parent.
	 */
	public Decorations getParent(final Menu menu) {
		return (Decorations) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getParentMenu()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the parent menu.
	 */
	public Menu getParentMenu(final Menu menu) {
		return (Menu) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getParentMenu();
			}
		});
	}

	/**
	 * Gets the top-most (root) {@link Menu} of a menu hierarchy.
	 */
	public Menu getRootMenu(Menu menu) {
		Menu parent = getParentMenu(menu);
		if (parent == null)
			return menu;
		return getRootMenu(parent);
	}

	/**
	 * Proxy for {@link Menu#getShell()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the shell of the menu.
	 */
	public Shell getShell(final Menu menu) {
		return (Shell) Displays.syncExec(menu.getDisplay(), new Result() {
			public Object result() {
				return menu.getShell();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#getVisible()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the menu's visibility state.
	 */
	public boolean getVisible(final Menu menu) {
		return Displays.syncExec(menu.getDisplay(), new BooleanResult() {
			public boolean result() {
				return menu.getVisible();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#indexOf(org.eclipse.swt.widgets.MenuItem)}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @param menuItem
	 *            the item to check.
	 * @return the index of the item given.
	 */
	public int indexOf(final Menu menu, final MenuItem menuItem) {
		return Displays.syncExec(menu.getDisplay(), new IntResult() {
			public int result() {
				return menu.indexOf(menuItem);
			}
		});
	}

	/**
	 * Proxy for {@link Menu#isEnabled()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return true if the menu is enabled.
	 */
	public boolean isEnabled(final Menu menu) {
		return Displays.syncExec(menu.getDisplay(), new BooleanResult() {
			public boolean result() {
				return menu.isEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link Menu#isVisible()}. <p/>
	 * 
	 * @param menu
	 *            the menu under test.
	 * @return the menu's visibility state.
	 */
	public boolean isVisible(final Menu menu) {
		return syncExec(new BooleanResult() {
			public boolean result() {
				return menu.isVisible() && menu.getParent().isVisible();
			}
		});
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof Menu)
			return isVisible((Menu) widget);
		return super.isVisible(widget);
	}

	/* Miscellaneous */

	public boolean isPopUp(Menu menu) {
		return (getStyle(menu) & SWT.POP_UP) != 0;
	}

}
