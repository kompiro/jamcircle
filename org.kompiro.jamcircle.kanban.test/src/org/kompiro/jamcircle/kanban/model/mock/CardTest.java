package org.kompiro.jamcircle.kanban.model.mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.kompiro.jamcircle.kanban.model.ColorTypes;
import org.kompiro.jamcircle.kanban.model.FlagTypes;

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

	@Test
	public void firePropFrom() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		User user = new User();
		card.setFrom(user);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
		assertEquals(user, card.getFrom());
	}

	@Test
	public void firePropAddFile() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		File file = mock(File.class);
		card.addFile(file);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropRemoveFile() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		File file = mock(File.class);
		card.addFile(file);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
		card.deleteFile(file);
		verify(listener,times(2)).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropUUID() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setUUID(UUID.randomUUID().toString());
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropColorType() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setColorType(ColorTypes.BLUE);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}	

	@Test
	public void firePropCompleted() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setCompleted(true);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropBoard() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setBoard(mock(Board.class));
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropCompleteDate() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setCompletedDate(mock(Date.class));
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}

	@Test
	public void firePropDueDate() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setDueDate(mock(Date.class));
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}
	
	@Test
	public void firePropFlagType() throws Exception {
		Card card = new Card();
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		card.addPropertyChangeListener(listener);
		
		card.setFlagType(FlagTypes.GREEN);
		verify(listener).propertyChange(any(PropertyChangeEvent.class));
	}


}
