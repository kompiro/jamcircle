package abbot.swt.finder;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.generic.FinderImpl;

/**
 * A {@link WidgetFinder} the provides basic support for finding {@link Widget}s in a
 * {@link WidgetHierarchy}.
 */

public class WidgetFinderImpl extends FinderImpl<Widget> implements WidgetFinder {

	private static WidgetFinder Default;

	public static synchronized WidgetFinder getDefault() {
		if (Default == null)
			Default = new WidgetFinderImpl(WidgetHierarchyImpl.getDefault());
		return Default;
	}

	/**
	 * Constructs a new {@link WidgetFinderImpl} on a {@link WidgetHierarchy}.
	 * <p>
	 * <b>Note:</b> You should probably use {@link #getDefault()} instead of this constructor
	 * (unless you need a {@link WidgetFinderImpl} on something other than the default
	 * {@link WidgetHierarchyImpl}.
	 * 
	 * @param hierarchy
	 *            {@link Widget} {@link WidgetHierarchy} to use
	 */
	public WidgetFinderImpl(WidgetHierarchy hierarchy) {
		super(hierarchy);
	}
}
