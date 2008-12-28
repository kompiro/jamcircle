package abbot.swt.finder;

import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.hierarchy.Hierarchy;

/**
 * Provides access to all {@link Widget}s in an SWT hierarchy.
 */
public interface WidgetHierarchy extends Hierarchy<Widget> {

	public static WidgetHierarchy Default = WidgetHierarchyImpl.getDefault();

	public interface WidgetVisitor extends Visitor<Widget> {}

	/** Gets the {@link Display} with which this {@link WidgetHierarchy} is associated. */
	Display getDisplay();

	/**
	 * Gets all sub{@link Widget}s of the the specified {@link Widget}.<br>
	 * <b>Note:</b> What constitutes a sub{@link Widget}s may vary depending on the
	 * {@link WidgetHierarchy} implementation.
	 */
	Collection getWidgets(Widget widget);

	/**
	 * Provide proper disposal of the specified {@link Shell}, appropriate to this
	 * {@link WidgetHierarchy}. After disposal, the {@link Shell} and its descendents will no longer be
	 * reachable from this {@link WidgetHierarchy}.
	 */
	void dispose(Shell shell);
}
