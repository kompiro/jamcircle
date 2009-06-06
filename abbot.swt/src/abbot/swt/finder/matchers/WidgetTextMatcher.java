package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.tester.ButtonTester;
import abbot.swt.tester.ComboTester;
import abbot.swt.tester.GroupTester;
import abbot.swt.tester.ItemTester;
import abbot.swt.tester.LabelTester;
import abbot.swt.tester.ShellTester;
import abbot.swt.tester.TextTester;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.ExtendedComparator;
import abbot.swt.utilities.Displays.Result;

/**
 * A {@link WidgetMatcher} that matches Widgets by their text (typically by their
 * <code>getText()</code> method, although {@link TableItem} is a notable exception).
 * 
 * @author Richard Birenheide
 * @author Gary Johnston
 */
public class WidgetTextMatcher extends WidgetClassMatcher {

	/**
	 * The text to match.
	 */
	protected final String text;

	/**
	 * Constructs a TextMatcher that will match Widgets with the specified text.
	 * 
	 * @param text
	 *            the text to match
	 * @exception IllegalArgumentException
	 *                if text is null
	 */
	public WidgetTextMatcher(String text, Class clazz, boolean mustBeShowing) {
		super(clazz, mustBeShowing);
		if (text == null)
			throw new IllegalArgumentException("text is null");
		this.text = text;
	}

	/**
	 * Constructs a TextMatcher that will match Widgets with the specified text.
	 * 
	 * @param text
	 *            the text to match
	 * @exception IllegalArgumentException
	 *                if text is null
	 */
	public WidgetTextMatcher(String text, boolean mustBeShowing) {
		this(text, Widget.class, mustBeShowing);
	}

	/**
	 * Constructs a TextMatcher that will match Widgets with the specified text.
	 * 
	 * @param text
	 *            the text to match
	 * @exception IllegalArgumentException
	 *                if text is null
	 */
	public WidgetTextMatcher(String text, Class clazz) {
		this(text, clazz, false);
	}

	/**
	 * Constructs a TextMatcher that will match Widgets with the specified text.
	 * 
	 * @param text
	 *            the text to match
	 * @exception IllegalArgumentException
	 *                if text is null
	 */
	public WidgetTextMatcher(String text) {
		this(text, Widget.class, false);
	}

	/** Gets this {@link WidgetTextMatcher}'s text. */
	public String getText() {
		return text;
	}

	/** @see abbot.swt.finder.matchers.WidgetClassMatcher#matches(org.eclipse.swt.widgets.Widget) */
	public boolean matches(Widget widget) {

		// First see if super matches.
		if (super.matches(widget)) {

			// If the widget's text is scalar and it matches our text then return true.
			String widgetText = getText(widget);
			if (widgetText != null && ExtendedComparator.stringsMatch(text, widgetText))
				return true;

			// If the widget has an array of texts and any of them match then return true.
			String[] widgetTextArray = getTextArray(widget);
			if (widgetTextArray != null && widgetTextArray.length > 0) {
				for (int i = 0; i < widgetTextArray.length; i++) {
					if (ExtendedComparator.stringsMatch(text, widgetTextArray[i]))
						return true;
				}
			}
		}

		// If we got here then no match.
		return false;
	}

	/**
	 * Gets a {@link Widget}'s scalar text.
	 * 
	 * @param widget
	 *            the {@link Widget} whose scalar text to get
	 * @return the {@link Widget}'s scalar text (or null if it has none)
	 */
	protected String getText(Widget widget) {

		if (widget instanceof Button)
			return ButtonTester.getButtonTester().getText((Button) widget);

		if (widget instanceof Combo)
			return ComboTester.getComboTester().getText((Combo) widget);

		if (widget instanceof Shell)
			return ShellTester.getShellTester().getText((Shell) widget);

		if (widget instanceof Group)
			return GroupTester.getGroupTester().getText((Group) widget);

		if (widget instanceof Label)
			return LabelTester.getLabelTester().getText((Label) widget);

		if (widget instanceof Text)
			return TextTester.getTextTester().getText((Text) widget);

		if (widget instanceof Item)
			return ItemTester.getItemTester().getText((Item) widget);

		return null;
	}

	/**
	 * Gets a {@link Widget}'s text array.
	 * <p>
	 * <strong>Note:</strong> {@link TableItem} is the only currently known {@link Widget} that can
	 * have an array of text values.
	 * 
	 * @param widget
	 *            the {@link Widget} whose text array to get
	 * @return the specified {@link Widget}'s text array (or null if it doesn't have one).
	 */
	protected String[] getTextArray(Widget widget) {
		if (widget instanceof TableItem) {
			final TableItem tableItem = (TableItem) widget;
			return (String[]) Displays.syncExec(tableItem.getDisplay(), new Result() {
				public Object result() {
					int columnCount = tableItem.getParent().getColumnCount();
					if (columnCount > 1) {
						String[] textArray = new String[columnCount];
						for (int i = 0; i < columnCount; i++) {
							textArray[i] = tableItem.getText(i);
						}
						return textArray;
					}
					return null;
				}
			});
		}
		return null;
	}
}
