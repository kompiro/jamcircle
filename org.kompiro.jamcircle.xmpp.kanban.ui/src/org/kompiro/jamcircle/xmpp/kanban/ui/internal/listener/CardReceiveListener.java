package org.kompiro.jamcircle.xmpp.kanban.ui.internal.listener;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.kompiro.jamcircle.kanban.model.*;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.kanban.ui.model.BoardModel;
import org.kompiro.jamcircle.kanban.ui.util.WorkbenchUtil;
import org.kompiro.jamcircle.xmpp.kanban.ui.internal.command.CreateCardCommand;
import org.kompiro.jamcircle.xmpp.service.XMPPConnectionService;

class CardReceiveListener implements ChatManagerListener {
	
	private KanbanXMPPLoginListener kanbanXMPPLoginListener;
	
	public CardReceiveListener(KanbanXMPPLoginListener kanbanXMPPLoginListener) {
		this.kanbanXMPPLoginListener = kanbanXMPPLoginListener;
	}

	public void chatCreated(Chat chat, boolean createdLocally) {
		chat.addMessageListener(new MessageListener() {
			public void processMessage(Chat chat, Message message) {
				Object obj = message
						.getProperty(XMPPConnectionService.PROP_SEND_CARD);
				String fromUserId = chat.getParticipant();
				if (obj instanceof CardDTO) {
					CreateCardCommand command = new CreateCardCommand();
					command.setContainer(getBoardModel());
					final CardDTO dto = (CardDTO) obj;
					User fromUser = null;
					if (fromUserId != null) {
						fromUser = getKanbanService().findUser(fromUserId);
					}
					Card card = createCard(dto, fromUser);
					card.setDeletedVisuals(false);
					command.setModel(card);
					WorkbenchUtil.getDisplay().asyncExec(
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
	
	protected BoardModel getBoardModel() {
		return kanbanXMPPLoginListener.getBoardModel();
	}

	private KanbanService getKanbanService() {
		return kanbanXMPPLoginListener.getKanbanService();
	}

	private final class CreateCommandRunnable implements Runnable {
		private CreateCardCommand command;

		private CreateCommandRunnable(CreateCardCommand command) {
			this.command = command;
		}

		public void run() {
			kanbanXMPPLoginListener.getCommandStack().execute(command);
		}

	}


}
