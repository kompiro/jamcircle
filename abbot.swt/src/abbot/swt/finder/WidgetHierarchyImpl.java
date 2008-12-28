package abbot.swt.finder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.Log;
import abbot.swt.hierarchy.HierarchyImpl;
import abbot.swt.tester.ActionFailedException;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.Result;

/**
 * A {@link WidgetHierarchy} implementation for SWT {@link Widget}s.
 */

public class WidgetHierarchyImpl extends HierarchyImpl<Widget> implements WidgetHierarchy {

	private static WidgetHierarchy Default;

	public static synchronized WidgetHierarchy getDefault() {
		if (Default == null)
			Default = new WidgetHierarchyImpl(Display.getDefault());
		return Default;
	}

	/** Our display. */
	private final Display display;

	/**
	 * Constructs a new {@link WidgetHierarchyImpl}.
	 * <p>
	 * <b>Note:</b> In general you should <i>not</i> create your own {@link WidgetHierarchy}s.
	 * Use {@link #getDefault()} instead.
	 */
	public WidgetHierarchyImpl(Display display) {
		this.display = display;
	}

	/** @see WidgetHierarchy#getRoots() */
	public Collection<Widget> getRoots() {
		return Displays.getShells(display);
	}

	public Collection<Widget> getChildren(Widget widget) {
		return getWidgets(widget);
	}

	/** @see HierarchyImpl */
	public Widget getParent(final Widget widget) {
		checkWidget(widget);
		return (Widget) Displays.syncExec(display, new Result() {
			public Object result() {
				if (widget instanceof Control)
					return ((Control) widget).getParent();
				if (widget instanceof Caret)
					return ((Caret) widget).getParent();
				if (widget instanceof Menu)
					return ((Menu) widget).getParent();
				if (widget instanceof ScrollBar)
					return ((ScrollBar) widget).getParent();
				if (widget instanceof CoolItem)
					return ((CoolItem) widget).getParent();
				if (widget instanceof MenuItem)
					return ((MenuItem) widget).getParent();
				if (widget instanceof TabItem)
					return ((TabItem) widget).getParent();
				if (widget instanceof TableColumn)
					return ((TableColumn) widget).getParent();
				if (widget instanceof TableItem)
					return ((TableItem) widget).getParent();
				if (widget instanceof ToolItem)
					return ((ToolItem) widget).getParent();
				if (widget instanceof TreeColumn)
					return ((TreeColumn) widget).getParent();
				if (widget instanceof TreeItem)
					return ((TreeItem) widget).getParent();
				if (widget instanceof DragSource)
					return ((DragSource) widget).getControl().getParent();
				if (widget instanceof DropTarget)
					return ((DropTarget) widget).getControl().getParent();
				if (widget instanceof Tracker)
					Log.warn("cannot get a Tracker's parent");
				return null;
			}
		});
	}

	/** Gets this {@link WidgetHierarchy}'s display. */
	public Display getDisplay() {
		return display;
	}

	/** @see WidgetHierarchy#getWidgets(Widget) */
	public Collection<Widget> getWidgets(final Widget widget) {
		checkWidget(widget);

		if (widget.isDisposed())
			return Collections.emptyList();

		final List<Widget> list = new ArrayList<Widget>();
		Displays.syncExec(display, new Runnable() {

			public void run() {

				if (widget.isDisposed())
					return;

				if (widget instanceof Shell) {

					// MenuBar.
					add(list, ((Shell) widget).getMenuBar());

					// Added menus.
					// No API so need to use reflection.
					try {
						Field menusField = Decorations.class.getDeclaredField("menus");
						menusField.setAccessible(true);
						Menu[] menus = (Menu[]) menusField.get(widget);
						if (menus != null) {
							for (Menu menu : menus) {
								if (menu != null)
									list.add(menu);
							}
						}
					} catch (SecurityException e) {
						throw new ActionFailedException(e);
					} catch (NoSuchFieldException e) {
						throw new ActionFailedException(e);
					} catch (IllegalArgumentException e) {
						throw new ActionFailedException(e);
					} catch (IllegalAccessException e) {
						throw new ActionFailedException(e);
					}
				}

				if (widget instanceof Control)
					add(list, ((Control) widget).getMenu());

				if (widget instanceof Scrollable) {
					Scrollable scrollable = (Scrollable) widget;
					add(list, scrollable.getVerticalBar());
					add(list, scrollable.getHorizontalBar());
				}

				if (widget instanceof TreeItem)
					add(list, ((TreeItem) widget).getItems());

				if (widget instanceof Menu)
					add(list, ((Menu) widget).getItems());

				if (widget instanceof MenuItem)
					add(list, ((MenuItem) widget).getMenu());

				if (widget instanceof Composite) {

					add(list, ((Composite) widget).getChildren());

					if (widget instanceof ToolBar) {
						add(list, ((ToolBar) widget).getItems());
					}

					if (widget instanceof Table) {
						Table table = (Table) widget;
						add(list, table.getItems());
						add(list, table.getColumns());
					}

					if (widget instanceof Tree) {
						Tree tree = (Tree) widget;
						add(list, tree.getColumns());
						add(list, tree.getItems());
					}

					if (widget instanceof CoolBar)
						add(list, ((CoolBar) widget).getItems());

					if (widget instanceof TabFolder)
						add(list, ((TabFolder) widget).getItems());

					if (widget instanceof CTabFolder)
						add(list, ((CTabFolder) widget).getItems());
				}
			}

			private void add(List<Widget> list, Widget widget) {
				if (widget != null)
					list.add(widget);
			}

			private void add(List<Widget> list, Widget[] widgets) {
				if (widgets.length > 0)
					Collections.addAll(list, widgets);
			}
		});
		return list;
	}

	/** @see WidgetHierarchy#dispose(Shell) */
	public void dispose(final Shell shell) {
		checkWidget(shell);
		Displays.syncExec(display, new Runnable() {
			public void run() {
				shell.dispose();
			}
		});
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the specified {@link Widget}'s display isn't
	 * the same as the receiver's. Used for argument checking.
	 */
	protected void checkWidget(Widget widget) {
		if (widget == null)
			throw new IllegalArgumentException("widget is null");
		if (!widget.isDisposed())
			checkDisplay(widget.getDisplay());
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the specified display isn't the same as the
	 * receiver's. Used for argument checking.
	 */
	protected void checkDisplay(Display display) {
		if (display != this.display)
			throw new IllegalArgumentException("invalid display");
	};
}
