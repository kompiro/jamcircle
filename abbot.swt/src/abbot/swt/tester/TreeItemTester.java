package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.script.Condition;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link TreeItem}s.
 * 
 * @author Gary Johnston
 * @author Henry McEuen
 * @author Chris Jaun
 */
public class TreeItemTester extends ItemTester {

	/**
	 * Default number of milliseconds to wait for a {@link TreeItem} to show.
	 * 
	 * @see #waitShowing(TreeItem)
	 */
	public static final long ITEM_SHOWING_TIMEOUT = 15000L;

	/**
	 * Factory method.
	 */
	public static TreeItemTester getTreeItemTester() {
		return (TreeItemTester) WidgetTester.getTester(TreeItem.class);
	}

	private final TreeTester treeTester;

	/**
	 * Constructs a new {@link TreeItemTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public TreeItemTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
		treeTester = TreeTester.getTreeTester();
	}

	/**
	 * Shows a {@link TreeItem} under another {@link TreeItem} given a String of default-delimited
	 * text values. {@link TreeItem}s in the path (<b>except</b> for the last one, the one to be
	 * returned) are expanded as necessary in order to ensure that their child items get created.
	 * 
	 * @param treeItem
	 *            the {@link TreeItem}
	 * @param path
	 *            the delimited list of text values
	 * @return the TreeItem found (or null) @
	 * @see ItemPath#DEFAULT_DELIMITER
	 */
	public TreeItem actionShowItem(TreeItem treeItem, String path) {
		checkWidget(treeItem);
		return showItem(treeItem, new ItemPath(path));
	}

	/**
	 * Shows a {@link TreeItem} under another {@link TreeItem} given a String of delimited text
	 * values. {@link TreeItem}s in the path (<b>except</b> for the last one, the one to be
	 * returned) are expanded as necessary in order to ensure that their child items get created.
	 * 
	 * @param treeItem
	 *            the {@link TreeItem}
	 * @param path
	 *            the delimited list of text values
	 * @param delimiter
	 *            the text value delimiter used in the path
	 * @return the TreeItem found (or null) @
	 */
	public TreeItem actionShowItem(TreeItem treeItem, String path, String delimiter) {
		checkWidget(treeItem);
		return showItem(treeItem, new ItemPath(path, delimiter));
	}

	/**
	 * Shows a {@link TreeItem} under another {@link TreeItem} given an {@link ItemPath}.
	 * {@link TreeItem}s in the path (<b>except</b> for the last one, the one to be returned) are
	 * expanded as necessary in order to ensure that their child items get created.
	 * 
	 * @param treeItem
	 *            the {@link TreeItem}
	 * @param path
	 *            the path to the desired {@link TreeItem}
	 * @return the TreeItem found (or null) @
	 */
	public TreeItem actionShowItem(TreeItem treeItem, ItemPath path) {
		checkWidget(treeItem);
		return showItem(treeItem, path);
	}

	/**
	 * Shows a {@link TreeItem} under another {@link TreeItem} given an {@link ItemPath}.
	 * {@link TreeItem}s in the path (<b>except</b> for the last one, the one to be returned) are
	 * expanded as necessary in order to ensure that their child items get created.
	 * 
	 * @param item
	 *            the {@link TreeItem}
	 * @param path
	 *            the path to the desired {@link TreeItem}
	 * @return the TreeItem found
	 */
	TreeItem showItem(TreeItem item, ItemPath path) {
		for (String text : path) {
			expandItem(item, true); // To ensure that child items get populated.
			item = findItem(getItemsInternal(item), text);
		}

		// Ensure that the item to be returned is visible.
		showItemInternal(item);

		return item;
	}

	TreeItem findItem(TreeItem[] items, String text) {
		for (TreeItem item : items) {
			if (text.equals(getText(item)))
				return item;
		}
		throw new ActionItemNotFoundException(text);
	}

	public TreeItem actionShowItem(TreeItem item) {
		checkWidget(item);
		showItem(item);
		return item;
	}

	void showItem(TreeItem item) {
		treeTester.showItem(getParent(item), getPath(item));
	}

	private void showItemInternal(TreeItem item) {

		if (!isVisible(item)) {
			treeTester.showItem(item);
			
			/*
			 * TreeTester.showItem(TreeItem) doesn't work sometimes.
			 * The workaround is to check for this and try again if necessary.
			 */
			if (!isVisible(item)) {
				sleep(500);
				treeTester.showItem(item);
				waitVisible(item);
			}
		}

	}
	
	/*
	 * Lame, desperate attempt to work around the "jumpy tree" problem.
	 */
	/*
	 * private void noJumpy(TreeItem item) { sleep(500L); // sleep(500L); // #1. Apparently, if we
	 * do showItem() when a previously queued tree // paint event hasn't // been handled yet then
	 * the paint event sort of undoes the showItem(). // Or something. // Solution: Wait up to 200ms
	 * for a paint event. Tree tree = getParent(item); final boolean[] painted = new boolean[1];
	 * PaintListener listener = new PaintListener() { public void paintControl(PaintEvent event) {
	 * painted[0] = true; } }; treeTester.addPaintListener(tree, listener); try { wait(new
	 * Condition() { public boolean test() { return painted[0]; } }, 200L); } catch
	 * (WaitTimedOutError e) { // Ignored } finally { treeTester.removePaintListener(tree,
	 * listener); } // #2. Maybe we need to make sure the UI thread isn't still busy. // Yes, this
	 * is desperation. waitForIdle(); }
	 */

	/* Actions for clicking on a TreeItem. */

	public TreeItem actionClickItem(TreeItem item, String path) {
		return actionClickItem(item, new ItemPath(path));
	}

	public TreeItem actionClickItem(TreeItem item, String path, String delimiter) {
		return actionClickItem(item, new ItemPath(path, delimiter));
	}
	
	/**
	 * Assumes the item is visible.
	 */
	private void clickInternal(TreeItem item) {

		// Get the clickpoint (display coordinates).
		Point clickpoint = getGlobalClickPoint(item);
		if (clickpoint == null)
			throw new IllegalStateException("item is off screen");

		// Get the parent tree's client area (display coordinates).
		Tree tree = getParent(item);
		Rectangle clientArea = treeTester.getClientArea(tree);
		Point location = treeTester.toDisplay(tree, clientArea.x, clientArea.y);
		clientArea.x = location.x;
		clientArea.y = location.y;

		// Check that the clickpoint is on the parent tree.
		if (!clientArea.contains(clickpoint))
			throw new IllegalStateException("clickpoint not on tree");

		// Check that the clickpoint is on the item.
		location = treeTester.toControl(tree, clickpoint);
		Item cursorItem = treeTester.getItem(tree, location);
		if (cursorItem != item)
			throw new IllegalStateException("clickpoint not on item");

		click(clickpoint.x, clickpoint.y);
		
		// Wait for tree to do any moving or scrolling.
		sleep(500);
	}

	public TreeItem actionClickItem(TreeItem item, ItemPath path) {
		checkWidget(item);

		// Show (and get) the item that the path specifies.
		item = showItem(item, path);

		clickInternal(item);
		
		return item;
	}

//	private class SelectionListener implements Listener {
//		
//		private final TreeItem item;
//		
//		private final Tree tree;
//
//		private boolean clicked = false;
//
//		public SelectionListener(TreeItem item) {
//			this.item = item;
//			tree = getParent(item);
//			addListener(tree, SWT.Selection, this);
//		}
//
//		public void dispose() {
//			removeListener(tree, SWT.Selection, this);
//		}
//
//		public void waitEvent() {
//			TreeItemTester.this.wait(new Condition() {
//				public boolean test() {
//					return isClicked();
//				}
//			}, 1000L);
//		}
//
//		public synchronized boolean isClicked() {
//			return clicked;
//		}
//
//		public synchronized void setClicked(boolean clicked) {
//			this.clicked = clicked;
//		}
//
//		public void handleEvent(Event event) {
//			if (event.type == SWT.Selection && event.widget == tree && event.item == item)
//				setClicked(true);
//		}
//	}

	public TreeItem actionClickItem(TreeItem item) {
		checkWidget(item);
		showItem(item);
		clickInternal(item);
		return item;
	}

	/* Actions for double-clicking on a TreeItem. */

	public TreeItem actionDoubleClickItem(TreeItem item, String path) {
		return actionDoubleClickItem(item, new ItemPath(path));
	}

	public TreeItem actionDoubleClickItem(TreeItem item, String path, String delimiter) {
		return actionDoubleClickItem(item, new ItemPath(path, delimiter));
	}

	public TreeItem actionDoubleClickItem(TreeItem item, ItemPath path) {
		checkWidget(item);
		item = showItem(item, path);
		doubleClick(item);
		return item;
	}

	public TreeItem actionDoubleClickItem(TreeItem item) {
		checkWidget(item);
		showItem(item);
		doubleClick(item);
		return item;
	}

	protected Rectangle getClickBounds(final Widget widget) {
		if (widget instanceof TreeItem)
			return getClickBounds((TreeItem) widget);
		return super.getClickBounds(widget);
	}

	protected Rectangle getClickBounds(final TreeItem treeItem) {
		checkWidget(treeItem);

		// Get the item's bounds (tree coordinates).
		Rectangle clickbounds = getBounds(treeItem);
		
		// Prune it down to at most 20x20.
		clickbounds.width = Math.min(clickbounds.width, 20);
		clickbounds.height = Math.min(clickbounds.height, 20);
		
		// Prune off anything not within the tree's client area.
		Tree tree = getParent(treeItem);
		Rectangle treeClientArea = treeTester.getClientArea(tree);
		clickbounds.intersect(treeClientArea);
		
		// Convert to display coordinates.
		Point p = treeTester.toDisplay(tree, clickbounds.x, clickbounds.y);
		clickbounds.x = p.x;
		clickbounds.y = p.y;

		return clickbounds;
	}

	/* Actions for expanding or collapsing a TreeItem. */

	public TreeItem actionExpandItem(TreeItem item, String path, boolean expand) {
		return actionExpandItem(item, new ItemPath(path), expand);
	}

	public TreeItem actionExpandItem(TreeItem item, String path, String delimiter, boolean expand) {
		return actionExpandItem(item, new ItemPath(path, delimiter), expand);
	}

	public TreeItem actionExpandItem(TreeItem item, ItemPath path, boolean expand) {
		checkWidget(item);
		item = showItem(item, path);
		expandItem(item, expand);
		return item;
	}

	public void actionExpandItem(TreeItem item, boolean expand) {
		checkWidget(item);
		showItem(item);
		expandItem(item, expand);
	}

	void expandItem(final TreeItem item, final boolean expand) {

		// If it's already expanded or cannot be expanded, bail.
		if (getExpanded(item) == expand || getItemCount(item) == 0)
			return;

		// Ensure that it is showing.
		showItemInternal(item);

		// Select it.
		clickInternal(item);
		wait(new Condition() {
			public boolean test() {
				return isSelected(item);
			}
		}, 5000);

		// Expand or collapse it by hitting the right or left arrow key,
		// respectively.
		// Note: This only works on platforms that use the right/left arrow keys
		// as shortcuts
		// for tree expansion/collapse. The TreeItem can be expanded
		// programmatically but in this
		// case the expansion listeners are not notified, which is a problem for
		// trees that are
		// dynamically populated by JFace TreeViewers. The method call below
		// doesn't solve the
		// problem either; apparently it's not notifying the proper listeners.
		for (int i = 0; i < 5; i++) {
			actionKey(expand ? TREE_EXPAND_ACCEL : TREE_COLLAPSE_ACCEL);
			sleep(100L);
			if (getExpanded(item) == expand)
				break;
			clickInternal(item);
			wait(new Condition() {
				public boolean test() {
					return isSelected(item);
				}
			}, 5000);
		}
		// try {
		// wait(new Condition() {
		// public boolean test() {
		// return getExpanded(item) == expand;
		// }
		// }, 10000);
		// } catch (WaitTimedOutError exception) {
		//
		// // FIXME Cheating...
		// System.err.printf("Using API to expand or collapse TreeItem %s\n",
		// getText(item));
		// syncExec(new Runnable() {
		// public void run() {
		// item.setExpanded(expand);
		// }
		// });
		// }

	}

	/* Actions for checking or unchecking a TreeItem. */

	public TreeItem actionCheckItem(TreeItem item, String path, boolean check) {
		return actionCheckItem(item, new ItemPath(path), check);
	}

	public TreeItem actionCheckItem(TreeItem item, String path, String delimiter, boolean check) {
		return actionCheckItem(item, new ItemPath(path, delimiter), check);
	}

	public TreeItem actionCheckItem(TreeItem item, ItemPath path, boolean check) {
		checkWidget(item);
		item = showItem(item, path);
		check(item, check);
		return item;
	}

	/**
	 */
	public TreeItem actionCheckItem(TreeItem item, boolean check) {
		checkWidget(item);
		showItem(item);
		check(item, check);
		return item;
	}

	void check(final TreeItem treeItem, final boolean check) {

		if (getChecked(treeItem) == check)
			return;

		actionClickItem(treeItem);

		// Need to move focus over to the checkbox.
		if (SWT.getPlatform().equals("gtk")) {
			actionKey(SWT.ARROW_LEFT);
			actionKey(SWT.ARROW_LEFT);
		}
		actionKey(' ');

		wait(new Condition() {
			public boolean test() {
				return getChecked(treeItem) == check;
			}
		}, 5000L);

	}

	/* Actions for invoking a MenuItem on a TreeItem's context menu. */

	/**
	 * @see WidgetTester#actionClickMenuItem(Widget, ItemPath)
	 */
	public void actionClickMenuItem(Widget widget, ItemPath menuPath) {
		checkWidget(widget);
		if (widget instanceof TreeItem)
			showItem((TreeItem) widget);
		clickMenuItem(widget, menuPath);
	}

	public void actionClickMenuItem(TreeItem item, ItemPath path, ItemPath menuPath) {
		checkWidget(item);
		item = showItem(item, path);
		clickMenuItem(item, menuPath);
	}

	public void actionClickMenuItem(TreeItem item, String path, ItemPath menuPath) {
		actionClickMenuItem(item, new ItemPath(path), menuPath);
	}

	public void actionClickMenuItem(TreeItem item, String path, String delimiter, ItemPath menuPath) {
		actionClickMenuItem(item, new ItemPath(path, delimiter), menuPath);
	}

	/**
	 * @see ItemTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		if (widget instanceof TreeItem) {
			Tree tree = getParent((TreeItem) widget);
			return treeTester.getMenu(tree);
		}
		return super.getMenu(widget);
	}

	/**
	 * @see ItemTester#getParentItem(Item)
	 */
	protected Item getParentItem(Item item) {
		return getParentItem((TreeItem) item);
	}

	/* Proxies */

	/**
	 * @see TreeItem#getBackground()
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @return the background {@link Color}.
	 */
	public Color getBackground(final TreeItem treeItem) {
		checkWidget(treeItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return treeItem.getBackground();
			}
		});
	}

	/**
	 * @see TreeItem#getBackground(int)
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @param index
	 *            the column index
	 * @return the background {@link Color}.
	 */
	public Color getBackground(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return treeItem.getBackground(index);
			}
		});
	}

	/**
	 * @see TreeItem#getBounds()
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @return the bounding {@link Rectangle} of treeItem
	 */
	public Rectangle getBounds(final TreeItem treeItem) {
		checkWidget(treeItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return treeItem.getBounds();
			}
		});
	}

	/**
	 * @see TreeItem#getBounds(int)
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @param index
	 *            the column index
	 * @return the bounding {@link Rectangle} of treeItem
	 */
	public Rectangle getBounds(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return treeItem.getBounds(index);
			}
		});
	}

	/**
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @param index
	 *            the column index
	 * @return the bounding {@link Rectangle} of treeItem in display coordinates
	 */
	public Rectangle getGlobalBounds(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				Rectangle bounds = treeItem.getBounds(index);
				Point p = treeItem.getParent().toDisplay(bounds.x, bounds.y);
				bounds.x = p.x;
				bounds.y = p.y;
				return bounds;
			}
		});
	}

	/**
	 * @see TreeItem#getChecked()
	 */
	public boolean getChecked(final TreeItem treeItem) {
		checkWidget(treeItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return treeItem.getChecked();
			}
		});
	}

	/**
	 * @see TreeItem#getExpanded()
	 */
	public boolean getExpanded(final TreeItem treeItem) {
		checkWidget(treeItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return treeItem.getExpanded();
			}
		});
	}

	/**
	 * @see TreeItem#getFont()
	 */
	public Font getFont(final TreeItem treeItem) {
		checkWidget(treeItem);
		return (Font) syncExec(new Result() {
			public Object result() {
				return treeItem.getFont();
			}
		});
	}

	/**
	 * @see TreeItem#getFont(int)
	 */
	public Font getFont(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Font) syncExec(new Result() {
			public Object result() {
				return treeItem.getFont(index);
			}
		});
	}

	/**
	 * @see TreeItem#getForeground()
	 */
	public Color getForeground(final TreeItem treeItem) {
		checkWidget(treeItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return treeItem.getForeground();
			}
		});
	}

	/**
	 * @see TreeItem#getForeground(int)
	 */
	public Color getForeground(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return treeItem.getForeground(index);
			}
		});
	}

	/**
	 * @see TreeItem#getGrayed()
	 * @param treeItem
	 *            the tree treeItem under test.
	 * @return the grayed state of the treeItem.
	 */
	public boolean getGrayed(final TreeItem treeItem) {
		checkWidget(treeItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return treeItem.getGrayed();
			}
		});
	}

	/**
	 * @see TreeItem#getImage(int)
	 */
	public Image getImage(final TreeItem item, final int index) {
		checkWidget(item);
		return (Image) syncExec(new Result() {
			public Object result() {
				return item.getImage(index);
			}
		});
	}

	/**
	 * @see TreeItem#getImageBounds(int)
	 */
	public Rectangle getImageBounds(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return treeItem.getImageBounds(index);
			}
		});
	}

	/**
	 * @see TreeItem#getItem(int)
	 */
	public TreeItem getItem(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (TreeItem) syncExec(new Result() {
			public Object result() {
				return treeItem.getItem(index);
			}
		});
	}

	/**
	 * @see TreeItem#getItemCount()
	 */
	public int getItemCount(final TreeItem treeItem) {
		checkWidget(treeItem);
		return syncExec(new IntResult() {
			public int result() {
				return treeItem.getItemCount();
			}
		});
	}

	/**
	 * @see TreeItem#getItems()
	 */
	public TreeItem[] getItems(TreeItem treeItem) {
		checkWidget(treeItem);
		return getItemsInternal(treeItem);
	}

	TreeItem[] getItemsInternal(final TreeItem treeItem) {
		return (TreeItem[]) syncExec(new Result() {
			public Object result() {
				return treeItem.getItems();
			}
		});
	}

	/**
	 * @see TreeItem#getParent()
	 */
	public Tree getParent(final TreeItem treeItem) {
		checkWidget(treeItem);
		return (Tree) syncExec(new Result() {
			public Object result() {
				return treeItem.getParent();
			}
		});
	}

	/**
	 * @see TreeItem#getParentItem()
	 */
	public TreeItem getParentItem(final TreeItem treeItem) {
		checkWidget(treeItem);
		return (TreeItem) syncExec(new Result() {
			public Object result() {
				return treeItem.getParentItem();
			}
		});
	}

	/**
	 * @see TreeItem#getText(int)
	 */
	public String getText(final TreeItem item, final int index) {
		checkWidget(item);
		return syncExec(new StringResult() {
			public String result() {
				return item.getText(index);
			}
		});
	}

	/**
	 * @see TreeItem#getTextBounds(int)
	 */
	public Rectangle getTextBounds(final TreeItem treeItem, final int index) {
		checkWidget(treeItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return treeItem.getTextBounds(index);
			}
		});
	}

	/**
	 * @see TreeItem#indexOf(TreeItem)
	 */
	public int indexOf(final TreeItem parent, final TreeItem child) {
		checkWidget(parent);
		checkWidget(child);
		return syncExec(new IntResult() {
			public int result() {
				return parent.indexOf(child);
			}
		});
	}

	/* Miscellaneous */

	/**
	 * Determines whether or not a {@link TreeItem} is currently selected.
	 * 
	 * @param treeItem
	 *            a TreeItem
	 * @return <code>true</code> if treeItem is selected, false otherwise
	 */
	public boolean isSelected(final TreeItem treeItem) {
		Tree tree = getParent(treeItem);
		TreeItem[] items = treeTester.getSelection(tree);
		for (TreeItem item : items) {
			if (item.equals(treeItem))
				return true;
		}
		return false;
	}

	/**
	 * @deprecated Use {@link #waitVisible(Widget)}
	 */
	public void waitShowing(TreeItem item) {
		waitVisible(item);
	}

	/**
	 * @deprecated Use {@link #waitVisible(Widget, long)}
	 */
	public void waitShowing(final TreeItem item, long time) {
		waitVisible(item, time);
	}

	/**
	 * @param item
	 *            a {@link TreeItem}
	 * @return <code>true</code> if the {@link TreeItem} is visible, <code>false</code>
	 *         otherwise
	 */
	// public boolean isVisible(TreeItem item) {
	// VisibilityInfo info = getInfo(item);
	// if (info.isItemVisible) {
	// for (int i = 0; i < 5; i++) {
	// sleep(100L);
	// info = getInfo(item);
	// if (!info.isItemVisible)
	// return false;
	// }
	// return true;
	// }
	//
	// return false;
	// }
	public boolean isVisible(TreeItem item) {

		Tree tree = getParent(item);
		if (treeTester.isVisible(tree)) {

			// Get the parent tree's client area (tree coordinates).
			Rectangle treeClientArea = treeTester.getClientArea(tree);

			// Get the parent tree's location (display coordinates).
			Point treeLocation = treeTester.toDisplay(tree, treeClientArea.x,
					treeClientArea.y);

			// Get the item's click-bounds (display coordinates).
			Rectangle clickBounds = getClickBounds(item);

			// The item is visible iff its click-bounds intersects its parent
			// tree's client area.
			return !clickBounds.isEmpty()
					&& clickBounds.intersects(treeLocation.x, treeLocation.y,
							treeClientArea.width, treeClientArea.height);
		}

		return false;

	}

	// private class VisibilityInfo {
	//
	// public boolean isTreeVisible;
	//
	// public Rectangle treeClientArea;
	//
	// public Rectangle itemBounds;
	//
	// public Point itemClickPoint;
	//
	// public boolean isItemVisible;
	//
	// public String toString() {
	// return String.format("[tree:%s,%s | item:%s,%s,%s]", isTreeVisible,
	// treeClientArea, itemBounds, itemClickPoint, isItemVisible);
	// }
	// }

	/**
	 * @param item
	 *            a {@link TreeItem}
	 * @return <code>true</code> if the {@link TreeItem} is visible, <code>false</code>
	 *         otherwise
	 */
	// private VisibilityInfo getInfo(TreeItem item) {
	// Tree tree = getParent(item);
	// VisibilityInfo info = new VisibilityInfo();
	// info.isTreeVisible = treeTester.isVisible(tree);
	// if (info.isTreeVisible) {
	//
	// // Get the tree's client area (which is relative to the tree itself).
	// info.treeClientArea = treeTester.getClientArea(tree);
	//
	// // Get the item's bounds (relative to the parent tree).
	// info.itemBounds = getBounds(item);
	//
	// // Get the item's clickpoint (relative to its parent tree).
	// Point p = getGlobalClickPoint(item);
	// info.itemClickPoint = treeTester.toControl(tree, p);
	// // info.itemClickPoint.x += info.itemBounds.x;
	// // info.itemClickPoint.y += info.itemBounds.y;
	//
	// // The item is visible iff the clickpoint is within the tree's
	// // client area.
	// info.isItemVisible = info.treeClientArea
	// .contains(info.itemClickPoint);
	// }
	// return info;
	// }
	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof TreeItem)
			return isVisible((TreeItem) widget);
		return super.isVisible(widget);
	}

	/* Junkyard */

	/** @deprecated */
	public static final int NONCHECKABLE = 0;

	/** @deprecated */
	public static final int SETCHECKED = 1;

	/** @deprecated */
	public static final int SETUNCHECKED = 2;

	/** @deprecated */
	public static final int TOGGLECHECKED = 3;

	/**
	 * @deprecated Use {@link TreeTester#actionClickItem(Tree, String)}.
	 */
	public TreeItem getTreeItemByPath(String path, Tree tree) {
		return treeTester.actionClickItem(tree, path);
	}

	/**
	 * @deprecated Use {@link TreeTester#actionClickItem(Tree, String, String)}.
	 */
	public TreeItem getTreeItemByPath(String path, String delimiter, Tree tree) {
		return treeTester.actionClickItem(tree, path, delimiter);
	}

	/**
	 * @deprecated Use {@link #actionExpandItem(TreeItem, boolean)}
	 */
	public void actionExpandItem(TreeItem treeItem) {
		actionExpandItem(treeItem, true);
	}

	/**
	 * @deprecated Use {@link #actionExpandItem(TreeItem, boolean)}.
	 */
	public void setExpanded(final TreeItem treeItem, final boolean expand) {
		actionExpandItem(treeItem, expand);
	}

	/**
	 * Clicks all {@link TreeItem}s in the path to the given TreeItem, including the specified
	 * {@link TreeItem}.
	 * 
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @param delay
	 *            the delay in milliseconds between each pair of clicks
	 * @deprecated Use {@link #actionClickItem(TreeItem)}.
	 */
	public void actionClickSubTreeItem(TreeItem treeItem, final long delay) {
		actionClickItem(treeItem);
	}

	/**
	 * Checks all {@link TreeItem}s in the path to the given TreeItem, including the specified
	 * {@link TreeItem}.
	 * 
	 * @param treeItem
	 *            a {@link TreeItem}
	 * @param delay
	 *            the delay in milliseconds between each pair of checks
	 * @deprecated Use {@link #actionCheckItem(TreeItem, boolean)}
	 */
	public void actionCheckSubTreeItem(TreeItem treeItem, final int delay) {
		actionCheckItem(treeItem, true);
	}

	/**
	 * Invokes a context menu-item on a {@link TreeItem}.<br>
	 * Code adapted from <a href="https://sourceforge.net/mailarchive/message.php?msg_id=11680274">
	 * Joerg Weingarten</a>
	 * 
	 * @param item
	 *            the tree treeItem to select
	 * @param menuItemText
	 * the text of the menu treeItem to select @
	 * @deprecated Use {@link #actionClickMenuItem(Widget, ItemPath)}.
	 */
	public void actionContextClickTreeItem(TreeItem item, String menuItemText) {
		actionClickMenuItem(item, new ItemPath(new String[] { menuItemText }));
	}

	/**
	 * Given a delimited TreeItem path, select(single-click), and expand as needed, all the items
	 * from left to right. Finally, clicking/selecting the last TreeItem in the path. The TreeItem
	 * path delimiter for the given path is "/". The click location for EVERY TreeItem is determined
	 * by TreeItemTester.getDefaultRelativeClickLocation(final TreeItem treeItem), which clicks on
	 * the middle left of the TreeItem, in order to handled horizontally scrolled items.
	 * 
	 * @param path
	 *            The full path to the TreeItem; each treeItem is represented by its getText()
	 *            value.
	 * @param tree
	 * The Tree which contains ALL of the items in the given path. @
	 * @deprecated Use {@link TreeTester#actionClickItem(Tree, String)}
	 */
//	public void actionSelectTreeItem(String path, final Tree tree) {
//		clickTreeItem(tree, new ItemPath(path), -1, -1, NONCHECKABLE, 1);
//	}

	/**
	 * @deprecated Do not use. This method exists only to help implement other deprecated methods.
	 */
//	private TreeItem clickTreeItem(Tree tree, ItemPath path, int x, int y, int checkstate,
//			int clicks) {
//
//		// Expose the item.
//		checkWidget(tree);
//		TreeItem item = treeTester.showItem(tree, path);
//
//		// Do check/uncheck/toggle (or not).
//		switch (checkstate) {
//			case SETCHECKED:
//				check(item, true);
//				break;
//			case SETUNCHECKED:
//				check(item, false);
//			case TOGGLECHECKED:
//				check(item, !getChecked(item));
//				break;
//			case NONCHECKABLE:
//				break;
//			default:
//				throw new IllegalArgumentException("invalid checkstate");
//		}
//
//		// Determine where we'll click (display coordinates).
//		Point p = null;
//		if (x < 0 || y < 0) {
//			p = getGlobalClickPoint(item);
//		} else {
//			p = getDisplayLocation(item);
//			p.x += x;
//			p.y += y;
//		}
//
//		// Do clik/double-click.
//		switch (clicks) {
//			case 1:
//				click(p.x, p.y);
//				break;
//			case 2:
//				doubleClick(p.x, p.y);
//				break;
//			default:
//				throw new IllegalArgumentException("invalid clicks");
//		}
//
//		// Return the affected item.
//		return item;
//	}

	/**
	 * @deprecated Use {@link #actionCheckItem(TreeItem, boolean)}.
	 */
	public void setChecked(final TreeItem treeItem, final boolean checked) {
		checkWidget(treeItem);
		syncExec(new Runnable() {
			public void run() {
				treeItem.setChecked(checked);
			}
		});
	}

	/**
	 * @deprecated Use {@link #actionCheckItem(TreeItem, boolean)}.
	 */
	public void actionCheckItem(final TreeItem treeItem, final int checkstate) {
		switch (checkstate) {
			case SETCHECKED:
				actionCheckItem(treeItem, true);
				break;
			case SETUNCHECKED:
				actionCheckItem(treeItem, false);
				break;
			case TOGGLECHECKED:
				actionCheckItem(treeItem, !getChecked(treeItem));
				break;
			default:
				throw new IllegalArgumentException("invalid checkstate");
		}
	}

}
