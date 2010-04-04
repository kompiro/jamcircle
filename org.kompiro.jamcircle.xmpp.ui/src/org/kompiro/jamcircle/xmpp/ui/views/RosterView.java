package org.kompiro.jamcircle.xmpp.ui.views;

import java.util.*;
import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.kompiro.jamcircle.xmpp.XMPPStatusHandler;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;
import org.kompiro.jamcircle.xmpp.ui.*;


public class RosterView extends ViewPart implements RosterListener, XMPPLoginListener {
	
	private static final String POPUP_MENU = "#PopupMenu"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$

	public final class AddUserAction extends Action {
		
		private RosterEntry entry;

		public AddUserAction(){
			super(Messages.RosterView_enable_message);
			setImageDescriptor(getImageRegistry().getDescriptor(XMPPImageConstants.USER_ORANGE.toString()));
		}
				
		@Override
		public void run() {
			getConnectionService().addUser(entry.getUser());
		}
		
		public void setEntry(RosterEntry entry){
			this.entry = entry;
		}
	}

	
	public final class DeleteUserAction extends Action {
		
		private RosterEntry entry;

		public DeleteUserAction(){
			super(Messages.RosterView_disable_message);
			setImageDescriptor(getImageRegistry().getDescriptor(XMPPImageConstants.USER_GRAY.toString()));
		}
		
		public void setEntry(RosterEntry entry) {
			this.entry = entry;
		}
				
		@Override
		public void run() {
			getConnectionService().deleteUser(entry.getUser());
		}
	}


	private final class DeleteEntryAction extends Action {
		
		public DeleteEntryAction() {
			setText(Messages.RosterView_delete_text);
			setToolTipText(Messages.RosterView_delete_tooltip);
			setImageDescriptor(getImageRegistry().getDescriptor(XMPPImageConstants.USER_DELETE.toString()));
		}
				
		public void run() {
			Roster roster = getConnection().getRoster();
			ISelection selection = viewer.getSelection();
			if (selection instanceof StructuredSelection) {
				StructuredSelection sel = (StructuredSelection) selection;
				for(Object target : sel.toArray()){
					if (target instanceof RosterEntry) {
						RosterEntry entry = (RosterEntry) target;
						try {
						Shell shell = getShell();
						String message = String.format(Messages.RosterView_remove_entity_message, entry.getUser());
						MessageDialog.openConfirm(shell,
								Messages.RosterView_remove_title,
								message);
							roster.removeEntry(entry);
						} catch (XMPPException e) {
							XMPPStatusHandler.fail(e, Messages.RosterView_error_message);
						}
						
					}
				}
			}
		}
	}


	private class AddEntryPage extends WizardPage {

		private String user;
		private String groupName;
		private String nickName;

		protected AddEntryPage(String pageName) {
			super(pageName);
			setTitle(Messages.RosterView_add_entry);
			setDescription(Messages.RosterView_add_entry_description);
		}

		public void createControl(Composite parent) {
			Composite comp = new Composite(parent,SWT.None);
			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(comp);

			Label userLabel = new Label(comp,SWT.None);
			userLabel.setText(Messages.RosterView_user_label);
			Text userText = new Text(comp,SWT.BORDER);
			userText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					user = ((Text)e.widget).getText();
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);
			Label nickNameLabel = new Label(comp,SWT.None);
			nickNameLabel.setText(Messages.RosterView_nickname_label);
			Text nickNameText = new Text(comp,SWT.BORDER);
			nickNameText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					nickName = ((Text)e.widget).getText();
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(nickNameText);
			Label groupLabel = new Label(comp,SWT.None);
			groupLabel.setText(Messages.RosterView_group_label);
			CCombo groupCombo = new CCombo(comp,SWT.BORDER);
			
			Collection<RosterGroup> groups = getRosterGroups();
			for(RosterGroup group : groups){
				groupCombo.add(group.getName());
			}
			groupCombo.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					groupName = ((CCombo)e.widget).getText();
				}
			});
			groupCombo.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					groupName = ((CCombo)e.widget).getText();
				}
			});
			GridDataFactory.fillDefaults().grab(true, false).applyTo(groupCombo);
			setControl(parent);
		}

		private Collection<RosterGroup> getRosterGroups() {
			XMPPConnection connection = getConnection();
			if(connection == null) return null;
			Roster roster = connection.getRoster();
			if(roster == null) return null;
			return roster.getGroups();
		}

		public String getGroupName() {
			return this.groupName;
		}

		public String getUser() {
			return this.user;
		}

		public String getNickName() {
			return this.nickName;
		}

	}


	private class AddEntryWizard extends Wizard {

		private AddEntryPage page;

		AddEntryWizard(){
			page = new AddEntryPage(Messages.RosterView_add_entry_page);
			addPage(page);
		}
		
		@Override
		public boolean performFinish() {
			Roster roster = getConnection().getRoster();
			String user = page.getUser();
			if(roster.contains(user)){
				String message = String.format(Messages.RosterView_already_exist_entry_error_message,user);
				MessageDialog.openWarning(getShell(), Messages.RosterView_already_exist_error_title,message);
				return false;
			}
			String groupName = page.getGroupName();
			String[] joinToGroups =null;
			if(groupName != null){
				RosterGroup group = roster.getGroup(groupName);
				if(group == null){
					group = roster.createGroup(groupName);
				}
				joinToGroups = new String[]{groupName};
			}
			try {
				roster.createEntry(user, page.getNickName(), joinToGroups);
				return true;
			} catch (XMPPException e) {
				XMPPStatusHandler.fail(e, Messages.RosterView_error_message);
				return false;
			}
		}

	}

	
	private final class AddEntryAction extends Action {
		
		public AddEntryAction(){
			setText(Messages.RosterView_new_entry_text);
			setToolTipText(Messages.RosterView_new_entry_tooltip);
			setImageDescriptor(getImageRegistry().getDescriptor(XMPPImageConstants.USER_ADD.toString()));
		}

		public void run() {
			Shell shell = getShell();
			WizardDialog dialog = new WizardDialog(shell,new AddEntryWizard());
			dialog.open();
		}
	}
	
	private class RosterStatusWrapper{
		private RosterEntry entry;
		private Presence presence;

		protected RosterStatusWrapper(RosterEntry entry,Presence presence){
			this.entry = entry;
			this.presence = presence;
		}
	}

	private class RosterContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider{

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof XMPPConnection) {
				XMPPConnection connection = (XMPPConnection)parent;
				return getChildren(connection.getRoster());
			}
			return getChildren(parent);
		}
		
		public Object getParent(Object child) {
			if (child instanceof RosterEntry) {
				return ((RosterEntry)child).getGroups();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if(parent == null){
				return new Object[]{};
			}
			if (parent instanceof Roster) {
				Roster roster = (Roster)parent;
				Set<Object> result = new LinkedHashSet<Object>();
				result.addAll(roster.getGroups());
				Collection<RosterEntry> unfiledEntries = roster.getUnfiledEntries();
				for(RosterEntry entry:unfiledEntries){
					RosterStatusWrapper rosterStatusWrapper = getRosterStatusWrapper(entry);
					if(rosterStatusWrapper != null){
						result.add(rosterStatusWrapper);
					}else{
						result.add(entry);
					}					
				}
				return result.toArray();
			}
			if (parent instanceof RosterGroup) {
				RosterGroup group = (RosterGroup)parent;
				List<Object> result = new ArrayList<Object>();
				for(RosterEntry entry:group.getEntries()){
					RosterStatusWrapper rosterStatusWrapper = getRosterStatusWrapper(entry);
					if(rosterStatusWrapper != null){
						result.add(rosterStatusWrapper);
					}else{
						result.add(entry);
					}
				}
				return result.toArray();
			}
			if(parent instanceof RosterEntry){
				RosterEntry entry = (RosterEntry) parent;
				
				Roster roster = getConnection().getRoster();
				String user = entry.getUser();
				Iterator<Presence> presences = roster.getPresences(user);
				List<Presence> result = new ArrayList<Presence>();
				while(presences.hasNext()){
					result.add(presences.next());
				}
				if(result.size() != 1){
					return result.toArray();
				}
			}
			return null;
		}
		
		private RosterStatusWrapper getRosterStatusWrapper(RosterEntry entry){
			Roster roster = getConnection().getRoster();
			Presence presence = roster.getPresence(entry.getUser());
			if( ! presence.isAvailable()){
				return new RosterStatusWrapper(entry,presence);
			}
			String user = entry.getUser();
			Iterator<Presence> presences = roster.getPresences(user);
			List<Presence> list = new ArrayList<Presence>();
			while(presences.hasNext()){
				list.add(presences.next());
			}
			if(list.size() == 1){
				return new RosterStatusWrapper(entry,presence);
			}
			return null;
		}
		
		public boolean hasChildren(Object parent) {
			if (parent instanceof XMPPConnection){
				XMPPConnection connection = (XMPPConnection) parent;
				return connection.isConnected() && connection.getRoster() != null;
			}
			if(parent instanceof RosterEntry){
				RosterEntry entry = (RosterEntry)parent;
				Presence presence = getConnection().getRoster().getPresence(entry.getUser());
				boolean available = presence.isAvailable();
				return available;
			}
			
			if (parent instanceof Roster)
				return ((Roster)parent).getEntryCount() != 0;
			if (parent instanceof RosterGroup)
				return ((RosterGroup)parent).getEntryCount() != 0;
			return false;
		}
	}
	
	private class RosterLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			ImageRegistry imageRegistry = getImageRegistry();
			
			if (element instanceof RosterGroup){
				switch(columnIndex){
				case 0:
					return imageRegistry.get(XMPPImageConstants.GROUP.toString());
				default:
					return null;
				}
			}
			if(element instanceof RosterStatusWrapper){
				Presence presence = ((RosterStatusWrapper)element).presence;
				switch(columnIndex){
				case 0:
					return imageRegistry.get(XMPPImageConstants.USER.toString());
				case 1:
					if(presence.isAvailable()){
						return imageRegistry.get(XMPPImageConstants.STATUS_ONLINE.toString());
					}else if(presence.isAvailable() && Presence.Mode.dnd.name().equals(presence.getMode().name())){
						return imageRegistry.get(XMPPImageConstants.STATUS_BUSY.toString());
					}else if(presence.isAway()){
						return imageRegistry.get(XMPPImageConstants.STATUS_AWAY.toString());
					}else {
						return imageRegistry.get(XMPPImageConstants.STATUS_OFFLINE.toString());						
					}
				default:
					return null;
				}
			}
			if(element instanceof RosterEntry){
				if(columnIndex == 0){
					return imageRegistry.get(XMPPImageConstants.USER.toString());
				}
			}
			if(element instanceof Presence){
				Presence presence = (Presence) element;
				switch(columnIndex){
				case 1:
					if(presence.isAvailable()){
						return imageRegistry.get(XMPPImageConstants.STATUS_ONLINE.toString());
					}else if(presence.isAvailable() && Presence.Mode.dnd.name().equals(presence.getMode().name())){
						return imageRegistry.get(XMPPImageConstants.STATUS_BUSY.toString());
					}else if(presence.isAway()){
						return imageRegistry.get(XMPPImageConstants.STATUS_AWAY.toString());
					}
				default:
					return null;
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof RosterGroup) {
				RosterGroup group = (RosterGroup) element;
				switch(columnIndex){
				case 0:
					return group.getName();
				default:
					return null;
				}
			}else if(element instanceof RosterStatusWrapper){
				RosterStatusWrapper status = (RosterStatusWrapper) element;
				switch(columnIndex){
				case 0:
					RosterEntry entry = status.entry;
					String name = entry.getName();
					if (name == null || EMPTY.equals(name)){
						name = entry.getUser();
					}
					return name;
				case 1:
					return status.presence.getFrom();						
				case 2:
					return status.presence.getStatus();						
				default:
					return null;
				}
			}
			else if(element instanceof RosterEntry){
				RosterEntry entry = (RosterEntry) element;
				switch(columnIndex){
				case 0:
					String name = entry.getName();
					if (name == null || EMPTY.equals(name))
						name = entry.getUser();
					return name;
				default:
					return null;
				}
			}else if(element instanceof Presence){
				Presence presence = (Presence) element;
				switch(columnIndex){
				case 1:
					return presence.getFrom();
				case 2:
					return presence.getStatus();
				default:
					return null;
				}
			}
			return null;
		}
	}

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action addEntryAction;
	private Action deleteEntryAction;
	private AddUserAction addUserAction;
	private Action doubleClickAction;
	private DeleteUserAction deleteUserAction;

	public RosterView() {
		getConnectionService().addXMPPLoginListener(this);
	}

	@Override
	public void dispose() {
		getConnectionService().removeXMPPLoginListener(this);
	}
	
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new RosterContentProvider());
		viewer.setLabelProvider(new RosterLabelProvider());
		makeColumns();
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		initialize(getConnection());
	}


	private void makeColumns() {
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		TreeColumn username = new TreeColumn(tree,SWT.NONE);
		username.setResizable(true);
		username.setWidth(200);
		username.setText(Messages.RosterView_user_name_label);
		TreeColumn from = new TreeColumn(tree,SWT.NONE);
		from.setResizable(true);
		from.setWidth(200);
		from.setText(Messages.RosterView_from_label);
		TreeColumn precenseStatus = new TreeColumn(tree,SWT.NONE);
		precenseStatus.setResizable(true);
		precenseStatus.setWidth(200);
		precenseStatus.setText(Messages.RosterView_status_label);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager(POPUP_MENU); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RosterView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}


	private void fillContextMenu(IMenuManager manager) {
		manager.add(addEntryAction);
		ISelection sel = viewer.getSelection();
		if (sel instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) sel;
			if(selection.size() == 1){
				Object obj = selection.getFirstElement();
				if (obj instanceof RosterEntry) {
					RosterEntry entry = (RosterEntry) obj;
					addEntryActions(manager, entry);
				}else if(obj instanceof RosterStatusWrapper){
					RosterEntry entry = ((RosterStatusWrapper) obj).entry;
					addEntryActions(manager, entry);					
				}
			}
		}

		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void addEntryActions(IMenuManager manager, RosterEntry entry) {
		if(getConnectionService().hasUser(entry.getUser())){
			manager.add(deleteUserAction);
			deleteUserAction.setEntry(entry);
		}else{
			manager.add(addUserAction);
			addUserAction.setEntry(entry);
		}
		manager.add(deleteEntryAction);
	}
	
	private void makeActions() {
		addEntryAction = new AddEntryAction();
		deleteEntryAction = new DeleteEntryAction();
		addUserAction = new AddUserAction();
		deleteUserAction = new DeleteUserAction();
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage(String.format(Messages.RosterView_double_click_message, obj.toString()));
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			Messages.RosterView_roasters_title,
			message);
	}

	public void setFocus() {
	}
	
	public void entriesAdded(Collection<String> addresses) {
		updateEntry();
	}
	public void entriesDeleted(Collection<String> addresses) {
		updateEntry();
	}

	public void entriesUpdated(Collection<String> addresses) {
		updateEntry();
	}

	public void presenceChanged(Presence presence) {
		updateEntry();
	}
	
	public void updateEntry(){
		Runnable runnable = new Runnable(){
			public void run() {
				viewer.setInput(getConnection());
			}
		};
		getDisplay().asyncExec(runnable);
	}

	public void afterLoggedIn(XMPPConnection connection) {
		initialize(connection);
	}

	public void beforeLoggedOut(XMPPConnection connection) {
		connection.getRoster().removeRosterListener(this);
		getDisplay().asyncExec(new Runnable(){
			public void run() {
				IStatusLineManager manager = getStatusLineManager();
				Image disconnectIcon = getImageRegistry().get(XMPPImageConstants.DISCONNECT.toString());
				manager.setMessage(disconnectIcon,EMPTY);
				viewer.setInput(null);
			}
		});
	}

	
	private void initialize(final XMPPConnection connection) {
		if(connection == null) return;
		connection.getRoster().addRosterListener(this);
		getDisplay().asyncExec(new Runnable(){
			public void run() {
				IStatusLineManager manager = getStatusLineManager();
				Image connectIcon = getImageRegistry().get(XMPPImageConstants.CONNECT.toString());
				manager.setMessage(connectIcon,connection.getUser());
				viewer.setInput(connection);
			}
		});
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	private IStatusLineManager getStatusLineManager() {
		IActionBars bars = getViewSite().getActionBars();
		IStatusLineManager manager = bars.getStatusLineManager();
		return manager;
	}

	private ImageRegistry getImageRegistry() {
		return XMPPUIActivator.getDefault().getImageRegistry();
	}

	private Shell getShell() {
		return getViewSite().getWorkbenchWindow().getShell();
	}
	
	private XMPPConnection getConnection(){
		XMPPConnectionService connectionService = getConnectionService();
		if(connectionService == null) return null;
		return connectionService.getConnection();
	}

	private XMPPConnectionService getConnectionService() {
		return XMPPUIActivator.getDefault().getConnectionService();
	}


}