package edu.upenn.cis455.utils;

import java.util.TimerTask;

/**
 * This class is a timer for periodically logging errors and messages
 * @author martinng
 *
 */
public class LoggerTimer extends TimerTask {
	@Override
	public void run() {
		Logger.logError();
		Logger.logMessage();
	}
}
