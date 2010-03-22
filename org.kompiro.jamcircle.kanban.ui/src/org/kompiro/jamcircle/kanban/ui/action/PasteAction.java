package org.kompiro.jamcircle.kanban.ui.action;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.kompiro.jamcircle.kanban.ui.internal.editpart.CardEditPart;

public class PasteAction extends
		WorkbenchPartAction {
	private static final Conditional cardCond = new Conditional(){
		public boolean evaluate(EditPart editpart) {
			boolean isCardEditPart = editpart instanceof CardEditPart;
			return ! isCardEditPart;
		}
	};

	public PasteAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		setText("Paste");
		setToolTipText("Paste");
		setId(ActionFactory.PASTE.getId());
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	private List<?> getContentsFromClipboard() {
		return (List<?>) Clipboard.getDefault().getContents();
	}

	@Override
	public void run() {
		GraphicalViewer viewer = getCurrentViewer();
		if(viewer == null) return;
		if(getContentsFromClipboard() == null) return;
		Point currentMouseLocationOfControl = new Point(
			viewer.getControl().toControl(
				Display.getCurrent().getCursorLocation()));
		ChangeBoundsRequest request = createRequest(currentMouseLocationOfControl);

		EditPart target = viewer.findObjectAtExcluding(currentMouseLocationOfControl, Collections.EMPTY_SET,cardCond);
		Command command = target.getCommand(request);
		getCommandStack().execute(command);
	}

	private ChangeBoundsRequest createRequest(Point location) {
		ChangeBoundsRequest request = new ChangeBoundsRequest();
		Point viewLocation = getViewport().getViewLocation();
		request.setLocation(location.translate(viewLocation));
		request.setType(RequestConstants.REQ_CLONE);
		request.setEditParts(getContentsFromClipboard());
		return request;
	}

	private Viewport getViewport() {
		GraphicalEditPart rootEditPart = (GraphicalEditPart) getCurrentViewer()
				.getRootEditPart();
		Viewport port = (Viewport) rootEditPart.getFigure();
		return port;
	}

	private GraphicalViewer getCurrentViewer() {
		return  (GraphicalViewer) getWorkbenchPart().getAdapter(GraphicalViewer.class);
	}

}
