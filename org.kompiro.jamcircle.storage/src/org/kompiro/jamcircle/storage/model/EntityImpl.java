package org.kompiro.jamcircle.storage.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import net.java.ao.Entity;

/**
 * This implementation describes ActiveObjects Entity implementation.<br>
 * When you add some model of ActiveObjects and use Implementation
 * wrapper,extends this class.
 */
public abstract class EntityImpl {

	public static final String QUERY = " = ?";//$NON-NLS-1$

	private List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
	protected Entity entity;

	protected ExecutorHandler handler = new DefaultExecutorHandle();

	public EntityImpl(Entity entity) {
		this.entity = entity;
	}

	protected List<PropertyChangeListener> getListeners() {
		return listeners;
	}

	protected void fireEvent(PropertyChangeEvent event) {
		for (PropertyChangeListener listener : getListeners()) {
			listener.propertyChange(event);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (entity != null) {
			entity.addPropertyChangeListener(listener);
		}
		this.getListeners().add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.getListeners().remove(listener);
		if (entity != null) {
			entity.removePropertyChangeListener(listener);
		}
	}

	public void save(boolean directExecution) {
		if (directExecution) {
			saveAndFlush();
			return;
		}
		Runnable runnable = new Runnable() {
			public void run() {
				saveAndFlush();
			}
		};
		handler.handle(runnable);
	}

	private void saveAndFlush() {
		entity.save();

	}

	public void setHandler(ExecutorHandler handler) {
		this.handler = handler;
	}

}
