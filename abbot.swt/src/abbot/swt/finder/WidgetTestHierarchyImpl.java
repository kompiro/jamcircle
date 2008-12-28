package abbot.swt.finder;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.TestHierarchy;
import abbot.swt.hierarchy.HierarchyImpl;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.Result;

/**
 * Provide filtering of {@link Widget}s in the current hierarchy to facilitate testing.
 * <p>
 * In general, only {@link Shell}s are filtered, and {@link #isFiltered(Widget)} checks for both
 * self and {@link Shell}s being filtered. So {@link #getWidgets(Widget)} returns a filtered set
 * only when the parent is a Shell.
 */
public class WidgetTestHierarchyImpl extends WidgetHierarchyImpl {

	public static WidgetHierarchy getDefault(boolean ignoreExisting) {
		return new WidgetTestHierarchyImpl(Display.getDefault(), ignoreExisting);
	}

	public static WidgetHierarchy getDefault() {
		return getDefault(false);
	}

	/**
	 * Keys are the {@link Shell}s currently being filtered.
	 */
	private final Map filtered;

	/**
	 * Constructs a new {@link WidgetTestHierarchyImpl}.
	 * 
	 * @param display
	 *            the {@link Display}
	 * @param ignoreExisting
	 *            if <code>true</code> then any existing {@link Shell}s will be ignored by the
	 *            new {@link WidgetTestHierarchyImpl}
	 */
	public WidgetTestHierarchyImpl(Display display, boolean ignoreExisting) {
		super(display);
		this.filtered = new WeakHashMap();
		if (ignoreExisting)
			ignoreExisting();
	}

	/**
	 * Sets any existing {@link Shell}s to be filtered (ignored).
	 * 
	 * @see TestHierarchy#TestHierarchy(boolean)
	 */
	private void ignoreExisting() {
		for (Iterator iterator = getRoots().iterator(); iterator.hasNext();) {
			Widget widget = (Widget) iterator.next();
			setFiltered(widget, true);
		}
	}

	/**
	 * Gets the root {@link Shell}s of this {@link WidgetHierarchy}, excluding any that are being
	 * filtered.
	 * 
	 * @see WidgetHierarchyImpl#getRoots()
	 */
	public Collection getRoots() {
		Collection roots = super.getRoots();
		roots.removeAll(filtered.keySet());
		return roots;
	}

	/**
	 * Gets all of the direct child {@link Widget}s of the specified {@link Widget}, excluding any
	 * {@link Shell}s that are currently being filtered.
	 * 
	 * @see WidgetHierarchy#getWidgets(Widget)
	 */
	public Collection getWidgets(Widget widget) {
		if (!widget.isDisposed()) {
			if (!isFiltered(widget)) {
				Collection widgets = super.getWidgets(widget);
				widgets.removeAll(filtered.keySet());
				return widgets;
			}
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see HierarchyImpl
	 */
	public Widget getParent(Widget widget) {
		Widget parent = super.getParent(widget);
		if (parent == null || isFiltered(parent))
			return null;
		return parent;
	}

	/**
	 * @see WidgetHierarchy#dispose(Shell)
	 */
	public void dispose(Shell shell) {
		if (!isFiltered(shell))
			super.dispose(shell);
	}

	/**
	 * Returns <code>true</code> iff the specified {@link Widget} is currently being filtered.
	 */
	public boolean isFiltered(Widget widget) {
		return filtered.containsKey(widget);
	}

	/**
	 * Sets whether or not the specified {@link Widget} is to be considered as being included in the
	 * {@link WidgetHierarchy}. If the {@link Widget} is a {@link Shell}, it recursively applies
	 * the specified filtering to all owned sub{@link Shell}s.
	 */
	public void setFiltered(Widget widget, boolean filter) {

		// Update the filtered map this the specified widget.
		if (filter)
			filtered.put(widget, Boolean.TRUE);
		else
			filtered.remove(widget);

		// If the widget is a shell, recursively set the same filtering on all of its subshells.
		if (widget instanceof Shell) {
			final Shell shell = (Shell) widget;
			Shell[] shells = (Shell[]) Displays.syncExec(getDisplay(), new Result() {
				public Object result() {
					return shell.getShells();
				}
			});
			for (int i = 0; i < shells.length; i++) {
				setFiltered(shells[i], filter);
			}
		}
	}
}
