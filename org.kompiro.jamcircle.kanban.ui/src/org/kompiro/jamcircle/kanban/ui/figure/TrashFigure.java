package org.kompiro.jamcircle.kanban.ui.figure;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.model.TrashModel;

public class TrashFigure extends Figure {

	private ImageFigure figure;
	private TrashModel trash;
	
	public TrashFigure(TrashModel trash){
		setLayoutManager(new XYLayout());
		this.trash = trash;

		figure = new ImageFigure();	
		figure.setSize(72, 72);
		setImage();
		setSize(74,74);
		add(figure,new Rectangle(1,1,72,72));
		setLocation(trash.getLocation());
	}

	public void setImage() {
		Image image;
		if(trash.isEmpty()){
			image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.TRASH_EMPTY_IMAGE.toString());			
		}else{
			image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.TRASH_FULL_IMAGE.toString());
		}
		figure.setImage(image);
		figure.repaint();
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
