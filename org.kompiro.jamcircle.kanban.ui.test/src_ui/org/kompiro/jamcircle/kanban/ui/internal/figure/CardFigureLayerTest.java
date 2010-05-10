package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.ui.KanbanJFaceResource;


public class CardFigureLayerTest {

	{
		Display.getDefault();
		KanbanJFaceResource.initialize();
	}

	@Test
	public void createFigure() throws Exception {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		LightweightSystem lws = new LightweightSystem(shell);
		CardFigureLayer figure = new CardFigureLayer();
		figure.setColorType(ColorTypes.BLUE);
		lws.setContents(figure);
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}

}
