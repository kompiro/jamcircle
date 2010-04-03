package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.kompiro.jamcircle.kanban.ui.dialog.DialogConstants.KEY_OF_DATA_ID;

import java.io.File;
import java.util.*;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.ShowdownConverter;
import org.kompiro.jamcircle.kanban.ui.*;

public class CardEditDialog extends Dialog{

	private static final String EMPTY = ""; //$NON-NLS-1$

	public static final String ID_SUBJECT = "subject"; //$NON-NLS-1$
	public static final String ID_CONTENT = "contents";
	public static final String ID_CONTENTS_BROWSER = "contents_browser"; //$NON-NLS-1$
	public static final String ID_CONTENTS_BROWSER_TAB = "contents_browser_tab"; //$NON-NLS-1$
	public static final String ID_DUE_DATE = "due_date"; //$NON-NLS-1$
	public static final String ID_DUE_TAB = "due_tab"; //$NON-NLS-1$
	public static final String ID_FILES_TAB = "files_tab"; //$NON-NLS-1$
	public static final String ID_FILE_LIST = "file_list"; //$NON-NLS-1$
	public static final String ID_DELETE_BUTTON = "delete_button"; //$NON-NLS-1$

	private Text subjectText;
	private Text bodyText;
	private String subject;
	private String content;
	private Date due;
	private Button addButton;
	private ListViewer fileList;
	private Button deleteButton;
	private List<File> files = new ArrayList<File>();
	private Image fileImage;
	private Browser bodyBrowser;
	private Date dueDate;
	
	private static final ShowdownConverter converter = ShowdownConverter.getInstance();

	public static final String ID_CONTENT_TAB = "content_tab"; //$NON-NLS-1$

	public CardEditDialog(Shell parentShell,Card card) {
		super(parentShell);
		this.subject = card.getSubject();
		this.content = card.getContent();
		this.files.addAll(card.getFiles());
		this.dueDate = card.getDueDate();
		if(Platform.isRunning()){
			fileImage = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.FILE_GO_IMAGE.toString());
		}
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.CardEditDialog_title);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent,SWT.None);
		GridLayoutFactory.fillDefaults().applyTo(comp);
		
		createSubjectArea(comp);
		createBodyArea(comp);
		createAtributeArea(comp);
		return super.createContents(parent);
	}

	private void createSubjectArea(Composite comp) {
		Group subjectGroup = new Group(comp,SWT.NONE);
		subjectGroup.setText(Messages.CardEditDialog_subject_group_name);
		subjectGroup.setLayout(new GridLayout());
		subjectText = new Text(subjectGroup,SWT.BORDER);
		subjectText.setData(KEY_OF_DATA_ID, ID_SUBJECT);
		if(subject != null){
			subjectText.setText(subject);
		}
		subjectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				CardEditDialog.this.subject = CardEditDialog.this.subjectText.getText();
			}
		});
		GridDataFactory.fillDefaults().hint(400, 20).grab(true, true).applyTo(subjectText);
		GridDataFactory.fillDefaults().applyTo(subjectGroup);
	}

	private void createBodyArea(Composite comp) {
		Group contentGroup = new Group(comp,SWT.NONE);
		contentGroup.setText(Messages.CardEditDialog_content_group_name);
		contentGroup.setLayout(new FillLayout());

		CTabFolder folder = new CTabFolder(contentGroup,SWT.BOTTOM);
		GridDataFactory.fillDefaults().hint(400, 200).applyTo(contentGroup);

		CTabItem contentTabItem = new CTabItem(folder,SWT.NONE);
		contentTabItem.setData(KEY_OF_DATA_ID,ID_CONTENT_TAB);

		bodyText = new Text(folder,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.WRAP);
		if(content != null){
			bodyText.setText(content);
		}
		bodyText.setData(KEY_OF_DATA_ID, ID_CONTENT);
		bodyText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String body = CardEditDialog.this.bodyText.getText();
				CardEditDialog.this.content = body;
			}
		});
		contentTabItem.setControl(bodyText);
		contentTabItem.setText(Messages.CardEditDialog_content_tab_name);
		final CTabItem browserArea = new CTabItem(folder,SWT.NONE);
		bodyBrowser = new Browser(folder,SWT.BORDER);
		browserArea.setControl(bodyBrowser);
		browserArea.setText(Messages.CardEditDialog_browser_name);
		browserArea.setData(KEY_OF_DATA_ID, ID_CONTENTS_BROWSER_TAB);
		bodyBrowser.setData(KEY_OF_DATA_ID, ID_CONTENTS_BROWSER);

		folder.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item == browserArea){
					String converted = converter.convert(content);
					bodyBrowser.setText(converted);
				}
			}
		});
		folder.setSelection(0);
	}
	
	private void createAtributeArea(Composite comp) {
		Group attributeGroup = new Group(comp,SWT.None);
		attributeGroup.setText(Messages.CardEditDialog_attribute_group_name);
		attributeGroup.setLayout(new FillLayout());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(attributeGroup);
		
		CTabFolder folder = new CTabFolder(attributeGroup,SWT.BOTTOM);
		createDueArea(folder);
		
		createFileTab(folder);
		folder.setSelection(0);

	}

	private void createDueArea(CTabFolder folder) {
		CTabItem dueTabItem = new CTabItem(folder,SWT.NONE);
		dueTabItem.setText(Messages.CardEditDialog_due_label);
		dueTabItem.setData(KEY_OF_DATA_ID,ID_DUE_TAB);
		Composite dueComp = new Composite(folder,SWT.BORDER);
		dueComp.setBackground(ColorConstants.white);
		dueComp.setLayout(new GridLayout(2,false));
		
		final DateTime dueDateWidget = new DateTime(dueComp,SWT.CALENDAR);
		dueDateWidget.setData(KEY_OF_DATA_ID, ID_DUE_DATE);
		final Text dueText = new Text(dueComp,SWT.BORDER|SWT.READ_ONLY);
		dueDateWidget.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, dueDateWidget.getYear());
				cal.set(Calendar.MONTH, dueDateWidget.getMonth());
				cal.set(Calendar.DATE, dueDateWidget.getDay());
				due = cal.getTime();
				String date = String.format(Messages.CardEditDialog_due_format, dueDateWidget.getYear(),dueDateWidget.getMonth() + 1,dueDateWidget.getDay());
				dueText.setText(date);
			}
		});
		if(dueDate != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.dueDate);
			dueDateWidget.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
			String date = String.format(Messages.CardEditDialog_due_format, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
			dueText.setText(date);
		}
		
		GridDataFactory.fillDefaults().span(2,1).applyTo(dueDateWidget);
		
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(dueText);
		Button deleteDueButton = new Button(dueComp,SWT.None);
		deleteDueButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				due = null;
				dueText.setText(EMPTY);
			}
		});
		Image image = null;
		if(KanbanUIActivator.getDefault() != null){
			image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.DELETE_IMAGE.toString());
		}
		if(image != null){
			deleteDueButton.setImage(image);
		}else{
			deleteDueButton.setText(Messages.CardEditDialog_delete_button_label);
		}
		dueTabItem.setControl(dueComp);
	}

	private void createFileTab(CTabFolder folder) {
		CTabItem fileTabItem = new CTabItem(folder,SWT.NONE);
		fileTabItem.setData(KEY_OF_DATA_ID, ID_FILES_TAB);
		fileTabItem.setText(Messages.CardEditDialog_file_label);
		
		Composite fileComp = new Composite(folder,SWT.None);
		fileComp.setLayout(new GridLayout(2,false));
		fileComp.setBackground(ColorConstants.white);
		fileList = new ListViewer(fileComp);
		fileList.getList().setData(KEY_OF_DATA_ID,ID_FILE_LIST);
		fileList.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof File) {
					File file = (File) element;
					return file.getName();
				}
				return EMPTY;
			}
		});
		fileList.setContentProvider(new ArrayContentProvider());
		fileList.setInput(files);
		Menu popupFileList = new Menu(fileList.getList());
		fileList.getList().setMenu(popupFileList);
		MenuItem openFile = new MenuItem(popupFileList,SWT.PUSH);
		openFile.setText(Messages.CardEditDialog_open_label);
		openFile.setImage(fileImage);
		openFile.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				StructuredSelection sel = (StructuredSelection)fileList.getSelection();
				launchFile(sel);
			}
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
		fileList.addOpenListener(new IOpenListener(){
			public void open(OpenEvent event) {
				StructuredSelection sel = (StructuredSelection)event.getSelection();
				launchFile(sel);
			}
		});
		
		GridDataFactory.fillDefaults().hint(300,80).grab(true,true).applyTo(fileList.getList());

		Composite fileBtnComp = new Composite(fileComp,SWT.None);
		fileBtnComp.setBackground(ColorConstants.white);
		GridDataFactory.fillDefaults().hint(80,80).applyTo(fileBtnComp);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(fileBtnComp);

		createAddFileButton(fileBtnComp);

		createDeleteFileButton(fileBtnComp);
		fileTabItem.setControl(fileComp);
	}

	private void createDeleteFileButton(Composite fileBtnComp) {
		deleteButton = new Button(fileBtnComp,SWT.PUSH);
		deleteButton.setText(Messages.CardEditDialog_delete_file_label);
		deleteButton.setData(KEY_OF_DATA_ID,ID_DELETE_BUTTON);
		deleteButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = fileList.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection list = (StructuredSelection) selection;
					for(Object obj : list.toArray()){
						if (obj instanceof File) {
							File file = (File) obj;
							files.remove(file);
						}
					}
				}
				fileList.setInput(files);
			}
		});
		GridDataFactory.swtDefaults().applyTo(deleteButton);
	}

	private void createAddFileButton(Composite fileBtnComp) {
		addButton = new Button(fileBtnComp,SWT.PUSH);
		addButton.setText(Messages.CardEditDialog_add_file_label);
		addButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(),SWT.OPEN);
				String filePath = dialog.open();
				if(filePath != null) {
					File file = new File(filePath);
					if( ! file.exists()){
						KanbanUIStatusHandler.info(Messages.CardEditDialog_info_file_not_exist,file,true);
						return;
					}else if( ! file.isFile()){
						KanbanUIStatusHandler.info(Messages.CardEditDialog_info_selection_is_not_file,file,true);
						return;
					}
					for(File target : files){
						if(target.getAbsolutePath().equals(file.getAbsolutePath())){
							KanbanUIStatusHandler.info(Messages.CardEditDialog_info_already_selected,file,true);							
							return;
						}
					}
					files.add(file);
					fileList.setInput(files);
				}
			}
		});
		GridDataFactory.swtDefaults().applyTo(addButton);
	}

	private void launchFile(StructuredSelection sel) {
		File file = (File) sel.getFirstElement();
		Program.launch(file.getAbsolutePath());
	}
	
	public String getSubjectText(){
		return subject;
	}
	
	public String getContentText(){
		return content;
	}
	
	public Date getDueDate(){
		return due;
	}
	

	public List<File> getFiles() {
		return files;
	}
}
