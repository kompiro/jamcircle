package abbot.swt.utilities;

import java.lang.reflect.Array;

/**
 * This class is essentially a clone of <code>abbot.util.ExtendedComparator</code> except that it
 * uses Java's built-in regular expression support (<code>java.util.regex</code>) rather than
 * GNU's. It exists only to remove a dependency on abbot.plain.
 */
public class ExtendedComparator {

	/** Allow no instances. */
	private ExtendedComparator() {}

	/**
	 * Match with a regexp if the pattern contains a ".*" or is bounded by slashes (/regexp/).
	 * Multiline matches are enabled by /(?m)regexp/. Embedded newlines ("\n") in the match string
	 * will then match end-of-lines.
	 */
	// Requiring exact matches eliminates the need to always include the start
	// "^" and finish "$" symbols.
	public static boolean stringsMatch(String pattern, String candidate) {
		if (isDelimitedRegex(pattern)) {
			pattern = pattern.substring(1, pattern.length() - 1);
			return candidate.matches(pattern);
		}
		if (isWildcardRegex(pattern)) {
			return candidate.matches(pattern);
		}
		return candidate.equals(pattern);
	}

	/**
	 * Determine whether or not a {@link String} is a regular expression pattern.
	 * 
	 * @param string
	 *            the alleged regular expression pattern
	 * @return <code>true</code> if string is a regular expression, <code>false</code> otherwise
	 */
	public static boolean isRegex(String string) {
		return isDelimitedRegex(string) || isWildcardRegex(string);
	}

	/**
	 * Determine whether or not a {@link String} is a regular expression pattern delimited by '/'.
	 * 
	 * @param string
	 *            the alleged regular expression pattern
	 * @return <code>true</code> if string is a '/'-delimited regular expression,
	 *         <code>false</code> otherwise
	 */
	public static boolean isDelimitedRegex(String string) {
		return string.startsWith("/") && string.substring(1).endsWith("/");
	}

	/**
	 * Determine whether or not a {@link String} is a regular expression pattern containing ".*".
	 * 
	 * @param string
	 *            the alleged regular expression pattern
	 * @return <code>true</code> if string is a ".*"-containing regular expression,
	 *         <code>false</code> otherwise
	 */
	public static boolean isWildcardRegex(String string) {
		return string.indexOf(".*") != -1;
	}

	/**
	 * Perform element-by-element comparisons of arrays in addition to regular comparisons.
	 */
	public static boolean equals(Object obj1, Object obj2) {
		boolean result = false;
		if (obj1 == null && obj2 == null) {
			result = true;
		} else if (obj1 == null && obj2 != null || obj2 == null && obj1 != null) {
			result = false;
		} else if (obj1.equals(obj2)) {
			result = true;
		}
		// If both are strings, check for a regexp match
		else if (obj1 instanceof String && obj2 instanceof String) {
			result = stringsMatch((String) obj1, (String) obj2);
		} else if (obj1.getClass().isArray() && obj2.getClass().isArray()) {
			if (Array.getLength(obj1) == Array.getLength(obj2)) {
				result = true;
				for (int i = 0; i < Array.getLength(obj1); i++) {
					if (!equals(Array.get(obj1, i), Array.get(obj2, i))) {
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}
}
