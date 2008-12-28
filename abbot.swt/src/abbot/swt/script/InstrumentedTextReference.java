package abbot.swt.script;

import org.eclipse.swt.widgets.Widget;

/**
 * @author tlroche
 * @version $Id :$
 * A bit more convenient than <code>TextReference</code>
 * for the common case where you have instrumented a <code>Text</code>
 * and made its name and ID the same.
 */
public class InstrumentedTextReference extends TextReference {
	/**
	 * @param idIsName

	 */
	public InstrumentedTextReference(String idIsName) {
		super(idIsName, idIsName);
	}

	/**
	 * @param idIsName

	 * @param tag
	 */
	public InstrumentedTextReference(
			String idIsName,
			String tag) {
		super(idIsName, idIsName, tag);
	}

	/**
	 * @param idIsName

	 * @param tag
	 * @param title
	 */
	public InstrumentedTextReference(
			String idIsName,
			String tag,
			String title) {
		super(idIsName, idIsName, tag, title);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 * @param text
	 */
	public InstrumentedTextReference(
			String idIsName,
			String tag,
			String title,
			String text) {
		super(idIsName, idIsName, tag, title, text);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 */
	public InstrumentedTextReference(
			String idIsName,
			String tag,
			String title,
			WidgetReference parent,
			int index) {
		super(idIsName, idIsName, tag, title, parent, index);
	}

	/**
	 * @param idIsName
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 * @param invokerOrWindow
	 * @param text
	 */
	public InstrumentedTextReference(
			String idIsName,
			String tag,
			String title,
			WidgetReference parent,
			int index,
			WidgetReference invokerOrWindow,
			String text) {
		super(
				idIsName,
				idIsName,
				tag,
				title,
				parent,
				index,
				invokerOrWindow,
				text);
	}

	/**
	 * @param resolver
	 * @param widget
	 */
	public InstrumentedTextReference(Resolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
