package org.kompiro.jamcircle.kanban.model;

import net.java.ao.schema.Default;
import net.java.ao.schema.Ignore;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;
/**
 * This interface describes implementation has color type.
 * @author kompiro
 */
public interface HasColorTypeEntity extends GraphicalEntity{
	
	public static String PROP_COLOR_TYPE = "colorType"; //$NON-NLS-1$
	
	public ColorTypes getColorType();
	
	@Default(value="2")
	public void setColorType(ColorTypes colorType);
	
	@Deprecated
	@Ignore
	/**
	 * @see #setColorType(ColorTypes)
	 */
	public void setColorType(int colorType);
}
