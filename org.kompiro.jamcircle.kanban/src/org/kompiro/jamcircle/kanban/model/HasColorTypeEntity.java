package org.kompiro.jamcircle.kanban.model;

import org.kompiro.jamcircle.storage.model.GraphicalEntity;

import net.java.ao.schema.Default;
import net.java.ao.schema.Ignore;

public interface HasColorTypeEntity extends GraphicalEntity{
	
	public static String PROP_COLOR_TYPE = "colorType";
	
	public ColorTypes getColorType();
	
	@Default(value="2")
	public void setColorType(ColorTypes colorType);
	
	@Deprecated
	@Ignore
	public void setColorType(int colorType);
}
