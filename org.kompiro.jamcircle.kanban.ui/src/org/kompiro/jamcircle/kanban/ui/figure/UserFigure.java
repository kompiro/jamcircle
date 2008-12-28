package org.kompiro.jamcircle.kanban.ui.figure;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.KanbanUIActivator;
import org.kompiro.jamcircle.kanban.ui.model.UserModel;

public class UserFigure extends Figure {

	private  class UserTooltipFigure extends Figure {
		private UserTooltipFigure(){
			setLayoutManager(new GridLayout(2,false));
			add(new BoldLabel("Name:"));
			add(new Label(model.getName()));
			add(new BoldLabel("User ID:"));
			add(new Label(model.getUserId()));
		}
	}


	private ImageFigure figure;
	
	private UserModel model;

	public UserFigure(UserModel model){
		this.model = model;
		setLayoutManager(new XYLayout());

		figure = new ImageFigure();		
		figure.setSize(72, 72);
		checkUserStateAndSetImage();
		figure.setOpaque(false);
		setSize(74,74);
		add(figure,new Rectangle(1,1,72,72));
		Label nameLabel;
		String name = model.getName();
		if(name != null && !name.equals("")){
			nameLabel = new Label(name);
		}else{
			nameLabel = new Label(model.getUserId());
		}
		nameLabel.setTextAlignment(PositionConstants.CENTER);
		add(nameLabel,new Rectangle(0,50,72,10));
		setToolTip(new UserTooltipFigure());
	}
	
	@Override
	public void repaint() {
		checkUserStateAndSetImage();
		super.repaint();
	}


	private void checkUserStateAndSetImage() {
		Image image;
		if(model.isAvailable()){
			image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.SEND_ON_IMAGE.toString());
		}else{
			image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.SEND_OFF_IMAGE.toString());			
		}
		figure.setImage(image);
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
