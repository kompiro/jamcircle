package abbot.swt.tester;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.WidgetTester.Textable;
import abbot.swt.utilities.Displays.Result;
import abbot.swt.utilities.Displays.StringResult;

/**
 * A tester for {@link Item}s.
 */
public class ItemTester extends WidgetTester implements Textable {

	/**
	 * Factory method.
	 */
	public static ItemTester getItemTester() {
		return (ItemTester) WidgetTester.getTester(Item.class);
	}

	/**
	 * Constructs a new {@link ItemTester} associated with the specified {@link abbot.swt.Robot}.
	 */
	public ItemTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/* Actions */

	/* Proxies */

	/**
	 * Proxy for {@link Item#getImage()}. <p/>
	 * 
	 * @param item
	 *            the item under test.
	 * @return the image placed on the item.
	 */
	public Image getImage(final Item item) {
		checkWidget(item);
		return (Image) syncExec(new Result() {
			public Object result() {
				return item.getImage();
			}
		});
	}

	/** @see Item#setImage(Image) */
	public void setImage(final Item item, final Image image) {
		checkWidget(item);
		syncExec(new Runnable() {
			public void run() {
				item.setImage(image);
			}
		});
	}

	/**
	 * Proxy for {@link Item#getText()}. <p/>
	 * 
	 * @param item
	 *            the item under test.
	 * @return the text of the item.
	 */
	public String getText(final Item item) {
		checkWidget(item);
		return syncExec(new StringResult() {
			public String result() {
				return item.getText();
			}
		});
	}

	/**
	 * @see Textable#getText(Widget)
	 */
	public String getText(Widget widget) {
		return getText((Item) widget);
	}

	public boolean isTextEditable(Widget widget) {
		return false;
	}

	/** @see Item#setText(String) */
	public void setText(final Item item, final String string) {
		checkWidget(item);
		syncExec(new Runnable() {
			public void run() {
				item.setText(string);
			}
		});
	}

	/* Miscellaneous */

	public ItemPath getPath(Item item) {
		List<String> segments = new ArrayList<String>();
		while (item != null) {
			segments.add(0, getText(item));
			item = getParentItem(item);
		}
		return new ItemPath(segments);
	}

	/**
	 * Extenders that represent hierarchical Item trees should redefine this method.
	 * 
	 * @see TreeItemTester#getParentItem(Item)
	 * @see MenuItemTester#getParentItem(Item)
	 */
	protected Item getParentItem(Item item) {
		return null;
	}

}
