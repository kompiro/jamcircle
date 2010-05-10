package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;


public class LaneCustomizedIconFigureTest {
	
	@Test
	public void createFigure() throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		LightweightSystem lws = new LightweightSystem(shell);
		LaneCustomizedIconFigure figure = new LaneCustomizedIconFigure();
		figure.setStatus("test");
		ImageLoader loader = new ImageLoader();
		ImageData[] load = loader.load(this.getClass().getResourceAsStream("kanban_128.gif"));
		Image image = new Image(display,load[0]);
		figure.setImage(image);
		lws.setContents(figure);
		shell.pack();
		shell.open();

		load = loader.load(this.getClass().getResourceAsStream("trac_bullet.png"));
		image = new Image(display,load[0]);
		figure.setImage(image);
		
	}

}
