package abbot.swt.tester;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link ToolItem}s.
 * <p>
 * Created on 30.05.2005 by Richard Birenheide Copyright SAP AG 2005
 * 
 * @author Richard Birenheide
 */
public class ToolItemTester extends ItemTester {

	/**
	 * Factory method.
	 */
	public static ToolItemTester getToolItemTester() {
		return (ToolItemTester) WidgetTester.getTester(ToolItem.class);
	}

	/**
	 * Constructs a new {@link ToolItemTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ToolItemTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	/* Proxies */

	/**
	 * @return <code>true</code> if the {@link ToolItem} is visible, <code>false</code>
	 *         otherwise
	 */
	public boolean isVisible(ToolItem item) {
		ToolBar bar = getParent(item);
		return ToolBarTester.getToolBarTester().isVisible(bar);
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof ToolItem)
			return isVisible((ToolItem) widget);
		return super.isVisible(widget);
	}

	/**
	 * @see WidgetTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		if (widget instanceof ToolItem) {
			ToolBar bar = getParent((ToolItem) widget);
			return ToolBarTester.getToolBarTester().getMenu(bar);
		}
		return super.getMenu(widget);
	}

	/* Proxies */

	/**
	 * Proxy for {@link ToolItem#getBounds()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the bounding rectangle relative to the parent.
	 */
	public Rectangle getBounds(final ToolItem toolItem) {
		checkWidget(toolItem);
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return toolItem.getBounds();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getControl()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the control for the separator.
	 */
	public Control getControl(final ToolItem toolItem) {
		checkWidget(toolItem);
		return (Control) syncExec(new Result() {
			public Object result() {
				return toolItem.getControl();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getDisabledImage()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the disabled image of the toolItem.
	 */
	public Image getDisabledImage(final ToolItem toolItem) {
		checkWidget(toolItem);
		return (Image) syncExec(new Result() {
			public Object result() {
				return toolItem.getDisabledImage();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getEnabled()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the enabled state of the toolItem.
	 */
	public boolean getEnabled(final ToolItem toolItem) {
		checkWidget(toolItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return toolItem.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getHotImage()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the hot image of the toolItem.
	 */
	public Image getHotImage(final ToolItem toolItem) {
		checkWidget(toolItem);
		return (Image) syncExec(new Result() {
			public Object result() {
				return toolItem.getHotImage();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getParent()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the parent of the toolItem.
	 */
	public ToolBar getParent(final ToolItem toolItem) {
		checkWidget(toolItem);
		return (ToolBar) syncExec(new Result() {
			public Object result() {
				return toolItem.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getSelection()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return true if the toolItem is selected.
	 */
	public boolean getSelection(final ToolItem toolItem) {
		checkWidget(toolItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return toolItem.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getToolTipText()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the tool tip text of the toolItem.
	 */
	public String getToolTipText(final ToolItem toolItem) {
		checkWidget(toolItem);
		return syncExec(new StringResult() {
			public String result() {
				return toolItem.getToolTipText();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#getWidth()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return the width of the toolItem.
	 */
	public int getWidth(final ToolItem toolItem) {
		checkWidget(toolItem);
		return syncExec(new IntResult() {
			public int result() {
				return toolItem.getWidth();
			}
		});
	}

	/**
	 * Proxy for {@link ToolItem#isEnabled()}. <p/>
	 * 
	 * @param toolItem
	 *            the tool toolItem under test.
	 * @return true if the toolItem and all of its ancestors are enabled.
	 */
	public boolean isEnabled(final ToolItem toolItem) {
		checkWidget(toolItem);
		return syncExec(new BooleanResult() {
			public boolean result() {
				return toolItem.isEnabled();
			}
		});
	}
}
