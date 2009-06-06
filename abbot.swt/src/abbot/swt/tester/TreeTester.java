package abbot.swt.tester;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Tree}s.
 */
public class TreeTester extends CompositeTester {

	/**
	 * Factory method.
	 */
	public static TreeTester getTreeTester() {
		return (TreeTester) WidgetTester.getTester(Tree.class);
	}

	/**
	 * Constructs a new {@link TreeTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public TreeTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions for showing a TreeItem in a Tree. */

	/**
	 * Shows a {@link TreeItem} in a {@link Tree} given a String of default-delimited text values.
	 * {@link TreeItem}s in the path (<b>except</b> for the last one, the one to be returned) are
	 * expanded as necessary in order to ensure that their child items get created.
	 * 
	 * @param tree
	 *            the {@link Tree}
	 * @param path
	 *            the delimited list of text values
	 * @return the TreeItem found (or null) @
	 * @see ItemPath#DEFAULT_DELIMITER
	 */
	public TreeItem actionShowItem(Tree tree, String path) {
		checkWidget(tree);
		return showItem(tree, new ItemPath(path));
	}

	/**
	 * Shows a {@link TreeItem} in a {@link Tree} given a String of delimited text values.
	 * {@link TreeItem}s in the path (<b>except</b> for the last one, the one to be returned) are
	 * expanded as necessary in order to ensure that their child items get created.
	 * 
	 * @param tree
	 *            the {@link Tree}
	 * @param path
	 *            the delimited list of text values
	 * @param delimiter
	 *            the text value delimiter used in the path
	 * @return the TreeItem found (or null) @
	 */
	public TreeItem actionShowItem(Tree tree, String path, String delimiter) {
		checkWidget(tree);
		return showItem(tree, new ItemPath(path, delimiter));
	}

	/**
	 * Shows a {@link TreeItem} in a {@link Tree} given an {@link ItemPath}. {@link TreeItem}s in
	 * the path (<b>except</b> for the last one, the one to be returned) are expanded as necessary
	 * in order to ensure that their child items get created.
	 * 
	 * @param tree
	 *            the {@link Tree}
	 * @param path
	 *            the path to the desired {@link TreeItem}
	 * @return the TreeItem found @
	 */
	public TreeItem actionShowItem(Tree tree, ItemPath path) {
		checkWidget(tree);
		return showItem(tree, path);
	}

	/**
	 * Shows a {@link TreeItem} in a {@link Tree} given an {@link ItemPath}. {@link TreeItem}s in
	 * the path (<b>except</b> for the last one, the one to be returned) are expanded as necessary
	 * in order to ensure that their child items get created.
	 * 
	 * @param tree
	 *            the {@link Tree}
	 * @param path
	 *            the path to the desired {@link TreeItem}
	 * @return the TreeItem found @
	 */
	TreeItem showItem(Tree tree, ItemPath path) {
		TreeItem item = getItem(tree, path.getSegment(0));
		return TreeItemTester.getTreeItemTester().showItem(item, path.subPath(1));
	}

	void showItem(final TreeItem item) {
		syncExec(new Runnable() {
			public void run() {
				item.getParent().showItem(item);
			}
		});
	}

	/**
	 * @param tree
	 *            a Tree
	 * @param text
	 *            the text value of the root TreeItem wanted
	 * @return the root {@link TreeItem} whose text matches text, or <code>null</code> if there
	 * isn't one. @
	 */
	TreeItem getItem(Tree tree, String text) {
		TreeItem[] items = getItems(tree);
		return TreeItemTester.getTreeItemTester().findItem(items, text);
	}

	/* Methods for clicking or double-clicking on a TreeItem in a Tree. */

	public TreeItem actionClickItem(Tree tree, String path) {
		return actionClickItem(tree, new ItemPath(path));
	}

	public TreeItem actionClickItem(Tree tree, String path, String delimiter) {
		return actionClickItem(tree, new ItemPath(path, delimiter));
	}

	public TreeItem actionClickItem(Tree tree, ItemPath path) {
		checkWidget(tree);
		TreeItem item = getItem(tree, path.getSegment(0));
		return TreeItemTester.getTreeItemTester().actionClickItem(item, path.subPath(1));
	}

	public TreeItem actionDoubleClickItem(Tree tree, String path) {
		return actionDoubleClickItem(tree, new ItemPath(path));
	}

	public TreeItem actionDoubleClickItem(Tree tree, String path, String delimiter) {
		return actionDoubleClickItem(tree, new ItemPath(path, delimiter));
	}

	public TreeItem actionDoubleClickItem(Tree tree, ItemPath path) {
		checkWidget(tree);
		TreeItem item = getItem(tree, path.getSegment(0));
		return TreeItemTester.getTreeItemTester().actionDoubleClickItem(item, path.subPath(1));
	}

	/* Methods for checking/unchecking a TreeItem in a (checkable) Tree. */

	public TreeItem actionCheckItem(Tree tree, String path, boolean check) {
		return actionCheckItem(tree, new ItemPath(path), check);
	}

	public TreeItem actionCheckItem(Tree tree, String path, String delimiter, boolean check) {
		return actionCheckItem(tree, new ItemPath(path, delimiter), check);
	}

	public TreeItem actionCheckItem(Tree tree, ItemPath path, boolean check) {
		checkWidget(tree);
		TreeItem item = getItem(tree, path.getSegment(0));
		return TreeItemTester.getTreeItemTester().actionCheckItem(item, path.subPath(1), check);
	}

	/* Actions for invoking menu items. */

	public void actionClickMenuItem(Tree tree, String treeItemPath, ItemPath menuItemPath) {
		actionClickMenuItem(tree, new ItemPath(treeItemPath), menuItemPath);
	}

	public void actionClickMenuItem(Tree tree, String treeItemPath, String delimiter,
			ItemPath menuItemPath) {
		actionClickMenuItem(tree, new ItemPath(treeItemPath, delimiter), menuItemPath);
	}

	public void actionClickMenuItem(Tree tree, ItemPath treeItemPath, ItemPath menuItemPath) {
		checkWidget(tree);
		TreeItem item = getItem(tree, treeItemPath.getSegment(0));
		TreeItemTester.getTreeItemTester().actionClickMenuItem(
				item,
				treeItemPath.subPath(1),
				menuItemPath);
	}

	/* Proxy methods. */

	/**
	 * Proxy for {@link Tree#addSelectionListener(SelectionListener)}.
	 */
	public void addSelectionListener(final Tree tree, final SelectionListener listener) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#addTreeListener(TreeListener)}.
	 */
	public void addTreeListener(final Tree tree, final TreeListener listener) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.addTreeListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#removeSelectionListener(SelectionListener)}.
	 */
	public void removeSelectionListener(final Tree tree, final SelectionListener listener) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#removeTreeListener(TreeListener)}.
	 */
	public void removeTreeListener(final Tree tree, final TreeListener listener) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.removeTreeListener(listener);
			}
		});
	}

	public void deselectAll(final Tree tree) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.deselectAll();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getColumn(int)}.
	 */
	public TreeColumn getColumn(final Tree tree, final int index) {
		checkWidget(tree);
		return (TreeColumn) syncExec(new Result() {
			public Object result() {
				return tree.getColumn(index);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getColumnCount()}.
	 */
	public int getColumnCount(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getColumnCount();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getColumnOrder()}
	 */
	public int[] getColumnOrder(final Tree tree) {
		checkWidget(tree);
		return (int[]) syncExec(new Result() {
			public Object result() {
				return tree.getColumnOrder();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getColumns()}.
	 */
	public TreeColumn[] getColumns(final Tree tree) {
		checkWidget(tree);
		return (TreeColumn[]) syncExec(new Result() {
			public Object result() {
				return tree.getColumns();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getGridLineWidth()}.
	 */
	public int getGridLineWidth(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getGridLineWidth();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getHeaderHeight()}.
	 */
	public int getHeaderHeight(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getHeaderHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getHeaderVisible()}.
	 */
	public boolean getHeaderVisible(final Tree tree) {
		checkWidget(tree);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return tree.getHeaderVisible();
			}
		});
	}

	/**
	 * @see Tree#getItem(int)
	 */
	public TreeItem getItem(final Tree tree, final int index) {
		checkWidget(tree);
		return (TreeItem) syncExec(new Result() {
			public Object result() {
				return tree.getItem(index);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getItem(org.eclipse.swt.graphics.Point)}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @param point
	 *            the point under which the item to find.
	 * @return the tree item under the point.
	 */
	public TreeItem getItem(final Tree tree, final Point point) {
		checkWidget(tree);
		return (TreeItem) syncExec(new Result() {
			public Object result() {
				return tree.getItem(point);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getItemCount()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the number of items in the tree.
	 */
	public int getItemCount(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getItemHeight()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the height of the items.
	 */
	public int getItemHeight(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getItemHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getItems()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the children.
	 */
	public TreeItem[] getItems(final Tree tree) {
		checkWidget(tree);
		return (TreeItem[]) syncExec(new Result() {
			public Object result() {
				return tree.getItems();
			}
		});
	}

	/**
	 * @see Tree#getLinesVisible()
	 */
	public boolean getLinesVisible(final Tree tree) {
		checkWidget(tree);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return tree.getLinesVisible();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getParentItem()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the parent tree item.
	 */
	public TreeItem getParentItem(final Tree tree) {
		checkWidget(tree);
		return (TreeItem) syncExec(new Result() {
			public Object result() {
				return tree.getParentItem();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getSelection()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the slected items.
	 */
	public TreeItem[] getSelection(final Tree tree) {
		checkWidget(tree);
		return (TreeItem[]) syncExec(new Result() {
			public Object result() {
				return tree.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getSelectionCount()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the number of selected items.
	 */
	public int getSelectionCount(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getSelectionCount();
			}
		});
	}

	/**
	 * @see Tree#getSortColumn()
	 */
	public TreeColumn getSortColumn(final Tree tree) {
		checkWidget(tree);
		return (TreeColumn) syncExec(new Result() {
			public Object result() {
				return tree.getSortColumn();
			}
		});
	}

	/**
	 * @see Tree#getSortDirection()
	 */
	public int getSortDirection(final Tree tree) {
		checkWidget(tree);
		return syncExec(new IntResult() {
			public int result() {
				return tree.getSortDirection();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#getTopItem()}. <p/>
	 * 
	 * @param tree
	 *            the tree under test.
	 * @return the top item.
	 */
	public TreeItem getTopItem(final Tree tree) {
		checkWidget(tree);
		return (TreeItem) syncExec(new Result() {
			public Object result() {
				return tree.getTopItem();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#indexOf(TreeColumn)}.
	 */
	public int indexOf(final Tree tree, final TreeColumn treeColumn) {
		checkWidget(tree);
		checkWidget(treeColumn);
		return syncExec(new IntResult() {
			public int result() {
				return tree.indexOf(treeColumn);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#indexOf(TreeItem)}.
	 */
	public int indexOf(final Tree tree, final TreeItem treeItem) {
		checkWidget(tree);
		checkWidget(treeItem);
		return syncExec(new IntResult() {
			public int result() {
				return tree.indexOf(treeItem);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#selectAll()}.
	 * 
	 * @deprecated It's cheating...
	 */
	public void selectAll(final Tree tree) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.selectAll();
			}
		});
	}

	/**
	 * Proxy for {@link Tree#showColumn(TreeColumn)}.
	 * 
	 * @deprecated It's cheating...
	 */
	public void showColumn(final Tree tree, final TreeColumn treeColumn) {
		checkWidget(tree);
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				tree.showColumn(treeColumn);
			}
		});
	}

	/**
	 * Proxy for {@link Tree#showSelection()}.
	 * 
	 * @deprecated It's cheating...
	 */
	public void showSelection(final Tree tree) {
		checkWidget(tree);
		syncExec(new Runnable() {
			public void run() {
				tree.showSelection();
			}
		});
	}

}
