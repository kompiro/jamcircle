package org.kompiro.jamcircle.xmpp.kanban.ui.internal.listener;

import java.util.*;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.kompiro.jamcircle.kanban.model.Board;
import org.kompiro.jamcircle.kanban.model.User;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.xmpp.XMPPStatusHandler;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.BridgeXMPPActivator;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.util.XMPPUtil;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;

public class KanbanXMPPLoginListener implements XMPPLoginListener, IPartListener {

	private RosterListener rosterListener;
	private CardReceiveListener cardSendListener = new CardReceiveListener(this);
	private FileTransferManager manager;
	private CardReceiveFileTransferListener cardReceiveFileTransferListener;
	private KanbanView view;
	private KanbanService kanbanService;

	public KanbanXMPPLoginListener() {
		if(PlatformUI.isWorkbenchRunning() == false){
			return;
		}
		setKanbanView(WorkbenchUtil.findKanbanView());
		IPartService partService = WorkbenchUtil.getWorkbenchWindow().getPartService();
		partService.addPartListener(this);
		kanbanService = BridgeXMPPActivator.getDefault().getKanbanService();
	}
	
	private ConnectionListener connectionListener = new ConnectionListener() {

		public void connectionClosed() {
			getKanbanService().changeCurrentUser(null);
		}

		public void connectionClosedOnError(final Exception e) {
			XMPPStatusHandler.fail(e, "exception is occured when XMPP connection closing.");
		}

		public void reconnectingIn(int seconds) {
		}

		public void reconnectionFailed(Exception e) {
		}

		public void reconnectionSuccessful() {
		}
	};
	
	private Map<String,UserModel> userModelMap = new HashMap<String, UserModel>();
	
	private final class RosterListnerForUsers implements RosterListener {
		
		public RosterListnerForUsers() {
		}

		public void entriesAdded(Collection<String> addresses) {
		}

		public void entriesDeleted(Collection<String> addresses) {
		}

		public void entriesUpdated(Collection<String> addresses) {
		}

		public void presenceChanged(final Presence presence) {
			Runnable runnable = new Runnable() {
				public void run() {
					String from = presence.getFrom();
					from = XMPPUtil.getRemovedResourceUser(from);
					UserModel user = userModelMap.get(from);
					if (user != null) {
						user.setPresence(presence);
					}
				}
			};
			getDisplay().asyncExec(runnable);
		}
	}
	
	public void afterLoggedIn(XMPPConnection connection) {
		connection.addConnectionListener(connectionListener);
		PacketListener packetListener = new PacketListener() {
			public void processPacket(Packet packet) {
				Presence presence = (Presence) packet;
				String from = presence.getFrom();
				from = XMPPUtil.getRemovedResourceUser(from);
				UserModel userModel = userModelMap.get(from);
				if(userModel == null) return;
				userModel.setPresence(presence);
			}
		};
		PacketFilter packetFilter = new PacketTypeFilter(Presence.class);
		connection.addPacketListener(packetListener, packetFilter);
		final Roster roster = connection.getRoster();
		rosterListener = new RosterListnerForUsers();
		roster.addRosterListener(rosterListener);
		connection.getChatManager().addChatListener(cardSendListener);
		Map<String,User> userMap = new HashMap<String, User>();
		User[] userList = getKanbanService().findUsersOnBoard();
		if(isUserListNotEmpty(userList)){
			for(User user : userList){
				String key = user.getUserId();
				if(!userMap.containsKey(key)){
					userMap.put(key, user);
				}
			}
		}
		clearUsers();
		BoardModel boardModel = getBoardModel();
		userModelMap.clear();
		for(User user:userMap.values()){
			String userId = user.getUserId();
			Presence presence = roster.getPresence(userId);
			UserModel userModel = new UserModel(roster.getEntry(userId),presence,user);
			userModelMap.put(userId, userModel);
			boardModel.addIcon(userModel);
		}
		setCardReceiveFileTransferManager(connection);
	}

	private boolean isUserListNotEmpty(User[] userList) {
		return userList != null && userList.length != 0;
	}

	public void beforeLoggedOut(XMPPConnection connection) {
		clearUsers();
	}

	private void clearUsers() {
		BoardModel boardModel = getBoardModel();
		for(UserModel model: userModelMap.values()){
			boardModel.removeIcon(model);
		}
	}

	void setKanbanService(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}
	
	KanbanService getKanbanService() {
		return kanbanService;
	}

	private void setCardReceiveFileTransferManager(XMPPConnection connection) {
		manager = new FileTransferManager(connection);
		manager.removeFileTransferListener(cardReceiveFileTransferListener);
		cardReceiveFileTransferListener = new CardReceiveFileTransferListener(getKanbanService());
		manager.addFileTransferListener(cardReceiveFileTransferListener);
	}
	
	void setKanbanView(KanbanView view) {
		this.view = view;
	}

	
	BoardModel getBoardModel(){
		KanbanView kanbanView = getKanbanView();
		if(kanbanView == null) return null;
		return (BoardModel)kanbanView.getAdapter(Board.class);
	}

	CommandStack getCommandStack() {
		KanbanView kanbanView = getKanbanView();
		if(kanbanView == null) return null;
		return (CommandStack)kanbanView.getAdapter(CommandStack.class);
	}

	private KanbanView getKanbanView() {
		if(this.view == null){
			setKanbanView(WorkbenchUtil.findKanbanView());
		}
		return this.view;
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}
	
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof KanbanView) {
			KanbanView view = (KanbanView) part;
			setKanbanView(view);
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {}

	public void partClosed(IWorkbenchPart part) {
		if (part instanceof KanbanView) {
			setKanbanView(null);
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
	}

	public void partOpened(IWorkbenchPart part) {}


}
