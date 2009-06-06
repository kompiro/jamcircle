package abbot.swt.utilities;

import java.util.Comparator;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

/**
 * Compares <code>org.eclipse.swt.graphics.Image</code> objects on a per-pixel basis.
 * 
 * @author Gary Johnston
 * @author Kevin Dale
 */
public class ImageComparator implements Comparator {
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object object1, Object object2) {

		// Note: No need to try to avoid ClassCastExceptions here.
		ImageData image1 = ((Image) (object1)).getImageData();
		ImageData image2 = ((Image) (object2)).getImageData();

		if (image1.width != image2.width || image1.height != image2.height) {
			// The images do not have the same dimensions.
			// If their sizes (number of pixels) differ, return the difference.
			// Otherwise, if their heights differ, return the difference.
			// Otherwise, return the difference in their widths.
			int size1 = image1.width * image1.height;
			int size2 = image2.width * image2.height;
			if (size1 != size2)
				return size1 - size2;
			if (image1.height != image2.height)
				return image1.height - image2.height;
			return image1.width - image2.width;
		}

		// The images have the same dimensions.
		// If their brightnesses differ, return the difference.
		// Otherwise, if their saturations differ, return the difference.
		// Otherwise, return the difference in their hues.
		double h1 = 0.0;
		double s1 = 0.0;
		double b1 = 0.0;
		double h2 = 0.0;
		double s2 = 0.0;
		double b2 = 0.0;
		for (int i = 0; i < image1.width; i++) {
			for (int j = 0; j < image1.height; j++) {
				RGB rgb1 = image1.palette.getRGB(image1.getPixel(i, j));
				RGB rgb2 = image2.palette.getRGB(image2.getPixel(i, j));
//				if (i > 0 && j == 0)
//					System.out.println();
				if (!rgb1.equals(rgb2)) {
//					System.out.print('*');

					float[] hsb1 = rgb1.getHSB();
					float[] hsb2 = rgb2.getHSB();
					h1 += hsb1[0];
					s1 += hsb1[1];
					b1 += hsb1[2];
					h2 += hsb2[0];
					s2 += hsb2[1];
					b2 += hsb2[2];
				} else {
//					System.out.print('.');
				}
			}
		}
		if (b1 != b2)
			return (int) (b1 - b2);
		if (s1 != s2)
			return (int) (s1 - s2);
		return (int) (h1 - h2);
	}

}
