package edu.upenn.cis455.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import edu.upenn.cis455.global.Global;

/**
 * This class logs all messages and errors
 * 
 * @author martinng
 * 
 */
public class Logger {
	/*
	 * Properties
	 */
	private final static String ERROR_FILE = "./ErrorLog";
	private final static String MSG_FILE = "./MessageLog";

	private static Vector<String> errorLog = new Vector<String>();
	private static Vector<String> messageLog = new Vector<String>();

	private static FileUtil errorFileUtil = new FileUtil(ERROR_FILE);
	private static FileUtil msgFileUtil = new FileUtil(MSG_FILE);

	/**
	 * This function pushes error message to a vector
	 * 
	 * @param message
	 */
//	public synchronized static void error(String errorMsg) {
//		Calendar cal = Calendar.getInstance();
//		SimpleDateFormat format = new SimpleDateFormat(
//				"EEEEEE, dd-MMM-yy hh:mm:ss zzz");
//		String timeStamp = (format.format(cal.getTime()) + " ");
//		Logger.errorLog.add(timeStamp + errorMsg + Global.CRLF);
//	}
	
	/**
	 * This function logs all current errors to file
	 */
	synchronized static void logError() {
		for (String line : Logger.errorLog) {
			Logger.errorFileUtil.writeFile(line);
		}
		Logger.errorLog.clear();
	}
	
	/**
	 * This function pushes a normal message to a vector
	 * 
	 * @param message
	 */
	public synchronized static void message(String message) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(
				"EEEEEE, dd-MMM-yy hh:mm:ss zzz");
		String timeStamp = (format.format(cal.getTime()) + " ");
		Logger.messageLog.add(timeStamp + message + Global.CRLF);
	}
	
	/**
	 * This function logs all current normal messages to file
	 */
	synchronized static void logMessage() {
		for (String line : Logger.messageLog) {
			Logger.msgFileUtil.writeFile(line);
		}
		Logger.messageLog.clear();
	}
	
	public static void write() {
		synchronized (errorLog) {
			Iterator<String> it = errorLog.iterator();
			for (; it.hasNext();) {
				String error = (String) it.next();
				System.out.println(error);
			}
		}
	}
}
