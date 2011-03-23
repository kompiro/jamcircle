package org.kompiro.jamcircle.scripting.ui.internal.ruby.dialog;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.*;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.scripting.delegator.DefaultSaveHistoryDelegator;
import org.kompiro.jamcircle.scripting.delegator.SaveHistoryDeligator;
import org.kompiro.jamcircle.scripting.ui.ScriptingImageEnum;
import org.kompiro.jamcircle.scripting.ui.ScriptingUIActivator;

public class HistoryWindow extends ApplicationWindow {

	private class SaveRubyHistoryAction extends Action {

		private SaveRubyHistoryAction() {
			setToolTipText("save");
			setImageDescriptor(getImageDescriptor(ScriptingImageEnum.SCRIPT_SAVE));
		}

		@Override
		public void run() {
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setOverwrite(true);
			String file = dialog.open();
			if (file != null) {
				File target = new File(file);
				delegator.delegate(target, histories);
			}
		}

		private ImageDescriptor getImageDescriptor(ScriptingImageEnum key) {
			ScriptingUIActivator activator = ScriptingUIActivator.getDefault();
			if (activator == null) {
				return null;
			}
			return activator.getImageRegistry().getDescriptor(key.toString());
		}

	}

	private String histories;
	private SaveHistoryDeligator delegator = new DefaultSaveHistoryDelegator();

	public HistoryWindow(Shell parentShell, List<String> history) {
		super(parentShell);
		StringBuilder builder = new StringBuilder();
		for (String line : history) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		this.histories = builder.toString().trim();
		addMenuBar();
		addToolBar(SWT.FLAT);
	}

	@Override
	protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
		return new Rectangle(preferredSize.x, preferredSize.y, 500, 500);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.None);

		comp.setLayout(new FillLayout());

		CompositeRuler ruler = new CompositeRuler();
		ruler.addDecorator(0, new LineNumberRulerColumn());
		SourceViewer viewer = new SourceViewer(comp, ruler, SWT.READ_ONLY);
		Document document = new Document();
		document.set(histories);
		viewer.setDocument(document);
		return comp;
	}

	@Override
	protected void configureShell(Shell shell) {
		getToolBarManager2().add(new SaveRubyHistoryAction());
		shell.setText("History Viewer");
		super.configureShell(shell);
	}

	public void setDelegator(SaveHistoryDeligator delegator) {
		this.delegator = delegator;
	}

}
