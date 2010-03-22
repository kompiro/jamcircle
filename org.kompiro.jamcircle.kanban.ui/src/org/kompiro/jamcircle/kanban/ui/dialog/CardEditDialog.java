package org.kompiro.jamcircle.kanban.ui.dialog;

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

	private Text subjectText;
	private Text bodyText;
	private String subject;
	private String body;
	private Date due;
	private Button addButton;
	private ListViewer fileList;
	private Button deleteButton;
	private List<File> files = new ArrayList<File>();
	private Image fileImage;
	private Browser bodyBrowser;
	private Date dueDate;
	
	private static final ShowdownConverter converter = ShowdownConverter.getInstance();

	public CardEditDialog(Shell parentShell,Card card) {
		super(parentShell);
		this.subject = card.getSubject();
		this.body = card.getContent();
		this.files.addAll(card.getFiles());
		this.dueDate = card.getDueDate();
		if(Platform.isRunning()){
			fileImage = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.FILE_GO_IMAGE.toString());
		}
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Card Edit");
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
		subjectGroup.setText("subject");
		subjectGroup.setLayout(new GridLayout());
		subjectText = new Text(subjectGroup,SWT.BORDER);
		if(subject != null){
			subjectText.setText(subject);
		}
		subjectText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				CardEditDialog.this.subject = CardEditDialog.this.subjectText.getText();
			}
		});
		GridDataFactory.fillDefaults().hint(400, 20).grab(true, true).applyTo(subjectText);
		GridDataFactory.fillDefaults().applyTo(subjectGroup);
	}

	private void createBodyArea(Composite comp) {
		Group contentGroup = new Group(comp,SWT.NONE);
		contentGroup.setText("content");
		contentGroup.setLayout(new FillLayout());

		CTabFolder folder = new CTabFolder(contentGroup,SWT.BOTTOM);
		GridDataFactory.fillDefaults().hint(400, 200).applyTo(contentGroup);
		CTabItem textArea = new CTabItem(folder,SWT.NONE);
		bodyText = new Text(folder,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.WRAP);
		if(body != null){
			bodyText.setText(body);
		}
		bodyText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				String body = CardEditDialog.this.bodyText.getText();
				CardEditDialog.this.body = body;
			}
		});
		textArea.setControl(bodyText);
		textArea.setText("Text(Markdown/HTML)");
		final CTabItem browserArea = new CTabItem(folder,SWT.NONE);
		bodyBrowser = new Browser(folder,SWT.BORDER);
		browserArea.setControl(bodyBrowser);
		browserArea.setText("Browser");
		folder.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item == browserArea){
					String converted = converter.convert(body);
					bodyBrowser.setText(converted);
				}
			}
		});
		folder.setSelection(0);
	}
	
	private void createAtributeArea(Composite comp) {
		Group attributeGroup = new Group(comp,SWT.None);
		attributeGroup.setText("attribute");
		attributeGroup.setLayout(new FillLayout());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(attributeGroup);
		
		CTabFolder folder = new CTabFolder(attributeGroup,SWT.BOTTOM);
		createDueArea(folder);
		
		createFileTab(folder);
		folder.setSelection(0);

	}

	private void createDueArea(CTabFolder folder) {
		CTabItem dueArea = new CTabItem(folder,SWT.NONE);
		dueArea.setText("Due");
		Composite dueComp = new Composite(folder,SWT.BORDER);
		dueComp.setBackground(ColorConstants.white);
		dueComp.setLayout(new GridLayout(2,false));
		
		final DateTime dueDateWidget = new DateTime(dueComp,SWT.CALENDAR);
		final Text dueText = new Text(dueComp,SWT.BORDER|SWT.READ_ONLY);
		dueDateWidget.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, dueDateWidget.getYear());
				cal.set(Calendar.MONTH, dueDateWidget.getMonth());
				cal.set(Calendar.DATE, dueDateWidget.getDay());
				due = cal.getTime();
				String date = String.format("%04d/%02d/%02d", dueDateWidget.getYear(),dueDateWidget.getMonth() + 1,dueDateWidget.getDay());
				dueText.setText(date);
			}
		});
		if(dueDate != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.dueDate);
			dueDateWidget.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
			String date = String.format("%04d/%02d/%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
			dueText.setText(date);
		}
		
		GridDataFactory.fillDefaults().span(2,1).applyTo(dueDateWidget);
		
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(dueText);
		Button deleteDueButton = new Button(dueComp,SWT.None);
		deleteDueButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				due = null;
				dueText.setText("");
			}
		});
		Image image = null;
		if(KanbanUIActivator.getDefault() != null){
			image = KanbanUIActivator.getDefault().getImageRegistry().get(KanbanImageConstants.DELETE_IMAGE.toString());
		}
		if(image != null){
			deleteDueButton.setImage(image);
		}else{
			deleteDueButton.setText("Delete");
		}
		dueArea.setControl(dueComp);
	}

	private void createFileTab(CTabFolder folder) {
		CTabItem fileArea = new CTabItem(folder,SWT.NONE);
		fileArea.setText("File");
		
		Composite fileComp = new Composite(folder,SWT.None);
		fileComp.setLayout(new GridLayout(2,false));
		fileComp.setBackground(ColorConstants.white);
		fileList = new ListViewer(fileComp);
		fileList.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof File) {
					File file = (File) element;
					return file.getName();
				}
				return "";
			}
		});
		fileList.setContentProvider(new ArrayContentProvider());
		fileList.setInput(files);
		Menu popupFileList = new Menu(fileList.getList());
		fileList.getList().setMenu(popupFileList);
		MenuItem openFile = new MenuItem(popupFileList,SWT.PUSH);
		openFile.setText("Open");
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
		fileArea.setControl(fileComp);
	}

	private void createDeleteFileButton(Composite fileBtnComp) {
		deleteButton = new Button(fileBtnComp,SWT.PUSH);
		deleteButton.setText("Delete Files");
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
		addButton.setText("Add File");
		addButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(),SWT.OPEN);
				String filePath = dialog.open();
				if(filePath != null) {
					File file = new File(filePath);
					if( ! file.exists()){
						KanbanUIStatusHandler.info("The file is not exist.",file,true);
						return;
					}else if( ! file.isFile()){
						KanbanUIStatusHandler.info("Please select a file.",file,true);
						return;
					}
					for(File target : files){
						if(target.getAbsolutePath().equals(file.getAbsolutePath())){
							KanbanUIStatusHandler.info("The file is already selected.",file,true);							
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
	
	public String getBodyText(){
		return body;
	}
	
	public Date getDueDate(){
		return due;
	}
	
	
	public static void main(String[] args) {
		Shell shell = new Shell();
		List<File> files = new ArrayList<File>();
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		files.add(new File(System.getProperty("user.home")));
		Card card = new org.kompiro.jamcircle.kanban.model.mock.Card();
		card.setSubject("testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest");
		card.setContent("testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest");
		for(File file : files){
			card.addFile(file);
		}
		card.setDueDate(new Date());
		CardEditDialog dialog = new CardEditDialog(shell,card);
		dialog.open();
		System.out.println(dialog.getFiles());
	}

	public List<File> getFiles() {
		return files;
	}
}
