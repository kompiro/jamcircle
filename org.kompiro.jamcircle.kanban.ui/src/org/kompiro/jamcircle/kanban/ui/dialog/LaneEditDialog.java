package org.kompiro.jamcircle.kanban.ui.dialog;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.kompiro.jamcircle.scripting.ScriptTypes;

public class LaneEditDialog extends Dialog{

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
		newShell.setText("Lane Edit");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Group statusGroup = new Group(parent,SWT.None);
		statusGroup.setText("status");
		statusGroup.setLayout(new GridLayout());
		
		statusText = new Text(statusGroup,SWT.BORDER);
		if(status != null){
			statusText.setText(status);
		}
		statusText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				LaneEditDialog.this.status = LaneEditDialog.this.statusText.getText();
			}
		});
		GridDataFactory.fillDefaults().grab(true,true).applyTo(statusGroup);
		GridDataFactory.fillDefaults().grab(true,true).hint(400,20).applyTo(statusText);
				

		Group scriptGroup = new Group(parent,SWT.None);
		scriptGroup.setText("script");
		scriptGroup.setLayout(new GridLayout());
		scriptText = new Text(scriptGroup,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		if(script != null){
			scriptText.setText(script);
		}
		scriptText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				LaneEditDialog.this.script = LaneEditDialog.this.scriptText.getText();
			}
		});
		GridDataFactory.fillDefaults().hint(400, 200).grab(true,true).applyTo(scriptText);

		scriptTypeCombo = new ComboViewer(scriptGroup);
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
	
	public String getStatusText(){
		return status;
	}
	
	public String getScriptText(){
		return script;
	}
	
	public ScriptTypes getScriptType(){
		return type;
	}
	
	
	public static void main(String[] args) {
		Shell shell = new Shell();
		LaneEditDialog dialog = new LaneEditDialog(shell,
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				ScriptTypes.JRuby);
		dialog.open();
	}
	
}
