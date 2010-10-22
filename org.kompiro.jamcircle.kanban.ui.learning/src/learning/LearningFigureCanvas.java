package learning;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.snippets.Snippet237;
import org.eclipse.swt.widgets.*;

public class LearningFigureCanvas {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		Color color = display.getSystemColor(SWT.COLOR_CYAN);
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setLayout(new GridLayout());
		LightweightSystem lws = new LightweightSystem();
		org.eclipse.draw2d.Label figure = new org.eclipse.draw2d.Label();
		figure.setText("test");
		lws.getRootFigure().add(figure);
		FigureCanvas canvas = new FigureCanvas(SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_REDRAW_RESIZE, composite, lws);
		GridDataFactory.fillDefaults().hint(200, 200).applyTo(canvas);
		ImageLoader loader = new ImageLoader();
		ImageData[] load = loader.load(new Snippet237().getClass().getResourceAsStream("trac_bullet.png"));
		Image image = new Image(Display.getDefault(), load[0]);
		canvas.setBackgroundImage(image);
		canvas.setBackground(color);
		canvas.pack();

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}

}
