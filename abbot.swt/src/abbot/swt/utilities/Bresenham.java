package abbot.swt.utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.swt.graphics.Point;

/**
 * Uses the Bresenham 2D line-drawing algorithm to generate a series of
 * {@link Point}s that form a line between two endpoints (inclusive).
 */
public class Bresenham implements Iterable<Point>, Iterator<Point> {

	private Point next;

	private int x0;

	private int y0;

	private int x1;

	private int y1;

	private int dx;

	private int dy;

	private int stepx;

	private int stepy;

	private int fraction;

	/**
	 * Constructor
	 * 
	 * @param p0
	 *            the starting {@link Point}
	 * @param p1
	 *            the ending {@link Point}
	 */
	public Bresenham(Point p0, Point p1) {
		this(p0.x, p0.y, p1.x, p1.y);
	}

	/**
	 * @param x0
	 *            the x-coordinate of the starting {@link Point}
	 * @param y0
	 *            the y-coordinate of the starting {@link Point}
	 * @param x1
	 *            the x-coordinate of the ending {@link Point}
	 * @param y1
	 *            the y-coordinate of the ending {@link Point}
	 */
	public Bresenham(int x0, int y0, int x1, int y1) {

		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;

		dy = y1 - y0;
		dx = x1 - x0;

		if (dy < 0) {
			dy = -dy;
			stepy = -1;
		} else {
			stepy = 1;
		}
		if (dx < 0) {
			dx = -dx;
			stepx = -1;
		} else {
			stepx = 1;
		}
		dy <<= 1; // dy is now 2*dy
		dx <<= 1; // dx is now 2*dx

		if (dx > dy)
			fraction = dy - (dx >> 1); // same as 2*dy - dx
		else
			fraction = dx - (dy >> 1);

		next = new Point(x0, y0);
	}

	/**
	 * @see Iterable#iterator()
	 */
	public Iterator<Point> iterator() {
		return this;
	}

	/**
	 * @see Iterator#hasNext()
	 */
	public boolean hasNext() {
		return peek() != null;
	}

	/**
	 * @see Iterator#next()
	 */
	public synchronized Point next() {
		Point point = peek();
		if (point == null)
			throw new NoSuchElementException();
		next = null;
		return point;
	}

	/**
	 * Returns the next {@link Point} in the series but does not advance. That
	 * is, subsequent calls (without any intervening calls to {@link #next()})
	 * will return the same {@link Point}.
	 * 
	 * @return the next {@link Point} in the series, or <code>null</code> if
	 *         there isn't one.
	 */
	public synchronized Point peek() {
		if (next == null) {
			if (dx > dy) {
				if (x0 != x1) {
					if (fraction >= 0) {
						y0 += stepy;
						fraction -= dx; // same as fraction -= 2*dx
					}
					x0 += stepx;
					fraction += dy; // same as fraction -= 2*dy
					next = new Point(x0, y0);
				}
			} else {
				if (y0 != y1) {
					if (fraction >= 0) {
						x0 += stepx;
						fraction -= dy;
					}
					y0 += stepy;
					fraction += dx;
					next = new Point(x0, y0);
				}
			}
		}
		return next;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	/* Static methods just for debugging. */
	// public static void main(String[] args) {
	// run(5, 6, 20, 30);
	// run(20, 30, 5, 6);
	// run(6, 5, 30, 20);
	// run(30, 20, 6, 5);
	// }
	//
	// private static void run(int x0, int y0, int x1, int y1) {
	// Bresenham b = new Bresenham(x0, y0, x1, y1);
	// System.out.printf("%d,%d --> %d,%d\n", x0, y0, x1, y1);
	// while (b.hasNext()) {
	// Point p = b.next();
	// System.out.printf(" %d,%d\n", p.x, p.y);
	// }
	// }
}
