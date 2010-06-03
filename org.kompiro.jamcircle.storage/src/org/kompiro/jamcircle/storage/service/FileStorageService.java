package org.kompiro.jamcircle.storage.service;

import java.io.File;
import java.util.List;

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
	 * delete file from Stroe Root
	 * @param destDir
	 * @param targetFile
	 */
	public void deleteFile(String destDir,File targetFile);

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


}