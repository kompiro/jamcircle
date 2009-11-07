package org.kompiro.jamcircle.xmpp.kanban.ui.internal.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.File;

import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.service.KanbanService;

public class CardReceiveFileTransferListenerTest {

	@Test
	public void fileTransferRequest() throws Exception {
		
		KanbanService kanbanService = mock(KanbanService.class);
		FileTransferRequest request = mock(FileTransferRequest.class);
		IncomingFileTransfer accept = mock(IncomingFileTransfer.class);

		Card card = mock(Card.class);
		when(kanbanService.findCards(CardReceiveFileTransferListener.QUERY_OF_UUID_NOT_SENT_CARD,false,null)).thenReturn(new Card[]{card });
		when(request.getFileName()).thenReturn("test_file.text");
		when(request.accept()).thenReturn(accept);
	
		when(accept.isDone()).thenReturn(true);
		
		CardReceiveFileTransferListener listener = new CardReceiveFileTransferListener(kanbanService );
		listener.fileTransferRequest(request);
		verify(card).addFile((File)any());
		verify(card).save(false);
	}

}
