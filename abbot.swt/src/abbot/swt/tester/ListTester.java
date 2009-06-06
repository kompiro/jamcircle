package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.List;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link List}s.
 */
public class ListTester extends ScrollableTester {

	/**
	 * Factory method.
	 */
	public static ListTester getListTester() {
		return (ListTester) WidgetTester.getTester(List.class);
	}

	/**
	 * Constructs a new {@link ListTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public ListTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link List#addSelectionListener(SelectionListener)}.
	 */
	public void addSelectionListener(final List list,
			final SelectionListener listener) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link List#removeSelectionListener(SelectionListener)}.
	 */
	public void removeSelectionListener(final List list,
			final SelectionListener listener) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.removeSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for {@link List#getItem(int index)}.
	 */
	public String getItem(final List list, final int index) {
		checkWidget(list);
		return syncExec(new StringResult() {
			public String result() {
				return list.getItem(index);
			}
		});
	}

	/**
	 * Proxy for {@link List#getItemCount()}.
	 */
	public int getItemCount(final List list) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link List#getItemHeight()}.
	 */
	public int getItemHeight(final List list) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.getItemHeight();
			}
		});
	}

	/**
	 * Proxy for {@link List#getItems()}.
	 */
	public String[] getItems(final List list) {
		checkWidget(list);
		return (String[]) syncExec(new Result<Object>() {
			public Object result() {
				return list.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link List#getSelection()}.
	 */
	public String[] getSelection(final List list) {
		checkWidget(list);
		return (String[]) syncExec(new Result<Object>() {
			public Object result() {
				return list.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link List#getSelectionCount()}.
	 */
	public int getSelectionCount(final List list) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.getSelectionCount();
			}
		});
	}

	/**
	 * Proxy for {@link List#getSelectionIndex()}.
	 */
	public int getSelectionIndex(final List list) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.getSelectionIndex();
			}
		});
	}

	/**
	 * Proxy for {@link List#getSelectionIndices()}.
	 */
	public int[] getSelectionIndices(final List list) {
		checkWidget(list);
		return (int[]) syncExec(new Result<Object>() {
			public Object result() {
				return list.getSelectionIndices();
			}
		});
	}

	/**
	 * Proxy for {@link List#getTopIndex()}.
	 */
	public int getTopIndex(final List list) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.getTopIndex();
			}
		});
	}

	/**
	 * Proxy for {@link List#indexOf(String string)}
	 */
	public int indexOf(final List list, final String string) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.indexOf(string);
			}
		});
	}

	/**
	 * Proxy for {@link List#indexOf(String,int)}
	 */
	public int indexOf(final List list, final String string, final int start) {
		checkWidget(list);
		return syncExec(new IntResult() {
			public int result() {
				return list.indexOf(string, start);
			}
		});
	}

	/**
	 * Proxy for {@link List#isSelected(int)}
	 */
	public boolean isSelected(final List list, final int index) {
		checkWidget(list);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return list.isSelected(index);
			}
		});
	}

	/**
	 * Proxy for {@link List#showSelection()}.
	 */
	public void showSelection(final List list) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.showSelection();
			}
		});
	}

	/**
	 * Proxy for {@link List#select(int)}.
	 */
	public void select(final List list, final int index) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.select(index);
			}
		});
	}

	/**
	 * Proxy for {@link List#select(int,int)}.
	 */
	public void select(final List list, final int start, final int end) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.select(start, end);
			}
		});
	}

	/**
	 * Proxy for {@link List#select(int [])}.
	 */
	public void select(final List list, final int[] indices) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.select(indices);
			}
		});
	}

	/**
	 * Proxy for {@link List#selectAll()}.
	 */
	public void selectAll(final List list) {
		checkWidget(list);
		syncExec(new Runnable() {
			public void run() {
				list.selectAll();
			}
		});
	}

	public void setTopIndex(final List list, final int index) {
		syncExec(new Runnable() {
			public void run() {
				list.setTopIndex(index);
			}
		});
	}

	/** Move the mouse pointer over the item at the given index */
	protected Point getLocation(final List list, final int index) {
		checkWidget(list);

		setTopIndex(list, index);
		int n = index - getTopIndex(list);
		if (n < 0)
			throw new RuntimeException("huh?");

		Rectangle ca = getClientArea(list);
		int x = ca.x + ca.width / 2;

		// On GTK we apparently need to include the border width.
		// On Windows we apparently do not.
		int h = getItemHeight(list);
		if (SWT.getPlatform().equals("gtk"))
			h += getBorderWidth(list);
		int y = ca.y + n * h + h / 2;

		return toDisplay(list, x, y);
	}

	/** Click the item at the given index */
	public void actionClickIndex(List list, int index) {
		checkWidget(list);
		actionClickIndex(list, index, SWT.BUTTON1);
	}

	/** Click the first occurance of the specified item. */
	public void actionClickItem(List list, String item) {
		checkWidget(list);
		actionClickItem(list, item, SWT.BUTTON1);
	}

	/** Click the item at the given index based on the given accelerator * */
	public void actionClickIndex(List list, int index, int accelerator) {
		checkWidget(list);

		actionFocus(list); // Needed?

		Point p = getLocation(list, index);
		robot.mouseClick(p.x, p.y, accelerator);
	}

	/** Click the first occurance of given item based on the given accelerator * */
	public void actionClickItem(List list, String item, int accelerator) {
		checkWidget(list);

		int index = indexOf(list, item);
		if (index == -1)
			throw new ActionFailedException("no such item: " + item);

		Point p = getLocation(list, index);
		robot.mouseClick(p.x, p.y, accelerator);
	}

	/**
	 * Selects the item at the given index, or unselects it if it was already
	 * selected.
	 */
	public void actionSelectIndex(List list, int index) {
		checkWidget(list);

		actionFocus(list); // Needed?

		Point p = getLocation(list, index);
		robot.mouseClick(p.x, p.y, SWT.CTRL | SWT.BUTTON1);
	}

	/**
	 * Select the first occurance of an item from the given list if it wasn't
	 * already selected, and unselect it otherwise.
	 */
	public void actionSelectItem(List list, String item) {
		checkWidget(list);

		actionClickItem(list, item, SWT.CTRL | SWT.BUTTON1);
	}

	/**
	 * Double-click the item at the given index.
	 * 
	 * @author Markus Kuhn <markuskuhn@users.sourceforge.net>
	 */
	public void actionDoubleClickIndex(List list, int index) {
		checkWidget(list);

		actionDoubleClickIndex(list, index, SWT.BUTTON1);
	}

	/*
	 * @author Markus Kuhn <markuskuhn@users.sourceforge.net>
	 */
	public void actionDoubleClickIndex(List list, int index, int buttonmask) {
		checkWidget(list);

		actionFocus(list); // Needed?

		Point p = getLocation(list, index);
		robot.mouseDoubleClick(p.x, p.y, buttonmask);
	}
}
