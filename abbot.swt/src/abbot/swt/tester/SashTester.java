package abbot.swt.tester;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;

import abbot.swt.script.Condition;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for {@link Sash}es.
 */
public class SashTester extends ControlTester {

	/**
	 * Factory method.
	 */
	public static SashTester getSashTester() {
		return (SashTester) getTester(Sash.class);
	}

	/**
	 * Constructs a new {@link SashTester} associated with the specified
	 * {@link abbot.swt.Robot}.
	 */
	public SashTester(abbot.swt.Robot swtRobot) {
		super(swtRobot);
	}

	/**
	 * Sets the location of a {@link Sash}.
	 * <p>
	 * <strong>Note:</strong> The location of a <em>horizontal</em>
	 * {@link Sash} represents the desired <em>y</em>-coordinate. The
	 * location of a <em>vertical</em> {@link Sash} represents the new
	 * x-coordinate. Negative location values are automatically set to 0, and
	 * those that are larger than the reciever'sash parent are set to the width
	 * of the parent.
	 */
	public void actionSetSashLocation(final Sash sash, int location) {
		checkWidget(sash);

		// Bring location within valid bounds.
		if (location < 0)
			location = 0;
		Composite parent = getParent(sash);
		Rectangle parentClientArea = CompositeTester.getCompositeTester()
				.getClientArea(parent);
		final boolean isVertical = (getStyle(sash) & SWT.VERTICAL) != 0;
		Rectangle sashBounds = getBounds(sash);
		int maxLocation = (isVertical ? parentClientArea.width
				- sashBounds.width : parentClientArea.height
				- sashBounds.height) - 1;
		if (location > maxLocation)
			location = maxLocation;

		// Sash center (parent-relative).
		Point c0 = getCenter(sashBounds);

		// New Sash center (parent-relative).
		// Depends on whether Sash is vertical or horizontal.
		Point c1 = isVertical ? new Point(c0.x - (sashBounds.x - location),
				c0.y) : new Point(c0.x, c0.y - (sashBounds.y - location));

		// Convert Sash centers to display-relative and do the drag & drop.
		Point d0 = toDisplay(parent, c0);
		Point d1 = toDisplay(parent, c1);
		dragDrop(d0.x, d0.y, d1.x, d1.y, SWT.BUTTON1);

		// Wait for its location to be updated.
		waitLocation(sash, location);
	}

	private void waitLocation(final Sash sash, final int finalLocation) {
		if ((getStyle(sash) & SWT.VERTICAL) != 0) {
			wait(new Condition() {
				Point location = null;
				public boolean test() {
					location = getLocation(sash);
					return location.x == finalLocation;
				}
				public String toString() {
					return String.format("expected x == %d, got %d", finalLocation, location.x);
				}
			}, 5000);
		} else {
			wait(new Condition() {
				Point location = null;
				public boolean test() {
					location = getLocation(sash);
					return location.y == finalLocation;
				}
				public String toString() {
					return String.format("expected y == %d, got %d", finalLocation, location.y);
				}
			}, 5000);
		}
	}

	protected Point toDisplay(final Composite parent, final int x, final int y) {
		return (Point) syncExec(new Result() {
			public Object result() {
				return parent.toDisplay(x, y);
			}
		});
	}

	/**
	 * Moves the sash by the given amount, or to the edge of the reciver'sash
	 * parent.
	 */
	public void actionMoveSashBy(Sash sash, int amount) {
		checkWidget(sash);
		int style = getStyle(sash);
		if ((style & SWT.VERTICAL) == SWT.VERTICAL) {
			int currentX = getLocation(sash).x;
			int moveToX = currentX + amount;
			actionSetSashLocation(sash, moveToX);
		} else {
			int currentY = getLocation(sash).y;
			int moveToY = currentY + amount;
			actionSetSashLocation(sash, moveToY);
		}
	}

	/**
	 * Proxy for {@link Sash#addSelectionListener(SelectionListener listener)}.
	 */
	public void addSelectionListener(final Sash sash,
			final SelectionListener listener) {
		checkWidget(sash);
		syncExec(new Runnable() {
			public void run() {
				sash.addSelectionListener(listener);
			}
		});
	}

	/**
	 * Proxy for
	 * {@link Sash#removeSelectionListener(SelectionListener listener)}.
	 */
	public void removeSelectionListener(final Sash sash,
			final SelectionListener listener) {
		checkWidget(sash);
		syncExec(new Runnable() {
			public void run() {
				sash.removeSelectionListener(listener);
			}
		});
	}
}
