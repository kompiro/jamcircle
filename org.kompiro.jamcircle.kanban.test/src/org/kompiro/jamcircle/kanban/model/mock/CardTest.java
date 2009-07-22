package org.kompiro.jamcircle.kanban.model.mock;

import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

public class CardTest {
	
	@Test
	public void firePropSubject() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		PropertyChangeEvent evt = new PropertyChangeEvent(card, Card.PROP_SUBJECT, null, "hello!");
		card.addPropertyChangeListener(listener);
		card.setSubject("hello!");
		verify(listener).propertyChange((PropertyChangeEvent)eq(evt));
	}

}
