package edu.upenn.cis455.crawler.worker.robots;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class parses robots.txt and extracts effective information: user-agent,
 * disallow, allow, and crawl-delay
 * 
 * @author martinng
 * 
 */
public class RobotsParser {
	/*
	 * Properties
	 */
	private static final String PATTERNS_USERAGENT = "(?i)^User-agent:.*";
	private static final String PATTERNS_DISALLOW = "(?i)Disallow:.*";
	private static final String PATTERNS_ALLOW = "(?i)Allow:.*";
	private static final String PATTERNS_DELAY = "(?i)Crawl-delay:.*";

	private static final int PATTERNS_USERAGENT_LENGTH = 11;
	private static final int PATTERNS_DISALLOW_LENGTH = 9;
	private static final int PATTERNS_ALLOW_LENGTH = 6;
	private static final int PATTERNS_DELAY_LENGTH = 12;
	
	/**
	 * This function parses robots.txt and maps user-agent to robots
	 * 
	 * @param content
	 * @param myUserAgent
	 * @return
	 */
	public static Map<String, Robots> parse(String content) {
		Map<String, Robots>agentToRobots = new HashMap<String, Robots>();
		Robots currentRobots = new Robots();
		String currentAgent = null;
		StringTokenizer st = new StringTokenizer(content, "\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();

			int commentIndex = line.indexOf("#");
			if (commentIndex > -1) {
				line = line.substring(0, commentIndex);
			}

			/*
			 * remove any html markup
			 */
			line = line.replaceAll("<[^>]+>", "");

			line = line.trim();

			if (line.length() == 0) {
				continue;
			}

			if (line.matches(PATTERNS_USERAGENT)) {
				/*
				 * If a user-agent ends, put the previous one in map
				 */
				agentToRobots.put(currentAgent, currentRobots);
				currentRobots = new Robots();
				currentAgent = null;
				currentAgent = line.substring(PATTERNS_USERAGENT_LENGTH).trim()
						.toLowerCase();
			} else if (line.matches(PATTERNS_DISALLOW)) {
				/*
				 * Add disallow url to current robots
				 */
				String path = line.substring(PATTERNS_DISALLOW_LENGTH).trim();
				if (path.endsWith("*")) {
					path = path.substring(0, path.length() - 1);
				}
				path = path.trim();
				if (path.length() > 0) {
					currentRobots.addDisallow(path);
				}
			} else if (line.matches(PATTERNS_ALLOW)) {
				/*
				 * Add allow url to current robots
				 */
				String path = line.substring(PATTERNS_ALLOW_LENGTH).trim();
				if (path.endsWith("*")) {
					path = path.substring(0, path.length() - 1);
				}
				path = path.trim();
				currentRobots.addAllow(path);
			} else if (line.matches(PATTERNS_DELAY)) {
				/*
				 * Add crawl-delay to current robots
				 */
				long delay = Long.parseLong(line.substring(PATTERNS_DELAY_LENGTH).trim());
				currentRobots.setDelay(delay);
			}
		}
		
		/*
		 * Put the last one in map
		 */
		if (currentAgent != null) {
			agentToRobots.put(currentAgent, currentRobots);
		}
		
		return agentToRobots;
	}
}
