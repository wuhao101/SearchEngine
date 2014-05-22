package edu.upenn.cis455.crawler.worker.robots;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import edu.upenn.cis455.global.Global;

/**
 * This class fetches robots information from response body
 * 
 * @author martinng
 * 
 */
public class RobotsFetcher {
	private static final int BUFFER_SIZE = 1024;

	/**
	 * This function generates a map from user-agent to robots
	 * 
	 * @param contentWrapper
	 * @return
	 */
	public static Robots generateRobots(URL url) {
		HttpURLConnection connection = null;
		try {
			/*
			 * Get robots.txt from given host
			 */
			if (url.getProtocol() != null && url.getHost() != null) {
				String host = url.getHost();
				URL robotsURL = new URL(url.getProtocol() + "://" + host
						+ "/robots.txt");
				String robotsContent = new String();
				connection = (HttpURLConnection) robotsURL.openConnection();
				connection.setConnectTimeout(3000);
				connection.setReadTimeout(3000);
				connection.connect();
				InputStream robotsStream = connection.getInputStream();
				byte buffer[] = new byte[RobotsFetcher.BUFFER_SIZE];
				int character = -1;
				while ((character = robotsStream.read(buffer)) != -1) {
					robotsContent += new String(buffer, 0, character);
				}
				robotsStream.close();
				/*
				 * Parse robots.txt
				 */
				Map<String, Robots> agentToRobots = RobotsParser
						.parse(robotsContent);
				Robots robots = null;
				if (agentToRobots.containsKey(Global.USER_AGENT)) {
					robots = agentToRobots.get(Global.USER_AGENT);
				} else if (agentToRobots.containsKey("*")) {
					robots = agentToRobots.get("*");
				}
				return robots;
			} else {
				return null;
			}
		} catch (Exception e) {
//			Logger.error("generateRobots " + url.toString() + ": " + e.getMessage());
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
