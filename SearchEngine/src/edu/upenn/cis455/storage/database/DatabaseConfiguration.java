package edu.upenn.cis455.storage.database;

/**
 * This class contains basic database configurations
 * 
 * @author martinng
 *
 */
public class DatabaseConfiguration {
	static String directoryPath = null;
	static boolean isSetup = false;
	
	/**
	 * This function sets up configurations
	 * 
	 * @param directoryPath
	 */
	public static void setup(String directoryPath) {
		if (!DatabaseConfiguration.isSetup) {
			DatabaseConfiguration.directoryPath = directoryPath;
			DatabaseConfiguration.isSetup = true;
		}
	}
	
	/**
	 * This function returns the directory path for database
	 * 
	 * @return
	 */
	public static String getDirPath() {
		synchronized (DatabaseConfiguration.directoryPath) {
			return DatabaseConfiguration.directoryPath;
		}
	}
}
