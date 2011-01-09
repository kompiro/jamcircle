package org.kompiro.jamcircle.web.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;

final public class VCardAccept extends VAcceptCriterion {

	@Override
	protected boolean accept(VDragEvent drag, UIDL configuration) {
		return true;
	}

}
