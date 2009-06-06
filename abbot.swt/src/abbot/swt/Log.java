package abbot.swt;

import java.io.PrintStream;

public class Log {

	private static void log(PrintStream stream, String prefix, String format,
			Object... args) {

		if (prefix != null)
			stream.printf("%s: ", prefix);
		stream.printf(format, args);
		stream.println();

		for (Object object : args) {
			if (object instanceof Throwable)
				((Throwable) object).printStackTrace(stream);
		}
	}

	public static void log(String format, Object... args) {
		log(System.out, null, format, args);
	}

	public static void debug(String format, Object... args) {
		log(System.out, "debug", format, args);
	}

	public static void debug(Throwable throwable) {
		debug("%s", throwable);
	}

	public static void warn(String format, Object... args) {
		log(System.err, "warning", format, args);
	}

	public static void warn(Throwable throwable) {
		warn("%s", throwable);
	}

}
