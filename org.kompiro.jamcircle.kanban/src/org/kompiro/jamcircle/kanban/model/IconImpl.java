package org.kompiro.jamcircle.kanban.model;

import static java.lang.String.format;

/**
 * This implementation describes Icon Implementation.
 */
public class IconImpl extends GraphicalImpl{

	private static String TO_STRING_FORMAT = "ICON['#%d':'%s' point:'%d,%d']"; //$NON-NLS-1$

	private Icon icon;

	public IconImpl(Icon icon){
		super(icon);
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		return format(TO_STRING_FORMAT,icon.getID(),icon.getClassType(),icon.getX(),icon.getY());
	}
		
}
