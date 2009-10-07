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

public class BoardEditDialog extends Dialog{

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
		newShell.setText("Board Edit");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Group statusGroup = new Group(parent,SWT.None);
		statusGroup.setText("Title");
		statusGroup.setLayout(new GridLayout());
		
		titleText = new Text(statusGroup,SWT.BORDER);
		if(title != null){
			titleText.setText(title);
		}
		titleText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				BoardEditDialog.this.title = BoardEditDialog.this.titleText.getText();
			}
		});
		GridDataFactory.fillDefaults().grab(true,true).applyTo(statusGroup);
		GridDataFactory.fillDefaults().grab(true,true).hint(400,20).applyTo(titleText);
				

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
				BoardEditDialog.this.script = BoardEditDialog.this.scriptText.getText();
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
	
	public static void main(String[] args) {
		Shell shell = new Shell();
		BoardEditDialog dialog = new BoardEditDialog(shell,
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
				"testtesttesttesttesttesttesttesttesttesttesttesttesttesttest",
				ScriptTypes.JRuby);
		dialog.open();
	}

	
}
