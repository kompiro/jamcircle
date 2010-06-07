package org.kompiro.jamcircle.kanban.ui.internal.figure;

import static java.lang.String.format;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.kompiro.jamcircle.kanban.ui.Messages;

/**
 * Customized Icon for Lane Figure.
 * @author kompiro
 */
public class LaneCustomizedIconFigure extends Figure {
	
	private static final int ICON_DEFAULT_SIZE = 72;

	private static final String IMAGE = "image"; //$NON-NLS-1$

	private Label statusFigure;
	private ImageFigure imageFigure;

	public LaneCustomizedIconFigure(){
		createLayoutManager();

		setSize(ICON_DEFAULT_SIZE, ICON_DEFAULT_SIZE + 20);
		createImageFigure();
		createStatusFigure();
	}

	private void createStatusFigure() {
		GridData constraint;
		statusFigure = new Label();
		statusFigure.setFont(JFaceResources.getTextFont());
		statusFigure.setTextAlignment(PositionConstants.CENTER);
		constraint = new GridData();
		constraint.horizontalAlignment = SWT.CENTER;
		constraint.verticalAlignment = SWT.BOTTOM;
		add(statusFigure,constraint);
	}

	private void createImageFigure() {
		imageFigure = new ImageFigure();
		GridData constraint = new GridData(GridData.FILL_BOTH);
		add(imageFigure,constraint);
	}

	private void createLayoutManager() {
		GridLayout manager = new GridLayout(1,true);
		manager.marginHeight = 0;
		manager.verticalSpacing = 0;
		manager.marginWidth = 0;
		manager.horizontalSpacing = 0;
		setLayoutManager(manager);
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
		imageData = scaleTo200px(imageData);
		setSize(imageData.width,imageData.height + 20);
		Image newImage = new Image(Display.getDefault(),imageData);
		imageFigure.setImage(newImage);
		imageFigure.setSize(imageData.width,imageData.height);
		statusFigure.setSize(new Dimension(imageData.width,15));
	}
	
	private ImageData scaleTo200px(ImageData data) {
		int width = data.width;
		int height = data.height;
		if(width > 200 || height > 200){
			if(width > height){
				int calcHeight = (int)(height / (width / 200.0));
				data = data.scaledTo(200, calcHeight);
			}else{
				int calcWidth = (int)(width / (height / 200.0));
				data = data.scaledTo(calcWidth, 200);
			}
		}
		return data;
	}

	
	
}
