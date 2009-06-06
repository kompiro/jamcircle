package abbot.swt.script;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Widget;

/**
 * A bit more convenient than <code>WidgetReference</code>.
 * 
 * @author nntp_ds@fastmail.fm
 * @version $Id: StyledTextReference.java 2108 2007-01-15 13:12:56Z gjohnsto $
 */
public class StyledTextReference extends WidgetReference {
	/**
	 * @param id
	 */
	public StyledTextReference(String id) {
		super(id, StyledText.class);
	}

	/**
	 * @param id
	 * @param name
	 */
	public StyledTextReference(String id, String name) {
		super(id, StyledText.class, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag) {
		super(id, StyledText.class, name, tag);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title) {
		super(id, StyledText.class, name, tag, title);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param text
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title,
		String text) {
		super(id, StyledText.class, name, tag, title, text);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index) {
		super(id, StyledText.class, name, tag, title, parent, index);
	}

	/**
	 * @param id
	 * @param name
	 * @param tag
	 * @param title
	 * @param parent
	 * @param index
	 * @param invokerOrWindow
	 * @param text
	 */
	public StyledTextReference(
		String id,
		String name,
		String tag,
		String title,
		WidgetReference parent,
		int index,
		WidgetReference invokerOrWindow,
		String text) {
		super(
			id,
			StyledText.class,
			name,
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
	public StyledTextReference(Resolver resolver, Widget widget) {
		super(resolver, widget);
	}

}
