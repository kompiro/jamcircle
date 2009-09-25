package org.eclipse.swt.snippets;

/*
 * List example snippet: print selected items in a list
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Snippet59 {

public static void main (String [] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	final List list = new List (shell, SWT.BORDER);
	for (int i=0; i<128; i++) list.add ("Item " + i);
	list.setBounds (0, 0, 100, 100);
	list.addListener (SWT.Selection, new Listener () {
		public void handleEvent (Event e) {
			String string = "";
			int [] selection = list.getSelectionIndices ();
			for (int i=0; i<selection.length; i++) string += selection [i] + " ";
			System.out.println ("Selection={" + string + "}");
		}
	});
	list.addListener (SWT.DefaultSelection, new Listener () {
		public void handleEvent (Event e) {
			String string = "";
			int [] selection = list.getSelectionIndices ();
			for (int i=0; i<selection.length; i++) string += selection [i] + " ";
			System.out.println ("DefaultSelection={" + string + "}");
		}
	});
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 
