package org.kompiro.jamcircle.kanban.model.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class LaneTest {

	private Lane lane;
	private PropertyChangeListener listener;

	@Before
	public void before() throws Exception {
		lane = new Lane();
		listener = mock(PropertyChangeListener.class);
		lane.addPropertyChangeListener(listener);
	}

	@Test
	public void isMock() throws Exception {
		Lane lane = new Lane();
		assertThat(lane.isMock(), is(true));
	}

	@Test
	public void set_height() throws Exception {

		lane.setHeight(100);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());

		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_HEIGHT));
		assertThat(value.getOldValue(), is((Object) 500));
		assertThat(value.getNewValue(), is((Object) 100));

	}

	@Test
	public void set_width() throws Exception {

		lane.setWidth(100);

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());

		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_WIDTH));
		assertThat(value.getOldValue(), is((Object) 200));
		assertThat(value.getNewValue(), is((Object) 100));

	}

	@Test
	public void set_script() throws Exception {

		lane.setScript("p 'test'");

		ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
		verify(listener).propertyChange(captor.capture());

		PropertyChangeEvent value = captor.getValue();
		assertThat(value.getPropertyName(), is(Lane.PROP_SCRIPT));
		assertThat(value.getOldValue(), is(nullValue()));
		assertThat(value.getNewValue(), is((Object) "p 'test'"));

	}

}
