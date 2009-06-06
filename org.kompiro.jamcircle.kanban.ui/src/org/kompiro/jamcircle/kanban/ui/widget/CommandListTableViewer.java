package org.kompiro.jamcircle.kanban.ui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;


import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.kompiro.jamcircle.kanban.ui.KanbanView;

public class CommandListTableViewer {
	private TableViewer viewer;
	private GC gc;
	private int current;
	private TableLayout layout;
	private Table table;
	
	class ViewContentProvider implements IStructuredContentProvider {
		private static final int END_STACK = 1;
		private static final int ZERO_START = 1;
		private EditDomain domain;
		private CommandStackListener listener = new CommandStackListener(){
			public void commandStackChanged(EventObject event) {
				CommandListTableViewer.this.current = getCurrentCommandIndex();
				CommandListTableViewer.this.viewer.refresh();
			}
		};
		private CommandStack commandStack;
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
			domain.getCommandStack().removeCommandStackListener(listener);
		}
		public Object[] getElements(Object parent) {
			if(parent instanceof GraphicalViewer){
				GraphicalViewer viewer = (GraphicalViewer) parent;
				if(domain == null){
					domain = viewer.getEditDomain();
					initialize();
				}
				Object[] commands = commandStack.getCommands();
				List<CommandWrapper> results = new ArrayList<CommandWrapper>();
				for(int i = 0;i < commands.length;i++){
					Command command = (Command) commands[i];
					results.add(new CommandWrapper(i + 1,command));
				}
				return results.toArray();
			}
			return null;
		}
		
		private void initialize() {
			commandStack = domain.getCommandStack();
			commandStack.addCommandStackListener(listener);
			current = getCurrentCommandIndex();
		}
		private int getCurrentCommandIndex() {
			return Arrays.asList(commandStack.getCommands()).indexOf(commandStack.getUndoCommand()) + ZERO_START + END_STACK;
		}
	}
	
	class CommandWrapper {
		private int row;
		private Command command;

		CommandWrapper(int row,Command command){
			this.row = row;
			this.command = command;
		}
		
		int getRow(){
			return this.row;
		}
		
		Command getCommand(){
			return this.command;
		}
	}

	class DrawLabelProvider extends OwnerDrawLabelProvider{

		@Override
		protected void measure(Event event, Object element) {
		}

		@Override
		protected void paint(Event event, Object element) {
			int index = event.index;
			String text = "";
			CommandWrapper wrapper = (CommandWrapper) element;
			if(wrapper.getRow() == current - 1){
				event.gc.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			}
			switch(index){
			case 1:
				text = wrapper.getCommand().getDebugLabel();
				break;
			case 0:
				text = Integer.toString(wrapper.getRow());
				break;
			}
			event.gc.drawText(text, event.x, event.y,true);
		}
		
	}
	
	class NameSorter extends ViewerSorter {
	}

	public CommandListTableViewer() {
	}

	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent,SWT.None);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));
		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new DrawLabelProvider());
		table = viewer.getTable();
		layout = new TableLayout();
		table.setLayout(layout);
		gc = new GC(table.getDisplay());
		table.setHeaderVisible(true);
		commandRowLabel();
		commandColumnLabel();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridDataFactory.fillDefaults().hint(400, 300).applyTo(table);
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IViewPart part = activePage.findView(KanbanView.ID);
		viewer.setInput(part.getAdapter(GraphicalViewer.class));

	}

	private void commandRowLabel() {
		TableColumn tableColumn = new TableColumn(table, SWT.RIGHT);
		tableColumn.setText("index");
		layout.addColumnData(new ColumnPixelData(gc.stringExtent("00000").x));
	}


	private void commandColumnLabel() {
		TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("Command");
		layout.addColumnData(new ColumnPixelData(gc.stringExtent("Command").x));
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
