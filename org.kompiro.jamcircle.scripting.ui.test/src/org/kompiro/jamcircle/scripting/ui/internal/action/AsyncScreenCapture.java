package org.kompiro.jamcircle.scripting.ui.internal.action;

import java.io.File;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.ImageFormatConverter;

public class AsyncScreenCapture {

	private static Display display;

	public AsyncScreenCapture(Display display) {
		AsyncScreenCapture.display = display;
	}

	public void captureScreenshot(final String fileName) {
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				captureScreenshotInternal(fileName, display.getBounds());
			}
		});
	}

	private static boolean captureScreenshotInternal(final String fileName, Rectangle bounds) {
		GC gc = new GC(display);
		Image image = null;
		File file = new File(fileName);
		File parentDir = file.getParentFile();
		if (parentDir != null)
			parentDir.mkdirs();
		try {

			image = new Image(display, bounds.width, bounds.height);
			gc.copyArea(image, bounds.x, bounds.y);
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { image.getImageData() };
			imageLoader.save(fileName,
					new ImageFormatConverter().imageTypeOf(fileName.substring(fileName.lastIndexOf('.') + 1)));
			return true;
		} catch (Exception e) {
			File brokenImage = file.getAbsoluteFile();
			if (brokenImage.exists()) {
				try {
					brokenImage.deleteOnExit();
				} catch (Exception ex) {
				}
			}
			return false;
		} finally {
			gc.dispose();
			if (image != null) {
				image.dispose();
			}
		}
	}
}
