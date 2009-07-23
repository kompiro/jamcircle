package org.kompiro.jamcircle.kanban.model.mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import org.junit.Test;

public class CardTest {
	
	@Test
	public void firePropSubject() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setSubject("hello!");
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropContent() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setContent("content");
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropTrashed() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setTrashed(true);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropX() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setX(10);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropY() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setY(10);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropLane() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setLane(new Lane());
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropOwner() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setOwner(new User());
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropCreated() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setCreated("kompiro");
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropCreatedDate() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		card.setCreateDate(new Date());
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropTo() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		User toUser = new User();
		card.setTo(toUser);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
		assertEquals(toUser,card.getTo());
	}


}
