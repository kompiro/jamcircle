package org.kompiro.jamcircle.kanban.ui.wizard;

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
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.kanban.ui.widget.TableListColumnLabelProvider;
import org.kompiro.jamcircle.kanban.ui.widget.TableListWrapper;


public class BoardNewWizardPage extends WizardPage {
	
	private class TemplateWrapper implements TableListWrapper{

		private boolean even;
		private KanbanBoardTemplate template;

		TemplateWrapper(KanbanBoardTemplate template,boolean even){
			this.template = template;
			this.even = even;
		}
		
		public boolean isEven() {
			return even;
		}
		
	}
	
	private Text boardTitleText;
	private String boardTitle;
	private TemplateWrapper[] initializers;
	private TableViewer typeViewer;
	private KanbanBoardTemplate selectedInitializer;

	public BoardNewWizardPage(KanbanBoardTemplate[] kanbanDataInitializers) {
		super("new_board_wizard"); //$NON-NLS-1$
		setTitle(Messages.BoardNewWizardPage_title);
		setDescription(Messages.BoardNewWizardPage_description);
		this.initializers = trans(kanbanDataInitializers);
	}

	private TemplateWrapper[] trans(KanbanBoardTemplate[] inits) {
		TemplateWrapper[] wrappers = new TemplateWrapper[inits.length];
		for(int i = 0; i < wrappers.length;i++){
			wrappers[i] = new TemplateWrapper(inits[i], i % 2 == 0);
		}
		return wrappers;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label titleLabel = new Label(container, SWT.None);
		titleLabel.setText(Messages.BoardNewWizardPage_title_label);

		boardTitleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		boardTitleText.setLayoutData(gd);
		boardTitleText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				boardTitle = boardTitleText.getText();
			}
		});
		createTemplateTypeSelection(container);
		initialize();
		setControl(container);
	}

	private void createTemplateTypeSelection(Composite container) {
		Label typeLabel = new Label(container,SWT.None);
		typeLabel.setText(Messages.BoardNewWizardPage_template_type_label);
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
					selectedInitializer = ((TemplateWrapper[]) newInput)[0].template;
				}
			}
			
		});
		
		createNameColumn();
		createDescriptionColumn();
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				selectedInitializer = getTemplate(((IStructuredSelection)event.getSelection()).getFirstElement());
			}
			
		});
		Table table = typeViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createNameColumn() {
		TableViewerColumn column = new TableViewerColumn(typeViewer, SWT.LEAD);
		column.getColumn().setText(Messages.BoardNewWizardPage_board_name_label);
		column.getColumn().setWidth(100);
		column.setLabelProvider(new TableListColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				return getTemplate(element).getName();
			}

			@Override
			public Image getImage(Object element) {
				Image image = null;
				Display display = WorkbenchUtil.getDisplay();
				URL resource = getTemplate(element).getIconFromResource();
				if(resource == null) return null;
				try {
					InputStream stream = resource.openStream();
					image = new Image(display, stream);
				} catch (IOException e) {
				}
				return image;
			}
						
		});
	}

	private void createDescriptionColumn() {
		TableViewerColumn column = new TableViewerColumn(typeViewer, SWT.LEAD);
		column.getColumn().setText(Messages.BoardNewWizardPage_board_description_label);
		column.getColumn().setWidth(300);
		column.setLabelProvider(new TableListColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				return getTemplate(element).getDescription();
			}

		});
	}
	
	private AbstractBoardTemplate getTemplate(Object element) {
		return (AbstractBoardTemplate)((TemplateWrapper) element).template;
	}


	private void initialize() {
		String initializeTitle = DateFormat.getDateInstance().format(new Date());
		boardTitleText.setText(String.format(Messages.BoardNewWizardPage_initialize_board_name,initializeTitle));
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