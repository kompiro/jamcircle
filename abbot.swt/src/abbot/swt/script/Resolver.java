package abbot.swt.script;

import java.util.Collection;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetHierarchy;


/**
 * An interface that currently defines a subset of {@link abbot.script.Resolver}'s functionality.
 */
public interface Resolver {

	WidgetReference getWidgetReference(String name);

	WidgetReference getWidgetReference(Widget widget);

	WidgetReference addWidget(Widget widget);

	void addWidgetReference(WidgetReference reference);

	String getUniqueID(WidgetReference reference);

	Collection getWidgetReferences();

	/** Provide temporary storage of values. */
	void setProperty(String name, String value);

	/** Provide retrieval of values from temporary storage. */
	String getProperty(String name);

	/** Gets the {@link WidgetHierarchy} used by this {@link Resolver}. */
	WidgetHierarchy getHierarchy();

}
