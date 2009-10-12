/**
 * 
 */
package org.kompiro.jamcircle.kanban.ui.internal.editpart;


import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kompiro.jamcircle.kanban.model.CardContainer;
import org.kompiro.jamcircle.kanban.ui.widget.CardListListener;
import org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer;

final class ContainerContentsWindow extends ApplicationWindow {
	/**
	 * 
	 */
	private CardListTableViewer viewer;
	private CardListListener listener;
	private CardContainer container;

	ContainerContentsWindow(Shell parentShell,CardContainer container,CardListListener listener) {
		super(parentShell);
		this.container = container;
		this.listener = listener;
	}

	@Override
	protected Control createContents(Composite parent) {
		viewer = new CardListTableViewer(parent);
		viewer.addCardListListener(listener);
		container.addPropertyChangeListener(viewer);
		viewer.setInput(container);
		return parent;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		String title = String.format("Card [%s]", container.getContainerName());
		shell.setText(title);
	}

	@Override
	public boolean close() {
		viewer.removeCardListListener(listener);
		container.removePropertyChangeListener(viewer);
		return super.close();
	}
}