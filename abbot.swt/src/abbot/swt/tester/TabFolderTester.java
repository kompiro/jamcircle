package abbot.swt.tester;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link TabFolder}s.
 */
public class TabFolderTester extends CompositeTester {

	/**
	 * Factory method.
	 */
	public static TabFolderTester getTabFolderTester() {
		return (TabFolderTester) WidgetTester.getTester(TabFolder.class);
	}

	/**
	 * Constructs a new {@link TabFolderTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public TabFolderTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link TabFolder#addSelectionListener(SelectionListener listener)}.
	 */
	public void addSelectionListener(final TabFolder tabFolder, final SelectionListener listener) {
		checkWidget(tabFolder);
		syncExec(new Runnable() {
			public void run() {
				tabFolder.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#getClientArea()}.
	 */
	public Rectangle getClientArea(final TabFolder tabFolder) {
		checkWidget(tabFolder);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return tabFolder.getClientArea();
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#getItem(int)}.
	 */
	public TabItem getItem(final TabFolder tabFolder, final int index) {
		checkWidget(tabFolder);
		return (TabItem) syncExec(new Result() {
			public Object result() {
				return tabFolder.getItem(index);
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#getItemCount()}.
	 */
	public int getItemCount(final TabFolder tabFolder) {
		checkWidget(tabFolder);
		return syncExec(new IntResult() {
			public int result() {
				return tabFolder.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#getItems()}.
	 */
	public TabItem[] getItems(final TabFolder tabFolder) {
		checkWidget(tabFolder);
		return (TabItem[]) syncExec(new Result() {
			public Object result() {
				return tabFolder.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#getSelection()}.
	 */
	public TabItem[] getSelection(final TabFolder tabFolder) {
		checkWidget(tabFolder);
		return (TabItem[]) syncExec(new Result() {
			public Object result() {
				return tabFolder.getSelection();
			}
		});
	}

	/**
	 * Returns the first selected TabItem, or null, if no items are selected.
	 * 
	 * @param tabFolder
	 * @return the selected {@link TabItem}
	 */
	public TabItem getSelectionItem(final TabFolder tabFolder) {
		checkWidget(tabFolder);
		TabItem[] array = getSelection(tabFolder);
		if (array.length > 0)
			return array[0];
		return null;
	}

	/**
	 * Proxy for {@link TabFolder#getSelectionIndex()}.
	 */
	public int getSelectionIndex(final TabFolder tabFolder) {
		checkWidget(tabFolder);
		return syncExec(new IntResult() {
			public int result() {
				return tabFolder.getSelectionIndex();
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#indexOf(TabItem)}.
	 */
	public int indexOf(final TabFolder tabFolder, final TabItem item) {
		checkWidget(tabFolder);
		return syncExec(new IntResult() {
			public int result() {
				return tabFolder.indexOf(item);
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#setSelection(int)}.
	 */
	public void setSelection(final TabFolder tabFolder, final int index) {
		checkWidget(tabFolder);
		syncExec(new Runnable() {
			public void run() {
				tabFolder.setSelection(index);
			}
		});
	}

	/**
	 * Proxy for {@link TabFolder#removeSelectionListener(SelectionListener listener)}.
	 */
	public void removeSelectionListener(final TabFolder tabFolder, final SelectionListener listener) {
		checkWidget(tabFolder);
		syncExec(new Runnable() {
			public void run() {
				tabFolder.removeSelectionListener(listener);
			}
		});
	}
}
