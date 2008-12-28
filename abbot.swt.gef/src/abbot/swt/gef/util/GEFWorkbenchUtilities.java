package abbot.swt.gef.util;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;

public class GEFWorkbenchUtilities extends abbot.swt.eclipse.utils.WorkbenchUtilities {

	/*
	 * GraphicalViewer-oriented methods.
	 */

	public static RootEditPart getRootEditPart(GraphicalViewer viewer) {
		return viewer.getRootEditPart();
	}

	public static EditDomain getEditDomain(GraphicalViewer viewer) {
		return viewer.getEditDomain();
	}

	public static IFigure getRootFigure(GraphicalViewer viewer) {
		RootEditPart rootEditPart = viewer.getRootEditPart();
		if (rootEditPart != null)
			return ((GraphicalEditPart) rootEditPart).getFigure();
		return null;
	}

	public static PaletteViewer getPaletteViewer(GraphicalViewer viewer) {
		if (viewer instanceof PaletteViewer)
			return (PaletteViewer) viewer;
		EditDomain editDomain = viewer.getEditDomain();
		if (editDomain != null)
			return editDomain.getPaletteViewer();
		return null;
	}

	public static RootEditPart getPaletteRootEditPart(GraphicalViewer viewer) {
		PaletteViewer paletteViewer = getPaletteViewer(viewer);
		if (paletteViewer != null)
			return paletteViewer.getRootEditPart();
		return null;
	}

	public static IFigure getPaletteRootFigure(GraphicalViewer viewer) {
		RootEditPart rootEditPart = getPaletteRootEditPart(viewer);
		if (rootEditPart != null)
			return ((GraphicalEditPart) rootEditPart).getFigure();
		return null;
	}

	public static PaletteRoot getPaletteRoot(GraphicalViewer viewer) {
		PaletteViewer paletteViewer = getPaletteViewer(viewer);
		if (paletteViewer != null)
			return paletteViewer.getPaletteRoot();
		return null;
	}

	/*
	 * End of GraphicalViewer-oriented methods.
	 */

	/*
	 * GraphicalEditor-oriented methods.
	 */

	public static GraphicalViewer getViewer(GraphicalEditor editor) {
		return (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
	}

	public static RootEditPart getRootEditPart(GraphicalEditor editor) {
		GraphicalViewer viewer = getViewer(editor);
		if (viewer != null)
			return viewer.getRootEditPart();
		return null;
	}

	public static IFigure getRootFigure(GraphicalEditor editor) {
		RootEditPart rootEditPart = getRootEditPart(editor);
		if (rootEditPart != null)
			return ((GraphicalEditPart) rootEditPart).getFigure();
		return (IFigure) editor.getAdapter(IFigure.class);
	}

	public static PaletteViewer getPaletteViewer(GraphicalEditor editor) {
		GraphicalViewer viewer = getViewer(editor);
		if (viewer != null)
			return getPaletteViewer(viewer);
		return null;
	}

	public static RootEditPart getPaletteRootEditPart(GraphicalEditor editor) {
		GraphicalViewer viewer = getViewer(editor);
		if (viewer != null)
			return getPaletteRootEditPart(viewer);
		return null;
	}

	public static IFigure getPaletteRootFigure(GraphicalEditor editor) {
		GraphicalViewer viewer = getViewer(editor);
		if (viewer != null)
			return getPaletteRootFigure(viewer);
		return null;
	}

	public static EditDomain getEditDomain(GraphicalEditor editor) {
		GraphicalViewer viewer = getViewer(editor);
		if (viewer != null)
			return viewer.getEditDomain();
		return null;
	}

	public static PaletteRoot getPaletteRoot(GraphicalEditor editor) {
		PaletteViewer viewer = getPaletteViewer(editor);
		if (viewer != null)
			return viewer.getPaletteRoot();
		return null;
	}

	/*
	 * End of GraphicalEditor-oriented methods.
	 */

}
