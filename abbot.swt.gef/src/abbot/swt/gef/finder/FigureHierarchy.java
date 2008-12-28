package abbot.swt.gef.finder;

import org.eclipse.draw2d.IFigure;

import abbot.swt.hierarchy.Hierarchy;

/**
 * Provides access to all {@link IFigure}s in a GEF hierarchy.
 */
public interface FigureHierarchy extends Hierarchy<IFigure> {

	/**
	 * Get the root {@link IFigure} of this {@link FigureHierarchy}. This is just a convenience
	 * method provided because a {@link FigureHierarchy} always has exactly one root {@link IFigure}.
	 * 
	 * @return the root {@link IFigure} of the receiver
	 */
	IFigure getRoot();
}
