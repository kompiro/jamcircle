package org.kompiro.jamcircle.storage.service.internal;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.*;
import java.util.*;

import net.java.ao.*;

import org.eclipse.core.runtime.*;
import org.h2.tools.Csv;
import org.kompiro.jamcircle.storage.*;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
import org.kompiro.jamcircle.storage.service.*;

/**
 * @author kompiro
 * @TestContext org.kompiro.jamcircle.storage.service.internal.StorageServiceImplTest
 */
public class StorageServiceImpl implements StorageService {
	
	private final class StorageChageListenerComparator implements
			Comparator<StorageChageListener> {
		public int compare(StorageChageListener o1, StorageChageListener o2) {
			return o1.getPriority() - o2.getPriority();
		}
	}

	private static final String JAM_CIRCLE = "JAM_CIRCLE";
	private static final String APPLICATION_DATA = "Application Data";

	private static final String KEY_OF_SYSTEM_PROPERTY_STORAGE_STOREROOT = "storage.storeroot";
	private static final String KEY_OF_SYSTEM_STORAGE_ADDTIONAL_PATH = "storage.addtionalPath";
	private static final String KEY_OF_SYSTEM_STORAGE_DBNAME = "storage.dbname";
	static String dbName = System.getProperty(KEY_OF_SYSTEM_STORAGE_DBNAME,"storage");
	/** this property is used for testing. */
	private static String addtionalPath = "";
	public static boolean testmode;
	
	private List<StorageChageListener> listeners = new ArrayList<StorageChageListener>();
	private StorageSettings settings = new StorageSettings();
	
	static{
		try{
			ResourceBundle r = ResourceBundle.getBundle("storage");
			if(r != null){
				dbName = r.getString("dbname");
				addtionalPath = r.getString("addtionalPath");
				testmode = Boolean.valueOf(r.getString("testmode"));
			}
		}catch(MissingResourceException e){
			// Noting because there wouldn't like to set default dbname.
		}
		dbName = System.getProperty(KEY_OF_SYSTEM_STORAGE_DBNAME,dbName);
		addtionalPath = System.getProperty(KEY_OF_SYSTEM_STORAGE_ADDTIONAL_PATH,addtionalPath);
	}

	private EntityManager manager;
	private String storeRoot;
	private FileStorageService fileService = new FileStorageServiceImpl(this);
	private StorageCallbackHandlerLoader loader = new StorageCallbackHandlerLoader();

	public void activate(){
		loadStorageSetting();	
	}
	
	private void loadStorageSetting() {
		settings.loadSettings();
		if(settings.size() == 0){
			String uri = getDefaultStoreRoot();
			settings.add(-1,uri,StorageServiceImpl.CONNECTION_MODE.FILE.toString(), "sa", "");
		}
		StorageSetting setting = settings.get(0);
		try {
			loadStorage(setting,new NullProgressMonitor());
		} catch (StorageConnectException e) {
			if(!(StorageServiceImpl.testmode)){
				loader.setupStorageSetting();
			}else{
				System.err.println("can't connect storage. and now it set testmode.");
			}
		}
	}

	public void deactivate(){
		if(StorageServiceImpl.testmode){
			settings.clear();
		}
		settings.storeSttings();
	}
	
	public void addFile(String destDir,File srcFile) {
		fileService.addFile(destDir, srcFile);
	}
	
	public List<File> getFiles(String dir){
		return fileService.getFiles(dir);
	}
	
	public boolean fileExists(String path){
		return fileService.fileExists(path);
	}
	
	public String getStoreRoot() {
		assert storeRoot == null : "storeRoot isn't initialized.";
		if(addtionalPath != null && !"".equals(addtionalPath)){
			return new File(storeRoot).getAbsolutePath() + File.separator + addtionalPath + File.separator;
		}
		return new File(storeRoot).getAbsolutePath() + File.separator;
	}
	
	public void loadStorage(StorageSetting setting,IProgressMonitor monitor) throws StorageConnectException{
		monitor.beginTask("Connect to Database...", 110);
		if(manager != null){
			monitor.setTaskName("Begin dispose database connection.");
			manager.getProvider().dispose();
		}
		monitor.internalWorked(10);
		monitor.setTaskName("Load Settings...");
		storeRoot = System.getProperty(KEY_OF_SYSTEM_PROPERTY_STORAGE_STOREROOT);
		String uri = null;
		if(storeRoot == null || storeRoot.length() == 0){
			if(testmode || CONNECTION_MODE.MEM.toString().equals(setting.getMode())){
				storeRoot = setting.getUri();
				uri = "jdbc:h2:mem:TEST;DB_CLOSE_DELAY=-1";				
			}else
			if(CONNECTION_MODE.TCP.toString().equals(setting.getMode())){
				storeRoot = getDefaultStoreRoot();
				uri = "jdbc:h2:" + setting.getUri() + dbName;
			}else{
				storeRoot = setting.getUri();
				uri = "jdbc:h2:" + getDBPath();
			}
		}
		monitor.internalWorked(10);
		monitor.setTaskName("Create database connection...");
		createEntityManager(uri,setting.getUsername(),setting.getPassword());
		monitor.internalWorked(50);
		monitor.setTaskName("Store setting...");
		storeSetting(setting);
		monitor.internalWorked(10);
		monitor.setTaskName("notified listeners.");
		for(StorageChageListener listener : listeners){
			listener.changedStorage(monitor);
		}
		monitor.done();
	}
	
	private void storeSetting(StorageSetting setting) {
		StorageSettings settings = getSettings();
		if(settings != null){
			settings.add(setting);
			settings.storeSttings();
		}
	}

	private void createEntityManager(String uri, String username, String password) throws StorageConnectException{
		DatabaseProvider provider;
		provider = new H2DatabaseProvider(uri,username,password);
		try{
			provider.getConnection();
		}catch(SQLException e){
			throw new StorageConnectException(e);
		}
		if(StorageStatusHandler.isDebug()){
			StorageStatusHandler.info("path:" + uri,true);
		}
		manager = new EntityManager(provider);
	}
	
	public void recreateEntityManagerForTest() throws StorageConnectException{
		assert manager == null || !testmode  : "recreateEntityManager is called after manager is created.";
		DatabaseProvider provider = manager.getProvider();
		String uri = provider.getURI();
		String username = provider.getUsername();
		String password = provider.getPassword();
		provider.dispose();
		createEntityManager(uri, username, password);
	}
	
	public String getDefaultStoreRoot() {
		String storeRoot = "";
		URL userHome = null;
		if(Platform.isRunning()){
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
		}else{
			String tempDir = System.getProperty("java.io.tmpdir");
			File testDir = new File(tempDir,JAM_CIRCLE);
			testDir.mkdir();
			storeRoot = testDir.getAbsolutePath() + File.separator;
		}
		return storeRoot;
	}

	private boolean isWin32() {
		return Platform.OS_WIN32.equals(Platform.getOS());
	}

	private String[] getEntityColumnsFromCSV(File csvFile) {
		Csv csv = Csv.getInstance();
		List<String> result = new ArrayList<String>();
		try {
			FileReader reader = new FileReader(csvFile);
			ResultSet set = csv.read(reader, null);
			ResultSetMetaData metaData = set.getMetaData();
			for(int i = 1; i < metaData.getColumnCount() + 1;i++){
				result.add(metaData.getColumnLabel(i));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result.toArray(new String[]{});
	}

	public boolean exportEntity(File csvFile, Class<? extends Entity> entityClass) {
		String SQL = String.format("CALL CSVWRITE('%s', 'SELECT * FROM %s'); ",csvFile.getAbsolutePath(),entityClass.getSimpleName());
		StorageStatusHandler.debug(SQL);
		try {
			CallableStatement prepareCall = getEntityManager().getProvider().getConnection().prepareCall(SQL);
			return prepareCall.execute();
		} catch (SQLException e) {
			String errorMessage = String.format("StorageServiceImpl#exportEntity() is failed. executed '%s'.",SQL);
			StorageStatusHandler.fail(e, errorMessage,true);
			return false;
		}
	}

	public boolean importEntity(File csvFile, Class<? extends Entity> entityClass) {
		try {
			String[] columns = getEntityColumnsFromCSV(csvFile);
			StringBuilder target = new StringBuilder();
			boolean first = true;
			for(String column: columns){
				if("ID".equals(column.toUpperCase())) continue;
				if(!first){
					target.append(",");
				}
				first = false;
				target.append(column);
			}
			String SQL = String.format("INSERT INTO %s SELECT null as id,%s FROM CSVREAD('%s');",entityClass.getSimpleName() , target.toString(), csvFile.getAbsolutePath());
			StorageStatusHandler.debug(SQL);
			PreparedStatement prepareCall = getEntityManager().getProvider().getConnection().prepareStatement(SQL);
			prepareCall.execute();
			return true;
		} catch (Exception e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#importEntity()",true);
			return false;
		}
	}

	public void deleteAllEntity(Class<? extends Entity> entityClass) {
		try {
			Entity[] entities =  getEntityManager().find(entityClass);
			getEntityManager().delete(entities);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#deleteAllEntity()");			
		}
	}

	public void deleteTrashedEntity(Class<? extends GraphicalEntity> entityClass) {
		try {
			GraphicalEntity[] entities =  getEntityManager().find(entityClass,GraphicalEntity.PROP_TRASHED + "= ?",true);
			getEntityManager().delete(entities);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#deleteTrashedEntity()");			
		}
	}

	public String getDBPath() {
		return fileService.getDBPath();
	}
	
	public <T extends Entity> T createEntity(Class<T> clazz, DBParam[] params) {
		T entity = null;
		try {
			entity = getEntityManager().create(clazz, params);
		} catch (SQLException e) {
			StringBuilder builder = new StringBuilder();
			for(DBParam param:params){
				builder.append(String.format("%s:%s",param.getField(),param.getValue()));
				builder.append("\t");
			}
			StorageStatusHandler.fail(e, "StorageServiceImpl#create() '%s'",builder.toString());
		}
		return entity;

	}

	public void discard(GraphicalEntity entity) {
		entity.setTrashed(true);
		entity.save(false);
	}
	
	public void delete(Entity entity){
		try {
			getEntityManager().delete(entity);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#delete()");
		}
	}
	
	public void pickup(GraphicalEntity entity) {
		entity.setTrashed(false);
		entity.save(false);
	}
	
	public int countInTrash(Class<? extends GraphicalEntity> clazz) {
		GraphicalEntity[] results = findInTrash(clazz);
		if(results == null) return 0;
		return results.length;
	}


	public StorageSettings getSettings() {
		StorageActivator activator = StorageActivator.getDefault();
		if(activator == null) return null;
		return this.settings;
	}

	public void addStorageChangeListener(StorageChageListener listener) {
		this.listeners.add(listener);
		Collections.sort(this.listeners, new StorageChageListenerComparator());
	}

	public void removeStorageChangeListener(StorageChageListener listener) {
		this.listeners.remove(listener);
		Collections.sort(this.listeners, new StorageChageListenerComparator());
	}

	public <T extends Entity> T[] findInTrash(Class<T> clazz){
		T[] results = null;
		try {
			results = getEntityManager().find(clazz,GraphicalEntity.PROP_TRASHED + " = ?",true);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e,"StorageServiceImpl#countInTrash()",true);
		}
		return results;
	}
		
	public boolean isTestMode() {
		return testmode;
	}

	public void migrate(Class<? extends Entity>... entities) {
		try {
			if(isTestMode()){
				try {
					recreateEntityManagerForTest();
				} catch (StorageConnectException e) {
					StorageStatusHandler.fail(e, "StorageServiceImpl#migrate()",true);
				}
			}
			getEntityManager().migrate(entities);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#migrate()",true);
		}
	}

	public int count(Class<? extends Entity> clazz) {
		try {
			return getEntityManager().count(clazz);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#count()",true);
			return 0;
		}
	}

	public void setEntityManager(EntityManager manager) {
		this.manager = manager;
	}

	public EntityManager getEntityManager() {
		return manager;
	}

	public FileStorageService getFileService() {
		return fileService;
	}

	public void setFileService(FileStorageService fileService) {
		this.fileService = fileService;
	}

}
