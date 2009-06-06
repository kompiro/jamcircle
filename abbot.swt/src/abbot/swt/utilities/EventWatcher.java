package abbot.swt.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A simple debugging utility that prints dispatched SWT events to stdout.
 */
public class EventWatcher {

	private static final Map names;

	private static final int nameLength;

	private static int addName(Map map, int type, String name, int maxLength) {
		map.put(Integer.valueOf(type), name);
		return Math.max(maxLength, name.length());
	}

	static {

		// Initialize the event type --> name map, calculating the longest name.
		Map tempMap = new HashMap();
		int tempLength = 0;

		tempLength = addName(tempMap, SWT.None, "None", tempLength);
		tempLength = addName(tempMap, SWT.KeyDown, "KeyDown", tempLength);
		tempLength = addName(tempMap, SWT.KeyUp, "KeyUp", tempLength);
		tempLength = addName(tempMap, SWT.MouseDown, "MouseDown", tempLength);
		tempLength = addName(tempMap, SWT.MouseUp, "MouseUp", tempLength);
		tempLength = addName(tempMap, SWT.MouseMove, "MouseMove", tempLength);
		tempLength = addName(tempMap, SWT.MouseEnter, "MouseEnter", tempLength);
		tempLength = addName(tempMap, SWT.MouseExit, "MouseExit", tempLength);
		tempLength = addName(tempMap, SWT.MouseDoubleClick, "MouseDoubleClick", tempLength);
		tempLength = addName(tempMap, SWT.Paint, "Paint", tempLength);
		tempLength = addName(tempMap, SWT.Move, "Move", tempLength);
		tempLength = addName(tempMap, SWT.Resize, "Resize", tempLength);
		tempLength = addName(tempMap, SWT.Dispose, "Dispose", tempLength);
		tempLength = addName(tempMap, SWT.Selection, "Selection", tempLength);
		tempLength = addName(tempMap, SWT.DefaultSelection, "DefaultSelection", tempLength);
		tempLength = addName(tempMap, SWT.FocusIn, "FocusIn", tempLength);
		tempLength = addName(tempMap, SWT.FocusOut, "FocusOut", tempLength);
		tempLength = addName(tempMap, SWT.Expand, "Expand", tempLength);
		tempLength = addName(tempMap, SWT.Collapse, "Collapse", tempLength);
		tempLength = addName(tempMap, SWT.Iconify, "Iconify", tempLength);
		tempLength = addName(tempMap, SWT.Deiconify, "Deiconify", tempLength);
		tempLength = addName(tempMap, SWT.Close, "Close", tempLength);
		tempLength = addName(tempMap, SWT.Show, "Show", tempLength);
		tempLength = addName(tempMap, SWT.Hide, "Hide", tempLength);
		tempLength = addName(tempMap, SWT.Modify, "Modify", tempLength);
		tempLength = addName(tempMap, SWT.Verify, "Verify", tempLength);
		tempLength = addName(tempMap, SWT.Activate, "Activate", tempLength);
		tempLength = addName(tempMap, SWT.Deactivate, "Deactivate", tempLength);
		tempLength = addName(tempMap, SWT.Help, "Help", tempLength);
		tempLength = addName(tempMap, SWT.DragDetect, "DragDetect", tempLength);
		tempLength = addName(tempMap, SWT.Arm, "Arm", tempLength);
		tempLength = addName(tempMap, SWT.Traverse, "Traverse", tempLength);
		tempLength = addName(tempMap, SWT.MouseHover, "MouseHover", tempLength);
		tempLength = addName(tempMap, SWT.HardKeyDown, "HardKeyDown", tempLength);
		tempLength = addName(tempMap, SWT.HardKeyUp, "HardKeyUp", tempLength);
		tempLength = addName(tempMap, SWT.MenuDetect, "MenuDetect", tempLength);

		names = Collections.unmodifiableMap(tempMap);
		nameLength = tempLength;
	}

	private static String getName(int type) {
		return (String) names.get(Integer.valueOf(type));
	}

	private final Display display;

	private final Listener listener;

	public EventWatcher(Display display) {
		this.display = display;
		this.listener = new EventListener();

	}

	public void addEventWatcher() {
		for (Iterator iterator = names.keySet().iterator(); iterator.hasNext();) {
			int eventType = ((Integer) iterator.next()).intValue();
			display.addFilter(eventType, listener);
		}
	}

	public void removeEventWatcher() {
		for (Iterator iterator = names.keySet().iterator(); iterator.hasNext();) {
			int eventType = ((Integer) iterator.next()).intValue();
			display.removeFilter(eventType, listener);
		}
	}

	/**
	 * Allows extenders to filter which events are printed.
	 */
	protected boolean isSignificant(Event event) {
		return true;
	}

	/**
	 * Prints event information to System.out.
	 * 
	 * @see Listener
	 */
	private class EventListener implements Listener {

		public void handleEvent(Event event) {

			if (isSignificant(event)) {

				String name = getName(event.type);
				if (name == null)
					name = Integer.toHexString(event.type);
				StringBuffer buffer = new StringBuffer(nameLength);
				buffer.append(name);
				while (buffer.length() < nameLength)
					buffer.append(' ');

				append(buffer, "accel", getAccelerator(event));
				append(buffer, "button", event.button);
				append(buffer, "count", event.count);
				append(buffer, "detail", event.detail);
				append(buffer, "end", event.end);
				append(buffer, "data", event.data);
				append(buffer, "char", event.character);
				append(buffer, "keycode", event.keyCode);
				append(buffer, "stateMask", event.stateMask);
				append(buffer, "doit", event.doit);
				append(buffer, "display", event.display);
				append(buffer, "gc", event.gc);
				append(buffer, "item", event.item);
				append(buffer, "height", event.height);
				append(buffer, "start", event.start);
				append(buffer, "text", event.text);
				append(buffer, "time", event.time);
				append(buffer, "type", event.type);
				append(buffer, "width", event.width);
				append(buffer, "location", event.x, event.y);
				append(buffer, "widget", event.widget);

				System.out.println(buffer.toString());
			}
		}

		private int getAccelerator(Event event) {
			if (event.keyCode != 0)
				return SWT.KEYCODE_BIT | event.keyCode | event.stateMask;
			return event.character;
		}

		private void append(StringBuffer buffer, String name, Object value) {
			buffer.append(' ');
			buffer.append(name);
			buffer.append('=');
			buffer.append(value);
		}

		private void append(StringBuffer buffer, String name, int x, int y) {
			buffer.append(' ');
			buffer.append(name);
			buffer.append("=(");
			buffer.append(x);
			buffer.append(',');
			buffer.append(y);
			buffer.append(')');
		}

	}
}
