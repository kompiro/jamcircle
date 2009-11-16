package org.kompiro.jamcircle.storage.service.internal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.storage.service.FileStorageService;

public class FileStorageServiceImpl implements FileStorageService {

	private StorageServiceImpl storageService;

	public FileStorageServiceImpl(StorageServiceImpl storageServiceImpl) {
		this.storageService = storageServiceImpl;
	}

	public void addFile(String destDir, File srcFile) {
		String filePath = getStoreRoot() + destDir + File.separator + srcFile.getName();
		String message = String.format("StorageServiceImpl#fileExists(filePath:'%s')", filePath);
		StorageStatusHandler.debug(message);
		File destFile = new File(filePath);
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			String errorMessage = String.format("can't copy file '%s'",srcFile.getName());
			StorageStatusHandler.fail(e, errorMessage);
		}
	}

	public boolean fileExists(String path) {
		String filePath = getStoreRoot() + path;
		String message = String.format("StorageServiceImpl#fileExists(filePath:'%s')", filePath);
		StorageStatusHandler.debug(message);
		File file = new File(filePath);
		return file.exists();
	}

	public String getDBPath() {
		return getStoreRoot() + StorageServiceImpl.dbName;
	}

	public List<File> getFiles(String dir) {
		String filePath = getStoreRoot() + dir + File.separator;
		File dirFile = new File(filePath);
		String[] list = dirFile.list();
		if(list == null || list.length == 0) return null;
		return Arrays.asList(dirFile.listFiles());
	}

	private String getStoreRoot() {
		return storageService.getStoreRoot();
	}

}
