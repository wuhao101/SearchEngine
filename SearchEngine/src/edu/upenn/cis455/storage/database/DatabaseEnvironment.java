package edu.upenn.cis455.storage.database;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

/**
 * This class configures database environment
 * 
 * @author martinng
 * 
 */
public class DatabaseEnvironment {
	/**
	 * Properties
	 */
	private EnvironmentConfig m_envConfig;
	private Environment m_environment;
	private StoreConfig m_storeConfig;
	private EntityStore m_store;
	private String m_directoryPath;

	/**
	 * Constructor: set up the environment
	 * 
	 * @param direatoryPath
	 */
	public DatabaseEnvironment(String direatoryPath) {
		this.m_directoryPath = direatoryPath;
		File directory = getDirectory(this.m_directoryPath);
		configure();
		this.m_environment = new Environment(directory, this.m_envConfig);
		this.m_store = new EntityStore(this.m_environment, "EntityStore",
				this.m_storeConfig);
	}

	/**
	 * This function configures environment and store
	 */
	private void configure() {
		this.m_envConfig = new EnvironmentConfig();
		this.m_storeConfig = new StoreConfig();

		this.m_envConfig.setReadOnly(false);
		this.m_envConfig.setAllowCreate(true);
		this.m_envConfig.setTransactional(true);

		this.m_storeConfig.setReadOnly(false);
		this.m_storeConfig.setAllowCreate(true);
		this.m_storeConfig.setTransactional(true);
	}

	/**
	 * This function returns the file according to given path, if the file
	 * doesn't exist or isn't directory, create a new one
	 * 
	 * @param direatoryPath
	 * @return
	 */
	private File getDirectory(String direatoryPath) {
		File directory = new File(direatoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			return directory.mkdirs() ? directory : null;
		}
		return directory;
	}

	/**
	 * This function closes this environment
	 */
	public void closeEnvironment() {
		try {
			if (this.m_store != null) {
				this.m_store.close();
			}
			if (this.m_environment != null) {
				this.m_environment.close();
			}
		} catch (DatabaseException e) {
//			Logger.error("closeEnvironment: " + e.getMessage());
		}
	}

	/*
	 * Get methods
	 */

	/**
	 * This function returns the entity store
	 * 
	 * @return
	 */
	public EntityStore getStore() {
		return this.m_store;
	}

	/**
	 * This function returns the environment
	 * 
	 * @return
	 */
	public Environment getEnvironment() {
		return this.m_environment;
	}
}
