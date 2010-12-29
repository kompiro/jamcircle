package org.kompiro.jamcircle.scripting.ui.internal.ruby.dialog;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.*;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public class RubyHistoryWindow extends ApplicationWindow {

	private class SaveRubyHistoryAction extends Action {

	}

	private String histories;

	public RubyHistoryWindow(Shell parentShell, List<String> history) {
		super(parentShell);
		addToolBar(SWT.FLAT);
		StringBuilder builder = new StringBuilder();
		for (String line : history) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		this.histories = builder.toString().trim();
	}

	@Override
	protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
		return new Rectangle(preferredSize.x, preferredSize.y, 500, 500);
	}

	@Override
	protected Control createContents(Composite parent) {
		CompositeRuler ruler = new CompositeRuler();
		ruler.addDecorator(0, new LineNumberRulerColumn());
		SourceViewer viewer = new SourceViewer(parent, ruler, SWT.READ_ONLY);
		Document document = new Document();
		document.set(histories);
		viewer.setDocument(document);
		getToolBarManager2().add(new SaveRubyHistoryAction());
		return parent;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("History Viewer");
	}

}
