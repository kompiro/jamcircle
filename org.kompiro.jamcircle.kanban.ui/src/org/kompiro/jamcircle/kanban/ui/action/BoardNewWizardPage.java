package org.kompiro.jamcircle.kanban.ui.action;

import java.text.DateFormat;
import java.util.Date;


import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;


public class BoardNewWizardPage extends WizardPage {
	private Text boardTitleText;
	private String boardTitle;
	private KanbanBoardTemplate[] initializers;
	private ComboViewer typeCombo;
	private KanbanBoardTemplate selectedInitializer;

	public BoardNewWizardPage(KanbanBoardTemplate[] kanbanDataInitializers) {
		super("wizardPage");
		setTitle("Create New Board");
		setDescription("This wizard creates a new board.");
		this.initializers = kanbanDataInitializers;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label titleLabel = new Label(container, SWT.None);
		titleLabel.setText("&Title:");

		boardTitleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		boardTitleText.setLayoutData(gd);
		boardTitleText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				boardTitle = boardTitleText.getText();
			}
		});
		
		Label typeLabel = new Label(container,SWT.None);
		typeLabel.setText("&Board Type:");
		
		typeCombo = new ComboViewer(container);
		typeCombo.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				if(newInput != null){
					selectedInitializer = ((KanbanBoardTemplate[]) newInput)[0];
				}
			}
			
		});
		typeCombo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((KanbanBoardTemplate) element).getName();
			}
		});
		typeCombo.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				selectedInitializer = (KanbanBoardTemplate)((IStructuredSelection)event.getSelection()).getFirstElement();
			}
			
		});
		initialize();
		setControl(container);
	}

	private void initialize() {
		String initializeTitle = DateFormat.getDateInstance().format(new Date());
		boardTitleText.setText(String.format("%s's Board",initializeTitle));
		typeCombo.setInput(initializers);
		typeCombo.getCombo().select(0);
	}

	String getBoardTitle(){
		return this.boardTitle;
	}
	
	KanbanBoardTemplate getInitializer(){
		return this.selectedInitializer;
	}
}