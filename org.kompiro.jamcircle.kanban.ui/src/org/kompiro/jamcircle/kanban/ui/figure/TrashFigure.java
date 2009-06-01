package org.kompiro.jamcircle.kanban.ui.figure;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public class TrashFigure extends Figure {

	private ImageFigure figure;
	private TrashModel trash;
	private ImageRegistry imageRegistry;
	
	public TrashFigure(TrashModel trash){
		this(trash,null);
	}
	
	public TrashFigure(TrashModel trash,ImageRegistry imageRegistry){
		this.trash = trash;
		this.imageRegistry = imageRegistry;
	}

	public void setImage() {
		Image image;
		if(trash.isEmpty()){
			image = getImageRegistry().get(KanbanImageConstants.TRASH_EMPTY_IMAGE.toString());			
		}else{
			image = getImageRegistry().get(KanbanImageConstants.TRASH_FULL_IMAGE.toString());
		}
		if(image == null) return;
		figure.setImage(image);
		figure.repaint();
	}
	
	private ImageRegistry getImageRegistry() {
		if(this.imageRegistry == null) this.imageRegistry = JFaceResources.getImageRegistry();
		return this.imageRegistry;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
