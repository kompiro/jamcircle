package abbot.swt.gef.tester;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abbot.swt.finder.WidgetFinder;
import abbot.swt.finder.WidgetFinderImpl;
import abbot.swt.finder.WidgetHierarchyImpl;
import abbot.swt.finder.generic.MultipleFoundException;
import abbot.swt.finder.generic.NotFoundException;
import abbot.swt.finder.matchers.WidgetClassMatcher;
import abbot.swt.gef.util.GEFWorkbenchUtilities;
import abbot.swt.tester.AbstractTester;
import abbot.swt.tester.ActionFailedException;
import abbot.swt.tester.CanvasTester;
import abbot.swt.tester.ItemPath;
import abbot.swt.tester.MenuItemTester;
import abbot.swt.tester.MenuTester;
import abbot.swt.utilities.Displays;
import abbot.swt.utilities.Displays.Result;

/**
 * A tester for GEF {@link IFigure}s.
 */
public class FigureTester {

	public static FigureTester getFigureTester() {
		return new FigureTester(abbot.swt.tester.AbstractTester.getDefault());
	}

	/**
	 * A {@link AbstractTester} that does most of the heavy lifting for us.
	 */
	private final AbstractTester robot;

	/**
	 * Constructs a new {@link FigureTester} that will use the specified {@link AbstractTester}.
	 * 
	 * @param robot
	 *            the {@link AbstractTester} the new {@link FigureTester} will use
	 */
	public FigureTester(AbstractTester robot) {
		this.robot = robot;
	}

	/**
	 * Constructs a new {@link FigureTester} that will create and use a {@link AbstractTester} associated
	 * with the specified {@link Display}.
	 * 
	 * @param display
	 *            the {@link Display} with which the new {@link AbstractTester} for this {@link FigureTester}
	 *            will be associated
	 */
	public FigureTester(Display display) {
		this(new AbstractTester(new abbot.swt.Robot(display)));
	}

	/**
	 * Gets the receiver's {@link AbstractTester}.
	 * 
	 * @return the receiver's {@link AbstractTester}
	 */
	public AbstractTester getRobot() {
		return robot;
	}

	/**
	 * Get the {@link Display} that the receiver's {@link AbstractTester} is associated with.
	 * 
	 * @return the {@link Display} that the receiver's {@link AbstractTester} is associated with
	 */
	public Display getDisplay() {
		return robot.getDisplay();
	}

	/**
	 * @return a {@link Point} representing the current mouse cursor position in display coordinates
	 */
	public Point getCursorLocation() {
		return (Point) syncExec(new Result() {
			public Object result() {
				return robot.getDisplay().getCursorLocation();
			}
		});
	}

	/**
	 * Gets an {@link IFigure}'s top-level {@link Viewport}.
	 * 
	 * @param figure
	 *            the {@link IFigure}
	 * @return the {@link IFigure}'s top-level {@link Viewport} (or <code>null</code> if there
	 *         isn't one)
	 */
	public Viewport getViewport(IFigure figure) {
		checkFigure(figure);
		Viewport viewport = null;
		while (figure != null) {
			if (figure instanceof Viewport)
				viewport = (Viewport) figure;
			figure = figure.getParent();
		}
		return viewport;
	}

	/**
	 * Finds the {@link FigureCanvas} an {@link IFigure} is in.
	 * <p>
	 * <b>Note:</b> This is a relatively expensive operation because it involves searching the
	 * {@link Widget} hierarchy.
	 * 
	 * @param figure
	 *            the {@link IFigure}
	 * @return the {@link FigureCanvas} the figure is under, or <code>null</code> if there isn't
	 *         one
	 */
	public FigureCanvas findCanvas(IFigure figure) {
		checkFigure(figure);
		final IFigure root = getRoot(figure);
		try {
			WidgetFinder finder = new WidgetFinderImpl(new WidgetHierarchyImpl(getDisplay()));
			return (FigureCanvas) finder.find(new WidgetClassMatcher(FigureCanvas.class) {
				public boolean matches(Widget widget) {
					return super.matches(widget) && getRoot((FigureCanvas) widget) == root;
				}
			});
		} catch (NotFoundException e) {
			// Empty block intended. Fall through to return null.
		} catch (MultipleFoundException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * 
	 */
	private IFigure getRoot(FigureCanvas canvas) {
		return canvas.getLightweightSystem().getRootFigure();
	}

	/**
	 * Gets a figure's root figure.
	 * 
	 * @param figure
	 *            an {@link IFigure}
	 * @return the root {@link IFigure} of the figure's tree
	 */
	public IFigure getRoot(IFigure figure) {
		checkFigure(figure);
		IFigure parent = figure.getParent();
		if (parent == null)
			return figure;
		return getRoot(parent);
	}

	/**
	 * Gets the {@link GraphicalEditPart} that references an {@link IFigure}.
	 * 
	 * @param figure
	 *            an IFigure
	 * @return the {@link GraphicalEditPart} that references the <code>figure</code> or null
	 */
	public GraphicalEditPart findEditPart(IFigure figure) {
		checkFigure(figure);
		GraphicalViewer viewer = findViewer(figure);
		if (viewer != null)
			return (GraphicalEditPart) viewer.getVisualPartMap().get(figure);
		return null;
	}

	public GraphicalViewer findViewer(IFigure figure) {

		final IFigure root = getRoot(figure);

		// Viewers we've already checked.
		Set<GraphicalViewer> viewers = new HashSet();

		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {

				// Checking the Editors.
				for (IEditorReference editorReference : page.getEditorReferences()) {
					IEditorPart editor = editorReference.getEditor(false);
					GraphicalViewer viewer = getViewer(editor);
					if (viewer != null && viewers.add(viewer)) {

						// Checking the GraphicalViewer itself.
						if (getRoot(viewer) == root)
							return viewer;

						// Checking the GraphicalViewer's PaletteViewer.
						viewer = GEFWorkbenchUtilities.getPaletteViewer(viewer);
						if (viewer != null && viewers.add(viewer) && getRoot(viewer) == root)
							return viewer;
					}
				}

				// Checking the Views.
				for (IViewReference viewReference : page.getViewReferences()) {
					IViewPart view = viewReference.getView(false);
					GraphicalViewer viewer = getViewer(view);
					if (viewer != null && viewers.add(viewer) && getRoot(viewer) == root)
						return viewer;
				}
			}
		}

		return null;
	}

	private IFigure getRoot(GraphicalViewer viewer) {
		GraphicalEditPart rootEditPart = (GraphicalEditPart) viewer.getRootEditPart();
		return getRoot(rootEditPart.getFigure());
	}

	private GraphicalViewer getViewer(IWorkbenchPart part) {

		// Look for GraphicalViewer directly.
		GraphicalViewer viewer = (GraphicalViewer) part.getAdapter(GraphicalViewer.class);
		if (viewer != null)
			return viewer;

		// Look for GraphicalViewer indirectly through EditPart.
		EditPart editPart = (EditPart) part.getAdapter(EditPart.class);
		if (editPart != null) {
			EditPartViewer editPartViewer = editPart.getViewer();
			if (editPartViewer instanceof GraphicalViewer)
				return (GraphicalViewer) editPartViewer;
		}

		// No love.
		return null;

	}

	// private GraphicalEditor findEditor(IFigure figure) {
	// final Viewport viewport = getViewport(figure);
	// return (GraphicalEditor) GEFWorkbenchUtilities.findEditor(new EditorMatcher() {
	// public boolean matches(IEditorPart editor) {
	//
	// // It's a match iff it's a GraphicalEditor and its viewer's root edit part's figure
	// // is the one we're looking for.
	// if (editor instanceof GraphicalEditor) {
	// GraphicalViewer viewer = (GraphicalViewer) editor
	// .getAdapter(GraphicalViewer.class);
	// if (viewer != null) {
	// GraphicalEditPart rootEditPart = (GraphicalEditPart) viewer
	// .getRootEditPart();
	// if (rootEditPart.getFigure() == viewport)
	// return true;
	// }
	// }
	// return false;
	// }
	// }, false);
	// }

	// /**
	// * Get the {@link GraphicalEditPart} in an {@link EditPart} hierarchy that references a
	// * specified {@link IFigure}.
	// * <p>
	// * <b>Note:</b> Does not check arguments.
	// *
	// * @param editPart
	// * the root EditPart
	// * @param figure
	// * the IFigure
	// * @return the {@link GraphicalEditPart} in the hierarchy rooted at <code>editPart</code> that
	// * references <code>figure</code>.
	// */
	// private GraphicalEditPart findEditPart(EditPart editPart, IFigure figure) {
	// if (editPart instanceof GraphicalEditPart) {
	// GraphicalEditPart graphicalEditPart = (GraphicalEditPart) editPart;
	// if (graphicalEditPart.getFigure() == figure)
	// return graphicalEditPart;
	// }
	// for (Iterator iterator = editPart.getChildren().iterator(); iterator.hasNext();) {
	// EditPart childEditPart = (EditPart) iterator.next();
	// GraphicalEditPart foundEditPart = findEditPart(childEditPart, figure);
	// if (foundEditPart != null)
	// return foundEditPart;
	// }
	// return null;
	// }

	// /**
	// * Gets the root {@link GraphicalEditPart} that corresponds to the root of an {@link
	// IFigure}'s
	// * hierarchy.
	// * <p>
	// * <b>Note:</b> Does not check arguments.
	// *
	// * @param figure
	// * the IFigure
	// * @return the root {@link GraphicalEditPart} the corresponds to the root of the specified
	// * {@link IFigure}'s hierarchy (or <code>null</code> if there isn't one). In other
	// * words, the returned {@link GraphicalEditPart}'s figure will be the root figure in
	// * the {@link IFigure} hierarchy that contains the specified {@link IFigure}.
	// */
	// private GraphicalEditPart findRootEditPart(IFigure figure) {
	// Viewport figureRoot = getViewport(figure);
	// IWorkbench workbench = PlatformUI.getWorkbench();
	// IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
	// for (int i = 0; i < windows.length; i++) {
	// IWorkbenchWindow window = windows[i];
	// IWorkbenchPage[] pages = window.getPages();
	// for (int j = 0; j < pages.length; j++) {
	// IWorkbenchPage page = pages[j];
	// IEditorReference[] references = page.getEditorReferences();
	// for (int k = 0; k < references.length; k++) {
	// IEditorReference reference = references[k];
	// IEditorPart editor = reference.getEditor(false);
	// if (editor instanceof GraphicalEditor) {
	// GraphicalViewer viewer = (GraphicalViewer) editor
	// .getAdapter(GraphicalViewer.class);
	// if (viewer != null) {
	// GraphicalEditPart rootEditPart = (GraphicalEditPart) viewer
	// .getRootEditPart();
	// if (rootEditPart.getFigure() == figureRoot)
	// return (GraphicalEditPart) viewer.getVisualPartMap().get(figure);
	// }
	// }
	// }
	// }
	// }
	// return null;
	// }
	//
	// private GraphicalViewer findViewer(IFigure figure) {
	// GraphicalEditor editor = findEditor(figure);
	// if (editor != null)
	// return (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
	// return null;
	// }

	/**
	 * Gets an {@link IFigure}'s bounding rectangle
	 * 
	 * @param figure
	 *            the {@link IFigure}
	 * @return a {@link Rectangle} that describdes the {@link IFigure}'s location and size in
	 *         {@link Display} coordinates (or an empty {@link Rectangle} if there isn't one)
	 */
	public Rectangle getBounds(IFigure figure) {
		Rectangle relative = getBoundsRelative(figure);
		FigureCanvas canvas = findCanvas(figure);
		return toDisplay(canvas, relative);
	}

	/**
	 * Gets an {@link IFigure}'s bounding rectangle relative to its {@link FigureCanvas}.
	 * 
	 * @param figure
	 *            the {@link IFigure}
	 * @return a {@link Rectangle} that describdes the {@link IFigure}'s location and size in
	 *         {@link Display} coordinates (or an empty {@link Rectangle} if there isn't one)
	 */
	public Rectangle getBoundsRelative(IFigure figure) {
		return toAbsolute(figure, figure.getBounds());
	}

	/**
	 * Gets an {@link IFigure}'s client area in {@link Display} coordinates.
	 * 
	 * @param figure
	 *            an {@link IFigure}
	 * @return a {@link Rectangle} representing the {@link IFigure}'s client area in
	 *         {@link Display} coordinates
	 */
	public Rectangle getClientArea(IFigure figure) {
		Rectangle rectangle = toAbsolute(figure, figure.getClientArea());
		FigureCanvas canvas = findCanvas(figure);
		return toDisplay(canvas, rectangle);
	}

	/**
	 * Translate a {@link org.eclipse.draw2d.geometry.Rectangle} that is in the coordinate system of
	 * an {@link IFigure} into a {@link Rectangle} that is absolute (i.e., is in the coordinate
	 * system of the {@link IFigure}'s {@link FigureCanvas}.
	 * 
	 * @param figure
	 *            an IFigure
	 * @param rectangle
	 *            a {@link org.eclipse.draw2d.geometry.Rectangle} in the coordinate system of the
	 *            {@link IFigure}
	 * @return a {@link Rectangle} in the coordinate system of the {@link IFigure}'s
	 *         {@link FigureCanvas}
	 */
	public Rectangle toAbsolute(IFigure figure, org.eclipse.draw2d.geometry.Rectangle rectangle) {
		rectangle = rectangle.getCopy(); // Don't clobber the caller's...
		figure.translateToAbsolute(rectangle);
		return new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	/**
	 * Translate a {@link Rectangle} that is in the coordinate system of a {@link Control} (a
	 * {@link FigureCanvas}, for example) into a {@link Rectangle} in {@link Display} coordinates.
	 * 
	 * @param control
	 *            a {@link Control}
	 * @param rectangle
	 *            a {@link Rectangle} in the coordinate system of the {@link Control}
	 * @return a {@link Rectangle} in {@link Display} coordinates
	 */
	public Rectangle toDisplay(final Control control, final Rectangle rectangle) {
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return getDisplay().map(control, null, rectangle);
			}
		});
	}

	/**
	 * Gets the display's bounding rectangle
	 * 
	 * @return a {@link Rectangle} that describdes the {@link Display}'s location and size.
	 */
	protected Rectangle getDisplayBounds() {
		return (Rectangle) syncExec(new Result() {
			public Object result() {
				return getDisplay().getBounds();
			}
		});
	}

	/*
	 * Begin: Click support.
	 */

	/**
	 * Clicks mouse button 1 at the current cursor location.
	 */
	public void actionClick() {
		robot.click(SWT.BUTTON1);
	}

	/**
	 * Clicks one or more mouse buttons at the current cursor location.
	 */
	public void actionClick(int mask) {
		click(mask, 1);
	}

	/**
	 * Clicks mouse button 1 at a specified cursor location.
	 */
	public void actionClick(int x, int y) {
		click(null, x, y, SWT.BUTTON1, 1);
	}

	/**
	 * Clicks specified mouse buttons at a specified cursor location.
	 */
	public void actionClick(int x, int y, int mask) {
		click(null, x, y, mask, 1);
	}

	/**
	 * Clicks specified mouse buttons at a specified cursor location a specified number of times.
	 */
	public void actionClick(int x, int y, int mask, int count) {
		click(null, x, y, mask, count);
	}

	/**
	 * Clicks mouse button 1 on the center of an {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 */
	public void actionClick(IFigure figure) {
		checkFigure(figure);
		checkFigureShowing(figure);
		click(figure, -1, -1, SWT.BUTTON1, 1);
	}

	/**
	 * Clicks mouse button 1 at a particular point in an {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 * @param x
	 *            the x coordinate relative to the {@link IFigure}'s location
	 * @param y
	 *            the y coordinate relative to the {@link IFigure}'s location
	 */
	public void actionClick(IFigure figure, int x, int y) {
		checkFigure(figure);
		checkFigureShowing(figure);
		click(figure, x, y, SWT.BUTTON1, 1);
	}

	/**
	 * Clicks one or more mouse buttons at the center of an {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 * @param mask
	 *            the bitwise "or" of one or more of {@link SWT#BUTTON1}, {@link SWT#BUTTON2}, and
	 *            {@link SWT#BUTTON3}.
	 */
	public void actionClick(final IFigure figure, final int mask) {
		checkFigure(figure);
		checkFigureShowing(figure);
		click(figure, -1, -1, mask, 1);
	}

	/**
	 * Clicks one or more mouse buttons at a particular point in an {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 * @param x
	 *            the x coordinate relative to the {@link IFigure}'s location
	 * @param y
	 *            the y coordinate relative to the {@link IFigure}'s location
	 * @param mask
	 *            the bitwise "or" of one or more of {@link SWT#BUTTON1}, {@link SWT#BUTTON2}, and
	 *            {@link SWT#BUTTON3}.
	 */
	public void actionClick(IFigure figure, int x, int y, int mask) {
		checkFigure(figure);
		checkFigureShowing(figure);
		click(figure, x, y, mask, 1);
	}

	/**
	 * Clicks one or more mouse buttons a specified number of times at a particular point in an
	 * {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 * @param x
	 *            the x coordinate relative to the {@link IFigure}'s location
	 * @param y
	 *            the y coordinate relative to the {@link IFigure}'s location
	 * @param mask
	 *            the bitwise "or" of one or more of {@link SWT#BUTTON1}, {@link SWT#BUTTON2}, and
	 *            {@link SWT#BUTTON3}.
	 * @param count
	 *            the number of times to click the buttons
	 */
	public void actionClick(IFigure figure, int x, int y, int mask, int count) {
		checkFigure(figure);
		checkFigureShowing(figure);
		click(figure, x, y, mask, count);
	}

	/**
	 * Clicks one or more mouse buttons a specified number of times at a particular point in an
	 * {@link IFigure}.
	 * <p>
	 * <b>Note:</b> Does not check arguments.
	 * 
	 * @param figure
	 *            the IFigure (or null to indicate the entire display)
	 * @param x
	 *            the x coordinate relative to the {@link IFigure}'s location or display origin
	 * @param y
	 *            the y coordinate relative to the {@link IFigure}'s location or display origin
	 * @param mask
	 *            the bitwise "or" of one or more of {@link SWT#BUTTON1}, {@link SWT#BUTTON2}, and
	 *            {@link SWT#BUTTON3}.
	 * @param count
	 *            the number of times to click the buttons
	 */
	private void click(IFigure figure, int x, int y, final int mask, final int count) {

		final Rectangle bounds = figure == null ? getDisplayBounds() : getBounds(figure);
		if (x == -1 && y == -1) {
			x = bounds.width / 2;
			y = bounds.height / 2;
		}
		final int bx = x;
		final int by = y;
//		autoWaitForIdleDuring(new Runnable() {
//			public void run() {
				robot.click(bounds, bx, by, mask, count);
//			}
//		});
	}

	/**
	 * Clicks one or more mouse buttons a specified number of times at the current cursor location.
	 * <p>
	 * <b>Note:</b> Does not check arguments.
	 * 
	 * @param mask
	 *            the bitwise "or" of one or more of {@link SWT#BUTTON_MASK} (i.e.,
	 *            {@link SWT#BUTTON1}, {@link SWT#BUTTON2}, etc.).
	 * @param count
	 *            the number of times to click the button(s)
	 */
	private void click(final int mask, final int count) {
		autoWaitForIdleDuring(new Runnable() {
			public void run() {
				robot.click(mask, count);
			}
		});
	}

	/*
	 * End: Click support.
	 */

	/*
	 * Begin: Mouse move support.
	 */

	/**
	 * Moves the mouse pointer to a specified point on the {@link Display}.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public void actionMouseMove(int x, int y) {
		mouseMove(x, y);
	}

	/**
	 * Moves the mouse pointer to the center of an {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 */
	public void actionMouseMove(IFigure figure) {
		checkFigure(figure);
		checkFigureShowing(figure);
		mouseMove(figure, -1, -1);
	}

	/**
	 * Moves the mouse pointer to a specified point in an {@link IFigure}.
	 * 
	 * @param figure
	 *            the IFigure
	 * @param x
	 *            the x coordinate relative to the {@link IFigure}'s location
	 * @param y
	 *            the y coordinate relative to the {@link IFigure}'s location
	 */
	public void actionMouseMove(IFigure figure, int x, int y) {
		checkFigure(figure);
		checkFigureShowing(figure);
		mouseMove(figure, x, y);
	}

	/**
	 * Moves the mouse pointer to a specified point in an {@link IFigure}.
	 * <p>
	 * <b>Note:</b> Does not check arguments.
	 * 
	 * @param figure
	 *            the IFigure
	 * @param x
	 *            the x coordinate relative to the {@link IFigure}'s location
	 * @param y
	 *            the y coordinate relative to the {@link IFigure}'s location
	 */
	private void mouseMove(IFigure figure, int x, int y) {
		final Rectangle bounds = getBounds(figure);
		if (x == -1 && y == -1) {
			x = bounds.width / 2;
			y = bounds.height / 2;
		}
		final int bx = x;
		final int by = y;
		autoWaitForIdleDuring(new Runnable() {
			public void run() {
				robot.mouseMove(bounds, bx, by);
			}
		});
	}

	/**
	 * Moves the mouse pointer to a specified point on the display.
	 * <p>
	 * <b>Note:</b> Does not check arguments.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	private void mouseMove(final int x, final int y) {
		autoWaitForIdleDuring(new Runnable() {
			public void run() {
				robot.mouseMove(x, y);
			}
		});
	}

	/*
	 * End: Mouse move support.
	 */

	/*
	 * Begin: Drag & drop support.
	 */

	public void actionDragDrop(IFigure source, IFigure target) {
		actionDragDrop(source, SWT.BUTTON1, target);
	}

	public void actionDragDrop(IFigure source, int mask, IFigure target) {
		checkFigure(source);
		checkFigureShowing(source);
		checkFigure(target);
		checkFigureShowing(target);
		dragDrop(source, -1, -1, mask, target, -1, -1);
	}

	public void actionDragDrop(IFigure source, IFigure target, int tx, int ty) {
		actionDragDrop(source, SWT.BUTTON1, target, tx, ty);
	}

	public void actionDragDrop(IFigure source, int mask, IFigure target, int tx, int ty) {
		checkFigure(source);
		checkFigureShowing(source);
		checkFigure(target);
		checkFigureShowing(target);
		dragDrop(source, -1, -1, mask, target, tx, ty);
	}

	public void actionDragDrop(IFigure source, int tx, int ty) {
		actionDragDrop(source, SWT.BUTTON1, tx, ty);
	}

	public void actionDragDrop(IFigure source, int mask, int tx, int ty) {
		checkFigure(source);
		checkFigureShowing(source);
		dragDrop(source, -1, -1, mask, tx, ty);
	}

	public void actionDragDrop(IFigure source, int sx, int sy, IFigure target) {
		actionDragDrop(source, sx, sy, SWT.BUTTON1, target);
	}

	public void actionDragDrop(IFigure source, int sx, int sy, int mask, IFigure target) {
		checkFigure(source);
		checkFigureShowing(source);
		checkFigure(target);
		checkFigureShowing(target);
		dragDrop(source, sx, sy, mask, target, -1, -1);
	}

	public void actionDragDrop(IFigure source, int sx, int sy, IFigure target, int tx, int ty) {
		actionDragDrop(source, sx, sy, SWT.BUTTON1, target, tx, ty);
	}

	public void actionDragDrop(IFigure source, int sx, int sy, int mask, IFigure target, int tx,
			int ty) {
		checkFigure(source);
		checkFigureShowing(source);
		checkFigure(target);
		checkFigureShowing(target);
		dragDrop(source, sx, sy, mask, target, tx, ty);
	}

	public void actionDragDrop(IFigure source, int sx, int sy, int tx, int ty) {
		actionDragDrop(source, sx, sy, SWT.BUTTON1, tx, ty);
	}

	public void actionDragDrop(IFigure source, int sx, int sy, int mask, int tx, int ty) {
		checkFigure(source);
		checkFigureShowing(source);
		dragDrop(source, sx, sy, mask, tx, ty);
	}

	/**
	 * <p>
	 * <b>Note:</b> Does not check arguments.
	 * 
	 * @param sourceFigure
	 * @param sx
	 * @param sy
	 * @param mask
	 * @param targetFigure
	 * @param tx
	 * @param ty
	 */
	private void dragDrop(IFigure sourceFigure, int sx, int sy, int mask, IFigure targetFigure,
			int tx, int ty) {

		FigureCanvas canvas = findCanvas(sourceFigure);
		Rectangle source = toDisplay(canvas, toAbsolute(sourceFigure, sourceFigure.getBounds()));
		if (sx == -1 && sy == -1) {
			sx = source.width / 2;
			sy = source.height / 2;
		}

		Rectangle target = toDisplay(canvas, toAbsolute(targetFigure, targetFigure.getBounds()));
		if (tx == -1 && ty == -1) {
			tx = target.width / 2;
			ty = target.height / 2;
		}

		robot.dragDrop(source.x + sx, source.y + sy, target.x + tx, target.y + ty, mask);
	}

	/**
	 * Drag from a location within a source rectangle and drop at specified display coordinates.
	 * <p>
	 * <b>Note:</b> Does not check arguments.
	 * 
	 * @param sourceFigure
	 *            the bounding {@link Rectangle} of the drag source
	 * @param sx
	 *            the x coordinate within <code>source</code>.
	 * @param sy
	 *            the y coordinate within <code>source</code>.
	 * @param mask
	 *            zero or more accelerator keys (e.g., {@link SWT#SHIFT})
	 * @param tx
	 *            the x coordinate of the drop target location in display coordinates
	 * @param ty
	 *            the y coordinate of the drop target location in display coordinates
	 */
	private void dragDrop(IFigure sourceFigure, int sx, int sy, int mask, int tx, int ty) {

		Rectangle source = getBounds(sourceFigure);
		if (sx == -1 && sy == -1) {
			sx = source.width / 2;
			sy = source.height / 2;
		}

		robot.dragDrop(source.x + sx, source.y + sy, tx, ty, mask);
	}

	/*
	 * End: Drag & drop support.
	 */

	/*
	 * Begin: Keystroke support.
	 */

	/**
	 * Type (press and release) all of the keys contained in an accelerator.
	 * 
	 * @param accelerator
	 *            the accelerator
	 * @see AbstractTester#key(int)
	 */
	public void actionKey(int accelerator) {
		robot.key(accelerator);
	}

	/**
	 * Type (press and release) a character.
	 * 
	 * @param c
	 *            the character
	 * @see AbstractTester#key(int)
	 */
	public void actionKey(char c) {
		robot.key((int) c);
	}

	/**
	 * Type (press and release) each character in a {@link CharSequence} (e.g., a {@link String}, a
	 * {@link StringBuffer}, etc.).
	 * 
	 * @param characters
	 *            the {@link CharSequence}
	 * @see AbstractTester#key(int)
	 */
	public void actionKey(CharSequence characters) {
		if (characters == null)
			throw new IllegalArgumentException("characters is null");
		for (int i = 0; i < characters.length(); i++) {
			robot.key((int) characters.charAt(i));
		}
	}

	/*
	 * End: Keystroke support.
	 */

	/* Actions for invoking pop-up menus. */

	/**
	 * Clicks a menu item in an {@link IFigure}'s context (pop-up) menu.
	 */
	public void actionClickMenuItem(IFigure figure, ItemPath menuPath) {
		checkFigure(figure);
		clickMenuItem(figure, menuPath);
	}

	/**
	 * Clicks a menu item in a {@link Widget}'s context (pop-up) menu.
	 */
	public void actionClickMenuItem(IFigure figure, String menuPath) {
		actionClickMenuItem(figure, new ItemPath(menuPath));
	}

	/**
	 * Clicks a menu item in a {@link Widget}'s context (pop-up) menu.
	 */
	public void actionClickMenuItem(IFigure figure, String menuPath, String delimiter) {
		actionClickMenuItem(figure, new ItemPath(menuPath, delimiter));
	}

	void clickMenuItem(IFigure figure, ItemPath menuPath) {
		CanvasTester canvasTester = new CanvasTester(robot.getRobot());
		MenuTester menuTester = new MenuTester(robot.getRobot());

		// Bring up the figure's pop-up menu.
		actionClick(figure, AbstractTester.BUTTON_POPUP);
		FigureCanvas canvas = findCanvas(figure);
		Menu menu = canvasTester.getMenu(canvas);
		Assert.assertNotNull(menu);
		menuTester.waitVisible(menu);

		// Click the item specified by the path.
		menuTester.actionClickItem(menu, menuPath);
	}

	public void actionClickMenuItem(ItemPath path) {
		clickMenuItem(path);
	}

	public void actionClickMenuItem(String path) {
		actionClickMenuItem(new ItemPath(path));
	}

	public void actionClickMenuItem(String path, String delimiter) {
		actionClickMenuItem(new ItemPath(path, delimiter));
	}

	void clickMenuItem(ItemPath path) {

		// Get the FigureCanvas we're over (making sure we have one).
		Control control = (Control) syncExec(new Result() {
			public Object result() {
				return getDisplay().getCursorControl();
			}
		});
		if (control == null || !(control instanceof FigureCanvas))
			throw new ActionFailedException("cursor is not over a FigureCanvas");

		// Get its menu.
		FigureCanvasTester canvasTester = FigureCanvasTester.getFigureCanvasTester();
		Menu menu = canvasTester.getMenu(control);
		if (menu == null)
			throw new ActionFailedException("no menu");

		// Bring up the pop-up menu.
		actionClick(AbstractTester.BUTTON_POPUP);
		MenuTester menuTester = MenuTester.getMenuTester();
		menuTester.waitVisible(menu);

		// Click the item specified by the path.
		menuTester.actionClickItem(menu, path);

	}

	/**
	 * Click a context menu item on an {@link IFigure}.
	 * <p>
	 * <b>Note:</b> Only top-level items are currently supported.
	 * 
	 * @deprecated Use {@link #actionClickMenuItem(IFigure, String)}.
	 */
	public void actionSelectPopupMenuItem(IFigure figure, final String text) {
		actionClickMenuItem(figure, text);
	}

	/**
	 * Selects a {@link MenuItem} from the context menu at the current cursor position.
	 * 
	 * @deprecated Use {@link #actionClickMenuItem(IFigure, ItemPath)}.
	 * @param item
	 *            the MenuItem to select
	 */
	public void actionSelectPopupMenuItem(MenuItem item) {
		MenuItemTester menuItemTester = MenuItemTester.getMenuItemTester();
		ItemPath path = menuItemTester.getPath(item);
		clickMenuItem(path);
	}

	/*
	 * End of actionf for invoking pop-up menus.
	 */

	/*
	 * Begin: Argument checking utilities.
	 */

	/**
	 * @param figure
	 *            the {@link IFigure}
	 * @throws IllegalArgumentException
	 *             if figure is null
	 */
	protected void checkFigure(IFigure figure) {
		if (figure == null)
			throw new IllegalArgumentException("figure is null");
	}

	/**
	 * @param figure
	 *            the {@link IFigure}
	 * @throws IllegalArgumentException
	 *             if figure is not showing
	 * @see IFigure#isShowing()
	 */
	protected void checkFigureShowing(IFigure figure) {
		if (!figure.isShowing())
			throw new IllegalArgumentException("figure not showing");
	}

	/*
	 * End: Argument checking utilities.
	 */

	/**
	 * Runs a {@link Runnable} while the receiver's {@link AbstractTester} is set to automatically wait after
	 * generating an event for the input event queue to be empty.
	 * 
	 * @param runnable
	 *            the Runnable to {@link Runnable}
	 * @see AbstractTester#setAutoWaitForIdle(boolean)
	 */
	protected void autoWaitForIdleDuring(Runnable runnable) {
		if (robot.setAutoWaitForIdle(true)) {
			runnable.run();
		} else {
			try {
				runnable.run();
			} finally {
				robot.setAutoWaitForIdle(false);
			}
		}
	}

	/**
	 * Runs a {@link Runnable} on the receiver's {@link Display} thread.
	 * 
	 * @param runnable
	 *            the {@link Runnable} to run
	 */
	protected void syncExec(Runnable runnable) {
		Displays.syncExec(getDisplay(), runnable);
	}

	/**
	 * Returns a result returned from running a {@link Result} on the receiver's {@link Display}
	 * thread.
	 * 
	 * @param result
	 *            the {@link Result} to run
	 * @return the {@link Object} returned by the {@link Result}
	 */
	protected Object syncExec(Result result) {
		return Displays.syncExec(getDisplay(), result);
	}

}
