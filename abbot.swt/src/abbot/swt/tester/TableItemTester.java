package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.script.Condition;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link TableItem}s.
 * 
 * @author nntp_ds@fastmail.fm
 */
public class TableItemTester extends ItemTester {

	/**
	 * Factory method.
	 */
	public static TableItemTester getTableItemTester() {
		return (TableItemTester) WidgetTester.getTester(TableItem.class);
	}

	/**
	 * Constructs a new {@link TableItemTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public TableItemTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	/* Proxies */

	/**
	 * Determines whether or not a {@link TableItem} is visible.
	 * 
	 * @param item
	 *            a TableItem
	 * @return <code>true</code> if the {@link TableItem} is visible,
	 *         <code>false</code> otherwise
	 */
	public boolean isVisible(TableItem item) {
		Table table = getParent(item);
		return TableTester.getTableTester().isVisible(table);
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof TableItem)
			return isVisible((TableItem) widget);
		return super.isVisible(widget);
	}

	/**
	 * @see ItemTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		if (widget instanceof TableItem) {
			Table table = getParent((TableItem) widget);
			return TableTester.getTableTester().getMenu(table);
		}
		return super.getMenu(widget);
	}

	/* Proxies */

	/**
	 * Proxy for {@link TableItem#getBackground()}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @return the background color.
	 */
	public Color getBackground(final TableItem tableItem) {
		checkWidget(tableItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return tableItem.getBackground();
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getBackground(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @param column
	 *            the column to check.
	 * @return the background color.
	 */
	public Color getBackground(final TableItem tableItem, final int column) {
		checkWidget(tableItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return tableItem.getBackground(column);
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getBounds(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @param index
	 *            the column to check.
	 * @return the bounds of the column within the tableItem.
	 */
	public Rectangle getBounds(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return tableItem.getBounds(index);
			}
		});
	}

	/**
	 * @see TableItem#getChecked()
	 */
	public boolean getChecked(final TableItem tableItem) {
		checkWidget(tableItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return tableItem.getChecked();
			}
		});
	}

	/**
	 * @return <code>true</code> if a specified {@link TableItem} is currently
	 *         selected, <code>false</code> otherwise.
	 */
	public boolean getSelected(final TableItem item) {
		TableItem selectedItems[] = TableTester.getTableTester().getSelection(
				getParent(item));
		for (TableItem selectedItem : selectedItems) {
			if (selectedItem == item)
				return true;
		}
		return false;
	}

	/**
	 * Proxy for {@link TableItem#getFont()}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @return the font of the tableItem under test.
	 */
	public Font getFont(final TableItem tableItem) {
		checkWidget(tableItem);
		return (Font) syncExec(new Result() {
			public Object result() {
				return tableItem.getFont();
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getFont(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @param index
	 *            the column to check.
	 * @return the font of the column within the tableItem.
	 */
	public Font getFont(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		return (Font) syncExec(new Result() {
			public Object result() {
				return tableItem.getFont(index);
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getForeground()}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @return the foreground color.
	 */
	public Color getForeground(final TableItem tableItem) {
		checkWidget(tableItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return tableItem.getForeground();
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getForeground(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @param index
	 *            the column to check
	 * @return the foreground color of the column within the tableItem.
	 */
	public Color getForeground(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		return (Color) syncExec(new Result() {
			public Object result() {
				return tableItem.getForeground(index);
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getGrayed()}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @return the grayed state of the checkbox.
	 */
	public boolean getGrayed(final TableItem tableItem) {
		checkWidget(tableItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return tableItem.getGrayed();
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getImageBounds(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @param index
	 *            the column to check.
	 * @return the image bounds of the column within the tableItem.
	 */
	public Rectangle getImageBounds(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return tableItem.getImageBounds(index);
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getImageIndent()}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @return the image indent.
	 */
	public int getImageIndent(final TableItem tableItem) {
		checkWidget(tableItem);
		return syncExec(new IntResult() {
			public int result() {
				return tableItem.getImageIndent();
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getParent()}. <p/>
	 * 
	 * @param tableItem
	 *            the TableItem under test.
	 * @return the parent table of the tableItem under test.
	 */
	public Table getParent(final TableItem tableItem) {
		checkWidget(tableItem);
		return (Table) syncExec(new Result() {
			public Object result() {
				return tableItem.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getText(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the tableItem to retrieve the text from.
	 * @param index
	 *            the column for which the text will be retrieved.
	 * @return the text.
	 */
	public String getText(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		return syncExec(new StringResult() {
			public String result() {
				return tableItem.getText(index);
			}
		});
	}

	/**
	 * Proxy for {@link TableItem#getImage(int)}. <p/>
	 * 
	 * @param tableItem
	 *            the tableItem to retrieve the image from.
	 * @param index
	 *            the column for which the image will be retrieved.
	 * @return the image for the column.
	 */
	public Image getImage(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		return (Image) syncExec(new Result() {
			public Object result() {
				return tableItem.getImage(index);
			}
		});
	}

	/**
	 * Clicks in the center of the TableItem and the column given. <p/>
	 * 
	 * @param tableItem
	 *            the tableItem to click onto.
	 * @param index
	 *            the column to click onto.
	 */
	public void actionClickTableItem(final TableItem tableItem, final int index) {
		checkWidget(tableItem);
		syncExec(new Runnable() {
			public void run() {
				if (index >= 0
						&& index <= tableItem.getParent().getColumnCount()) {
					Rectangle bounds = tableItem.getBounds(index);
					TableItemTester.this.actionClick(tableItem.getParent(),
							bounds.x + bounds.width / 2, bounds.y
									+ bounds.height / 2);
				}
			}
		});
	}

	protected Rectangle getClickBounds(Widget widget) {
		if (widget instanceof TableItem)
			return getClickBounds((TableItem) widget);
		return super.getClickBounds(widget);
	}

	protected Rectangle getClickBounds(TableItem item) {
		return getClickBounds(item, 0);
	}

	protected Rectangle getClickBounds(final TableItem item, int index) {

		// Determine the item's text alignment as best we can.
		int align = SWT.LEFT;
		TableTester tableTester = TableTester.getTableTester();
		Table table = getParent(item);
		int columnCount = tableTester.getColumnCount(table);
		if (columnCount > 0) {
			TableColumn col = tableTester.getColumn(table, index);
			int style = TableColumnTester.getTableColumnTester().getStyle(col);
			align = style & (SWT.LEFT | SWT.RIGHT | SWT.CENTER);
		}
		
		// Feature in Windows: Apparently, column 0 text is always left-aligned
		// no matter what style is set on it (even though the style bit set may
		// indicate otherwise.
		if (index == 0 && SWT.getPlatform().equals("win32"))
			align = SWT.LEFT;

		// Determine the text's actual size (extent).
		Point textSize = (Point) syncExec(new Result<Point>() {
			public Point result() {
				GC gc = new GC(getDisplay());
				try {
					gc.setFont(item.getFont());
					return gc.textExtent(item.getText());
				} finally {
					if (gc != null)
						gc.dispose();
				}
			}
		});

		// Return the text's actual bounds.
		Rectangle bounds = getTextBounds(item, index);
		switch (align) {
		case SWT.LEFT:
			break;
		case SWT.RIGHT:
			bounds.x = bounds.x + bounds.width - textSize.x;
			break;
		case SWT.CENTER:
			bounds.x = bounds.x + (bounds.width - textSize.x) / 2;
			break;
		default:
			// Shouldn't be possible...
			throw new RuntimeException("bad style");
		}
		bounds.width = textSize.x;
		Point p = tableTester.toDisplay(getParent(item), bounds.x, bounds.y);
		bounds.x = p.x;
		bounds.y = p.y;
		return bounds;
	}

	/**
	 * @see TableItem#getTextBounds(int)
	 */
	public Rectangle getTextBounds(final TableItem item, final int index) {
		checkWidget(item);
		return (Rectangle) syncExec(new Result<Rectangle>() {
			public Rectangle result() {
				return item.getTextBounds(index);
			}
		});
	}

	public void actionSelectItem(final TableItem item, final boolean select) {
		checkWidget(item);

		if (getSelected(item) != select) {

			// Determine whether table is multi- or single-select.
			int tableStyle = TableTester.getTableTester().getStyle(
					getParent(item));
			boolean isMulti = (tableStyle & SWT.MULTI) != 0;

			// We cannot deselect the selected item in a single-select table.
			if (!isMulti && !select)
				throw new RuntimeException("cannot deselect");

			// Make sure the item is visible.
			showItem(item);

			// Click with button 1 (plus Ctrl if it's a multi-select table).
			actionClick(item, isMulti ? SWT.CTRL | SWT.BUTTON1 : SWT.BUTTON1);

			// Wait for the selection state to change.
			wait(new Condition() {
				private boolean selected;
				public boolean test() {
					selected = getSelected(item);
					return selected == select;
				}
				public String toString() {
					return String.format("selected %s %s (got %s)", item, select, selected);
				}
			}, 5000L);
		}
	}

	public void actionCheckItem(final TableItem item, final boolean check) {
		checkWidget(item);

		if (getChecked(item) != check) {

			showItem(item);

			actionClick(item);

			// Need to move focus over to the checkbox.
			if (SWT.getPlatform().equals("gtk")) {
				actionKey(SWT.ARROW_LEFT);
				actionKey(SWT.ARROW_LEFT);
			}

			actionKey(' ');

			wait(new Condition() {
				public boolean test() {
					return getChecked(item) == check;
				}
			}, 5000L);
		}
	}

	void showItem(TableItem item) {
		TableTester.getTableTester().showItem(getParent(item), item);
	}

}
