package org.kompiro.jamcircle.kanban.ui.internal.figure;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.ui.KanbanJFaceResource;


public class ActionAreaTest  {

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
		LaneFigure laneFigure = new LaneFigure();
		ActionArea<LaneFigure> figure = new ActionArea<LaneFigure>(laneFigure);
		figure.setSize(200,500);
		laneFigure.setStatus("test");
		lws.setContents(figure);
		shell.open();
	}

}
