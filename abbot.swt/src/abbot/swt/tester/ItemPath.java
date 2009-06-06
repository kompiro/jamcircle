package abbot.swt.tester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// import java.util.StringTokenizer;

/**
 * An ItemPath is immutable.
 */
public class ItemPath implements Iterable<String> {

	public static final String DEFAULT_DELIMITER = "/";

	public static final char REGEX_DELIMITER = '/';

	public static final String REGEX_INDICATOR = ".*";

	private final String[] segments;

	public ItemPath(String path) {
		this(path, DEFAULT_DELIMITER);
	}

	public ItemPath(String path, String delimiter) {
		if (path == null)
			throw new IllegalArgumentException("path is null");
		if (delimiter == null)
			throw new IllegalArgumentException("delimiter is null");

		segments = getSegments(path, delimiter);
	}

	public ItemPath(String[] segments) {
		if (segments == null)
			throw new IllegalArgumentException("segments is null");
		int i = Arrays.asList(segments).indexOf(null);
		if (i != -1)
			throw new IllegalArgumentException("segments[" + i + "] is null");

		this.segments = new String[segments.length];
		System.arraycopy(segments, 0, this.segments, 0, segments.length);
	}

	public ItemPath(String[] segments, int fromIndex, int toIndex) {
		if (segments == null)
			throw new IllegalArgumentException("segments is null");
		if (fromIndex < 0 || toIndex > segments.length || fromIndex > toIndex)
			throw new IndexOutOfBoundsException();
		int i = Arrays.asList(segments).subList(fromIndex, toIndex).indexOf(null);
		if (i != -1)
			throw new IllegalArgumentException("segments[" + (i + fromIndex) + "] is null");

		this.segments = new String[toIndex - fromIndex];
		System.arraycopy(segments, fromIndex, this.segments, 0, this.segments.length);
	}

	public ItemPath(List<String> segments) {
		this.segments = segments.toArray(new String[segments.size()]);
	}

	public ItemPath subPath(int fromIndex, int toIndex) {
		return new ItemPath(segments, fromIndex, toIndex);
	}

	public ItemPath subPath(int fromIndex) {
		return subPath(fromIndex, segmentCount());
	}

	public String getSegment(int index) {
		return segments[index];
	}

	public int segmentCount() {
		return segments.length;
	}

	public String toString() {
		return toString(DEFAULT_DELIMITER);
	}

	public String toString(String delimiter) {
		StringBuilder builder = new StringBuilder();
		for (String segment : segments) {
			if (builder.length() > 0)
				builder.append(delimiter);
			builder.append(segment);
		}
		return builder.toString();
	}

	public Iterator<String> iterator() {
		return new ItemPathIterator();
	}

	private class ItemPathIterator implements Iterator<String> {

		private int i = 0;

		public boolean hasNext() {
			return i < segments.length;
		}

		public String next() {
			return segments[i++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private ItemPath(int segmentCount) {
		segments = new String[segmentCount];
	}

	public ItemPath append(ItemPath path) {
		ItemPath newPath = new ItemPath(segments.length + path.segments.length);
		System.arraycopy(segments, 0, newPath.segments, 0, segments.length);
		System.arraycopy(path.segments, 0, newPath.segments, segments.length, path.segments.length);
		return newPath;
	}

	public ItemPath append(String path, String delimiter) {
		String[] newSegments = getSegments(path, delimiter);
		ItemPath newPath = new ItemPath(segments.length + newSegments.length);
		System.arraycopy(segments, 0, newPath.segments, 0, segments.length);
		System.arraycopy(newSegments, 0, newPath.segments, segments.length, newSegments.length);
		return newPath;
	}

	public ItemPath append(String path) {
		return append(path, DEFAULT_DELIMITER);
	}

	private final static class StringIterator {

		private final String string;

		private int index;

		public StringIterator(String string) {
			this.string = string;
			index = 0;
		}

		public boolean hasNext() {
			return index < string.length();
		}

		public char peek() {
			if (hasNext())
				return string.charAt(index);
			throw new NoSuchElementException();
		}

		public char next() {
			if (hasNext())
				return string.charAt(index++);
			throw new NoSuchElementException();
		}
	}

	private static String[] getSegments(String path, String delimiter) {
		List<String> segmentList = new ArrayList<String>();
		for (StringIterator iterator = new StringIterator(path); iterator.hasNext();) {
			segmentList.add(nextSegment(iterator, delimiter));
		}
		return segmentList.toArray(new String[segmentList.size()]);
	}

	private static String nextSegment(StringIterator iterator, String delimiter) {
		if (iterator.peek() == REGEX_DELIMITER)
			return nextRegexpSegment(iterator, delimiter);
		return nextPlainSegment(iterator, delimiter);
	}

	private static String nextRegexpSegment(StringIterator iterator, String delimiter) {
		StringBuilder buffer = new StringBuilder();
		char c = iterator.next();
		if (c != REGEX_DELIMITER)
			throw new IllegalArgumentException();
		buffer.append(c);
		boolean sawBash = false;
		boolean done = false;
		while (!done && iterator.hasNext()) {
			c = iterator.next();
			if (sawBash) {
				if (c != REGEX_DELIMITER)
					buffer.append('\\');
				buffer.append(c);
				sawBash = false;
			} else {
				switch (c) {
					case '\\':
						sawBash = true;
						break;
					case REGEX_DELIMITER:
						done = true;
						buffer.append(c);
						break;
					default:
						buffer.append(c);
						break;
				}
			}
		}

		if (iterator.hasNext()) {
			c = iterator.next();
			if (!isDelimiter(c, delimiter))
				throw new IllegalArgumentException();
		}
		return buffer.toString();
	}

	private static String nextPlainSegment(StringIterator iterator, String delimiter) {
		StringBuilder buffer = new StringBuilder();
		while (iterator.hasNext()) {
			char c = iterator.next();
			if (isDelimiter(c, delimiter))
				break;
			buffer.append(c);
		}
		return buffer.toString();
	}

	private static boolean isDelimiter(char c, String delimiter) {
		return delimiter.indexOf(c) != -1;
	}
}
