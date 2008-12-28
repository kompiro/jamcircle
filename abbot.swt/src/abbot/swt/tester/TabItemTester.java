package abbot.swt.tester;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link TabItem}s.
 * 
 * @author gjohnsto
 */
public class TabItemTester extends ItemTester {

	/**
	 * Factory method.
	 */
	public static TabItemTester getTabItemTester() {
		return (TabItemTester) WidgetTester.getTester(TabItem.class);
	}

	/**
	 * Constructs a new {@link TabItemTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public TabItemTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Determines whether or not a {@link TabItem} is visible.
	 * 
	 * @param item
	 *            a {@link TabItem}
	 * @return <code>true</code> if the {@link TabItem} is visible, <code>false</code> otherwise
	 */
	public boolean isVisible(TabItem item) {
		TabFolder folder = getParent(item);
		TabFolderTester folderTester = TabFolderTester.getTabFolderTester();
		return folderTester.isVisible(folder) && folderTester.getSelectionItem(folder) == item;
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof TabItem)
			return isVisible((TabItem) widget);
		return super.isVisible(widget);
	}

	/**
	 * @see ItemTester#getMenu(Widget)
	 */
	protected Menu getMenu(Widget widget) {
		if (widget instanceof TabItem) {
			TabFolder folder = getParent((TabItem) widget);
			return TabFolderTester.getTabFolderTester().getMenu(folder);
		}
		return super.getMenu(widget);
	}

	/**
	 * @see TabItem#getParent()
	 */
	public TabFolder getParent(final TabItem tabItem) {
		checkWidget(tabItem);
		return (TabFolder) syncExec(new Result() {
			public Object result() {
				return tabItem.getParent();
			}
		});
	}

}
