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
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.util.XMPPUtil;
import org.kompiro.jamcircle.xmpp.kanban.ui.model.UserModel;
import org.kompiro.jamcircle.xmpp.service.XMPPLoginListener;

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
	private KanbanView view;

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

		private BoardModel boardModel;
		
		public RosterListnerForUsers(BoardModel boardModel) {
			this.boardModel = boardModel;
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
					UserModel user = getUser(from);
					if (user != null) {
						user.setPresence(presence);
					}
				}

			};
			getDisplay().asyncExec(runnable);
		}

		private UserModel getUser(String from) {
			for(IconModel icon : boardModel.getIconModels()){
				if (icon instanceof UserModel) {
					UserModel userModel = (UserModel) icon;
					if(userModel.getUserId().equals(from)){
						return userModel;
					}
				};
			}
			return null;
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
		if(getKanbanView() == null){
			view = WorkbenchUtil.findKanbanView();
		}
		setUsers(connection);
	}

	public void beforeLoggedOut(XMPPConnection connection) {
		BoardModel boardModel = getBoardModel();
		clearUsers(boardModel);
	}

	private void setUsers(XMPPConnection connection){
		connection.addConnectionListener(connectionListener);
		manager = new FileTransferManager(connection);
		final Roster roster = connection.getRoster();
		BoardModel boardModel = getBoardModel();
		if(boardModel == null) return;
		rosterListener = new RosterListnerForUsers(boardModel);
		roster.addRosterListener(rosterListener);
		connection.getChatManager().addChatListener(cardSendListener);
		Map<String,User> userMap = new HashMap<String, User>();
		User[] userList = getKanbanService().findUsersOnBoard();
		for(User user : userList){
			String key = user.getUserId();
			if(!userMap.containsKey(key)){
				userMap.put(key, user);
			}
		}
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
		ListIterator<IconModel> listIterator = boardModel.getIconModels().listIterator();
		while(listIterator.hasNext()){
			IconModel model = listIterator.next();
			if (model instanceof UserModel){
				listIterator.remove();
				boardModel.firePropertyChange(BoardModel.PROP_ICON, model, null);
			}
		}
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

	public void setKanbanView(KanbanView view) {
		this.view = view;
	}

	
	private BoardModel getBoardModel(){
		return (BoardModel)getKanbanView().getAdapter(Board.class);
	}

	private CommandStack getCommandStack() {
		return (CommandStack)getKanbanView().getAdapter(CommandStack.class);
	}

	private KanbanView getKanbanView() {
		return this.view;
	}

	private Display getDisplay() {
		return WorkbenchUtil.getDisplay();
	}

}
