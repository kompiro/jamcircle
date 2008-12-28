package abbot.swt.finder.generic;

import org.eclipse.swt.widgets.Widget;

import abbot.swt.finder.WidgetHierarchy;

/**
 * The parent class of {@link Exception}s that may be thrown due to finding {@link Widget}s in a
 * {@link WidgetHierarchy}.
 * 
 * @see MultipleFoundException
 * @see NotFoundException
 * @see Finder
 */
public abstract class FinderException extends Exception {

	/** Constructs a new {@link FinderException}. */
	public FinderException() {
		super();
	}

	/** Constructs a new {@link FinderException} with the specified message. */
	public FinderException(String msg) {
		super(msg);
	}
}
