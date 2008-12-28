package abbot.swt.script;

import java.util.Collection;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetHierarchy;

public abstract class AbstractResolver implements Resolver {

	protected final WidgetHierarchy hierarchy;

	/**
	 * Constructs a new {@link AbstractResolver} that will use the specified {@link WidgetHierarchy}.
	 * 
	 * @param hierarchy
	 *            the Hierarchy to be used
	 */
	public AbstractResolver(WidgetHierarchy hierarchy) {
		super();
		this.hierarchy = hierarchy;
	}

	/**
	 * Gets the {@link WidgetHierarchy} we're using.
	 * 
	 * @see Resolver#getHierarchy()
	 */
	public WidgetHierarchy getHierarchy() {
		return hierarchy;
	}

	/**
	 * @see abbot.swt.script.Resolver#getWidgetReference(java.lang.String)
	 */
	public abstract WidgetReference getWidgetReference(String name);

	/**
	 * @see abbot.swt.script.Resolver#getWidgetReference(org.eclipse.swt.widgets.Widget)
	 */
	public abstract WidgetReference getWidgetReference(Widget widget);

	/**
	 * @see abbot.swt.script.Resolver#addWidget(org.eclipse.swt.widgets.Widget)
	 */
	public abstract WidgetReference addWidget(Widget widget);

	/**
	 * @see abbot.swt.script.Resolver#addWidgetReference(abbot.swt.script.WidgetReference)
	 */
	public abstract void addWidgetReference(WidgetReference ref);

	/**
	 * @see abbot.swt.script.Resolver#getUniqueID(abbot.swt.script.WidgetReference)
	 */
	public abstract String getUniqueID(WidgetReference ref);

	/**
	 * @see abbot.swt.script.Resolver#getWidgetReferences()
	 */
	public abstract Collection getWidgetReferences();

	/**
	 * @see abbot.swt.script.Resolver#setProperty(java.lang.String, java.lang.String)
	 */
	public abstract void setProperty(String name, String value);

	/**
	 * @see abbot.swt.script.Resolver#getProperty(java.lang.String)
	 */
	public abstract String getProperty(String name);
}
