package org.kompiro.jamcircle.kanban.model.mock;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import net.java.ao.*;
import net.java.ao.schema.PrimaryKey;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kompiro.jamcircle.storage.model.ExecutorHandler;

/**
 * This implementation describes Entity of Mock and isn't able to store any
 * persistence.
 * 
 * @author kompiro
 */
class MockEntity implements Entity {

	private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	@PrimaryKey("id")
	public int getID() {
		return 0;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	public EntityManager getEntityManager() {
		return null;
	}

	public Class<? extends RawEntity<Integer>> getEntityType() {
		return this.getClass();
	}

	public void init() {
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	public void save() {
	}

	public boolean isMock() {
		return true;
	}

	protected void fireProperty(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(evt);
		}
	}

	public void setHandler(ExecutorHandler handler) {

	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
