package abbot.swt.utilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import abbot.swt.Robot;

/**
 * Captures an image from a {@link Display} and saves it to a file.
 * 
 * @see Robot#capture(Rectangle)
 * @see Robot#capture()
 */
public class ScreenCapture {

	/**
	 * The default file name (in the current directory).
	 */
	public static final String DEFAULT_FILENAME = "capture";

	/**
	 * The default image file format to save as.
	 */
	public static final int DEFAULT_FORMAT = SWT.IMAGE_PNG;

	/** No instances. */
	private ScreenCapture() {}

	/**
	 * Capture a rectangular area of a display and save it to a file with a specified format.
	 * @param area
	 *            the {@link Rectangle} area in the {@link Display} to be captured
	 * @param name
	 *            the name (including path, if desired) of the target file
	 * @param format
	 *            the image file format to use (see {@link SWT#IMAGE_PNG} et al.)
	 */
	public static void capture(Rectangle area, String name, int format) {
		Image image = null;
		try {
			image = getImage(area);
			String filename = getFilename(name, format);
			saveImage(image, filename, format);
		} finally {
			if (image != null)
				image.dispose();
		}
	}

	/**
	 * Capture a rectangular area of a display and save it to a file with the default format.
	 * 
	 * @param display
	 *            the {@link Display} to capture from
	 * @param area
	 *            the {@link Rectangle} area in the {@link Display} to be captured
	 * @param name
	 *            the name (including path, if desired) of the target file
	 * @see #DEFAULT_FORMAT
	 */
	public static void capture(Display display, Rectangle area, String name) {
		capture(area, name, DEFAULT_FORMAT);
	}

	/**
	 * Capture a rectangular area of a display and save it to a file with the default name and
	 * format.
	 * 
	 * @param display
	 *            the {@link Display} to capture from
	 * @param area
	 *            the {@link Rectangle} area in the {@link Display} to be captured
	 * @see #DEFAULT_FORMAT
	 * @see #DEFAULT_FILENAME
	 */
	public static void capture(Display display, Rectangle area) {
		capture(display, area, DEFAULT_FILENAME);
	}

	/**
	 * Capture an entire display and save it to a file with a specified format.
	 * 
	 * @param display
	 *            the {@link Display} to capture from
	 * @param name
	 *            the name (including path, if desired) of the target file
	 * @param format
	 *            the image file format to use (see {@link SWT#IMAGE_PNG} et al.)
	 */
	public static void capture(Display display, String name, int format) {
		capture(display.getBounds(), name, format);
	}

	/**
	 * Capture an entire display and save it to a file with the default format.
	 * 
	 * @param display
	 *            the {@link Display} to capture from
	 * @param name
	 *            the name (including path, if desired) of the target file
	 * @see #DEFAULT_FORMAT
	 */
	public static void capture(Display display, String name) {
		capture(display, name, DEFAULT_FORMAT);
	}

	/**
	 * Capture an entire display and save it to a file with the default name and format.
	 * 
	 * @param display
	 *            the {@link Display} to capture from
	 * @see #DEFAULT_FORMAT
	 * @see #DEFAULT_FILENAME
	 */
	public static void capture(Display display) {
		capture(display, DEFAULT_FILENAME);
	}

	/**
	 * Captures a {@link Rectangle} area of a {@link Display} and returns it as an {@link Image}.
	 * <p>
	 * <strong>Note:</strong> The caller is responsible for disposing the {@link Image} that is
	 * returned from this method.
	 * @param area
	 *            the {@link Rectangle} area of the {@link Display} to capture
	 * 
	 * @return an {@link Image}
	 * @see Robot#capture(Rectangle)
	 */
	private static Image getImage(Rectangle area) {
		Robot robot = Robot.getDefault();
		return robot.capture(area);
	}

	/**
	 * Gets an appropriate file name to use for saving an image in a particular format.
	 * 
	 * @param filename
	 *            the proposed file name
	 * @param format
	 *            the image file format
	 * @return the file name, which will be either the original filename (if it already had the
	 *         appropriate extension for the specified format) or the filename with the appropriate
	 *         extension appended.
	 */
	private static String getFilename(String filename, int format) {
		String extension = getExtension(format);
		if (filename.endsWith(extension))
			return filename;
		return filename + extension;
	}

	/**
	 * Gets the appropriate file name extension for a specified image file format.
	 * 
	 * @param format
	 *            the image file format
	 * @return the appropriate corresponding file name extension
	 */
	private static String getExtension(int format) {
		switch (format) {
			case SWT.IMAGE_PNG:
				return ".png";
			case SWT.IMAGE_JPEG:
				return ".jpg";
			case SWT.IMAGE_GIF:
				return ".gif";
			case SWT.IMAGE_BMP:
			case SWT.IMAGE_BMP_RLE:
			case SWT.IMAGE_OS2_BMP:
				return ".bmp";
			case SWT.IMAGE_ICO:
				return ".ico";
			case SWT.IMAGE_TIFF:
				return ".tif";
			default:
				SWT.error(SWT.ERROR_UNSUPPORTED_FORMAT);
		}
		throw new RuntimeException("unreachable");
	}

	/**
	 * Saves an {@link Image} to a file with a specified image file format.
	 * 
	 * @param image
	 *            the {@link Image} to save
	 * @param filename
	 *            the name (including path, if desired) of the target file
	 * @param format
	 *            the image file format to use
	 */
	private static void saveImage(Image image, String filename, int format) {
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(filename, format);
	}

}
