package org.kompiro.jamcircle.storage.service;

import java.io.File;

import net.java.ao.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;


public interface StorageService {

	public enum ConnectionMode{
		FILE,TCP,MEM
	}
	
	public EntityManager getEntityManager();

	public StorageSettings getSettings();
	
	public void loadStorage(StorageSetting setting,IProgressMonitor monitor) throws StorageConnectException;
	
	public boolean exportEntity(File csvFile,Class<? extends Entity> entityClass);
	
	public boolean importEntity(File csvFile,Class<? extends Entity> entityClass);
	
	public void deleteAllEntity(Class<? extends Entity> entityClass);
	
	public void deleteTrashedEntity(Class<? extends GraphicalEntity> entityClass);
	
	public void addStorageChangeListener(StorageChangeListener listener);

	public void removeStorageChangeListener(StorageChangeListener listener);

	public <T extends Entity> T createEntity(Class<T> clazz, DBParam[] params);

	public void discard(GraphicalEntity entity);
	
	public void delete(Entity entity);

	public int countInTrash(Class<? extends GraphicalEntity> clazz);

	public void pickup(GraphicalEntity entity);

//	public <T extends Entity> T[] find(Class<T> clazz, String query, Object... objects);

	public <T extends Entity> T[] findInTrash(Class<T> clazz);
	
	public boolean isTestMode();

	public void migrate(Class<? extends Entity>... classes);

	public int count(Class<? extends Entity> clazz);
	
	public FileStorageService getFileService();

}
