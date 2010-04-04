package org.kompiro.jamcircle.xmpp.kanban.ui.internal.figure;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.figure.BoldLabel;
import org.kompiro.jamcircle.xmpp.kanban.ui.Messages;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;

public class UserFigure extends Figure {

	private static final String EMPTY = ""; //$NON-NLS-1$

	private  class UserTooltipFigure extends Figure {
		private UserTooltipFigure(){
			setLayoutManager(new GridLayout(2,false));
			add(new BoldLabel(Messages.UserFigure_name_label));
			add(new Label(model.getName()));
			add(new BoldLabel(Messages.UserFigure_user_id_label));
			add(new Label(model.getUserId()));
			add(new BoldLabel(Messages.UserFigure_from_label));
			add(new Label(model.getPresenceFrom()));
		}
	}


	private ImageFigure figure;
	
	private UserModel model;

	private ImageRegistry imageRegistry;

	public UserFigure(UserModel model, ImageRegistry imageRegistry){
		this.model = model;
		this.imageRegistry = imageRegistry;
		setLayoutManager(new XYLayout());

		figure = new ImageFigure();		
		figure.setSize(72, 72);
		checkUserStateAndSetImage();
		figure.setOpaque(false);
		setSize(74,74);
		add(figure,new Rectangle(1,1,72,72));
		Label nameLabel;
		String name = model.getName();
		if(name != null && !name.equals(EMPTY)){
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
			image = getImageRegistry().get(KanbanImageConstants.SEND_ON_IMAGE.toString());
		}else{
			image = getImageRegistry().get(KanbanImageConstants.SEND_OFF_IMAGE.toString());			
		}
		figure.setImage(image);
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
