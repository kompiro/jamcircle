package abbot.swt.tester;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link ToolBar}s.
 */
public class ToolBarTester extends CompositeTester {

	/**
	 * Factory method.
	 */
	public static ToolBarTester getToolBarTester() {
		return (ToolBarTester) WidgetTester.getTester(ToolBar.class);
	}

	/**
	 * Constructs a new {@link ToolBarTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ToolBarTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link ToolBar#getItem(int)}. <p/>
	 * 
	 * @param toolBar
	 *            the toolbar under test.
	 * @param index
	 *            the index for the item.
	 * @return the item at the index given.
	 */
	public ToolItem getItem(final ToolBar toolBar, final int index) {
		checkWidget(toolBar);
		return (ToolItem) syncExec(new Result() {
			public Object result() {
				return toolBar.getItem(index);
			}
		});
	}

	/**
	 * Proxy for {@link ToolBar#getItem(org.eclipse.swt.graphics.Point)}. <p/>
	 * 
	 * @param toolBar
	 *            the toolbar under test.
	 * @param point
	 *            the point to locate an item.
	 * @return the item under the point.
	 */
	public ToolItem getItem(final ToolBar toolBar, final Point point) {
		checkWidget(toolBar);
		return (ToolItem) syncExec(new Result() {
			public Object result() {
				return toolBar.getItem(point);
			}
		});
	}

	/**
	 * Proxy for {@link ToolBar#getItemCount()}. <p/>
	 * 
	 * @param toolBar
	 *            the toolbar under test.
	 * @return the number of items in the toolbar.
	 */
	public int getItemCount(final ToolBar toolBar) {
		checkWidget(toolBar);
		return syncExec(new IntResult() {
			public int result() {
				return toolBar.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link ToolBar#getItems()}. <p/>
	 * 
	 * @param toolBar
	 *            the toolbar under test.
	 * @return the items in the toolbar.
	 */
	public ToolItem[] getItems(final ToolBar toolBar) {
		checkWidget(toolBar);
		return (ToolItem[]) syncExec(new Result() {
			public Object result() {
				return toolBar.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link ToolBar#getRowCount()}. <p/>
	 * 
	 * @param toolBar
	 *            the toolbar under test.
	 * @return the number of rows.
	 */
	public int getRowCount(final ToolBar toolBar) {
		checkWidget(toolBar);
		return syncExec(new IntResult() {
			public int result() {
				return toolBar.getRowCount();
			}
		});
	}

	/**
	 * Proxy for {@link ToolBar#indexOf(org.eclipse.swt.widgets.ToolItem)}. <p/>
	 * 
	 * @param toolBar
	 *            the toolbar under test.
	 * @param toolItem
	 *            the search item.
	 * @return the index of the item.
	 */
	public int indexOf(final ToolBar toolBar, final ToolItem toolItem) {
		checkWidget(toolBar);
		checkWidget(toolItem);
		return syncExec(new IntResult() {
			public int result() {
				return toolBar.indexOf(toolItem);
			}
		});
	}
}
