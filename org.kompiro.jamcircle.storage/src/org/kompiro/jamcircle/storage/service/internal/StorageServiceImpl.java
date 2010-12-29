package org.kompiro.jamcircle.storage.service.internal;

import static java.lang.String.format;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import net.java.ao.*;

import org.eclipse.core.runtime.*;
import org.h2.tools.Csv;
import org.kompiro.jamcircle.storage.*;
import org.kompiro.jamcircle.storage.exception.StorageConnectException;
import org.kompiro.jamcircle.storage.model.GraphicalEntity;
import org.kompiro.jamcircle.storage.service.*;
import org.osgi.framework.Bundle;

/**
 * This service provides storage service.
 * 
 * @author kompiro
 * @TestContext
 *              org.kompiro.jamcircle.storage.service.internal.
 *              StorageServiceImplTest
 * @TestContext org.kompiro.jamcircle.storage.service.internal.
 *              StorageServiceImplInitializeTest
 */
public class StorageServiceImpl implements StorageService {

	private static final String ID = "ID"; //$NON-NLS-1$
	private static final String COMMA = ","; //$NON-NLS-1$
	private static final String TAB = "\t"; //$NON-NLS-1$
	private static final String QUERY = "= ?"; //$NON-NLS-1$
	private static final String DEFAULT_ADMIN_NAME = "sa"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String PASSWORD = EMPTY;
	private static final String DB_NAME = "dbname"; //$NON-NLS-1$
	private static final String RESOURCE_NAME = "storage"; //$NON-NLS-1$

	private final class StorageChageListenerComparator implements
			Comparator<StorageChangeListener> {
		public int compare(StorageChangeListener o1, StorageChangeListener o2) {
			return o1.getPriority() - o2.getPriority();
		}
	}

	private static final String KEY_OF_SYSTEM_PROPERTY_STORAGE_STOREROOT = "storage.storeroot"; //$NON-NLS-1$
	private static final String KEY_OF_SYSTEM_STORAGE_DBNAME = "storage.dbname"; //$NON-NLS-1$
	static String dbName = System.getProperty(KEY_OF_SYSTEM_STORAGE_DBNAME, RESOURCE_NAME);

	private List<StorageChangeListener> listeners = new ArrayList<StorageChangeListener>();
	private StorageSettings settings = new StorageSettings();
	public static boolean testmode = FileStorageServiceImpl.testmode;
	public static final ExecutorService STORAGE_ACCESS_EXECUTOR = Executors.newSingleThreadExecutor();

	static {
		try {
			ResourceBundle r = ResourceBundle.getBundle(RESOURCE_NAME);
			if (r != null) {
				dbName = r.getString(DB_NAME);
			}
		} catch (MissingResourceException e) {
			// Noting because there wouldn't like to set default dbname.
		}
		dbName = System.getProperty(KEY_OF_SYSTEM_STORAGE_DBNAME, dbName);
	}

	private EntityManager manager;
	private FileStorageService fileService;
	private StorageCallbackHandlerLoader loader = new StorageCallbackHandlerLoader();
	private DatabaseMigrator migrator = new DatabaseMigrator();

	public void activate() {
		loadStorageSetting();
	}

	private void loadStorageSetting() {
		settings.loadSettings();
		if (settings.size() == 0) {
			fileService = new FileStorageServiceImpl();
			settings.add(-1, fileService.getStoreRoot(), ConnectionMode.FILE.toString(), DEFAULT_ADMIN_NAME, PASSWORD);
		}
		StorageSetting setting = settings.get(0);
		try {
			loadStorage(setting, new NullProgressMonitor());
		} catch (StorageConnectException e) {
			if (!(StorageServiceImpl.testmode)) {
				loader.setupStorageSetting();
			} else {
				System.err.println(Messages.StorageServiceImpl_test_mode_message);
			}
		}
	}

	public void deactivate() {
		if (StorageServiceImpl.testmode) {
			settings.clear();
		}
		settings.storeSttings();
	}

	public void loadStorage(StorageSetting setting, IProgressMonitor monitor) throws StorageConnectException {
		monitor.beginTask(Messages.StorageServiceImpl_connect_database_task, 110);
		if (manager != null) {
			monitor.subTask(Messages.StorageServiceImpl_disposed_connection_task);
			manager.getProvider().dispose();
		}
		progress(monitor, 10, Messages.StorageServiceImpl_load_setting_message);
		String storeRoot = System.getProperty(KEY_OF_SYSTEM_PROPERTY_STORAGE_STOREROOT);
		String uri = null;
		if (storeRoot != null) {
			fileService = new FileStorageServiceImpl(storeRoot);
		} else if (storeRoot == null || storeRoot.length() == 0) {
			if (testmode || ConnectionMode.MEM.toString().equals(setting.getMode())) {
				fileService = new FileStorageServiceImpl(setting.getUri());
				uri = "jdbc:h2:mem:TEST;DB_CLOSE_DELAY=-1"; //$NON-NLS-1$
			} else if (ConnectionMode.TCP.toString().equals(setting.getMode())) {
				fileService = new FileStorageServiceImpl();
				uri = format("jdbc:h2:%s", setting.getUri() + dbName); //$NON-NLS-1$
			} else {
				fileService = new FileStorageServiceImpl(setting.getUri());
				uri = format("jdbc:h2:%s;AUTO_SERVER=TRUE", getDBPath()); //$NON-NLS-1$
			}
		}

		progress(monitor, 10, Messages.StorageServiceImpl_connection_message);
		createEntityManager(uri, setting.getUsername(), setting.getPassword());
		progress(monitor, 60, Messages.StorageServiceImpl_store_setting_message);
		storeSetting(setting);
		for (StorageChangeListener listener : listeners) {
			listener.changedStorage(monitor);
		}
		monitor.done();
	}

	private void progress(IProgressMonitor monitor, int worked, String task) {
		monitor.internalWorked(worked);
		monitor.subTask(task);
	}

	private void storeSetting(StorageSetting setting) {
		StorageSettings settings = getSettings();
		if (settings != null) {
			settings.add(setting);
			settings.storeSttings();
		}
	}

	protected void createEntityManager(String uri, String username, String password) throws StorageConnectException {
		DatabaseProvider provider = createDatabaseProvider(uri, username, password);
		try {
			provider.getConnection();
		} catch (SQLException e) {
			if (org.h2.constant.ErrorCode.FILE_VERSION_ERROR_1 == e.getErrorCode()) {
				databaseMigration(username, password);
			} else {
				throw new StorageConnectException(e);
			}
		}
		if (StorageStatusHandler.isDebug()) {
			StorageStatusHandler.info(format("path:%s", uri), true); //$NON-NLS-1$
		}
		manager = new EntityManager(provider);
	}

	private void databaseMigration(String username, String password) throws StorageConnectException {
		File oldH2Jar = getOldH2Jar();
		try {
			migrator.execute(oldH2Jar, new File(fileService.getStoreRoot()), true, username, password, false);
		} catch (Exception ex) {
			throw new StorageConnectException(ex);
		}
	}

	private File getOldH2Jar() throws StorageConnectException {
		File bundleFile = getBundleFile();
		if (bundleFile == null)
			return null;
		File oldH2Jar = new File(bundleFile, "lib/h2.jar"); //$NON-NLS-1$
		return oldH2Jar;
	}

	private File getBundleFile() throws StorageConnectException {
		StorageActivator activator = StorageActivator.getDefault();
		if (activator == null)
			return null;
		Bundle bundle = activator.getBundle();
		File bundleFile;
		try {
			bundleFile = FileLocator.getBundleFile(bundle);
		} catch (IOException ex) {
			throw new StorageConnectException(ex);
		}
		return bundleFile;
	}

	protected DatabaseProvider createDatabaseProvider(String uri, String username, String password) {
		return new H2DatabaseProvider(uri, username, password);
	}

	public void recreateEntityManagerForTest() throws StorageConnectException {
		assert manager == null || !testmode : Messages.StorageServiceImpl_recreated_message;
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
			for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
				result.add(metaData.getColumnLabel(i));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result.toArray(new String[] {});
	}

	public boolean exportEntity(File csvFile, Class<? extends Entity> entityClass) {
		String SQL = String.format(
				"CALL CSVWRITE('%s', 'SELECT * FROM %s'); ", csvFile.getAbsolutePath(), entityClass.getSimpleName()); //$NON-NLS-1$
		StorageStatusHandler.debug(SQL);
		try {
			CallableStatement prepareCall = getEntityManager().getProvider().getConnection().prepareCall(SQL);
			return prepareCall.execute();
		} catch (SQLException e) {
			String errorMessage = String.format("StorageServiceImpl#exportEntity() is failed. executed '%s'.", SQL); //$NON-NLS-1$
			StorageStatusHandler.fail(e, errorMessage, true);
			return false;
		}
	}

	public boolean importEntity(File csvFile, Class<? extends Entity> entityClass) {
		try {
			String[] columns = getEntityColumnsFromCSV(csvFile);
			StringBuilder target = new StringBuilder();
			boolean first = true;
			for (String column : columns) {
				if (ID.equals(column.toUpperCase()))
					continue;
				if (!first) {
					target.append(COMMA);
				}
				first = false;
				target.append(column);
			}
			String SQL = String
					.format("INSERT INTO %s SELECT null as id,%s FROM CSVREAD('%s');", entityClass.getSimpleName(), target.toString(), csvFile.getAbsolutePath()); //$NON-NLS-1$
			StorageStatusHandler.debug(SQL);
			PreparedStatement prepareCall = getEntityManager().getProvider().getConnection().prepareStatement(SQL);
			prepareCall.execute();
			return true;
		} catch (Exception e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#importEntity()", true); //$NON-NLS-1$
			return false;
		}
	}

	public void deleteAllEntity(Class<? extends Entity> entityClass) {
		try {
			Entity[] entities = getEntityManager().find(entityClass);
			getEntityManager().delete(entities);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#deleteAllEntity()"); //$NON-NLS-1$
		}
	}

	public void deleteTrashedEntity(final Class<? extends GraphicalEntity> entityClass) {
		STORAGE_ACCESS_EXECUTOR.submit(new Callable<Object>() {
			public Object call() throws Exception {
				try {
					EntityManager entityManager = getEntityManager();
					GraphicalEntity[] entities = entityManager.find(entityClass,
							GraphicalEntity.PROP_TRASHED + QUERY,
							true);
					entityManager.delete(entities);
				} catch (SQLException e) {
					StorageStatusHandler.fail(e, "StorageServiceImpl#deleteTrashedEntity()"); //$NON-NLS-1$
				}
				return null;
			}
		});
	}

	public String getDBPath() {
		return fileService.getStoreRoot() + StorageServiceImpl.dbName;
	}

	public <T extends Entity> T createEntity(final Class<T> clazz, final DBParam[] params) {
		Future<T> future = STORAGE_ACCESS_EXECUTOR.submit(new Callable<T>() {
			public T call() throws Exception {
				T entity = null;
				try {
					entity = getEntityManager().create(clazz, params);
				} catch (SQLException e) {
					StringBuilder builder = new StringBuilder();
					for (DBParam param : params) {
						builder.append(String.format("%s:%s", param.getField(), param.getValue())); //$NON-NLS-1$
						builder.append(TAB);
					}
					StorageStatusHandler.fail(e, "StorageServiceImpl#create() '%s'", builder.toString()); //$NON-NLS-1$
				}
				return entity;
			}
		});
		try {
			return future.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#create()", true); //$NON-NLS-1$
		}
		return null;
	}

	public void discard(GraphicalEntity entity) {
		entity.setTrashed(true);
		entity.save(false);
	}

	public void delete(final Entity entity) {
		STORAGE_ACCESS_EXECUTOR.submit(new Callable<Object>() {
			public Object call() throws Exception {
				try {
					getEntityManager().delete(entity);
				} catch (SQLException e) {
					StorageStatusHandler.fail(e, "StorageServiceImpl#delete()"); //$NON-NLS-1$
				}
				return null;
			}
		});
	}

	public void pickup(GraphicalEntity entity) {
		entity.setTrashed(false);
		entity.save(false);
	}

	public int countInTrash(Class<? extends GraphicalEntity> clazz) {
		GraphicalEntity[] results = findInTrash(clazz);
		if (results == null)
			return 0;
		return results.length;
	}

	public StorageSettings getSettings() {
		return this.settings;
	}

	public void setSettings(StorageSettings settings) {
		this.settings = settings;
	}

	public void addStorageChangeListener(StorageChangeListener listener) {
		this.listeners.add(listener);
		Collections.sort(this.listeners, new StorageChageListenerComparator());
	}

	public void removeStorageChangeListener(StorageChangeListener listener) {
		this.listeners.remove(listener);
		Collections.sort(this.listeners, new StorageChageListenerComparator());
	}

	public <T extends Entity> T[] findInTrash(final Class<T> clazz) {
		Future<T[]> future = STORAGE_ACCESS_EXECUTOR.submit(new Callable<T[]>() {
			public T[] call() throws Exception {
				T[] founds = null;
				try {
					founds = getEntityManager().find(clazz, GraphicalEntity.PROP_TRASHED + QUERY, true);
				} catch (SQLException e) {
					StorageStatusHandler.fail(e, "StorageServiceImpl#countInTrash()", true); //$NON-NLS-1$
				}
				return founds;
			}
		});
		try {
			return future.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#countInTrash()", true); //$NON-NLS-1$
		}
		return null;
	}

	public boolean isTestMode() {
		return testmode;
	}

	public void migrate(Class<? extends Entity>... entities) {
		try {
			if (isTestMode()) {
				try {
					recreateEntityManagerForTest();
				} catch (StorageConnectException e) {
					StorageStatusHandler.fail(e, "StorageServiceImpl#migrate()", true); //$NON-NLS-1$
				}
			}
			getEntityManager().migrate(entities);
		} catch (SQLException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#migrate()", true); //$NON-NLS-1$
		}
	}

	public int count(final Class<? extends Entity> clazz) {
		Future<Integer> future = STORAGE_ACCESS_EXECUTOR.submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				try {
					return getEntityManager().count(clazz);
				} catch (SQLException e) {
					StorageStatusHandler.fail(e, "StorageServiceImpl#count()", true); //$NON-NLS-1$
					return 0;
				}
			}
		});
		try {
			return future.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			StorageStatusHandler.fail(e, "StorageServiceImpl#count()", true); //$NON-NLS-1$
		}
		return 0;
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

	public void setDatabaseMigrator(DatabaseMigrator migrator) {
		this.migrator = migrator;
	}

}
