package org.kompiro.jamcircle.rcp.internal.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.program.Program;
import org.kompiro.jamcircle.Messages;

public class SiteAction extends Action {
	private static final String SITE_URL = "http://kompiro.org/jamcircle/"; //$NON-NLS-1$

	public SiteAction() {
		super(Messages.SiteAction_text);
	}

	@Override
	public void run() {
		Program.launch(SITE_URL);
	}
}
