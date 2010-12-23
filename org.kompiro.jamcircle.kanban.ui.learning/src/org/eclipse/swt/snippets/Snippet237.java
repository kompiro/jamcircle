/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.snippets;

/* 
 * Composite Snippet: inherit a background color or image
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class Snippet237 {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Composite.setBackgroundMode()");
		shell.setLayout(new RowLayout(SWT.VERTICAL));

		Color color = display.getSystemColor(SWT.COLOR_CYAN);

		Group group = new Group(shell, SWT.NONE);
		group.setText("SWT.INHERIT_NONE");
		group.setBackground(color);
		group.setBackgroundMode(SWT.INHERIT_NONE);
		createChildren(group);

		group = new Group(shell, SWT.NONE);
		group.setBackground(color);
		group.setText("SWT.INHERIT_DEFAULT");
		group.setBackgroundMode(SWT.INHERIT_DEFAULT);
		createChildren(group);

		group = new Group(shell, SWT.NONE);
		group.setBackground(color);
		group.setText("SWT.INHERIT_FORCE");
		group.setBackgroundMode(SWT.INHERIT_FORCE);
		createChildren(group);

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
		canvas.pack();

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	static void createChildren(final Composite parent) {
		parent.setLayout(new RowLayout());
		List list = new List(parent, SWT.BORDER | SWT.MULTI);
		list.add("List item 1");
		list.add("List item 2");
		Label label = new Label(parent, SWT.NONE);
		label.setText("Label");
		Button button = new Button(parent, SWT.RADIO);
		button.setText("Radio Button");
		button = new Button(parent, SWT.CHECK);
		button.setText("Check box Button");
		button = new Button(parent, SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImageLoader loader = new ImageLoader();
				ImageData[] load = loader.load(new Snippet237().getClass().getResourceAsStream("trac_bullet.png"));
				Image image = new Image(Display.getDefault(), load[0]);
				parent.setBackgroundImage(image);
			}
		});
		button.setText("Push Button");
		Text text = new Text(parent, SWT.BORDER);
		text.setText("Text");
	}
}