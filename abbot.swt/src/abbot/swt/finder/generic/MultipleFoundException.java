package abbot.swt.finder.generic;

import abbot.swt.hierarchy.Hierarchy;

/**
 * A {@link FinderException} that indicates that more than one match was found when only one was
 * expected when a {@link Finder} was searching a {@link Hierarchy} but .
 * 
 * @see Finder
 */
public class MultipleFoundException extends FinderException {

	private static final long serialVersionUID = 9148363984124884287L;

	/**
	 * Constructs a new {@link MultipleFoundException} with no message.
	 */
	public MultipleFoundException() {}

	/**
	 * Constructs a new {@link MultipleFoundException} with a message.
	 * 
	 * @param message
	 *            the message
	 */
	public MultipleFoundException(String message) {
		super(message);
	}

}
