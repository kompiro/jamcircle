package org.kompiro.jamcircle.scripting.ui.internal.action;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class AsyncScreenshotCaptureListener extends RunListener {
	private static int screenshotCounter = 0;
	private AsyncScreenCapture capture;

	public AsyncScreenshotCaptureListener(Display display) {
		capture = new AsyncScreenCapture(display);

	}

	public void testFailure(Failure failure) throws Exception {
		captureScreenshot(failure);
	}

	private void captureScreenshot(Failure failure) {
		try {
			int maximumScreenshots = SWTBotPreferences.MAX_ERROR_SCREENSHOT_COUNT;
			String fileName = SWTBotPreferences.SCREENSHOTS_DIR
					+ "/" + failure.getTestHeader() + "." + SWTBotPreferences.SCREENSHOT_FORMAT.toLowerCase(); //$NON-NLS-1$
			if (++screenshotCounter <= maximumScreenshots) {
				captureScreenshot(fileName);
			} else {
			}
		} catch (Exception e) {
		}
	}

	private void captureScreenshot(String fileName) {
		capture.captureScreenshot(fileName);
	}

	public int hashCode() {
		return 31;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
