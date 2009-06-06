package abbot.swt.finder.matchers;

import org.eclipse.swt.widgets.Widget;

/**
 * Provides matching of Widgets by widget "name" and widget class (optional).
 * <p>
 * In order to be matched, the "name" of the widget must be stored in the widget by using
 * {@link Widget#setData(String, Object)}. The value must be of type String. The key used is the
 * String which can be obtained by {@link #getNameTag()} and defaults to "name".
 * 
 * @author Richard Birenheide
 * @author Gary Johnston
 * @deprecated use {@link StringDataMatcher}
 */
public class NameMatcher extends StringDataMatcher {

	public static final String DEFAULT_NAME_TAG = "name";

	private static String NAME_TAG = DEFAULT_NAME_TAG;

	/**
	 * Provides the possibility to set the name tag globally.
	 * <p>
	 * The default for the name tag is "name". If this is not allowable for ones application under
	 * test one may change this key generally.
	 * 
	 * @param nameTag
	 *            the key under which the names are stored with widgets.
	 * @see Widget#setData(String, Object)
	 */
	public static synchronized void setNameTag(String nameTag) {
		NAME_TAG = nameTag;
	}

	/**
	 * Retrieves the name tag.
	 * 
	 * @return the current default name tag used as name key in {@link Widget#getData(String)}.
	 */
	public static synchronized String getNameTag() {
		return NAME_TAG;
	}

	/**
	 * Constructs a name matcher with the name, class, and name key given. <p/>
	 * 
	 * @param name
	 *            the name to match.
	 * @param clazz
	 *            the class to match.
	 * @param nameKey
	 *            the key under which the name of the widget is stored using
	 *            {@link Widget#setData(java.lang.String, java.lang.Object)}.
	 */
	public NameMatcher(String name, Class clazz, String nameKey) {
		super(nameKey, name, clazz);
	}

	/**
	 * Constructs a name matcher with the name and the class given.
	 * <p>
	 * The name key will be the one valid at the time the constructor is called.
	 * 
	 * @param name
	 *            the name to match.
	 * @param clazz
	 *            the class to match.
	 */
	public NameMatcher(String name, Class clazz) {
		this(name, clazz, NAME_TAG);
	}

	/**
	 * Constructs a name matcher with the name given. The name key will be the one valid at the time
	 * the constructor is called.
	 * 
	 * @param name
	 *            the name to match.
	 */
	public NameMatcher(String name) {
		this(name, Widget.class);
	}
}
