package org.kompiro.jamcircle.xmpp.kanban.ui.internal;

import java.io.File;
import java.util.*;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Display;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.*;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.model.IconModel;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.xmpp.XMPPStatusHandler;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.command.CreateCardCommand;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.model.UserModel;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;
import org.kompiro.jamcircle.xmpp.util.XMPPUtil;

public class KanbanXMPPLoginListener implements XMPPLoginListener {

	private RosterListener rosterListener;
	private CardReceiveListener cardSendListener = new CardReceiveListener();
	private FileTransferManager manager;
	private CardReceiveFileTransferListener cardReceiveFileTransferListener;

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

	private final class CardReceiveFileTransferListener implements
			FileTransferListener {
		public void fileTransferRequest(FileTransferRequest request) {

			String uuid = request.getDescription();
			IncomingFileTransfer accept = request.accept();
			try {
				File tmpFile = new File(System.getProperty("java.io.tmpdir"),
						request.getFileName());
				if (tmpFile.exists()) {
					tmpFile.delete();
				}
				accept.recieveFile(tmpFile);
				while (!accept.isDone()) {
				}
				Card tmpCard = getCard(uuid);
				int time = 0;
				while (tmpCard == null) {
					try {
						if (time > 3) {
							throw new RuntimeException("can't get card data.");
						}
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					tmpCard = getCard(uuid);
					time++;
				}
				final Card card = tmpCard;
				final File file = tmpFile;
				card.addFile(file);
				card.save(false);
			} catch (XMPPException e) {
				XMPPStatusHandler.fail(e, "error has occured.");
				request.reject();
			}
		}

		private Card getCard(String uuid) {
			return getKanbanService().findCards(
					Card.PROP_TRASHED + " = ? and" + Card.PROP_UUID
							+ " = ? and " + Card.PROP_TO + " is null", false,
					uuid)[0];
		}
	}

	private final class RosterListnerForUsers implements RosterListener {

		public RosterListnerForUsers(Roster roster) {
		}

		public void entriesAdded(Collection<String> addresses) {
		}

		public void entriesDeleted(Collection<String> addresses) {
		}

		public void entriesUpdated(Collection<String> addresses) {
		}

		public void presenceChanged(final Presence presence) {
			if (getBoardModel() == null)
				return;
			Runnable runnable = new Runnable() {
				public void run() {
					String from = presence.getFrom();
					from = XMPPUtil.getRemovedResourceUser(from);
					UserModel user = getUser(from);
					if (user != null) {
						user.setPresence(presence);
					}
				}

			};
			getDisplay().asyncExec(runnable);
		}


	}
	
	private final class CardReceiveListener implements ChatManagerListener {
		public void chatCreated(Chat chat, boolean createdLocally) {
			chat.addMessageListener(new MessageListener() {
				public void processMessage(Chat chat, Message message) {
					Object obj = message
							.getProperty(KanbanView.PROP_MESSAGE_MODEL);
					String fromUserId = chat.getParticipant();
					if (obj instanceof CardDTO) {
						CreateCardCommand command = new CreateCardCommand();
						BoardModel boardModel = getBoardModel();
						command.setContainer(boardModel);
						final CardDTO dto = (CardDTO) obj;
						User fromUser = null;
						if (fromUserId != null) {
							fromUser = getKanbanService().findUser(fromUserId);
						}
						Card card = createCard(dto, fromUser);
						card.setDeletedVisuals(false);
						command.setModel(card);
						getDisplay().asyncExec(
								new CreateCommandRunnable(command));
					}
				}

				private Card createCard(CardDTO dto, User fromUser) {
					KanbanService service = getKanbanService();
					return service.createReceiveCard(getBoardModel().getBoard(),
							dto, fromUser);
				}
			});
		}
	}

	private final class CreateCommandRunnable implements Runnable {
		private CreateCardCommand command;

		private CreateCommandRunnable(CreateCardCommand command) {
			this.command = command;
		}

		public void run() {
			getCommandStack().execute(command);
		}

	}

	public void afterLoggedIn(XMPPConnection connection) {
		setUsers(connection);
	}

	public void beforeLoggedOut(XMPPConnection connection) {
	}

	private void setUsers(XMPPConnection connection){
		connection.addConnectionListener(connectionListener);
		manager = new FileTransferManager(connection);
		final Roster roster = connection.getRoster();
		rosterListener = new RosterListnerForUsers(roster);
		roster.addRosterListener(rosterListener);
		connection.getChatManager().addChatListener(cardSendListener);
		final Map<String,User> userMap = new HashMap<String, User>();
		User[] userList = getKanbanService().findUsersOnBoard();
		for(User user : userList){
			String key = user.getUserId();
			if(!userMap.containsKey(key)){
				userMap.put(key, user);
			}
		}
		BoardModel boardModel = getBoardModel();
		if(boardModel == null) return;
		clearUsers(boardModel);
		for(User user:userMap.values()){
			Presence presence = roster.getPresence(user.getUserId());
			UserModel userModel = new UserModel(roster.getEntry(user.getUserId()),presence,user);
			presence.isAvailable();
			boardModel.addIcon(userModel);
		}
		setCardReceiveFileTransferManager();
		
	}

	private void clearUsers(BoardModel boardModel) {
	}

	protected UserModel getUser(String from) {
		for(IconModel icon : getBoardModel().getIconModels()){
			if (icon instanceof UserModel) {
				UserModel userModel = (UserModel) icon;
				if(userModel.getUserId().equals(from)){
					return userModel;
				}
			};
		}
		return null;
	}

	private KanbanService getKanbanService() {
		return BridgeXMPPActivator.getDefault().getKanbanService();
	}

	private void setCardReceiveFileTransferManager() {
		FileTransferManager manager = getFileTransferManager();
		manager.removeFileTransferListener(cardReceiveFileTransferListener);
		cardReceiveFileTransferListener = new CardReceiveFileTransferListener();
		manager.addFileTransferListener(cardReceiveFileTransferListener);
	}
	
	public FileTransferManager getFileTransferManager() {
		return manager;
	}

	private KanbanView getKanbanView() {
		return WorkbenchUtil.findKanbanView();
	}
	
	private BoardModel getBoardModel(){
		return WorkbenchUtil.getCurrentKanbanBoard();
	}

	private CommandStack getCommandStack() {
		return (CommandStack)getKanbanView().getAdapter(CommandStack.class);
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}

}
