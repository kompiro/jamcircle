package abbot.swt.utilities;

import java.io.Closeable;
import java.io.Flushable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;

import org.eclipse.swt.widgets.Event;

public class EventFormatter implements Closeable, Flushable {

	public static String toString(Event event) {
		Formatter formatter = new Formatter();
		new EventFormattable(event).formatTo(formatter, 0, -1, -1);
		return formatter.toString();
	}

	public static String toString(Event[] events) {
		StringBuilder buffer = new StringBuilder();
		Formatter formatter = new Formatter(buffer);
		buffer.append("[ ");
		if (events.length > 0)
			new EventFormattable(events[0]).formatTo(formatter, 0, -1, -1);
		for (int i = 1; i < events.length; i++) {
			buffer.append(", ");
			new EventFormattable(events[i]).formatTo(formatter, 0, -1, -1);
		}
		buffer.append(" ]");
		return formatter.toString();
	}

	private final Formatter formatter;

	public EventFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public EventFormatter(Appendable appendable) {
		this(new Formatter(appendable));
	}

	public EventFormatter() {
		this(new Formatter());
	}

	public EventFormatter(PrintStream stream) {
		this(new Formatter(stream));
	}

	public EventFormatter(OutputStream stream) {
		this(new Formatter(stream));
	}

	/**
	 * Closes the underlying {@link Formatter}.
	 * 
	 * @see Formatter#close()
	 */
	public void close() {
		formatter.close();
	}

	/**
	 * Flushes the underlying {@link Formatter}.
	 * 
	 * @see Formatter#flush()
	 */
	public void flush() {
		formatter.flush();
	}

	public String toString() {
		return formatter.toString();
	}

	public EventFormatter format(String format, Object... args) {
		Object[] args2 = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object object = args[i];
			args2[i] = (object instanceof Event) ? new EventFormattable((Event) object) : object;
		}
		formatter.format(format, args2);
		return this;
	}

	public EventFormatter format(Event event) {
		new EventFormattable(event).formatTo(formatter, 0, -1, -1);
		return this;
	}

	public EventFormatter format(Collection<Event> events) {
		format("%s [", events.getClass().getName());
		for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
			format(iterator.next());
			if (iterator.hasNext())
				formatter.format(", ");
		}
		formatter.format("]");
		return this;
	}

}
