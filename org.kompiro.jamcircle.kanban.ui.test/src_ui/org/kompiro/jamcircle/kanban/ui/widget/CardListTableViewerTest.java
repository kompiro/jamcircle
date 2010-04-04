package org.kompiro.jamcircle.kanban.ui.widget;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kompiro.jamcircle.kanban.ui.widget.CardListTableViewer.ID_CARD_LIST;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.*;


public class CardListTableViewerTest {

	@Test
	public void show() throws Exception {
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		CardListTableViewer viewer = new CardListTableViewer(shell);
		CardContainer cards = new CardContainer.Mock();
		for(int i = 0; i < 10; i++){
			Card card = mock(Card.class);
			when(card.getStatus()).thenReturn("Todo");
			when(card.getID()).thenReturn(i);
			when(card.getSubject()).thenReturn("Card List " + i);
			User user = mock(User.class);
			when(user.getUserName()).thenReturn("User " + i);
			when(card.getFrom()).thenReturn(user );
			cards.addCard(card);
		}
		viewer.setInput(cards);
		shell.pack();
		shell.open();
		SWTBot bot = new SWTBot(shell);
		SWTBotTable table = bot.tableWithId(ID_CARD_LIST);
		assertThat(table.cell(0, 0),is("0"));
		assertThat(table.cell(1, 1),is("Card List 1"));
		assertThat(table.cell(2, 2),is("Todo"));
		assertThat(table.cell(0, 3),is("User 0"));
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		CardListTableViewer viewer = new CardListTableViewer(shell);
		CardContainer cards = new CardContainer.Mock();
		for(int i = 0; i < 10; i++){
			Card card = mock(Card.class);
			when(card.getStatus()).thenReturn("Todo");
			when(card.getID()).thenReturn(i);
			when(card.getSubject()).thenReturn("Card List" + i);
			User user = mock(User.class);
			when(user.getUserName()).thenReturn("User " + i);
			when(card.getFrom()).thenReturn(user );
			cards.addCard(card);
		}
		viewer.setInput(cards);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}


}
