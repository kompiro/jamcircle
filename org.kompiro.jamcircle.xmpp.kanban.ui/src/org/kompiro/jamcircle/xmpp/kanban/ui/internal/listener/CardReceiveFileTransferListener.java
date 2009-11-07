package org.kompiro.jamcircle.xmpp.kanban.ui.internal.listener;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.*;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.service.KanbanService;
import org.kompiro.jamcircle.xmpp.XMPPStatusHandler;

class CardReceiveFileTransferListener implements FileTransferListener {
	
	static final String QUERY_OF_UUID_NOT_SENT_CARD = Card.PROP_TRASHED + " = ? and" + Card.PROP_UUID
							+ " = ? and " + Card.PROP_TO + " is null";
	private KanbanService kanbanService;

	public CardReceiveFileTransferListener(KanbanService kanbanService) {
		this.kanbanService = kanbanService;
	}
	
	public void fileTransferRequest(final FileTransferRequest request) {

		String uuid = request.getDescription();
		IncomingFileTransfer accept = request.accept();
		try {
			String fileName = request.getFileName();
			File tmpFile = new File(System.getProperty("java.io.tmpdir"),fileName);
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
		return kanbanService.findCards(
				QUERY_OF_UUID_NOT_SENT_CARD, false,
				uuid)[0];
	}
}
