/**
 * 
 */
package abbot.swt.utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import abbot.swt.Log;

/**
 * Some {@link Thread} utilities.
 */
public class Threads {
	
	public static Iterable<Thread> all() {
		return new Iterable<Thread>() {
			public Iterator<Thread> iterator() {
				return new ThreadIterator(null);
			}
		};
	}

	private static class ThreadIterator implements Iterator<Thread> {

		private int count;

		private Thread[] threads;

		private int i = 0;

		public ThreadIterator(ThreadGroup group) {

			if (group == null) {
				// Find the root thread group (i.e., the one with no parent).
				group = Thread.currentThread().getThreadGroup();
				while (group.getParent() != null)
					group = group.getParent();
			}

			// Get all threads in the group, recursively.
			threads = new Thread[group.activeCount() + 20];
			count = group.enumerate(threads);
			if (count == threads.length)
				Log.warn("might have missed some threads/displays");
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return i < count;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Thread next() {
			if (i < count)
				return threads[i++];
			throw new NoSuchElementException();
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
