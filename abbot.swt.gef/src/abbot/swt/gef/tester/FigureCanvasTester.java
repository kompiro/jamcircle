package abbot.swt.gef.tester;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import abbot.swt.Robot;
import abbot.swt.tester.CanvasTester;
import abbot.swt.tester.WidgetTester;

/**
 * A tester for a {@link FigureCanvas} that can impose a grid on the underlying {@link FigureCanvas}.
 * It is intended for use with {@link FigureCanvas}es in tests of GEF-based editors (and views,
 * etc.).
 * <p>
 * TODO Rename this class to FigureCanvasTester!
 */
public class FigureCanvasTester extends CanvasTester {

	static {
		WidgetTester.getFactory().addPackage(
				FigureCanvasTester.class.getPackage().getName(),
				FigureCanvasTester.class.getClassLoader());
	}

	public static FigureCanvasTester getFigureCanvasTester() {
		return (FigureCanvasTester) WidgetTester.getTester(FigureCanvas.class);
	}

	/**
	 * Default grid spacing (in pixels).
	 */
	private static final int DEFAULT_GRID_XY = 50;

	private final Point grid;

	public FigureCanvasTester(Robot robot, int x, int y) {
		super(robot);
		this.grid = new Point(x, y);
	}

	public FigureCanvasTester(Robot robot, Point grid) {
		this(robot, grid.x, grid.y);
	}

	public FigureCanvasTester(int x, int y) {
		this(Robot.getDefault(), x, y);
	}

	public FigureCanvasTester(Point grid) {
		this(grid.x, grid.y);
	}

	public FigureCanvasTester(int xy) {
		this(xy, xy);
	}

	public FigureCanvasTester() {
		this(DEFAULT_GRID_XY);
	}

	public synchronized Point getGrid() {
		return new Point(grid.x, grid.y);
	}

	public synchronized void setGrid(int x, int y) {
		grid.x = x;
		grid.y = y;
	}

	public void setGrid(int xy) {
		setGrid(xy, xy);
	}

	public void setGrid(Point grid) {
		setGrid(grid.x, grid.y);
	}

	/**
	 * Gets the bounding rectangle (location-relative) of a specified grid sector.
	 */
	public Rectangle getSectorBounds(int sx, int sy) {
		return new Rectangle(grid.x * sx, grid.y * sy, grid.x, grid.y);
	}

	/**
	 * Gets the center point (location-relative) of a specified grid sector.
	 */
	public Point getSectorCenter(int sx, int sy) {
		Rectangle bounds = getSectorBounds(sx, sy);
		return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	/**
	 * Get the sector in which a point lies.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return a Point representing the sector in which the point x,y lies.
	 */
	public Point getSector(int x, int y) {
		return new Point(x / grid.x, y / grid.y);
	}

	/**
	 * Click specified mouse button(s) in the center of a {@link FigureCanvas}' grid sector in
	 * which the specified coordinates lie.
	 * 
	 * @param canvas
	 *            the {@link FigureCanvas}
	 * @param x
	 *            the x coordinate within the canvas
	 * @param y
	 *            the y coordinate within the canvas
	 * @param buttons
	 *            the buttons to press & release. See
	 *            {@link WidgetTester#actionClick(org.eclipse.swt.widgets.Widget, int, int, String)}.
	 */
	public void actionClickSnapped(FigureCanvas canvas, int x, int y, String buttons) {
		Point sector = getSector(x, y);
		actionClickSector(canvas, sector.x, sector.y, buttons);
	}

	/**
	 * Click mouse button 1 in the center of a {@link FigureCanvas}' grid sector in which the
	 * specified coordinates lie.
	 * 
	 * @param canvas
	 *            the {@link FigureCanvas}
	 * @param x
	 *            the x coordinate within the canvas
	 * @param y
	 *            the y coordinate within the canvas
	 */
	public void actionClickSnapped(FigureCanvas canvas, int x, int y) {
		actionClickSnapped(canvas, x, y, "BUTTON1");
	}

	public void actionClickSector(FigureCanvas canvas, int sx, int sy, String buttons) {
		Point center = getSectorCenter(sx, sy);
		actionClick(canvas, center.x, center.y, buttons);
	}

	public void actionClickSector(FigureCanvas canvas, int sx, int sy) {
		Point center = getSectorCenter(sx, sy);
		actionClick(canvas, center.x, center.y, SWT.BUTTON1);
	}

	public synchronized Point getGridSize(FigureCanvas canvas) {
		Point size = getSize(canvas);
		return new Point(((size.x - 1) / grid.x) + 1, ((size.y - 1) / grid.y) + 1);
	}

	public EditPartViewer findViewer(FigureCanvas canvas) {
		IFigure rootFigure = canvas.getContents();
		if (rootFigure != null)
			return FigureTester.getFigureTester().findViewer(rootFigure);
		return null;
	}

	/**
	 * Forces a complete redraw and update/paint of the specified {@link FigureCanvas} (including
	 * its children).
	 * 
	 * @deprecated This method probably shouldn't exist (not here, at least) because it is really
	 *             just wrapping an SWT API call that manipulates the FigureCanvas in a way that the
	 *             user could not do via the UI itself. That is, it's not an action nor a state
	 *             inspector, so it probably is not appropriate for being a public *Tester method.
	 * @param canvas
	 *            the {@link FigureCanvas} to redraw
	 */
	public void redraw(final FigureCanvas canvas) {
		syncExec(new Runnable() {
			public void run() {
				Point size = canvas.getSize();
				canvas.redraw(0, 0, size.x, size.y, true);
				canvas.update();
			}
		});
	}
}
