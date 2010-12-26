package org.kompiro.jamcircle.storage;

/**
 * This interface indicates callback handler for StorageService.
 */
public interface IUIStorageCallbackHandler {

	/**
	 * provides to show storage setting UI.
	 */
	public void setupStorageSetting();

	/**
	 * provides to show database migration UI
	 */
	public void databaseMigrate();

}
