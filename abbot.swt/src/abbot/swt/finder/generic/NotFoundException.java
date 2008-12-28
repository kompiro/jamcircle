package abbot.swt.finder.generic;

import abbot.swt.hierarchy.Hierarchy;

/**
 * A {@link FinderException} that indicates that no match was found by a {@link Finder} when
 * searching a {@link Hierarchy}.
 * 
 * @see Finder
 */
public class NotFoundException extends FinderException {

	private static final long serialVersionUID = 7841331293544214546L;

	/**
	 * Constructs a new {@link NotFoundException}.
	 */
	public NotFoundException() {}

	/**
	 * Constructs a new {@link NotFoundException} with the specified message.
	 */
	public NotFoundException(String message) {
		super(message);
	}
}
