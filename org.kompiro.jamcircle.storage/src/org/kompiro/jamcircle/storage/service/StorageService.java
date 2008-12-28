package org.kompiro.jamcircle.storage.service;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;


import net.java.ao.Entity;
import net.java.ao.EntityManager;


public interface StorageService {

	public enum MODE{
		FILE,TCP
	}
	
	public EntityManager getEntityManager();

	public void addFile(String destDir, File srcFile);
	
	public List<File> getFiles(String dir);

	public boolean fileExists(String path);

	public String getDBPath();
	
	public StorageSettings getSettings();
	
	public void loadStorage(StorageSetting setting,IProgressMonitor monitor) throws StorageConnectException;
	
	public boolean exportEntity(File csvFile,Class<? extends Entity> entityClass);
	
	public boolean importEntity(File csvFile,Class<? extends Entity> entityClass);
	
	public void deleteAllEntity(Class<? extends Entity> entityClass);
	
	public void deleteTrashedEntity(Class<? extends GraphicalEntity> entityClass);
	
	public void addStorageChangeListener(StorageChageListener listener);

	public void removeStorageChangeListener(StorageChageListener listener);

	public String getStoreRoot();

}
