package abbot.swt.junit.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Display;

public class UserThread extends Thread {
	
	public static final String NAME = "abbot.swt.user";
	
	public static boolean isOnUserThread() {
		return Thread.currentThread().getName().equals(NAME);
	}

	public interface Executable {
		void execute() throws Throwable;
	}

	public static UserThread syncExec(Executable executable) {
		UserThread userThread = new UserThread(executable);
		userThread.setDaemon(true);
		userThread.start();
		userThread.sync();
		return userThread;
	}

	private final Executable executable;
	
	private boolean finished;

	private Throwable exception;

	private List<Throwable> exceptions;

	public UserThread(Executable executable) {
		super(NAME);
		if (executable == null)
			throw new IllegalArgumentException("executable is null");
		this.executable = executable;
		setFinished(false);
	}
	
	private synchronized void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	private synchronized boolean isFinished() {
		return finished;
	}

	public synchronized Throwable getException() {
		return exception;
	}

	private synchronized void setException(Throwable exception) {
		this.exception = exception;
	}

	public synchronized List<Throwable> getExceptions() {
		if (exceptions == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(exceptions);
	}

	private synchronized void addException(Throwable throwable) {
		if (exceptions == null)
			exceptions = new ArrayList<Throwable>();
		exceptions.add(throwable);
	}

	public void run() {
		try {
			executable.execute();
		} catch (Throwable throwable) {
			setException(throwable);
		} finally {
			setFinished(true);
			Display.getDefault().wake();
		}
	}

	public void sync() {

		Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalStateException("not on display thread");

		// Run an event loop while waiting for the user thread to finish, saving any exceptions that
		// occur.
		while (isAlive() && !isFinished()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (ThreadDeath death) {
               	// ThreadDeath is a normal error when the thread is dying.  We must
               	// propagate it in order for it to properly terminate.
				throw death;
			} catch (Throwable throwable) {
				addException(throwable);
			}
		}
	}

}
