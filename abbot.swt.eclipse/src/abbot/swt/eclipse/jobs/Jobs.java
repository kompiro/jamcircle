package abbot.swt.eclipse.jobs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import abbot.swt.Log;

public class Jobs {

	/**
	 * Names of Jobs to be ignored by {@link #join()} and {@link #join(long)}
	 */
	private static Set<String> ignored = new HashSet<String>();

	/**
	 * Add a Job name to the ones to be ignored by {@link #join()} and {@link #join(long)}
	 * 
	 * @param name -
	 *            the name of the Job to ignore
	 */
	public static synchronized void ignore(String name) {
		// Log.debug("Jobs: Ignoring Jobs named {0}", name);
		ignored.add(name);
	}

	/**
	 * Remove a Job name from the ones to be ignored by {@link #join()} and {@link #join(long)}
	 * 
	 * @param name -
	 *            the name of the Job to no longer ignore
	 */
	public static synchronized void unignore(String name) {
		// Log.debug("Jobs: Unignoring Jobs named {0}", name);
		ignored.remove(name);
	}

	/**
	 * Remove all Job names from the ones to be ignored by {@link #join()} and {@link #join(long)}
	 */
	public static synchronized void unignoreAll() {
		// Log.debug("Jobs: Unignoring all Jobs");
		ignored.clear();
	}

	/**
	 * Wait for all currently waiting, executing and sleeping Jobs to complete <em>except</em> for
	 * those currently being ignored.
	 */
	public static void join() {
		Log.debug("Jobs: Joining all Jobs");
		checkThread();
		Job[] jobs = getJobs();
		for (int i = 0; i < jobs.length; i++) {
			Job job = jobs[i];
			if (shouldJoin(job))
				join(job);
		}
		Log.debug("Jobs: Joining all Jobs complete");
	}

	private static void checkThread() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (display.getThread() == Thread.currentThread())
			throw new IllegalStateException("on display thread");
	}

	private static Job[] getJobs() {
		return Job.getJobManager().find(null);
	}

	private static boolean shouldJoin(Job job) {
		return job.getPriority() != Job.DECORATE && !ignored.contains(job.getName());
	}

	private static boolean join(Job job) {
		Log.debug("Jobs: Joining Job: {0}", job);
		try {
			job.join();
			Log.debug("Jobs: Joining Job complete: {0}", job);
			return true;
		} catch (InterruptedException exception) {
			// Fall through to return false.
		}
		Log.debug("Jobs: Joining Job interrupted: {0}", job);
		return false;
	}

	/**
	 * Wait for Jobs to complete.
	 * 
	 * @param timeout -
	 *            the number of milliseconds to wait for any one job. Once the wait on a job has
	 *            exceeded this value, the job is skipped.
	 */
	public static void join(long timeout) {
		Log.debug("Jobs: Joining all Jobs (timeout {0} ms.", timeout);
		checkThread();
		Job[] jobs = getJobs();
		for (int i = 0; i < jobs.length; i++) {
			Job job = jobs[i];
			if (shouldJoin(job))
				join(job, timeout);
		}
		Log.debug("Jobs: Joining all Jobs complete", timeout);
	}

	private static void join(Job job, long timeout) {

		Log.debug("Jobs: Joining Job ({0} ms.): {1}", timeout, job);

		// Create and start a Thread to join the Job.
		Joiner joiner = new Joiner(job);
		joiner.start();

		// Wait at most timeout milliseconds for the joiner Thread to finish.
		try {
			joiner.join(timeout);
			if (joiner.isAlive()) {
				joiner.quit();
				Log.debug("Jobs: Joining Job timed out: {0}", job);
			} else {
				Log.debug("Jobs: Joining Job complete: {0}", job);
			}
		} catch (InterruptedException e) {
			Log.debug("Jobs: Joining Job interrupted: {0}", job);
		}
	}

	private static class Joiner extends Thread {

		private final Job job;

		private boolean done;

		public Joiner(Job job) {
			this.job = job;
		}

		public void run() {
			while (!done) {
				try {
					job.join();
				} catch (InterruptedException exception) {
					// Empty block intended.
				}
			}
		}

		public void quit() {
			done = true;
			interrupt();
		}
	}

}
