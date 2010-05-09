package org.kompiro.jamcircle.kanban.ui.widget;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kompiro.jamcircle.kanban.model.Card;
import org.kompiro.jamcircle.kanban.model.CardContainer;


@RunWith(SWTBotJunit4ClassRunner.class)
public class CardListWindowTest {

	@Test
	public void show() throws Exception {
		
		Shell parentShell = new Shell();
		CardContainer container = mock(CardContainer.class);
		Card mock = mock(Card.class);
		when(mock.getSubject()).thenReturn("CardListWidgetTest");
		Card[] cards = new Card[]{mock} ;
		when(container.getCards()).thenReturn(cards);
		CommandStack commandStack = mock(CommandStack.class);
		CardListWindow window = new CardListWindow(parentShell , container , commandStack);
		window.open();
		SWTBot bot = new SWTBot(window.getShell());
		assertThat(bot.tableWithId(CardListTableViewer.ID_CARD_LIST).rowCount(),is(1));
	}
	
}
