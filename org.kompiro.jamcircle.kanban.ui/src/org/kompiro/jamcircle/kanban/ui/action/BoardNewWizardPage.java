package org.kompiro.jamcircle.kanban.ui.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.boardtemplate.AbstractBoardTemplate;
import org.kompiro.jamcircle.kanban.boardtemplate.KanbanBoardTemplate;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;


public class BoardNewWizardPage extends WizardPage {
	private Text boardTitleText;
	private String boardTitle;
	private KanbanBoardTemplate[] initializers;
	private TableViewer typeViewer;
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
		typeLabel.setText("&Template Type:");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(typeLabel);
		
		typeViewer = new TableViewer(container);
		typeViewer.setContentProvider(new IStructuredContentProvider(){
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
		typeViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((AbstractBoardTemplate) element).getName();
			}
			@Override
			public Image getImage(Object element) {
				Image image = null;
				Display display = WorkbenchUtil.getDisplay();
				URL resource = ((AbstractBoardTemplate) element).getIconFromResource();
				if(resource == null) return null;
				try {
					InputStream stream = resource.openStream();
					image = new Image(display, stream);
				} catch (IOException e) {
				}
				return image;
			}
		});
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener(){

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
		typeViewer.setInput(initializers);
		typeViewer.getTable().select(0);
	}

	String getBoardTitle(){
		return this.boardTitle;
	}
	
	KanbanBoardTemplate getInitializer(){
		return this.selectedInitializer;
	}
}