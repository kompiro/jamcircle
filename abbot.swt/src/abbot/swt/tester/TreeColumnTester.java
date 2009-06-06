package abbot.swt.tester;

import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link TreeColumn}s.
 * 
 * @author Gary Johnston
 */
public class TreeColumnTester extends ItemTester {

	/**
	 * Factory method.
	 */
	public static TreeColumnTester getTreeColumnTester() {
		return (TreeColumnTester) WidgetTester.getTester(TreeColumn.class);
	}

	/**
	 * Constructs a new {@link TreeColumnTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public TreeColumnTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	/* Proxies */

	/**
	 * @return <code>true</code> if the {@link TreeColumn} is visible, <code>false</code>
	 *         otherwise
	 */
	public boolean isVisible(TreeColumn column) {
		Tree tree = getParent(column);
		return TreeTester.getTreeTester().isVisible(tree);
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof TreeColumn)
			return isVisible((TreeColumn) widget);
		return super.isVisible(widget);
	}

	/**
	 * @see WidgetTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		if (widget instanceof TreeColumn) {
			Tree tree = getParent((TreeColumn) widget);
			return TreeTester.getTreeTester().getMenu(tree);
		}
		return super.getMenu(widget);
	}

	/* Proxies */

	/** @see TreeColumn#addControlListener(ControlListener) */
	public void addControlListener(final TreeColumn treeColumn, final ControlListener listener) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.addControlListener(listener);
			}
		});
	}

	/** @see TreeColumn#removeControlListener(ControlListener) */
	public void removeControlListener(final TreeColumn treeColumn, final ControlListener listener) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.removeControlListener(listener);
			}
		});
	}

	/** @see TreeColumn#addSelectionListener(SelectionListener) */
	public void addSelectionListener(final TreeColumn treeColumn, final SelectionListener listener) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.addSelectionListener(listener);
			}
		});
	}

	/** @see TreeColumn#removeSelectionListener(SelectionListener) */
	public void removeSelectionListener(final TreeColumn treeColumn,
			final SelectionListener listener) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.removeSelectionListener(listener);
			}
		});
	}

	/** @see TreeColumn#getAlignment() */
	public int getAlignment(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		return syncExec(new IntResult() {
			public int result() {
				return treeColumn.getAlignment();
			}
		});
	}

	/** @see TreeColumn#setAlignment(int) */
	public void setAlignment(final TreeColumn treeColumn, final int alignment) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.setAlignment(alignment);
			}
		});
	}

	/** @see TreeColumn#getMoveable() */
	public boolean getMoveable(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return treeColumn.getMoveable();
			}
		});
	}

	/** @see TreeColumn#setMoveable(boolean) */
	public void setMoveable(final TreeColumn treeColumn, final boolean moveable) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.setMoveable(moveable);
			}
		});
	}

	/** @see TreeColumn#getParent() */
	public Tree getParent(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		return (Tree) syncExec(new Result() {
			public Object result() {
				return treeColumn.getParent();
			}
		});
	}

	/** @see TreeColumn#getResizable() */
	public boolean getResizable(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return treeColumn.getResizable();
			}
		});
	}

	/** @see TreeColumn#setResizable(boolean) */
	public void setResizable(final TreeColumn treeColumn, final boolean resizable) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.setResizable(resizable);
			}
		});
	}

	/** @see TreeColumn#getToolTipText() */
	public String getToolTipText(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		return syncExec(new StringResult() {
			public String result() {
				return treeColumn.getToolTipText();
			}
		});
	}

	/** @see TreeColumn#setToolTipText(String) */
	public void setToolTipText(final TreeColumn treeColumn, final String string) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.setToolTipText(string);
			}
		});
	}

	/** @see TreeColumn#getWidth() */
	public int getWidth(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		return syncExec(new IntResult() {
			public int result() {
				return treeColumn.getWidth();
			}
		});
	}

	/** @see TreeColumn#setWidth(int) */
	public void setWidth(final TreeColumn treeColumn, final int width) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.setWidth(width);
			}
		});
	}

	/** @see TreeColumn#pack() */
	public void pack(final TreeColumn treeColumn) {
		checkWidget(treeColumn);
		syncExec(new Runnable() {
			public void run() {
				treeColumn.pack();
			}
		});
	}

}
