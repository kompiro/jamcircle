package org.kompiro.jamcircle.storage.service;

import java.io.File;
import java.util.List;

import org.kompiro.jamcircle.storage.exception.FileStorageException;

/**
 * This service provides File API for control JAM Circle's data.
 * The name of JAM Circle's data directory is "StoreRoot".
 * @author kompiro
 *
 */
public interface FileStorageService {

	/**
	 * add file to Store Root
	 * @param destDir canonical path from StoreRoot
	 * @param srcFile file
	 */
	public void addFile(String destDir, File srcFile);

	/**
	 * get files from StoreRoot's directory
	 * @param destDir get from target directory
	 * @return files
	 */
	public List<File> getFiles(String destDir);

	/**
	 * Check File exist status.
	 * @param path from store root
	 * @return true:exist 
	 */
	public boolean fileExists(String path);

	/**
	 * Return to store JAM Circle data storage root 
	 * @return
	 */
	public String getStoreRoot();

	/**
	 * CAUTION
	 * delete all files on storage root.
	 * @throws FileStorageException it cause that if you call on normal environment.
	 */
	public void deleteAll() throws FileStorageException;

}