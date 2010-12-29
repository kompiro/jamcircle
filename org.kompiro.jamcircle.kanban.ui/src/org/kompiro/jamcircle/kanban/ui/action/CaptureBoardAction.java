package org.kompiro.jamcircle.kanban.ui.action;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.kompiro.jamcircle.kanban.ui.KanbanImageConstants;
import org.kompiro.jamcircle.kanban.ui.Messages;

// TODO separate class
public class CaptureBoardAction extends Action {

	private static final String MENU_CLOSE = Messages.CaptureBoardAction_menu_close;
	private static final String TEXT_SAVE = Messages.CaptureBoardAction_save_action_title;
	private static final String MENU_FILE = Messages.CaptureBoardAction_menu_file;

	private static final String TEXT_CAPTURE_BOARD = Messages.CaptureBoardAction_capture_action_title;

	private class SaveImageAction extends Action {
		private static final String MATCHE_PATTERN_JPG_ = ".*\\.jpg$"; //$NON-NLS-1$
		private static final String EXTENSION_JPG = "*.jpg"; //$NON-NLS-1$
		private static final String EXTENSION_PNG = "*.png"; //$NON-NLS-1$
		private Shell parentShell;

		public SaveImageAction(Shell parentShell) {
			super(TEXT_SAVE);
			setImageDescriptor(KanbanImageConstants.SAVE_IMAGE.getImageDescriptor());
			this.parentShell = parentShell;
		}

		@Override
		public void run() {
			FileDialog dialog = new FileDialog(parentShell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] { EXTENSION_PNG, EXTENSION_JPG });
			dialog.setFilterNames(new String[] { EXTENSION_PNG, EXTENSION_JPG });
			String path = dialog.open();
			if (path == null)
				return;
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { shown.getImageData() };
			if (path.matches(MATCHE_PATTERN_JPG_)) {
				loader.save(path, SWT.IMAGE_JPEG);
			} else {
				loader.save(path, SWT.IMAGE_PNG);
			}
		}
	}

	private class CapturedWindow extends ApplicationWindow {
		public CapturedWindow(Shell parentShell) {
			super(parentShell);
			addMenuBar();
			addToolBar(SWT.FLAT);
		}

		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText(Messages.CaptureBoardAction_capture_action_title);
			shell.setImage(KanbanImageConstants.CAMERA_IMAGE.getIamge());
		}

		@Override
		protected Control createContents(Composite parent) {
			Composite comp = new Composite(parent, SWT.None);
			comp.setLayout(new FillLayout());

			IFigure fig = canvas.getContents();
			Rectangle clientArea = fig.getClientArea();
			shown = new Image(canvas.getDisplay(), clientArea.width,
					clientArea.height);
			GC gc = new GC(shown);
			SWTGraphics graphics = new SWTGraphics(gc);
			fig.paint(graphics);

			FigureCanvas fCanvas = new FigureCanvas(comp);
			fCanvas.setContents(new ImageFigure(shown));
			return comp;
		}
	}

	private Image shown;

	private FigureCanvas canvas;
	private IWorkbenchPart part;

	public CaptureBoardAction(IWorkbenchPart part) {
		super(TEXT_CAPTURE_BOARD);
		setImageDescriptor(KanbanImageConstants.CAMERA_IMAGE.getImageDescriptor());
		this.part = part;
	}

	public void run() {
		GraphicalViewerImpl viewer = (GraphicalViewerImpl) part.getAdapter(GraphicalViewer.class);
		canvas = (FigureCanvas) viewer.getControl();
		IWorkbenchWindow workbenchWindow = part.getSite().getWorkbenchWindow();
		final ApplicationWindow window = new CapturedWindow(workbenchWindow.getShell());
		window.setBlockOnOpen(true);
		MenuManager menuBarManager = window.getMenuBarManager();
		SaveImageAction saveAction = new SaveImageAction(workbenchWindow.getShell());
		MenuManager fileMenu = new MenuManager(MENU_FILE);
		menuBarManager.add(fileMenu);
		fileMenu.add(saveAction);
		fileMenu.add(new Action(MENU_CLOSE) {
			@Override
			public void run() {
				window.close();
			}
		});
		window.getToolBarManager().add(saveAction);
		window.open();
		shown.dispose();
	}

}
