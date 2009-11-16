package org.kompiro.jamcircle.storage.service;

import java.io.File;
import java.util.List;

public interface FileStorageService {

	public void addFile(String destDir, File srcFile);

	public List<File> getFiles(String dir);

	public boolean fileExists(String path);

	public String getDBPath();

}