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
		group.setLayout(new GridLayout());
		final Label icon = new Label(group, SWT.NONE);
		icon.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		if(customizeIcon != null){
			setIconImage(icon);
		}else{
			icon.setText("no data");
		}
		Button button = new Button(group, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		button.setText(Messages.CardEditDialog_add_file_label);
		button.addSelectionListener(new SelectionAdapter(){
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
					customizeIcon = file;
					icon.setText(null);
					setIconImage(icon);
				}
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
		ImageLoader loader = new ImageLoader();
		ImageData[] datas = loader.load(customizeIcon.getAbsolutePath());
		Image image = new Image(WorkbenchUtil.getDisplay(), datas[0]);
		icon.setImage(image);
	}
		
}
