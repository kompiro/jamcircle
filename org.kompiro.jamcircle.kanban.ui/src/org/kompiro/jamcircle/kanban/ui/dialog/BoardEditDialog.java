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

public class BoardEditDialog extends Dialog{

	static final String ID_SCRIPT_TYPE = "script_type";
	static final String ID_SCRIPT = "script";
	static final String ID_TITLE = "title";

	private Text titleText;
	private Text scriptText;
	private String title;
	private String script;
	private ScriptTypes type;
	private ComboViewer scriptTypeCombo;

	public BoardEditDialog(Shell parentShell,String title,String script,ScriptTypes type) {
		super(parentShell);
		this.title = title;
		this.script = script;
		this.type = type;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.BoardEditDialog_title);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Group statusGroup = new Group(parent,SWT.None);
		statusGroup.setText(Messages.BoardEditDialog_board_title_label);
		statusGroup.setLayout(new GridLayout());
		
		titleText = new Text(statusGroup,SWT.BORDER);
		if(title != null){
			titleText.setText(title);
		}
		titleText.setData(KEY_OF_DATA_ID, ID_TITLE);
		titleText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				BoardEditDialog.this.title = BoardEditDialog.this.titleText.getText();
			}
		});
		GridDataFactory.fillDefaults().grab(true,true).applyTo(statusGroup);
		GridDataFactory.fillDefaults().grab(true,true).hint(400,20).applyTo(titleText);
				

		Group scriptGroup = new Group(parent,SWT.None);
		scriptGroup.setText(Messages.BoardEditDialog_board_script_label);
		scriptGroup.setLayout(new GridLayout());
		scriptText = new Text(scriptGroup,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		scriptText.setData(KEY_OF_DATA_ID, ID_SCRIPT);
		if(script != null){
			scriptText.setText(script);
		}
		scriptText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				BoardEditDialog.this.script = BoardEditDialog.this.scriptText.getText();
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
				BoardEditDialog.this.type = (ScriptTypes)((StructuredSelection)sel).getFirstElement();
			}
		});

		
		return super.createContents(parent);
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getScript(){
		return script;
	}

	public ScriptTypes getScriptType() {
		return type;
	}
	
}
