package org.kompiro.jamcircle.storage.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.service.*;
import org.kompiro.jamcircle.storage.ui.Messages;
import org.kompiro.jamcircle.storage.ui.StorageUIActivator;

public class StorageSettingWizard extends Wizard {

	private String uri;
	private String mode = StorageService.ConnectionMode.FILE.toString();
	private String username;
	private String password;
	private StorageSettingPage page;
	
	private class StorageSettingPage extends WizardPage {

		private static final String PAGE_NAME = "StorageSetting"; //$NON-NLS-1$
		private CCombo uriCombo;
		private Text usernameText;
		private Text passwordText;
		private Button browseButton;
		private Button deleteButton;

		protected StorageSettingPage() {
			super(PAGE_NAME);
			setTitle(Messages.StorageSettingWizard_wizard_title);
			setDescription(Messages.StorageSettingWizard_wizard_description);
		}

		public void createControl(Composite parent) {
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new GridLayout());
			
			Group currentGroup = new Group(comp,SWT.None); 
			currentGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			currentGroup.setLayout(new FillLayout());
			currentGroup.setText(Messages.StorageSettingWizard_current_path_label);
			Text storePathText = new Text(currentGroup,SWT.BORDER|SWT.READ_ONLY);
			storePathText.setText(getStoreRoot());
			
			Group pathGroup = new Group(comp,SWT.None);
			pathGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			pathGroup.setText(Messages.StorageSettingWizard_store_path_label);
			pathGroup.setLayout(new GridLayout(2,false));

			uriCombo = new CCombo(pathGroup,SWT.BORDER);
			StorageSettings settings = getStorageSettings();
			for(StorageSetting setting : settings){
				String uriText = setting.getUri();
				uriCombo.add(uriText);
				uriCombo.setData(uriText, setting);
			}
			uriCombo.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					uri = uriCombo.getText();
				}
			});
			GridDataFactory.swtDefaults().align(SWT.FILL,SWT.CENTER).grab(true, false).applyTo(uriCombo);
		
			browseButton = new Button(pathGroup,SWT.BORDER);
			browseButton.setText(Messages.StorageSettingWizard_browse_label);
			browseButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					String filePath = dialog.open();
					if(filePath != null) {
						File file = new File(filePath);
						if(!file.isDirectory()){
							String message = String.format(Messages.StorageSettingWizard_not_found_directory_error,file.getAbsolutePath());
							MessageDialog.openError(getShell(), Messages.StorageSettingWizard_error_title, message);
							return;
						}
						uriCombo.setText(filePath);
					}

				}
			});
			
			createEmptyCell(pathGroup);
			deleteButton = new Button(pathGroup,SWT.BORDER);
			deleteButton.setText(Messages.StorageSettingWizard_delete_label);
			deleteButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					String uriText = uriCombo.getText();
					StorageSetting setting = (StorageSetting) uriCombo.getData(uriText);
					if(uriCombo.getItemCount() == 0) return;
					uriCombo.setData(uriText,null);
					uriCombo.remove(uriText);
					getStorageSettings().remove(setting);
					getStorageSettings().storeSttings();
					if(getStorageSettings().size() == 0){
						deleteButton.setEnabled(false);
					}
					resetSetting();
				}
			});
			
			Group settingGroup = new Group(comp,SWT.None);
			settingGroup.setText(Messages.StorageSettingWizard_auth_label);
			settingGroup.setLayout(new GridLayout(2,false));
			settingGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			Label usernameLabel = new Label(settingGroup,SWT.NONE);
			usernameLabel.setText(Messages.StorageSettingWizard_user_label);
			usernameText = new Text(settingGroup,SWT.BORDER);
			usernameText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					username = usernameText.getText();
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(usernameText);

			Label passwordLabel = new Label(settingGroup,SWT.NONE);
			passwordLabel.setText(Messages.StorageSettingWizard_password_label);
			passwordText = new Text(settingGroup,SWT.BORDER|SWT.PASSWORD);
			passwordText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					Text passwordText = (Text) e.widget;
					password = passwordText.getText();
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);
			
			uriCombo.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					resetSetting();
				}
			});
			if(uriCombo.getItemCount() != 0){
				uriCombo.select(0);
			}
			setControl(parent);
		}

		private void createEmptyCell(Group pathGroup) {
			new Label(pathGroup,SWT.None);
		}
		
		private void resetSetting() {
			StorageSetting setting = (StorageSetting) uriCombo.getData(uriCombo.getText());
			if(setting != null){
				if(!usernameText.isDisposed()) usernameText.setText(setting.getUsername());
				if(!passwordText.isDisposed()) passwordText.setText(setting.getPassword());
			}
		};
	}

	
	public StorageSettingWizard() {
		page = new StorageSettingPage();
		setNeedsProgressMonitor(true);
		addPage(page);
		setWindowTitle(Messages.StorageSettingWizard_window_title);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress(){

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					StorageService service = getStorageService();
					try {
						service.loadStorage(new StorageSetting(-1,uri, mode, username, password),monitor);
					} catch (StorageConnectException e) {
						throw new InvocationTargetException(e);
					}
				}
				
			});
		} catch (InvocationTargetException e) {
			page.setErrorMessage(e.getCause().getMessage());
			return false;
		} catch (InterruptedException e) {
			page.setErrorMessage(e.getCause().getMessage());
			return false;
		}
		return true;
	}

	private StorageService getStorageService() {
		StorageUIActivator activator = StorageUIActivator.getDefault();
		if(activator == null) return null;
		StorageService service = activator.getStorageService();
		return service;
	}
	
	private StorageSettings getStorageSettings(){
		StorageService storageService = getStorageService();
		if(storageService == null) return new StorageSettings();
		return storageService.getSettings();
	}

	private String getStoreRoot() {
		StorageService storageService = getStorageService();
		if(storageService == null) return ""; //$NON-NLS-1$
		return storageService.getFileService().getStoreRoot();
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		WizardDialog dialog = new WizardDialog(shell,new StorageSettingWizard());
		dialog.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	

}
