package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.kompiro.jamcircle.kanban.ui.dialog.DialogConstants.KEY_OF_DATA_ID;

import java.io.File;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.Messages;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class LaneEditDialog extends Dialog{

	static final String ID_SCRIPT_TYPE = "script_type"; //$NON-NLS-1$
	static final String ID_SCRIPT = "script"; //$NON-NLS-1$
	static final String ID_STATUS = "status"; //$NON-NLS-1$

	private Text statusText;
	private Text scriptText;
	private ComboViewer scriptTypeCombo;
	
	private String status;
	private String script;
	private ScriptTypes type;
	private File customizeIcon;

	public LaneEditDialog(Shell parentShell,String status,String script,ScriptTypes type,File customizeIcon) {
		super(parentShell);
		this.status = status;
		this.script = script;
		this.type = type;
		this.customizeIcon = customizeIcon;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.LaneEditDialog_title);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		createStatusGroup(parent);
		createScriptGroup(parent);
		createCustomIconGroup(parent);
		return super.createContents(parent);
	}

	private void createStatusGroup(Composite parent) {
		Group group = new Group(parent,SWT.None);
		group.setText(Messages.LaneEditDialog_status_label);
		group.setLayout(new GridLayout());
		
		statusText = new Text(group,SWT.BORDER);
		statusText.setData(KEY_OF_DATA_ID,ID_STATUS);
		if(status != null){
			statusText.setText(status);
		}
		statusText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				LaneEditDialog.this.status = LaneEditDialog.this.statusText.getText();
			}
		});
		
		GridDataFactory.fillDefaults().grab(true,true).applyTo(group);
		GridDataFactory.fillDefaults().grab(true,true).hint(400,20).applyTo(statusText);
	}

	
	private void createScriptGroup(Composite parent) {
		Group group = new Group(parent,SWT.None);
		group.setText(Messages.LaneEditDialog_script_label);
		group.setLayout(new GridLayout());
		scriptText = new Text(group,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		scriptText.setData(KEY_OF_DATA_ID, ID_SCRIPT);

		if(script != null){
			scriptText.setText(script);
		}
		scriptText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				LaneEditDialog.this.script = LaneEditDialog.this.scriptText.getText();
			}
		});
		GridDataFactory.fillDefaults().hint(400, 200).grab(true,true).applyTo(scriptText);

		scriptTypeCombo = new ComboViewer(group);
		scriptTypeCombo.getCombo().setData(KEY_OF_DATA_ID, ID_SCRIPT_TYPE);

		scriptTypeCombo.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
		scriptTypeCombo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ScriptTypes)element).name();
			}
		});
		scriptTypeCombo.setInput(ScriptTypes.values());
		StructuredSelection selection = new StructuredSelection(type);
		scriptTypeCombo.setSelection(selection);
		scriptTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				LaneEditDialog.this.type = (ScriptTypes)((StructuredSelection)sel).getFirstElement();
			}
		});
	}
	
	private void createCustomIconGroup(Composite parent) {
		Group group = new Group(parent,SWT.None);
		group.setText(Messages.LaneEditDialog_custom_icon_label);
		final GridLayout layout = new GridLayout();
		group.setLayout(layout);
		final Label icon = new Label(group, SWT.NONE);
		icon.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true,
				true));
		if(customizeIcon != null){
			setIconImage(icon);
		}else{
			icon.setText("no data");
		}
		Button selectButton = new Button(group, SWT.PUSH);
		selectButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		selectButton.setText("選択");
		selectButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(),SWT.OPEN);
				dialog.setFilterExtensions(new String[]{"*.png;*.gif;*.ico;*.jpg"});
				String filePath = dialog.open();
				if(filePath == null) return;
				File file = new File(filePath);
				if( ! file.exists()){
					KanbanUIStatusHandler.info(Messages.CardEditDialog_info_file_not_exist,file,true);
					return;
				}else if( ! file.isFile()){
					KanbanUIStatusHandler.info(Messages.CardEditDialog_info_selection_is_not_file,file,true);
					return;
				}
				customizeIcon = file;
				icon.setText("");
				setIconImage(icon);
				getShell().pack();
			}
		});
		Button deleteButton = new Button(group, SWT.PUSH);
		deleteButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		deleteButton.setText("削除");
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				icon.setText("no data");
				customizeIcon = null;
				setIconImage(icon);
			}
		});
		
	}

	public String getStatus(){
		return status;
	}
	
	public String getScript(){
		return script;
	}
	
	public ScriptTypes getScriptType(){
		return type;
	}
	
	public File getCustomizeIcon() {
		return customizeIcon;
	}

	private void setIconImage(final Label icon) {
		if(icon.getImage() != null){
			icon.getImage().dispose();
		}
		if(customizeIcon == null){
			getShell().layout();
			getShell().pack();
			return;
		}
		ImageLoader loader = new ImageLoader();
		ImageData[] datas = loader.load(customizeIcon.getAbsolutePath());
		ImageData data = datas[0];
		data = scaleTo200px(data);
		Image image = new Image(WorkbenchUtil.getDisplay(), data);
		icon.setImage(image);
		getShell().layout();
	}

	private ImageData scaleTo200px(ImageData data) {
		int width = data.width;
		int height = data.height;
		if(width > 200 || height > 200){
			if(width > height){
				int calcHeight = (int)(height / (width / 200.0));
				data = data.scaledTo(200, calcHeight);
			}else{
				int calcWidth = (int)(width / (height / 200.0));
				data = data.scaledTo(calcWidth, 200);
			}
		}
		return data;
	}
		
}
