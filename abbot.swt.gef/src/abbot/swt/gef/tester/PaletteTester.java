package abbot.swt.gef.tester;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.widgets.Display;

import abbot.swt.script.Condition;

public class PaletteTester {

	/**
	 * Singleton instance.
	 */
	public static final PaletteTester Default = new PaletteTester();

	/**
	 * Enforce singleton.
	 */
	private PaletteTester() {}

	/* Actions. */

	/**
	 * Expands or collapses a drawer.
	 */
	public boolean actionExpand(PaletteViewer viewer, String drawerLabel, boolean expand) {
		return expand(getDrawerEditPartInternal(viewer, drawerLabel), expand);
	}

	/**
	 * Expands or collapses a drawer.
	 */
	public boolean actionExpand(GraphicalEditPart drawerEditPart, boolean expand) {
		return expand(drawerEditPart, expand);
	}

	/**
	 * Expands or collapses a drawer.
	 * 
	 * @return <code>true</code> if the expanded state was changed, <code>false</code>
	 *         otherwise.
	 */
	private boolean expand(GraphicalEditPart drawerEditPart, boolean expand) {

		// If the expanded state needs to change, do it and return true.
		// Otherwise, do nothing and return false.
		if (isExpanded(drawerEditPart) != expand) {

			// Bail if the drawer isn't visible.
			if (!isVisible(drawerEditPart))
				throw new IllegalStateException("drawer is not visible");

			// Click on the drawer to toggle its expanded state.
			getFigureTester(drawerEditPart).actionClick(drawerEditPart.getFigure());

			// Return that its expanded state was changed.
			return true;
		}

		// Return that its expanded state was unchanged.
		return false;
	}

	/**
	 * Makes a palette item visible.
	 */
	public GraphicalEditPart actionShowItem(PaletteViewer viewer, String drawerLabel,
			String itemLabel) {
		return showItem(viewer, drawerLabel, itemLabel);
	}

	private GraphicalEditPart showItem(PaletteViewer viewer, String drawerLabel, String itemLabel) {

		// Ensure that the drawer is expanded.
		GraphicalEditPart drawerEditPart = getDrawerEditPartInternal(viewer, drawerLabel);
		expand(drawerEditPart, true);

		// Find the item and ensure that it is visible.
		GraphicalEditPart itemEditPart = getItemEditPartInternal(drawerEditPart, itemLabel);

		// Scroll one way or the other until the item is visible.
		final FigureTester tester = getFigureTester(drawerEditPart);
		for (;;) {

			int scroll = getScroll(drawerEditPart, itemEditPart);
			if (scroll == 0)
				break;

			Rectangle drawerBounds = drawerEditPart.getFigure().getBounds();
			Rectangle itemRect = itemEditPart.getFigure().getBounds();
			if (scroll == 1) {
				// Scroll up.
				tester.actionClick(
						drawerEditPart.getFigure(),
						drawerBounds.x + itemRect.width / 2,
						HACK_OFFSET + 5);
			} else {
				// Scroll down.
				tester.actionClick(
						drawerEditPart.getFigure(),
						drawerBounds.x + itemRect.width / 2,
						drawerBounds.height - itemRect.height / 2);
			}

			waitScrollComplete(itemEditPart, itemRect);
		}

		return itemEditPart;
	}

	private GraphicalEditPart getItemEditPartInternal(GraphicalEditPart drawerEditPart,
			String itemLabel) {
		List<GraphicalEditPart> itemEditParts = drawerEditPart.getChildren();
		for (GraphicalEditPart itemEditPart : itemEditParts) {
			PaletteEntry entry = (PaletteEntry) itemEditPart.getModel();
			if (itemLabel.equals(entry.getLabel()))
				return itemEditPart;
		}
		throw new RuntimeException(itemLabel + " not found");
	}

	/**
	 * Clicks on a palette item.
	 */
	public GraphicalEditPart actionClickItem(PaletteViewer viewer, String drawerLabel,
			String itemLabel) {
		GraphicalEditPart editPart = showItem(viewer, drawerLabel, itemLabel);
		return clickItem(editPart);
	}

	private GraphicalEditPart clickItem(GraphicalEditPart itemEditPart) {
		if (!itemEditPart.isSelectable())
			throw new IllegalStateException(itemEditPart + " is not selectable");

		FigureTester tester = getFigureTester(itemEditPart);
		tester.actionClick(itemEditPart.getFigure());
		return itemEditPart;
	}

	/**
	 * Drags & drops a palette item.
	 */
	public GraphicalEditPart actionDragDrop(PaletteViewer viewer, String drawerLabel,
			String itemLabel, int tx, int ty) {
		GraphicalEditPart editPart = showItem(viewer, drawerLabel, itemLabel);
		IFigure figure = editPart.getFigure();
		FigureTester tester = getFigureTester(viewer);
		tester.actionDragDrop(figure, tx, ty);
		return editPart;
	}

	/* Getters */

	public GraphicalEditPart getDrawerEditPart(PaletteViewer viewer, String drawerLabel) {
		return getDrawerEditPartInternal(viewer, drawerLabel);
	}

	private GraphicalEditPart getDrawerEditPartInternal(PaletteViewer viewer, String drawerLabel) {
		List<GraphicalEditPart> editParts = viewer.getContents().getChildren();
		for (GraphicalEditPart editPart : editParts) {
			if (editPart.getModel() instanceof PaletteDrawer) {
				String label = ((PaletteDrawer) editPart.getModel()).getLabel();
				if (drawerLabel.equals(label))
					return editPart;
			}
		}
		throw new RuntimeException(drawerLabel + " not found");
	}

//	private boolean isVisible(PaletteViewer viewer) {
//		Control control = viewer.getControl();
//		if (control != null)
//			return ControlTester.getControlTester().isVisible(control);
//		return false;
//	}

	private boolean isVisible(GraphicalEditPart editPart) {
		PaletteEntry model = (PaletteEntry) editPart.getModel();
		return model.isVisible();
	}

	private boolean isExpanded(GraphicalEditPart drawerEditPart) {
		return ((DrawerEditPart) drawerEditPart).isExpanded();
	}

	private FigureTester getFigureTester(GraphicalEditPart editPart) {
		return getFigureTester(editPart.getViewer());
	}

	private FigureTester getFigureTester(EditPartViewer viewer) {
		Display display = viewer.getControl().getDisplay();
		return new FigureTester(display);
	}

	private static final int HACK_OFFSET = 20;

	/**
	 * Finds out if an Palette Item is currently visible
	 * 
	 * @param drawerEditPart
	 * @param itemEditPart
	 * @return 1, -1 or 0 depending on whether we need to click up, down or not at all.
	 */
	private int getScroll(GraphicalEditPart drawerEditPart, GraphicalEditPart itemEditPart) {
		Rectangle drawerBounds = drawerEditPart.getFigure().getBounds();
		Rectangle itemBounds = itemEditPart.getFigure().getBounds();
		if (drawerBounds.y > (itemBounds.y + HACK_OFFSET))
			return 1;
		if (drawerBounds.y + drawerBounds.height < itemBounds.y)
			return -1;
		return 0;
	}

	private void waitScrollComplete(final GraphicalEditPart editPart, final Rectangle oldBounds) {
		getFigureTester(editPart.getViewer()).getRobot().wait(new Condition() {
			Rectangle prevBounds;;

			public boolean test() {
				Rectangle bounds = editPart.getFigure().getBounds();
				if (!bounds.equals(oldBounds) && prevBounds != null && bounds.equals(prevBounds))
					return true;
				prevBounds = bounds;
				return false;
			}
		}, 5000L, 250L);
	}

}
