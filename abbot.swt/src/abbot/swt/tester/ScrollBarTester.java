package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.BooleanResult;
import abbot.swt.utilities.Displays.IntResult;
import abbot.swt.utilities.Displays.Result;

/**
 * Provides widget-specific actions for testing a scrollBar.
 * 
 * @version $Id: ScrollBarTester.java 2121 2007-01-16 15:21:23Z gjohnsto $
 */
public class ScrollBarTester extends WidgetTester {
	/**
	 * Factory method.
	 */
	public static ScrollBarTester getScrollBarTester() {
		return (ScrollBarTester) WidgetTester.getTester(ScrollBar.class);
	}

	/**
	 * Constructs a new {@link ScrollBarTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public ScrollBarTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * These getter methods return a particular property of the given widget.
	 * 
	 * @see the corresponding member function in class Widget
	 */
	/* Begin getters */
	/**
	 * Proxy for {@link ScrollBar#getEnabled()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the enabled state.
	 */
	public boolean getEnabled(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new BooleanResult() {
			public boolean result() {
				return bar.getEnabled();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getIncrement()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the increment.
	 */
	public int getIncrement(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new IntResult() {
			public int result() {
				return bar.getIncrement();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getMaximum()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the maximum.
	 */
	public int getMaximum(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new IntResult() {
			public int result() {
				return bar.getMaximum();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getMinimum()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the minimum.
	 */
	public int getMinimum(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new IntResult() {
			public int result() {
				return bar.getMinimum();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getPageIncrement()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the page increment.
	 */
	public int getPageIncrement(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new IntResult() {
			public int result() {
				return bar.getPageIncrement();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getParent()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the parent.
	 */
	public Scrollable getParent(final ScrollBar bar) {
		return (Scrollable) Displays.syncExec(bar.getDisplay(), new Result() {
			public Object result() {
				return bar.getParent();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getSelection()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the selection.
	 */
	public int getSelection(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new IntResult() {
			public int result() {
				return bar.getSelection();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getSize()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the point describing the size.
	 */
	public Point getSize(final ScrollBar bar) {
		return (Point) Displays.syncExec(bar.getDisplay(), new Result() {
			public Object result() {
				return bar.getSize();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getThumb()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the thumb value.
	 */
	public int getThumb(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new IntResult() {
			public int result() {
				return bar.getThumb();
			}
		});
	}

	/**
	 * Proxy for {@link ScrollBar#getVisible()}. <p/>
	 * 
	 * @param bar
	 *            the bar under test.
	 * @return the visible state.
	 */
	public boolean getVisible(final ScrollBar bar) {
		return Displays.syncExec(bar.getDisplay(), new BooleanResult() {
			public boolean result() {
				return bar.getVisible();
			}
		});
	}

	public boolean isVisible(final ScrollBar scrollBar) {
		return syncExec(new BooleanResult() {
			public boolean result() {
				return scrollBar.isVisible();
			}
		});
	}

	/**
	 * @see WidgetTester#isVisible(Widget)
	 */
	public boolean isVisible(Widget widget) {
		if (widget instanceof ScrollBar)
			return isVisible((ScrollBar) widget);
		return super.isVisible(widget);
	}

	/* End getters */

	// platform-dependent fields based on the rendering of the scrollbar
	public final static int BUTTON_SIZE = 17;

	public final static int THUMB_OFFSET = 2;

	/**
	 * Scroll the given amount, where amount==the number of times the arrow is pressed. Positive
	 * amounts scroll down for vertical scrollbars and right for horizontal ones.
	 */
	public void actionScroll(ScrollBar bar, int amount) {

		if (amount != 0) {

			// Needed? Why does the bar need focus?
			actionFocus(bar);

			// Get bounds in display coordinates.
			Rectangle bounds = getGlobalBounds(bar);

//			Log.debug("bar at: %s\n", bounds);

			// Move to the appropriate scroll button and make amount positive.
			int x, y;
			boolean isVertical = (getStyle(bar) & SWT.VERTICAL) != 0;
			int offset = (isVertical ? bounds.width : bounds.height) / 2;
			if (amount < 0) {
				if (isVertical) {
					// Top button.
					x = bounds.x + offset;
					y = bounds.y + offset;
				} else {
					// Left button.
					x = bounds.x + offset;
					y = bounds.y + offset;
				}
				amount = -amount;
			} else {
				if (isVertical) {
					// Bottom button.
					x = bounds.x + offset;
					y = bounds.y + bounds.height - offset;
				} else {
					// Right button.
					x = bounds.x + bounds.width - offset;
					y = bounds.y + offset;
				}
			}
//			Log.debug("button at: %d,%d\n", x, y);
			mouseMove(x, y);
//			sleep(5000);

			// Click the requested number of times.
			for (int i = 0; i < amount; i++)
				robot.mouseClick(SWT.BUTTON1);

			// Needed? What are we waiting for?
			actionWaitForIdle();
		}
	}

	/**
	 * Page the given amount, where amount==# of times that the bar is clicked in the page area
	 */
	public synchronized void actionPageScroll(final ScrollBar bar, int amount) {
		actionFocus(bar);
		final Rectangle bounds = getBounds(bar);

		// convert bounds to global bounds
		Point p = getParent(bar).toDisplay(bounds.x, bounds.y);
		bounds.x = p.x;
		bounds.y = p.y;

		// System.out.println("BOUNDS: "+bounds);
		int style = getStyle(bar);

		if (amount < 0) {
			if ((style & SWT.HORIZONTAL) == SWT.HORIZONTAL)
				mouseMove(bounds.x + BUTTON_SIZE, bounds.y + BUTTON_SIZE / 2);
			else
				mouseMove(bounds.x + BUTTON_SIZE / 2, bounds.y + BUTTON_SIZE);
			for (int i = 0; i < amount; i++) {
				robot.mouseClick(SWT.BUTTON1);
			}
		} else if (amount > 0) {
			if ((style & SWT.HORIZONTAL) == SWT.HORIZONTAL)
				mouseMove(bounds.x + bounds.width - BUTTON_SIZE, bounds.y + bounds.height
						- BUTTON_SIZE / 2);
			else
				mouseMove(bounds.x + bounds.width - BUTTON_SIZE / 2, bounds.y + bounds.height
						- BUTTON_SIZE);
			for (int i = 0; i < amount; i++) {
				robot.mouseClick(SWT.BUTTON1);
			}
		}
		actionWaitForIdle();
	}

	/**
	 * Sets the selection to the given value, or as close as possible, by dragging the slider. The
	 * smaller the ratio of bar.getThumb()/(bar.getMaximum()-bar.getMinimum()), the less accurate
	 * this method is.
	 */
	// TODO FIXME method loses the lock now between calls to syncExec, so
	// we need to do everything in one big syncExec block
	public synchronized void actionScrollSetSelection(final ScrollBar bar, final int val) {
		actionFocus(bar);
		final Rectangle bounds = getBounds(bar);

		// convert bounds to global bounds
		ScrollableTester tester = ScrollableTester.getScrollableTester();
		Point p = tester.toDisplay(getParent(bar), new Point(bounds.x, bounds.y));
		// Point p = getParent(bar).toDisplay(bounds.x, bounds.y);
		bounds.x = p.x;
		bounds.y = p.y;

		// get info about current selection
		int style = getStyle(bar);
		int selection = getSelection(bar);
		int increment = getIncrement(bar);
		int minimum = getMinimum(bar);
		int maximum = getMaximum(bar);
		int thumb = getThumb(bar);
		actionWaitForIdle();
		int setTo = val;
		if (setTo < minimum)
			setTo = minimum;
		else if (setTo > maximum - thumb)
			setTo = maximum - thumb;

		if ((style & SWT.HORIZONTAL) == SWT.HORIZONTAL) {
			double thumbEdge = (double) ((double) selection / (double) (maximum - minimum));
			double thumbWidth = (double) ((double) thumb / (double) (maximum - minimum));
			// double setEdge = (double) ((double) setTo / (double) (maximum - minimum));
			double delta = (double) ((double) increment / (double) (maximum - minimum));

			int stripWidth = bounds.width - 2 * BUTTON_SIZE;
			int thumbLocPixels = (int) (thumbEdge * stripWidth);
			int thumbWidthPixels = (int) (thumbWidth * stripWidth);
			// int setEdgePixels = (int) (setEdge * stripWidth);
			int deltaPixels = (int) (delta * stripWidth + 1);
			// System.out.println(deltaPixels);
			// System.out.println("THUMB:"+thumb+" MAX: "+max+"SELECTION:"+selection+"
			// THUMBEDGE:"+thumbEdge+" stripWidth:"+stripWidth+" THUMBLOC: "+thumbLoc);
			Point thumbPoint = new Point(bounds.x + BUTTON_SIZE + thumbLocPixels + thumbWidthPixels
					/ 2, bounds.y + bounds.height / 2);
			Point offset = new Point(0, 0);

			// Point moveTo = new Point(bounds.x + BUTTON_SIZE + setEdgePixels + thumbWidthPixels /
			// 2,
			// bounds.y + bounds.height / 2);
			mouseMove(thumbPoint.x, thumbPoint.y);
			// mousePress(SWT.BUTTON1);
			// mouseMove(moveTo.x,moveTo.y);
			// mouseRelease(SWT.BUTTON1);
			actionWaitForIdle();

			int oldSelection = setTo;

			if (setTo < selection) {
				while (true) {

					selection = getSelection(bar);

					if (oldSelection == selection) {
						// System.out.println("Exiting b/c oldSelection==selection");
						break;
					}

					if (selection <= setTo) {
						// System.out.println("Exiting b/c selection<=setTo");
						break;
					}
					offset.x -= deltaPixels;

					robot.mouseDragDrop(
							thumbPoint.x,
							thumbPoint.y,
							thumbPoint.x + offset.x,
							thumbPoint.y + offset.y,
							SWT.BUTTON1);

					oldSelection = selection;
				}
			}

			else if (setTo > selection) {
				while (true) {
					selection = getSelection(bar);

					if (oldSelection == selection)
						break;
					if (selection >= setTo)
						break;

					offset.x += deltaPixels;

					robot.mouseDragDrop(
							thumbPoint.x,
							thumbPoint.y,
							thumbPoint.x + offset.x,
							thumbPoint.y + offset.y,
							SWT.BUTTON1);

					oldSelection = selection;
				}
			}
		}

		else {// style==SWT.VERTICAL
			double thumbEdge = (double) ((double) selection / (double) (maximum - minimum));
			double thumbWidth = (double) ((double) thumb / (double) (maximum - minimum));
			double delta = (double) ((double) increment / (double) (maximum - minimum));
			int stripWidth = bounds.height - 2 * BUTTON_SIZE;
			int thumbLocPixels = (int) (thumbEdge * stripWidth);
			int thumbWidthPixels = (int) (thumbWidth * stripWidth);
			int deltaPixels = (int) (delta * stripWidth + 1);
			// System.out.println("THUMB:"+thumb+" MAX: "+max+"SELECTION:"+selection+"
			// THUMBEDGE:"+thumbEdge+" stripWidth:"+stripWidth+" THUMBLOC: "+thumbLoc);
			Point thumbPoint = new Point(bounds.x + bounds.width / 2, bounds.y + BUTTON_SIZE
					+ thumbLocPixels + thumbWidthPixels / 2);
			Point offset = new Point(0, 0);
			mouseMove(thumbPoint.x, thumbPoint.y);
			actionWaitForIdle();

			int oldSelection = setTo;

			if (setTo < selection) {
				while (true) {

					selection = getSelection(bar);

					if (oldSelection == selection) {
						// System.out.println("Exiting b/c oldSelection==selection");
						break;
					}

					if (selection <= setTo) {
						// System.out.println("Exiting b/c selection<=setTo");
						break;
					}
					offset.y -= deltaPixels;

					robot.mouseDragDrop(
							thumbPoint.x,
							thumbPoint.y,
							thumbPoint.x + offset.x,
							thumbPoint.y + offset.y,
							SWT.BUTTON1);

					oldSelection = selection;
				}
			}

			else if (setTo > selection) {
				while (true) {
					selection = getSelection(bar);
					// System.out.println((thumbPoint.x+offset.x)+","+(thumbPoint.y+offset.y));
					// System.out.println("setTo="+setTo+" selection="+selection);
					if (oldSelection == selection) {
						// System.out.println("Exiting b/c oldSelection==selection");
						break;
					}
					if (selection >= setTo) {
						// System.out.println("Exiting b/c selection>=setTo");
						break;
					}

					offset.y += deltaPixels;

					robot.mouseDragDrop(
							thumbPoint.x,
							thumbPoint.y,
							thumbPoint.x + offset.x,
							thumbPoint.y + offset.y,
							SWT.BUTTON1);

					oldSelection = selection;
				}
			}
		}

		// make sure that we actually changed the scroll position
		selection = getSelection(bar);

		// int rangeMin = setTo - increment;
		// int rangeMax = setTo + increment;
		// if( !(selection>=rangeMin && selection<=rangeMax) )
		// Log.warn("Failed to set scrollbar appropriately (setTo="+setTo+" selection="+selection);

		actionWaitForIdle();
	}
}
