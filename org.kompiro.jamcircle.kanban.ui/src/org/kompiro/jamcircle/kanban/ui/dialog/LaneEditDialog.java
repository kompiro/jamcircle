package org.kompiro.jamcircle.kanban.ui.dialog;

import static org.kompiro.jamcircle.kanban.ui.dialog.DialogConstants.KEY_OF_DATA_ID;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.kanban.ui.Messages;
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

	public LaneEditDialog(Shell parentShell,String status,String script,ScriptTypes type) {
		super(parentShell);
		this.status = status;
		this.script = script;
		this.type = type;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.LaneEditDialog_title);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Group statusGroup = new Group(parent,SWT.None);
		statusGroup.setText(Messages.LaneEditDialog_status_label);
		statusGroup.setLayout(new GridLayout());
		
		statusText = new Text(statusGroup,SWT.BORDER);
		statusText.setData(KEY_OF_DATA_ID,ID_STATUS);
		if(status != null){
			statusText.setText(status);
		}
		statusText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				LaneEditDialog.this.status = LaneEditDialog.this.statusText.getText();
			}
		});
		
		GridDataFactory.fillDefaults().grab(true,true).applyTo(statusGroup);
		GridDataFactory.fillDefaults().grab(true,true).hint(400,20).applyTo(statusText);
				

		Group scriptGroup = new Group(parent,SWT.None);
		scriptGroup.setText(Messages.LaneEditDialog_script_label);
		scriptGroup.setLayout(new GridLayout());
		scriptText = new Text(scriptGroup,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
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

		scriptTypeCombo = new ComboViewer(scriptGroup);
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
		return super.createContents(parent);
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
		
}
