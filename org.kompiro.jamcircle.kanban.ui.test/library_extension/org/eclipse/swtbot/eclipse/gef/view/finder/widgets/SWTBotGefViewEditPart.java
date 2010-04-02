/*******************************************************************************
 * Copyright (c) 2004, 2009 MAKE Technologies Inc and others
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.hamcrest.Matcher;

/**
 * represent an edit part of a graphical viewer.
 * 
 * @author David Green
 * 
 * @see SWTBotGefEditor
 */
public class SWTBotGefViewEditPart {
	protected final EditPart part;
	protected final SWTBotGefView graphicalEditorView;
	
	/**
	 * 
	 * @param graphicalEditor 
	 * @param parent the parent, or null if this is the root edit part
	 * @param part the GEF part
	 */
	SWTBotGefViewEditPart(final SWTBotGefView graphicalEditor, final EditPart part) {
		this.graphicalEditorView = graphicalEditor;
		this.part = part;
	}

	/**
	 * get the parent, or null if this is the root edit part.
	 */
	public SWTBotGefViewEditPart parent() {
		return UIThreadRunnable.syncExec(new Result<SWTBotGefViewEditPart>() {
			public SWTBotGefViewEditPart run() {
				return graphicalEditorView.createEditPart(part.getParent());
			}
		});
	}

	/**
	 * Get the children of this edit part.
	 * @return the edit part's children
	 */
	@SuppressWarnings("unchecked")
	public List<SWTBotGefViewEditPart> children() {
		return UIThreadRunnable.syncExec(new Result<List<SWTBotGefViewEditPart>>() {
			public List<SWTBotGefViewEditPart> run() {
				List<SWTBotGefViewEditPart> children = new ArrayList<SWTBotGefViewEditPart>();
				for (org.eclipse.gef.EditPart child: ((List<org.eclipse.gef.EditPart>)part.getChildren())) {
					children.add(graphicalEditorView.createEditPart(child));
				}
				return children;
			}
		});
	}

	/**
	 * find descendants that match.
	 * 
	 * @param matcher the matcher that matches against {@link org.eclipse.gef.EditPart}
	 * 
	 * @return a list of matches or an empty list if there are none
	 */
	@SuppressWarnings("unchecked")
	public List<SWTBotGefViewEditPart> descendants(final Matcher<? extends EditPart> matcher) {
		return  UIThreadRunnable.syncExec(new Result<List<SWTBotGefViewEditPart>>() {
			public List<SWTBotGefViewEditPart> run() {
				List<SWTBotGefViewEditPart> descendants = new ArrayList<SWTBotGefViewEditPart>();
				Stack<SWTBotGefViewEditPart> parts = new Stack<SWTBotGefViewEditPart>();
				parts.push(SWTBotGefViewEditPart.this);
				while (!parts.isEmpty()) {
					SWTBotGefViewEditPart part = parts.pop();
					for (org.eclipse.gef.EditPart child: ((List<org.eclipse.gef.EditPart>) part.part.getChildren())) {
						SWTBotGefViewEditPart childPart = graphicalEditorView.createEditPart(child);
						if (matcher.matches(child)) {
							descendants.add(childPart);
						}
						parts.push(childPart);
					}	
				}
				return descendants;
			}
		});
	}
	
	/**
	 * get the underlying wrapped {@link EditPart} instance
	 * @return the wrapped {@link EditPart}. 
	 */
	public EditPart part() {
		return part;
	}

	/**
	 * focus on this edit part
	 */
	public void focus() {
		UIThreadRunnable.syncExec(new VoidResult() {
			public void run() {
				graphicalEditorView.graphicalViewer.setFocus(part);
			}
		});
	}
	
	/**
	 * select this edit part as a single selection
	 */
	public SWTBotGefViewEditPart select() {
		graphicalEditorView.select(this);
		return this;
	}

	/**
	 * click on the edit part.
	 */
	public SWTBotGefViewEditPart click() {
		final Rectangle bounds = getBounds();
		return click(bounds.getTopLeft());
	}
	
	/**
	 * click on the edit part at the specified location
	 */
	public SWTBotGefViewEditPart click(final Point location) {
		graphicalEditorView.getCanvas().mouseEnterLeftClickAndExit(location.x, location.y);
		return this;
	}

	/**
	 * double click on the edit part.
	 */
	public SWTBotGefViewEditPart doubleClick() {
		final Rectangle bounds = getBounds();
		graphicalEditorView.getCanvas().mouseMoveDoubleClick(bounds.x, bounds.y);
		return this;
	}
	
	public SWTBotGefViewEditPart doubleClick(Point location) {
		graphicalEditorView.getCanvas().mouseMoveDoubleClick(location.x, location.y);
		return this;
	}


	private Rectangle getBounds() {
		final IFigure figure = ((GraphicalEditPart) part).getFigure();
		final Rectangle bounds = figure.getBounds().getCopy();
		figure.translateToAbsolute(bounds);
		return bounds;
	}
	
	public SWTBotGefViewEditPart activateDirectEdit() {
		return activateDirectEdit(null);
	}
	
	
	public SWTBotGefViewEditPart activateDirectEdit(final Object feature) {
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				DirectEditRequest request = new DirectEditRequest();
				if (feature != null)
					request.setDirectEditFeature(feature);
				part().performRequest(request);
			}
		});
		return this;
	}


	
	/**
	 * provide a description of this edit part that is useful for debugging purposes.
	 */
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		
		describe(out,0);
		
		out.close();
		return writer.toString();
	}


	private void describe(PrintWriter out,int indent) {
		out.print(indent(indent));
		out.print(part.getClass().getName());
		List<SWTBotGefViewEditPart> children = children();
		out.print(" children="+children.size());
		out.println();
		for (SWTBotGefViewEditPart child: children) {
			child.describe(out, indent+1);
		}
	}
	
	private String indent(int size) {
		if (size == 0) {
			return "";
		}
		StringBuilder buf = new StringBuilder(size);
		for (int x = 0;x<size;++x) {
			buf.append("\t");
		}
		return buf.toString();
	}

	@SuppressWarnings("unchecked")
	public List<SWTBotGefConnectionViewEditPart> sourceConnections() {
		return UIThreadRunnable.syncExec(new Result<List<SWTBotGefConnectionViewEditPart>>() {
			public List<SWTBotGefConnectionViewEditPart> run() {
				List<SWTBotGefConnectionViewEditPart> connections = new ArrayList<SWTBotGefConnectionViewEditPart>();
				List<org.eclipse.gef.ConnectionEditPart> sourceConnections = ((GraphicalEditPart)part).getSourceConnections();
				for (org.eclipse.gef.ConnectionEditPart c: sourceConnections) {
					connections.add(graphicalEditorView.createEditPart(c));
				}
				return connections;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<SWTBotGefConnectionViewEditPart> targetConnections() {
		return UIThreadRunnable.syncExec(new Result<List<SWTBotGefConnectionViewEditPart>>() {
			public List<SWTBotGefConnectionViewEditPart> run() {
				List<SWTBotGefConnectionViewEditPart> connections = new ArrayList<SWTBotGefConnectionViewEditPart>();
				List<org.eclipse.gef.ConnectionEditPart> targetConnections = ((GraphicalEditPart)part).getTargetConnections();
				for (org.eclipse.gef.ConnectionEditPart c: targetConnections) {
					connections.add(graphicalEditorView.createEditPart(c));
				}
				return connections;
			}
		});
	}	
}
