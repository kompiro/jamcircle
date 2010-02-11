package org.kompiro.jamcircle.storage.service.internal;

import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.*;

import net.java.ao.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.h2.tools.Csv;
import org.kompiro.jamcircle.storage.*;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
import org.kompiro.jamcircle.storage.service.*;

/**
 * @author kompiro
 * @TestContext org.kompiro.jamcircle.storage.service.internal.StorageServiceImplTest
 * @TestContext org.kompiro.jamcircle.storage.service.internal.StorageServiceImplInitializeTest
 */
public class StorageServiceImpl implements StorageService {
	
	private final class StorageChageListenerComparator implements
			Comparator<StorageChageListener> {
		public int compare(StorageChageListener o1, StorageChageListener o2) {
			return o1.getPriority() - o2.getPriority();
		}
	}

	private static final String KEY_OF_SYSTEM_PROPERTY_STORAGE_STOREROOT = "storage.storeroot";
	private static final String KEY_OF_SYSTEM_STORAGE_DBNAME = "storage.dbname";
	static String dbName = System.getProperty(KEY_OF_SYSTEM_STORAGE_DBNAME,"storage");
	
	private List<StorageChageListener> listeners = new ArrayList<StorageChageListener>();
	private StorageSettings settings = new StorageSettings();
	public static boolean testmode = FileStorageServiceImpl.testmode;
	
	static{
		try{
			ResourceBundle r = ResourceBundle.getBundle("storage");
			if(r != null){
				dbName = r.getString("dbname");
			}
		}catch(MissingResourceException e){
			// Noting because there wouldn't like to set default dbname.
		}
		dbName = System.getProperty(KEY_OF_SYSTEM_STORAGE_DBNAME,dbName);
	}

	private EntityManager manager;
	private FileStorageService fileService;
	private StorageCallbackHandlerLoader loader = new StorageCallbackHandlerLoader();

	public void activate(){
		loadStorageSetting();	
	}
	
	private void loadStorageSetting() {
		settings.loadSettings();
		if(settings.size() == 0){
			fileService = new FileStorageServiceImpl();
			settings.add(-1,fileService.getStoreRoot(),ConnectionMode.FILE.toString(), "sa", "");
		}
		StorageSetting setting = settings.get(0);
		try {
			loadStorage(setting,new NullProgressMonitor());
		} catch (StorageConnectException e) {
			if(!(StorageServiceImpl.testmode)){
				loader.setupStorageSetting();
			}else{
				System.err.println("can't connect storage. and now it is set testmode.");
			}
		}
	}

	public void deactivate(){
		if(StorageServiceImpl.testmode){
			settings.clear();
		}
		settings.storeSttings();
	}
			
	public void loadStorage(StorageSetting setting,IProgressMonitor monitor) throws StorageConnectException{
		monitor.beginTask("Connect to Database...", 110);
		if(manager != null){
			monitor.subTask("Disposed database connection.");
			manager.getProvider().dispose();
		}
		progress(monitor,10,"Load Settings...");
		String storeRoot = System.getProperty(KEY_OF_SYSTEM_PROPERTY_STORAGE_STOREROOT);
		String uri = null;
		if(storeRoot != null){
			fileService = new FileStorageServiceImpl(storeRoot);
		}else if(storeRoot == null || storeRoot.length() == 0){
			if(testmode || ConnectionMode.MEM.toString().equals(setting.getMode())){
				fileService = new FileStorageServiceImpl(setting.getUri());
				uri = "jdbc:h2:mem:TEST;DB_CLOSE_DELAY=-1";				
			}else
			if(ConnectionMode.TCP.toString().equals(setting.getMode())){
				fileService = new FileStorageServiceImpl();
				uri = "jdbc:h2:" + setting.getUri() + dbName;
			}else{
				fileService = new FileStorageServiceImpl(setting.getUri());
				uri = "jdbc:h2:" + getDBPath();
			}
		}
		
		progress(monitor, 10, "Create database connection...");
		createEntityManager(uri,setting.getUsername(),setting.getPassword());
		progress(monitor, 60, "Store setting...");
		storeSetting(setting);
		for(StorageChageListener listener : listeners){
			listener.changedStorage(monitor);
		}
		monitor.done();
	}
	
	private void progress(IProgressMonitor monitor,int worked,String task){
		monitor.internalWorked(worked);
		monitor.subTask(task);
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
		return fileService.getStoreRoot() + StorageServiceImpl.dbName;
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
		return this.settings;
	}
	
	public void setSettings(StorageSettings settings) {
		this.settings = settings;
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
