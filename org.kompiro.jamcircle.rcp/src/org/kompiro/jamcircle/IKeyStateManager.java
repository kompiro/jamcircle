package org.kompiro.jamcircle;


public interface IKeyStateManager {

	public abstract void install();

	public abstract void uninstall();

	public abstract void addKeyEventListener(KeyEventListener listener);

	public abstract void removeKeyEventListener(KeyEventListener listener);

}