package org.kompiro.jamcircle.xmpp.kanban.ui.internal.command;

import java.io.File;
import java.util.Iterator;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.ui.KanbanUIStatusHandler;
import org.kompiro.jamcircle.kanban.ui.KanbanView;
import org.kompiro.jamcircle.kanban.ui.command.CancelableCommand;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.BridgeXMPPActivator;

public class SendCardCommand extends AbstractCommand implements CancelableCommand{
	private final class MessageListenerImpl implements
			MessageListener {
		public void processMessage(Chat chat, Message message) {
		}
	}

	private EditPart part;
	private String target;
	private Shell shell;

	public SendCardCommand(Shell shell,EditPart part, String target) {
		this.shell = shell;
		this.part = part;
		this.target = target;
	}

	@Override
	public void doExecute() {
		XMPPConnection connection = getConnection();
		Presence precense = connection.getRoster().getPresence(target);
		String debugMessage = String.format("target:'%s'", target);
		KanbanUIStatusHandler.debug(debugMessage);
		if(!precense.isAvailable()){
			String userName = getUserName(connection);
			String message = String.format("The user '%s' is not available.", userName);
			MessageDialog.openInformation(shell, "Can't send message.", message);
			return;
		}
		

		ChatManager chatManager = connection.getChatManager();
		Object o = part.getModel();
		// Create the file transfer manager
		FileTransferManager manager = new FileTransferManager(connection);
		
		// Create the outgoing file transfer
		if (o instanceof Card) {
			Card card = (Card) o;
			CardDTO dto = new CardDTO(card);
			Message messageObject = new Message();
			messageObject.setBody("message form JAM Circle");
			messageObject.setProperty(KanbanView.PROP_MESSAGE_MODEL, dto);
			Iterator<Presence> precenses = connection.getRoster().getPresences(target);
			try {
				Chat chat = chatManager.createChat(this.target,	new MessageListenerImpl());
				chat.sendMessage(messageObject);
				while(precenses.hasNext()){
					Presence target = precenses.next();
					String targetUser = target.getFrom();
					// Send the file
					for(File file : card.getFiles()){
						String uuid = card.getUUID();
						OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(targetUser);
						transfer.sendFile(file,uuid);
					}
					
				}
			} catch (XMPPException e) {
				KanbanUIStatusHandler.fail(e, "send message failed.",true);
				return;
			}
			card.setTo(getUser(target));
			card.save();
		}

	}

	private XMPPConnection getConnection() {
		return BridgeXMPPActivator.getDefault()
				.getConnectionService().getConnection();
	}

	private String getUserName(XMPPConnection connection) {
		Roster roster = connection.getRoster();
		if(roster == null) return "";
		RosterEntry entry = roster.getEntry(target);
		if(entry == null)return "";
		return entry.getName();
	}

	private User getUser(String userId) {
		return getKanbanService().findUser(userId);
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public String getComfirmMessage() {
		return String.format("Are you realy want to send the card '%s'?", part.getModel().toString());
	}

}
