/*******************************************************************************
 * Copyright (c) 2004, 2009 MAKE Technologies Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MAKE Technologies Inc - initial API and implementation
 *     Mariot Chauvin <mariot.chauvin@obeo.fr> - refactoring
 *******************************************************************************/
package org.eclipse.swtbot.eclipse.gef.view.finder.widgets;


import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;

/**
 * represent a connection edit part of a graphical viewer.
 * 
 * @author David Green
 * 
 * @see SWTBotGefEditPart
 * @see SWTBotGefView
 */
public class SWTBotGefConnectionViewEditPart extends SWTBotGefViewEditPart {

    /**
     * Construct a new {@link SWTBotGefConnectionEditPart} instance.
     * @param graphicalEditor the graphical editor
     * @param part the {@link ConnectionEditPart} to wrap 
     */
	SWTBotGefConnectionViewEditPart(SWTBotGefView graphicalEditor, org.eclipse.gef.ConnectionEditPart part) {
		super(graphicalEditor, part);
	}

	/*
	 * {@inheritDoc}
	 * @see SWTBotGefEditPart#part()
	 */
	@Override
	public org.eclipse.gef.ConnectionEditPart part() {
		return (org.eclipse.gef.ConnectionEditPart) super.part();
	}

	/*
	 * {@inheritDoc}
	 *@see ConnectionEditPart#getSource()
	 */
	public SWTBotGefViewEditPart source() {
		return UIThreadRunnable.syncExec(new Result<SWTBotGefViewEditPart>() {
			public SWTBotGefViewEditPart run() {
				org.eclipse.gef.EditPart source = part().getSource();
				return graphicalEditorView.createEditPart(source);
			}
		});
	}
	
	/*
	 * {@inheritDoc}
	 *@see ConnectionEditPart#getTarget()
	 */
	public SWTBotGefViewEditPart target() {
		return UIThreadRunnable.syncExec(new Result<SWTBotGefViewEditPart>() {
			public SWTBotGefViewEditPart run() {
				org.eclipse.gef.EditPart target = part().getTarget();
				return graphicalEditorView.createEditPart(target);
			}
		});
	}
}
