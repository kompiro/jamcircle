package org.kompiro.jamcircle.storage.service.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.kompiro.jamcircle.storage.Messages;
import org.kompiro.jamcircle.storage.StorageStatusHandler;
import org.kompiro.jamcircle.storage.exception.FileStorageException;
import org.kompiro.jamcircle.storage.service.FileStorageService;

public class FileStorageServiceImpl implements FileStorageService {

	private static final String JAM_CIRCLE = "JAM_CIRCLE"; //$NON-NLS-1$
	private static final String APPLICATION_DATA = "Application Data"; //$NON-NLS-1$
	private static final String KEY_OF_SYSTEM_STORAGE_ADDTIONAL_PATH = "storage.addtionalPath"; //$NON-NLS-1$
	/** this property is used for testing. */
	private static String addtionalPath = ""; //$NON-NLS-1$
	public static boolean testmode;
	private String storeRoot;

	static{
		try{
			ResourceBundle r = ResourceBundle.getBundle("storage"); //$NON-NLS-1$
			if(r != null){
				testmode = Boolean.valueOf(r.getString("testmode")); //$NON-NLS-1$
				addtionalPath = r.getString("addtionalPath"); //$NON-NLS-1$
			}
		}catch(MissingResourceException e){
			// Noting because there wouldn't like to set default dbname.
		}
		addtionalPath = System.getProperty(KEY_OF_SYSTEM_STORAGE_ADDTIONAL_PATH,addtionalPath);
	}

	public FileStorageServiceImpl(String storeRoot) {
		this.storeRoot = storeRoot;
	}

	public FileStorageServiceImpl() {
		this.storeRoot = getDefaultStoreRoot();
	}

	public void addFile(String destDir, File srcFile) {
		String filePath = getStoreRoot() + destDir + File.separator + srcFile.getName();
		String message = String.format("StorageServiceImpl#fileExists(filePath:'%s')", filePath); //$NON-NLS-1$
		StorageStatusHandler.debug(message);
		File destFile = new File(filePath);
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			String errorMessage = String.format(Messages.FileStorageServiceImpl_copy_error_message,srcFile.getName());
			StorageStatusHandler.fail(e, errorMessage);
		}
	}

	public boolean fileExists(String path) {
		String filePath = getStoreRoot() + path;
		String message = String.format("StorageServiceImpl#fileExists(filePath:'%s')", filePath); //$NON-NLS-1$
		StorageStatusHandler.debug(message);
		File file = new File(filePath);
		return file.exists();
	}

	public List<File> getFiles(String dir) {
		String filePath = getStoreRoot() + dir + File.separator;
		File dirFile = new File(filePath);
		String[] list = dirFile.list();
		if(list == null || list.length == 0) return null;
		return Arrays.asList(dirFile.listFiles());
	}

	public String getStoreRoot() {
		assert storeRoot == null : Messages.FileStorageServiceImpl_initialized_error_message;
		if(addtionalPath != null && !"".equals(addtionalPath)){ //$NON-NLS-1$
			return new File(storeRoot).getAbsolutePath() + File.separator + addtionalPath + File.separator;
		}
		return new File(storeRoot).getAbsolutePath() + File.separator;
	}
	
	public void deleteAll()  throws FileStorageException{
		if(testmode == false){
			throw new FileStorageException("This API might call cleaning for test only.");
		}
		File root = new File(getStoreRoot());
		try {
			FileUtils.deleteDirectory(root);
		} catch (IOException e) {
			throw new FileStorageException(e);
		}
	};
	
	String getDefaultStoreRoot() {
		String storeRoot = ""; //$NON-NLS-1$
		if(testmode || !Platform.isRunning()){
			storeRoot = getDefaultRootForTest();
		}else{
			storeRoot = getDefaultRootForUse();
		}
		return storeRoot;
	}

	private String getDefaultRootForUse() {
		String storeRoot;
		URL userHome;
		userHome = Platform.getUserLocation().getURL();
		storeRoot = userHome.getPath();
		if(isWin32()){
			File file = new File(userHome.getFile() + File.separator
					+ APPLICATION_DATA + File.separator
					+ JAM_CIRCLE + File.separator);
			storeRoot = file.getAbsolutePath() + File.separator;
		}else{
			storeRoot = storeRoot + JAM_CIRCLE + File.separator;
		}
		return storeRoot;
	}

	private String getDefaultRootForTest() {
		String storeRoot;
		String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		File testDir = new File(tempDir,JAM_CIRCLE);
		testDir.mkdir();
		storeRoot = testDir.getAbsolutePath() + File.separator;
		return storeRoot;
	}

	private boolean isWin32() {
		return Platform.OS_WIN32.equals(Platform.getOS());
	}

	public void setStoreRoot(String storeRoot) {
		this.storeRoot = storeRoot;
	}

	
}
