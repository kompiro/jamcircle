package abbot.swt.tester;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link CoolBar}s.
 */
public class CoolBarTester extends CompositeTester {

	/**
	 * Factory method.
	 */
	public static CoolBarTester getCoolBarTester() {
		return (CoolBarTester) getTester(CoolBar.class);
	}

	/**
	 * Constructs a new {@link CoolBarTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public CoolBarTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Proxy for {@link CoolBar#getItem(int i)}.
	 */
	public CoolItem getItem(final CoolBar coolBar, final int i) {
		checkWidget(coolBar);
		return (CoolItem) syncExec(new Result() {
			public Object result() {
				return coolBar.getItem(i);
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#getItemCount()}.
	 */
	public int getItemCount(final CoolBar coolBar) {
		checkWidget(coolBar);
		return syncExec(new IntResult() {
			public int result() {
				return coolBar.getItemCount();
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#getItemOrder()}.
	 */
	public int[] getItemOrder(final CoolBar coolBar) {
		checkWidget(coolBar);
		return (int[]) syncExec(new Result() {
			public Object result() {
				return coolBar.getItemOrder();
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#getItems()}.
	 */
	public CoolItem[] getItems(final CoolBar coolBar) {
		checkWidget(coolBar);
		return (CoolItem[]) syncExec(new Result() {
			public Object result() {
				return coolBar.getItems();
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#getItemSizes()}.
	 */
	public Point[] getItemSizes(final CoolBar coolBar) {
		checkWidget(coolBar);
		return (Point[]) syncExec(new Result() {
			public Object result() {
				return coolBar.getItemSizes();
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#getLocked()}.
	 */
	public boolean getLocked(final CoolBar coolBar) {
		checkWidget(coolBar);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return coolBar.getLocked();
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#getWrapIndices()}.
	 */
	public int[] getWrappedIndices(final CoolBar coolBar) {
		checkWidget(coolBar);
		return (int[]) syncExec(new Result() {
			public Object result() {
				return coolBar.getWrapIndices();
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#indexOf(CoolItem)}.
	 */
	public int indexOf(final CoolBar coolBar, final CoolItem item) {
		checkWidget(coolBar);
		return syncExec(new IntResult() {
			public int result() {
				return coolBar.indexOf(item);
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#setLocked(boolean)}.
	 */
	public void setLocked(final CoolBar coolBar, final boolean locked) {
		checkWidget(coolBar);
		syncExec(new Runnable() {
			public void run() {
				coolBar.setLocked(locked);
			}
		});
	}

	/**
	 * Proxy for {@link CoolBar#setWrapIndices(int [])}.
	 */
	public void setWrapIndices(final CoolBar coolBar, final int[] indices) {
		checkWidget(coolBar);
		syncExec(new Runnable() {
			public void run() {
				coolBar.setWrapIndices(indices);
			}
		});
	}
}
