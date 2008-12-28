package abbot.swt.tester;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import abbot.swt.WidgetLocator;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Table}s.
 * <p>
 * Note that actionShowTableColumn, actionSelectTableColumn, and actionResizeTableColumn are
 * currently platform-dependent.
 */
public class TableTester extends CompositeTester {

	/**
	 * Factory method.
	 */
	public static TableTester getTableTester() {
		return (TableTester) WidgetTester.getTester(Table.class);
	}

	/**
	 * Constructs a new {@link TableTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public TableTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link Table#getItem(int)}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @param index
	 *            the index of the item.
	 * @return the item at the index.
	 */
	public TableItem getItem(final Table table, final int index) {
		checkWidget(table);
		return (TableItem) syncExec(new Result() {
			public Object result() {
				return table.getItem(index);
			}
		});
	}

	/**
	 * Gets a {@link TableItem} that has the specified text.
	 * 
	 * @param table
	 *            the table under test.
	 * @param text
	 *            the text of the item.
	 * @return the item that has the text (or <code>null</code> if there isn't one)
	 */
	public TableItem getItem(final Table table, final String text) {
		checkWidget(table);
		return (TableItem) syncExec(new Result() {
			public Object result() {
				TableItem[] items = table.getItems();
				for (TableItem item : items) {
					if (text.equals(item.getText()))
						return item;
				}
				return null;
			}
		});
	}

	/**
	 * Proxy for {@link Table#getItem(org.eclipse.swt.graphics.Point)}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @param point
	 *            the point to find the item under.
	 * @return the item at the point.
	 */
	public TableItem getItem(final Table table, final Point point) {
		checkWidget(table);
		return (TableItem) syncExec(new Result() {
			public Object result() {
				return table.getItem(point);
			}
		});
	}

	/**
	 * Proxy for {@link Table#getColumn(int)}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @param index
	 *            the index of the column.
	 * @return the column at the index.
	 */
	public TableColumn getColumn(final Table table, final int index) {
		checkWidget(table);
		return (TableColumn) syncExec(new Result() {
			public Object result() {
				return table.getColumn(index);
			}
		});
	}

	/**
	 * Proxy for {@link Table#getColumnCount()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the number of columns of this table.
	 */
	public int getColumnCount(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getColumnCount();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getColumns()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the table columns.
	 */
	public TableColumn[] getColumns(final Table table) {
		checkWidget(table);
		return (TableColumn[]) syncExec(new Result() {
			public Object result() {
				return table.getColumns();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getGridLineWidth()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the grid line width.
	 */
	public int getGridLineWidth(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getGridLineWidth();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getHeaderHeight()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the header height.
	 */
	public int getHeaderHeight(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getHeaderHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getHeaderVisible()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return true if the header is visible.
	 */
	public boolean getHeaderVisible(final Table table) {
		checkWidget(table);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return table.getHeaderVisible();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getItemCount()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the number of rows in the table.
	 */
	public int getItemCount(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getItemHeight()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the height of the items.
	 */
	public int getItemHeight(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getItemHeight();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getItems()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the items in the table.
	 */
	public TableItem[] getItems(final Table table) {
		checkWidget(table);
		return (TableItem[]) syncExec(new Result() {
			public Object result() {
				return table.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getLinesVisible()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return true if the lines are visible.
	 */
	public boolean getLinesVisible(final Table table) {
		checkWidget(table);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return table.getLinesVisible();
			}
		});
	}

	/**
	 * @deprecated Use {@link #getSelection(Table)}.
	 */
	public TableItem[] getSelectedTableItems(Table table) {
		return getSelection(table);
	}

	/**
	 * Proxy for {@link Table#getSelection()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the selected items.
	 */
	public TableItem[] getSelection(final Table table) {
		checkWidget(table);
		return (TableItem[]) syncExec(new Result() {
			public Object result() {
				return table.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getSelectionCount()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the number of selected items.
	 */
	public int getSelectionCount(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getSelectionCount();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getSelectionIndex()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the index of the selected item.
	 */
	public int getSelectionIndex(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getSelectionIndex();
			}
		});
	}

	/**
	 * Proxy for {@link Table#getTopIndex()}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @return the index of the top item.
	 */
	public int getTopIndex(final Table table) {
		checkWidget(table);
		return syncExec(new IntResult() {
			public int result() {
				return table.getTopIndex();
			}
		});
	}

	/**
	 * Proxy for {@link Table#indexOf(org.eclipse.swt.widgets.TableColumn)}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @param tableColumn
	 *            the column to return the index for.
	 * @return the index of the column given.
	 */
	public int indexOf(final Table table, final TableColumn tableColumn) {
		checkWidget(table);
		checkWidget(tableColumn);
		return syncExec(new IntResult() {
			public int result() {
				return table.indexOf(tableColumn);
			}
		});
	}

	/**
	 * Proxy for {@link Table#indexOf(org.eclipse.swt.widgets.TableItem)}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @param tableItem
	 *            the item to return the index for.
	 * @return the index of the item given.
	 */
	public int indexOf(final Table table, final TableItem tableItem) {
		checkWidget(table);
		checkWidget(tableItem);
		return syncExec(new IntResult() {
			public int result() {
				return table.indexOf(tableItem);
			}
		});
	}

	/**
	 * Proxy for {@link Table#isSelected(int)}. <p/>
	 * 
	 * @param table
	 *            the table under test.
	 * @param index
	 *            the index to return the selected property for.
	 * @return true if the index given is selected.
	 */
	public boolean isSelected(final Table table, final int index) {
		checkWidget(table);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return table.isSelected(index);
			}
		});
	}

	/* End getters */

	/** Returns the indices of all selected items in the given table * */
	public int[] getSelectedTableIndices(final Table table) {
		checkWidget(table);
		return (int[]) syncExec(new Result() {
			public Object result() {
				return table.getSelectionIndices();
			}
		});
	}

	/** Returns the all checked items in the given table * */
	public TableItem[] getCheckedTableItems(final Table table) {
		checkWidget(table);
		return (TableItem[]) syncExec(new Result() {
			public Object result() {
				List<TableItem> checked = new ArrayList<TableItem>();
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getChecked())
						checked.add(items[i]);
				}
				return checked.toArray(new TableItem[checked.size()]);
			}
		});
	}

	/** Returns the indices of all checked items in the given table * */
	public int[] getCheckedTableIndices(final Table table) {
		checkWidget(table);
		List checkedIndicesList = (List) syncExec(new Result() {
			public Object result() {
				List<Integer> checkedIndices = new ArrayList<Integer>();
				TableItem[] items = table.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getChecked())
						checkedIndices.add(Integer.valueOf(i));
				}
				return checkedIndices;
			}
		});
		int[] checkedIndices = new int[checkedIndicesList.size()];
		for (int i = 0; i < checkedIndices.length; i++) {
			checkedIndices[i] = ((Integer) checkedIndicesList.get(i)).intValue();
		}
		return checkedIndices;
	}

	/** Move the mouse pointer over the given TableItem * */
	protected synchronized void mouseMoveTableItem(final Table table, final TableItem tableItem) {
		checkWidget(table);
		checkWidget(tableItem);
		Point p = (Point) syncExec(new Result() {
			public Object result() {
				table.setFocus();
				table.showItem(tableItem);
				Rectangle bounds = WidgetLocator.getBounds(table, true);
				int col0Width = table.getColumn(0).getWidth();
				int itemHeight = table.getItemHeight();
				Point p = new Point(col0Width / 2, itemHeight / 2);
				TableItem itemAtPoint = null;
				while (true) {
					if (p.y > bounds.y + bounds.height)
						break;
					itemAtPoint = table.getItem(p);
					if (itemAtPoint == tableItem)
						break;
					p.y += itemHeight;
				}
				if (itemAtPoint == tableItem)
					return new Point(bounds.x + p.x, bounds.y + p.y);
				return null;
			}
		});
		if (p != null)
			mouseMove(p.x, p.y);
		actionWaitForIdle();
	}

	/** Move the mouse pointer over the TableItem at the given index * */
	protected void mouseMoveTableIndex(final Table table, final int index) {
		checkWidget(table);
		TableItem item = getItem(table, index);
		if (item != null)
			mouseMoveTableItem(table, item);
	}

	/** Move the mouse pointer to the top of the given column * */
	protected void mouseMoveTopOfColumn(final Table table, final TableColumn tableColumn) {
		checkWidget(table);
		checkWidget(tableColumn);
		syncExec(new Runnable() {
			public void run() {
				table.setFocus();
				TableColumn[] cols = table.getColumns();
				int width = 0;
				int i;
				for (i = 0; i < cols.length; i++) {
					width += cols[i].getWidth();
					if (tableColumn == cols[i])
						break;
				}
				if (tableColumn == cols[i]) {
					int height = table.getHeaderHeight() / 2;
					width -= tableColumn.getWidth() / 2;
					Point loc = WidgetLocator.getLocation(table);
					mouseMove(loc.x + width, loc.y + height);
				}
			}
		});
		actionWaitForIdle();
	}

	/**
	 * Click an item in the given table.
	 * <p>
	 * <b>Note:</b> Support has been added for the case when a table is being used as a list and
	 * has no columns, but this may not work in all cases depending on the alignment of the text in
	 * the table. The problem is that on some platforms only the text is clickable for purposes of
	 * making a selection, but the current SWT API provides no method for getting the bounds of this
	 * text.
	 */
	public void actionClickTableItem(final Table table, final TableItem tableItem) {
		checkWidget(table);
		checkWidget(tableItem);
		actionClickTableItem(table, tableItem, 0);
	}

	public void actionClickTableItem(final Table table, final TableItem tableItem, final int column) {
		checkWidget(table);
		checkWidget(tableItem);
		Point point = (Point) syncExec(new Result<Point>() {
			public Point result() {
				/* Give the Table focus. */
				table.setFocus();
				table.showItem(tableItem);

				/* Get relative bounds for table and item. */
				Rectangle relativeBoundsOfItem = tableItem.getBounds(column);
//				Log.log("relativeBoundsOfItem[" + column + "]:" + relativeBoundsOfItem);

				/* Determine relative click point. */
				int x = relativeBoundsOfItem.x + (relativeBoundsOfItem.width / 2);
				int y = relativeBoundsOfItem.y + (relativeBoundsOfItem.height / 2);

				/* Return clickpoint in display coordinates. */
				return table.toDisplay(x, y);
			}
		});
		robot.mouseClick(point.x, point.y, SWT.BUTTON1);
	}

	/** Click the item at the given index * */
	public void actionClickTableIndex(final Table table, final int index) {
		checkWidget(table);
		TableItem item = getItem(table, index);
		if (item != null)
			actionClickTableItem(table, item);
	}

	/** Click the given column's header (if headers are visible). */
	public void actionClickTableColumnHeader(final Table table, TableColumn tableColumn) {
		checkWidget(table);
		checkWidget(tableColumn);
		if (getHeaderVisible(table)) {
			actionShowTableColumn(table, tableColumn); // also positions pointer above header
			robot.mouseClick(SWT.BUTTON1);
		}
	}

	/* Methods for selecting/unselecting a TableItem in a Table. */

	public TableItem actionSelectItem(Table table, int index, boolean select) {
		checkWidget(table);
		TableItem item = getItem(table, index);
		TableItemTester.getTableItemTester().actionSelectItem(item, select);
		return item;
	}

	public TableItem actionSelectItem(Table table, String text, boolean select) {
		checkWidget(table);
		TableItem item = getItem(table, text);
		TableItemTester.getTableItemTester().actionSelectItem(item, select);
		return item;
	}

	/**
	 * @deprecated Use {@link #actionSelectItem(Table, int, boolean)} or
	 *             {@link #actionSelectItem(Table, String, boolean)} or
	 *             {@link TableItemTester#actionSelectItem(TableItem, boolean)}.
	 */
	public void actionSelectTableItem(final Table table, final TableItem item) {
		TableItemTester tester = TableItemTester.getTableItemTester();
		boolean selected = tester.getSelected(item);
		tester.actionSelectItem(item, !selected);
		// checkWidget(table);
		// checkWidget(tableItem);
		// Tao Weng 04/25/2005 10:39:05 AM:
		// > I have to comment out "mouseMoveTableItem(table,item);" from
		// > actionSelectTableItem. It seems to me that if I have that line
		// > uncommented, my script just hangs.
		// mouseMoveTableItem(table,tableItem);
		// syncExec(new Runnable() {
		// public void run() {
		// table.setFocus();
		// TableItem[] items = table.getItems();
		// int i;
		// for (i = 0; i < items.length; i++) {
		// if (items[i] == tableItem)
		// break;
		// }
		// int index = i;
		// if (items[i] == tableItem) {
		// table.showItem(tableItem);
		// TableItem[] selectedItems = getSelectedTableItems(table);
		// boolean selected = false;
		// for (i = 0; i < selectedItems.length; i++) {
		// if (selectedItems[i] == tableItem) {
		// selected = true;
		// table.deselect(i);
		// break;
		// }
		// }
		// if (!selected)
		// table.select(index);
		// }
		// }
		// });
		// actionWaitForIdle();
	}

	/**
	 * @deprecated Use {@link #actionSelectItem(Table, int, boolean)} or
	 *             {@link #actionSelectItem(Table, String, boolean)} or
	 *             {@link TableItemTester#actionSelectItem(TableItem, boolean)}.
	 */
	public void actionSelectTableIndex(Table table, int index) {
		TableItem item = getItem(table, index);
		TableItemTester tester = TableItemTester.getTableItemTester();
		boolean selected = tester.getSelected(item);
		tester.actionSelectItem(item, !selected);
		// checkWidget(table);
		// mouseMoveTableIndex(table, index);
		// syncExec(new Runnable() {
		// public void run() {
		// table.setFocus();
		// TableItem item = null;
		// try {
		// item = table.getItem(index);
		// } catch (Exception ignored) {
		// }
		// if (item != null) {
		// table.showItem(item);
		// TableItem[] selectedItems = getSelectedTableItems(table);
		// boolean selected = false;
		// for (int i = 0; i < selectedItems.length; i++) {
		// if (selectedItems[i] == item) {
		// selected = true;
		// table.deselect(i);
		// break;
		// }
		// }
		// if (!selected)
		// table.select(index);
		// }
		// }
		// });
		// actionWaitForIdle();
	}

	/** Put the item as high as possible in the given table's viewing window * */
	public void actionShowTableItem(final Table table, final TableItem tableItem) {
		checkWidget(table);
		checkWidget(tableItem);

		// TODO Needed?
		syncExec(new Runnable() {
			public void run() {
				table.setFocus();
			}
		});

		showItem(table, tableItem);

		// TODO Needed?
		actionWaitForIdle();
	}

	/** Put the item at the given index as high as possible in the table's viewing window * */
	public void actionShowTableIndex(final Table table, final int index) {
		checkWidget(table);
		syncExec(new Runnable() {
			public void run() {
				table.setFocus();
				table.setTopIndex(index);
			}
		});
		actionWaitForIdle();
	}

	/**
	 * Shows a specified {@link TableItem}.
	 * 
	 * @param table
	 *            the {@link Table}
	 * @param item
	 *            the {@link TableItem} to be shown
	 */
	void showItem(final Table table, final TableItem item) {
		syncExec(new Runnable() {
			public void run() {
				table.showItem(item);
			}
		});
	}

	public int getColumnWidth(final TableColumn column) {
		return syncExec(new IntResult() {
			public int result() {
				return column.getWidth();
			}
		});
	}

	/**
	 * Scroll so that the given table column is visible, and place the mouse over the column's
	 * heading, if visible.
	 */
	public void actionShowTableColumn(final Table table, final TableColumn tableColumn) {
		checkWidget(table);
		checkWidget(tableColumn);

		setFocus(table);

		TableColumn[] cols = getColumns(table);
		int width = 0;
		int totalWidth = 0;
		boolean found = false;
		for (int i = 0; i < cols.length; i++) {
			int w = getColumnWidth(cols[i]);
			totalWidth += w;
			if (cols[i] == tableColumn) {
				width += w / 2;
				found = true;
			}
			if (!found)
				width += w;
		}

		ScrollBarTester tester = (ScrollBarTester) WidgetTester.getTester(ScrollBar.class);
		ScrollBar scrollBar = getHorizontalBar(table);

		int max = tester.getMaximum(scrollBar);
		int min = tester.getMinimum(scrollBar);

		int tableWidth = getBounds(table).width;

		int setScroll = (int) ((double) ((double) width / (double) totalWidth) * (max - min))
				- tableWidth / 2;
		tester.actionScrollSetSelection(scrollBar, setScroll);

		Point tableLoc = getDisplayLocation(table);

		// Now move mouse to top of column.
		int selection = tester.getSelection(scrollBar);
		double thumbEdge = (double) ((double) selection / (double) (max - min));
		int leftEdgeOfTableShowing = (int) (thumbEdge * totalWidth);

		Rectangle scrollBounds = tester.getBounds(scrollBar);

		int offset = width - leftEdgeOfTableShowing;
		if (offset >= scrollBounds.width) {
//			System.out.println("offset to large(" + offset + ")");
			offset = scrollBounds.width - 2;
		}

		mouseMove(tableLoc.x + (offset), tableLoc.y + getHeaderHeight(table) / 2);

		actionWaitForIdle();
	}

	// public void actionShowTableColumn(final Table table, final TableColumn tableColumn) {
	// checkWidget(table);
	// checkWidget(tableColumn);
	// syncExec(new Runnable() {
	// public void run() {
	// table.setFocus();
	// ScrollBar scrollBar = table.getHorizontalBar();
	//
	// int width = 0;
	// int totalWidth = 0;
	// TableColumn[] cols = table.getColumns();
	// for (int i = 0; i < cols.length; i++) {
	// totalWidth += cols[i].getWidth();
	// }
	// for (int i = 0; i < cols.length && cols[i] != tableColumn; i++) {
	// width += cols[i].getWidth();
	// }
	// width += tableColumn.getWidth() / 2;
	//
	// ScrollBarTester tester = (ScrollBarTester) WidgetTester.getTester(ScrollBar.class);
	// Rectangle scrollBounds = SWTWorkarounds.getBounds(scrollBar);
	//
	// int max = scrollBar.getMaximum();
	// int min = scrollBar.getMinimum();
	//
	// int tableWidth = table.getBounds().width;
	// int setScroll = (int) ((double) ((double) width / (double) totalWidth) * (max - min))
	// - tableWidth / 2;
	// // System.out.println( "setScroll="+setScroll+" min="
	// // +scrollBar.getMinimum()+" max="+scrollBar.getMaximum()
	// // +" thumb="+scrollBar.getThumb()
	// // +" width="+width+" totalWidth="+totalWidth
	// // +" tableWidth="+tableWidth+"scrollWidth="+scrollBounds.width);
	//
	// tester.actionScrollSetSelection(scrollBar, setScroll);
	//
	// // now move mouse to top of column
	// int selection = scrollBar.getSelection();
	// // int thumb = scrollBar.getThumb();
	// double thumbEdge = (double) ((double) selection / (double) (max - min));
	// int leftEdgeOfTableShowing = (int) (thumbEdge * totalWidth);
	//
	// Point tableLoc = WidgetLocator.getLocation(table);
	//
	// // System.out.println("Selection="+selection+"
	// // leftEdge...="+leftEdgeOfTableShowing);
	// int offset = width - leftEdgeOfTableShowing;
	// if (offset >= scrollBounds.width) {
	// System.out.println("offset to large(" + offset + ")");
	// offset = scrollBounds.width - 2;
	// }
	// mouseMove(tableLoc.x + (offset), tableLoc.y + table.getHeaderHeight() / 2);
	// }
	// });
	// actionWaitForIdle();
	// }

	/* Methods for checking/unchecking a TableItem in a (checkable) Table. */

	public TableItem actionCheckItem(Table table, int index, boolean check) {
		checkWidget(table);

		TableItem item = getItem(table, index);
		TableItemTester.getTableItemTester().actionCheckItem(item, check);
		return item;
	}

	public TableItem actionCheckItem(Table table, String text, boolean check) {
		checkWidget(table);

		TableItem item = getItem(table, text);
		TableItemTester.getTableItemTester().actionCheckItem(item, check);
		return item;
	}

	/**
	 * Toggle the checked state of a {@link TableItem}.
	 * 
	 * @deprecated Use {@link #actionCheckItem(Table, int, boolean)} or
	 *             {@link #actionCheckItem(Table, String, boolean)} or
	 *             {@link TableItemTester#actionCheckItem(TableItem, boolean)}.
	 */
	public void actionCheckTableItem(final Table table, final TableItem item) {
		TableItemTester tester = TableItemTester.getTableItemTester();
		boolean checked = tester.getChecked(item);
		tester.actionCheckItem(item, checked);
	}

	/**
	 * Check the check box for the item at the given index in the table, or uncheck it if it was
	 * already checked.
	 * 
	 * @deprecated Use {@link #actionCheckItem(Table, int, boolean)} or
	 *             {@link #actionCheckItem(Table, String, boolean)} or
	 *             {@link TableItemTester#actionCheckItem(TableItem, boolean)}.
	 */
	public void actionCheckTableIndex(final Table table, final int index) {
		actionCheckTableItem(table, getItem(table, index));
	}

	/** Resize the column in the given table * */
	public void actionResizeTableColumn(final Table table, final TableColumn tableColumn,
			final int width) {
		checkWidget(table);
		checkWidget(tableColumn);
		actionShowTableColumn(table, tableColumn);
		mouseMoveTopOfColumn(table, tableColumn);
		syncExec(new Runnable() {
			public void run() {
				table.setFocus();
				tableColumn.setWidth(width);
				// TODO need to make the column visible here and maybe move the mouse over it
			}
		});
		actionWaitForIdle();
		mouseMoveTopOfColumn(table, tableColumn);
		actionWaitForIdle();
	}

	/**
	 * @see Table#addSelectionListener(SelectionListener)
	 */
	public void addSelectionListener(final Table table, final SelectionListener listener) {
		syncExec(new Runnable() {
			public void run() {
				table.addSelectionListener(listener);
			}
		});
	}

	/**
	 * @see Table#removeSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(final Table table, final SelectionListener listener) {
		syncExec(new Runnable() {
			public void run() {
				table.removeSelectionListener(listener);
			}
		});
	}

}
