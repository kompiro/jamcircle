package org.kompiro.jamcircle.xmpp.kanban.ui.internal.editpart;

import org.eclipse.gef.EditPart;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.ui.editpart.*;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;

public class XMPPEditPartFactory implements ExtendedEditPartFactory {

	public XMPPEditPartFactory() {
	}

	public SupportedClassPair[] supportedClasses() {
		return new SupportedClassPair[]{
				new SupportedClassPair(IBoardEditPart.class,UserModel.class),
		};
	}

	public EditPart createEditPart(EditPart context, Object model) {
		if (context instanceof IBoardEditPart) {
			IBoardEditPart part = (IBoardEditPart) context;
			return new UserEditPart(part.getBoardModel());
		}
		return null;
	}

}
