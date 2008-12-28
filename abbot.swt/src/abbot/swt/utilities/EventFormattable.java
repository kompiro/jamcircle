package abbot.swt.utilities;

import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays.StringResult;

public class EventFormattable implements Formattable {

	private final Event event;

	public boolean timestamps;

	public boolean mouseDownIntervals;

	private Formatter formatter;

	private long tzero;

	private boolean first;

	private int nextWidgetId;

	private Map<Integer, Integer> widgetIds;

	public EventFormattable(Event event) {
		this.event = event;
	}

	private void init(Formatter formatter) {
		this.formatter = formatter;
		timestamps = false;
		mouseDownIntervals = false;
		tzero = 0L;
		first = true;
		nextWidgetId = 0;
		widgetIds = new HashMap<Integer, Integer>();

	}

	private void format(String format, Object... args) {
		if (first) {
			if (format.length() > 0 && format.charAt(0) == ' ')
				format = format.substring(1);
			first = false;
		}
		formatter.format(format, args);
	}

	public void formatTo(Formatter formatter, int flags, int width, int precision) {

		init(formatter);

		// Timestamp
		if (timestamps && event.time != 0)
			format(" %1$tH:%1$tM:%1$tS.%1$tL", 0xFFFFFFFFL & (long) event.time);

		// Time since previous MouseDown
		if (mouseDownIntervals && event.type == SWT.MouseDown) {
			long now = System.currentTimeMillis();
			double delta = tzero == 0L ? 0.0 : (double) (now - tzero) / 1000.0;
			format(" (%3.3f)", delta);
			tzero = now;
		}

		// type
		format(" %s", getName(event.type));

		// display
		if (event.display != null && event.display != Display.getDefault())
			format(" display=%s", event.display);

		// widget
		formatWidget();

		// item
		formatItem();

		// keyCode
		formatKeyCode();

		// character
		formatCharacter();

		// statemask
		if (event.stateMask != 0)
			format(" stateMask=%x", event.stateMask);

		// button
		if (event.button != 0)
			format(" button=%d", event.button);

		// x & y
		if (event.x != 0 || event.y != 0)
			format(" @%d,%d", event.x, event.y);

		// width & height
		if (event.width != 0 || event.height != 0)
			format(" %dx%d", event.width, event.height);

		// start & end
		if (event.start != 0 || event.end != 0)
			format(" %d->%d", event.start, event.end);

		// count
		if (event.count != 0)
			format(" count=%d", event.count);

		// detail
		if (event.detail != 0)
			format(" detail=%x", event.detail);

		// data
		if (event.data != null)
			format(" data=%s", event.data);

		// doit
		if (!event.doit)
			format(" %s", "!doit");

		// text
		if (event.text != null)
			format(" text=%s", event.text);

		// gc
		if (event.gc != null)
			format(" gc=%s", event.gc);
	}

	private void formatWidget() {
		if (event.widget != null)
			formatWidget(event.widget, null);
	}

	private void formatItem() {
		if (event.item != null)
			formatWidget(event.item, "item");
	}

	private void formatWidget(final Widget widget, String label) {
		String string = Displays.syncExec(widget.getDisplay(), new StringResult() {
			public String result() {
				return widget.toString();
			}
		});
		int id = getId(widget);
		if (label != null)
			format(" %s=%s#%d", label, string, id);
		else
			format(" %s#%d", string, id);
	}

	private synchronized int getId(Widget widget) {
		int hashcode = widget.hashCode();
		Integer id = widgetIds.get(hashcode);
		if (id != null)
			return id.intValue();
		id = nextWidgetId++;
		widgetIds.put(Integer.valueOf(hashcode), Integer.valueOf(id));
		return id;
	}

	private void formatKeyCode() {
		if (event.keyCode != 0) {
			formatter.format(" keyCode[");
			int keyCode = event.keyCode;
			int n = 0;
			if ((keyCode & SWT.CTRL) != 0)
				formatter.format("%s%s", n++ == 0 ? "" : " ", "CTRL");
			if ((keyCode & SWT.ALT) != 0)
				formatter.format("%s%s", n++ == 0 ? "" : " ", "ALT");
			if ((keyCode & SWT.SHIFT) != 0)
				formatter.format("%s%s", n++ == 0 ? "" : " ", "SHIFT");
			if ((keyCode & SWT.COMMAND) != 0)
				formatter.format("%s%s", n++ == 0 ? "" : " ", "COMMAND");

			keyCode &= ~SWT.MODIFIER_MASK;
			if (keyCode != 0)
				formatter.format("%s%08x", n++ == 0 ? "" : " ", keyCode);

			formatter.format("]");
		}
	}

	private void formatCharacter() {
		if (event.character != '\0') {
			format(" char=%04x", (int) event.character);
			if (isPrintable(event.character))
				format("='%c'", event.character);
		}
	}

	private boolean isPrintable(char c) {
		return Character.isLetterOrDigit(c) || (c >= 0x20 && c <= 0xfe);
	}

	public static final int MinEventType = SWT.None; // 0

	public static final int MaxEventType = SWT.PaintItem; // 42

	public static final String[] EventTypeNames;

	static {

		EventTypeNames = new String[MaxEventType + 1];

		EventTypeNames[SWT.None] = "None";
		EventTypeNames[SWT.KeyDown] = "KeyDown";
		EventTypeNames[SWT.KeyUp] = "KeyUp";
		EventTypeNames[SWT.MouseDown] = "MouseDown";
		EventTypeNames[SWT.MouseUp] = "MouseUp";
		EventTypeNames[SWT.MouseMove] = "MouseMove";
		EventTypeNames[SWT.MouseEnter] = "MouseEnter";
		EventTypeNames[SWT.MouseExit] = "MouseExit";
		EventTypeNames[SWT.MouseDoubleClick] = "MouseDoubleClick";
		EventTypeNames[SWT.Paint] = "Paint";
		EventTypeNames[SWT.Move] = "Move";
		EventTypeNames[SWT.Resize] = "Resize";
		EventTypeNames[SWT.Dispose] = "Dispose";
		EventTypeNames[SWT.Selection] = "Selection";
		EventTypeNames[SWT.DefaultSelection] = "DefaultSelection";
		EventTypeNames[SWT.FocusIn] = "FocusIn";
		EventTypeNames[SWT.FocusOut] = "FocusOut";
		EventTypeNames[SWT.Expand] = "Expand";
		EventTypeNames[SWT.Collapse] = "Collapse";
		EventTypeNames[SWT.Iconify] = "Iconify";
		EventTypeNames[SWT.Deiconify] = "Deiconify";
		EventTypeNames[SWT.Close] = "Close";
		EventTypeNames[SWT.Show] = "Show";
		EventTypeNames[SWT.Hide] = "Hide";
		EventTypeNames[SWT.Modify] = "Modify";
		EventTypeNames[SWT.Verify] = "Verify";
		EventTypeNames[SWT.Activate] = "Activate";
		EventTypeNames[SWT.Deactivate] = "Deactivate";
		EventTypeNames[SWT.Help] = "Help";
		EventTypeNames[SWT.DragDetect] = "DragDetect";
		EventTypeNames[SWT.Arm] = "Arm";
		EventTypeNames[SWT.Traverse] = "Traverse";
		EventTypeNames[SWT.MouseHover] = "MouseHover";
		EventTypeNames[SWT.HardKeyDown] = "HardKeyDown";
		EventTypeNames[SWT.HardKeyUp] = "HardKeyUp";
		EventTypeNames[SWT.MenuDetect] = "MenuDetect";
		EventTypeNames[SWT.SetData] = "SetData";
		EventTypeNames[SWT.MouseWheel] = "MouseWheel";
		EventTypeNames[SWT.Settings] = "Settings";
		EventTypeNames[SWT.EraseItem] = "EraseItem";
		EventTypeNames[SWT.MeasureItem] = "MeasureItem";
		EventTypeNames[SWT.PaintItem] = "PaintItem";

	}

	public static String getName(int eventType) {
		if (eventType >= MinEventType && eventType <= MaxEventType) {
			String eventTypeName = EventTypeNames[eventType];
			if (eventTypeName != null)
				return eventTypeName;
		}
		return Integer.toString(eventType);
	}

}
