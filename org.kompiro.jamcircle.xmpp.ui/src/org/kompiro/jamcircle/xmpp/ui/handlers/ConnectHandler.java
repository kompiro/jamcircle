package org.kompiro.jamcircle.xmpp.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jivesoftware.smack.XMPPException;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.kompiro.jamcircle.xmpp.service.XMPPSettings;
import org.kompiro.jamcircle.xmpp.service.XMPPSettings.Setting;
import org.kompiro.jamcircle.xmpp.ui.*;

public class ConnectHandler extends AbstractHandler {
	private String host;
	private String resource;
	private String serviceName;
	private String username;
	private String password;
	private int port = 5222;
	public class ConnectionWizard extends Wizard {
		
		public ConnectionWizard(){
			addPage(new ConnectionSettingPage());
			setNeedsProgressMonitor(true);
		}

		@Override
		public boolean performFinish() {
			try {
				getContainer().run(false, false, new IRunnableWithProgress(){

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException {
						monitor.beginTask(Messages.ConnectHandler_connecting_message, 100);
						try {
							createConnection(monitor,host, resource,serviceName, username, password,port);
						} catch (XMPPException e) {
							throw new InvocationTargetException(e);
						}
						monitor.done();
					}
					
				});
			} catch (Exception e) {
				MessageDialog.openError(getShell(), Messages.ConnectHandler_connecting_error_message, e.getCause().getMessage());
				return false;
			}
			return true;
		}

	}
	public class ConnectionSettingPage extends WizardPage {
		
		private static final String INIT_PORT_NO = "5222"; //$NON-NLS-1$
		private static final String DOMAIN_OF_JABBER_ORG = "jabber.org"; //$NON-NLS-1$
		private static final String SERVICE_OF_GMAIL_COM = "gmail.com"; //$NON-NLS-1$
		private static final String DOMAIN_TALK_GOOGLE_COM = "talk.google.com"; //$NON-NLS-1$
		private static final String PAGE_NAME_CONNECTION_SETTING = "ConnectionSetting"; //$NON-NLS-1$
		private final String CONNECT_TO_XMPP_OTHER = Messages.ConnectHandler_xmpp_type;
		private final String CONNECT_TO_JABBER_ORG = Messages.ConnectHandler_jabber_org_type;
		private final String CONNECT_TO_GMAIL_SERVER = Messages.ConnectHandler_gtalk_type;
		private XMPPSettings settings;
		private Text serviceNameText;
		private Text portText;
		private Text hostText;
		private Text resourceText;
		private Text usernameText;
		private Text passwordText;
		private Button deleteButton;
		private Button serviceCheck;
		private CCombo settingCombo;
		private CCombo protocolCombo;

		public ConnectionSettingPage() {
			super(PAGE_NAME_CONNECTION_SETTING);
			setTitle(Messages.ConnectHandler_connecting_setting_title);
			setMessage(Messages.ConnectHandler_connecting_setting_message);
			settings = getService().getSettings();
		}

		public void createControl(Composite parent) {
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new GridLayout());
			createSettingGroup(comp);
			createLocationGroup(comp);
			createAuthenticationGroup(comp);
			if(settings.size() != 0){
				Setting setting = settings.get(0);
				hostText.setText(setting.getHost());
				portText.setText(String.valueOf(setting.getPort()));
				serviceNameText.setText(setting.getServiceName());
				usernameText.setText(setting.getUsername());
				passwordText.setText(setting.getPassword());
			}
			setControl(comp);
		}

		private void createLocationGroup(Composite comp) {
			Group locationGroup = new Group(comp, SWT.NONE);
			locationGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			locationGroup.setLayout(new GridLayout(3, false));
			locationGroup.setText(Messages.ConnectHandler_location_label);
			
			Label protocolLabel = new Label(locationGroup,SWT.NONE);
			protocolLabel.setText(Messages.ConnectHandler_protocol_label);
			
			protocolCombo = new CCombo(locationGroup,SWT.FLAT|SWT.BORDER);
			protocolCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
			protocolCombo.add(CONNECT_TO_XMPP_OTHER);
			protocolCombo.add(CONNECT_TO_GMAIL_SERVER);
			protocolCombo.add(CONNECT_TO_JABBER_ORG);
			protocolCombo.select(0);
			
			protocolCombo.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					CCombo source = (CCombo)e.getSource();
					String value = source.getText();
					if(CONNECT_TO_GMAIL_SERVER.equals(value)){
						hostText.setText(DOMAIN_TALK_GOOGLE_COM);
						serviceNameText.setText(SERVICE_OF_GMAIL_COM);
						resourceText.setText(XMPPConnectionService.DEFAULT_RESOURCE_NAME);
						disableLocationProperties();
					}else if(CONNECT_TO_JABBER_ORG.equals(value)){
						hostText.setText(DOMAIN_OF_JABBER_ORG);
						serviceNameText.setText(DOMAIN_OF_JABBER_ORG);
						resourceText.setText(XMPPConnectionService.DEFAULT_RESOURCE_NAME);
						disableLocationProperties();						
					}else{
						resourceText.setText(XMPPConnectionService.DEFAULT_RESOURCE_NAME);
						enableLocationPropeties();							
					}
				}

				private void enableLocationPropeties() {
					hostText.setEditable(true);
					portText.setEditable(true);
					serviceCheck.setVisible(true);
					serviceNameText.setEditable(true);
				}

				private void disableLocationProperties() {
					hostText.setEditable(false);
					portText.setEditable(false);
					serviceCheck.setVisible(false);
					serviceNameText.setEditable(false);
				}
			});
			
			Label hostLabel = new Label(locationGroup,SWT.None);
			hostLabel.setText("Host:");
			hostText = new Text(locationGroup, SWT.FLAT | SWT.BORDER);
			hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));

			hostText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					host = ((Text)e.widget).getText();
					serviceNameText.setText(host);
				}
			});
			
			Label portLabel = new Label(locationGroup,SWT.None);
			portLabel.setText("Port:");
			portText = new Text(locationGroup,SWT.BORDER);
			portText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
			portText.setText(INIT_PORT_NO);
			portText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					try{
						port = Integer.valueOf(portText.getText());
						setErrorMessage(null);
					}catch(NumberFormatException ex){
						setErrorMessage("port is number.'" + portText.getText() + "'");
					}
				}
			});
			
			createEmptySpage(locationGroup);
			serviceCheck = new Button(locationGroup,SWT.CHECK);
			Label caucation = new Label(locationGroup,SWT.None);
			caucation.setText(Messages.ConnectHandler_hostname_caution);
			GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(caucation);
			Label serviceNameLabel = new Label(locationGroup,SWT.None);
			serviceNameLabel.setText(Messages.ConnectHandler_service_name_label);
			
			serviceNameText = new Text(locationGroup,SWT.BORDER);
			serviceNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
			serviceNameText.setEditable(false);
			serviceNameText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					serviceName = serviceNameText.getText();
				}
			});
			serviceCheck.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					boolean selection = ((Button)e.widget).getSelection();
					serviceNameText.setEditable(selection);
				};
			});
			Label resourceLabel = new Label(locationGroup,SWT.None);
			resourceLabel.setText(Messages.ConnectHandler_resource_label);
			resourceText = new Text(locationGroup, SWT.FLAT | SWT.BORDER);
			resourceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
			resourceText.setText(XMPPConnectionService.DEFAULT_RESOURCE_NAME);
			resourceText.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					 resource = ((Text)e.widget).getText();
				}
			});
		}

		private void createSettingGroup(Composite comp) {
			Group settingGroup = new Group(comp,SWT.NONE);
			settingGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
			settingGroup.setLayout(new GridLayout(3,false));
			settingGroup .setText(Messages.ConnectHandler_setting_label);
			settingCombo = new CCombo(settingGroup,SWT.FLAT|SWT.BORDER);
			for (XMPPSettings.Setting setting : settings) {
				settingCombo.add(setting.getSettingName());
				settingCombo.setData(setting.getSettingName(), setting);
			}
			settingCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					resetSetting();
				}

			});
			settingCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));

			deleteButton = new Button(settingGroup, SWT.PUSH);
			deleteButton.setText(Messages.ConnectHandler_delete_setting_label);
			deleteButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					XMPPSettings.Setting setting = (Setting) settingCombo.getData(settingCombo.getText());
					if(settingCombo.getItemCount() == 0) return;
					settingCombo.setData(settingCombo.getText(), null);
					settingCombo.remove(settingCombo.getText());
					settings.remove(setting);
					settings.storeSttings();
					if(settings.size() == 0){
						disabledSettingSection();
					}
					settingCombo.select(0);
					resetSetting();
				}

			});
			GridDataFactory.fillDefaults().applyTo(deleteButton);

			if(settings.size() != 0){
				settingCombo.select(0);
			}else{
				disabledSettingSection();
			}
		}

		private void disabledSettingSection() {
			settingCombo.setEditable(false);
			settingCombo.setEnabled(false);
			deleteButton.setEnabled(false);
		}

		private void resetSetting() {
			XMPPSettings.Setting setting = (Setting) settingCombo.getData(settingCombo.getText());
			if(setting != null){
				hostText.setText(setting.getHost());
				portText.setText(String.valueOf(setting.getPort()));
				serviceNameText.setText(setting.getServiceName());
				usernameText.setText(setting.getUsername());
				passwordText.setText(setting.getPassword());
			}else{
				protocolCombo.select(0);
			}
		};


		private void createEmptySpage(Group locationGroup) {
			new Label(locationGroup,SWT.None);
		}
		
		private void createAuthenticationGroup(Composite comp) {
			Group authGroup = new Group(comp, SWT.NONE);
			authGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			authGroup.setLayout(new GridLayout(3, false));
			authGroup.setText(Messages.ConnectHandler_authentication_label);
			Label usernameLabel = new Label(authGroup,SWT.None);
			usernameLabel.setText(Messages.ConnectHandler_username_label);
			usernameText = new Text(authGroup,SWT.BORDER);
			usernameText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					username = usernameText.getText();
				}
			});
			usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
			Label passwordLabel = new Label(authGroup,SWT.None);
			passwordLabel.setText(Messages.ConnectHandler_password_label);
			passwordText = new Text(authGroup,SWT.BORDER|SWT.PASSWORD);
			passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
			passwordText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					password = passwordText.getText();
				}
			});
		}
		
		@Override
		public void dispose() {
			super.dispose();
			serviceNameText.dispose();
			portText.dispose();
			hostText.dispose();
			usernameText.dispose();
			passwordText.dispose();
			
		}

	}

	public ConnectHandler() {
		setBaseEnabled(isNotConnecting());
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		setBaseEnabled(isNotConnecting());
	}

	private boolean isNotConnecting() {
		return !getService().isConnecting();
	}


	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		WizardDialog dialog = new WizardDialog(window.getShell(),new ConnectionWizard()){
			@Override
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				ImageRegistry imageRegistry = XMPPUIActivator.getDefault().getImageRegistry();
				Image image = imageRegistry.get(XMPPImageConstants.CONNECT.toString());
				newShell.setImage(image);
				newShell.setText(Messages.ConnectHandler_connecting_settings_shell_title);
			}
		};
		dialog.open();
		return null;
	}

	private void createConnection(IProgressMonitor monitor,String host, String resource, String serviceName,String username, String password, int port) throws XMPPException {
		getService().login(monitor, host, resource, serviceName, port, username, password);
	}

	private XMPPConnectionService getService() {
		return XMPPUIActivator.getDefault().getConnectionService();
	}

}
