package org.kompiro.jamcircle.kanban.ui.internal.figure;

import static java.lang.String.format;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.ui.Messages;

/**
 * Customized Icon for Lane Figure.
 * @author kompiro
 */
public class LaneCustomizedIconFigure extends Figure {
	
	private static final String IMAGE = "image"; //$NON-NLS-1$

	private Label statusFigure;
	private ImageFigure imageFigure;

	public LaneCustomizedIconFigure(){
		setLayoutManager(new XYLayout());
		setSize(72, 92);
		imageFigure = new ImageFigure();
		add(imageFigure,new Rectangle(0, 0,72,72),0);

		statusFigure = new Label();
		statusFigure.setFont(JFaceResources.getTextFont());
		statusFigure.setTextAlignment(PositionConstants.CENTER);
		add(statusFigure,new Rectangle(0,77,72,15));
	}
	
	public void setStatus(String status){
		statusFigure.setText(status);
	}

	public void setImage(Image image) {
		if(image == null){ 
			throw new IllegalStateException(format(Messages.LaneCustomizedIconFigure_initialized_error_message,IMAGE));
		};
		Image oldImage = imageFigure.getImage();
		if(oldImage != null){
			oldImage.dispose();
		}
		ImageData imageData = image.getImageData();
		Image newImage = new Image(Display.getDefault(),imageData.scaledTo(72, 72));
		imageFigure.setImage(newImage);
	}
	
	
}
