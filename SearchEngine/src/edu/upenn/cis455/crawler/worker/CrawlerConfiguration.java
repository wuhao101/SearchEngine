package edu.upenn.cis455.crawler.worker;

import java.io.File;
import java.util.List;

import edu.upenn.cis455.crawler.message.NodeWrapper;
import edu.upenn.cis455.utils.SortUtil;

/**
 * This function manages basic configurations of crawler
 * 
 * @author martinng
 * 
 */
public class CrawlerConfiguration {
	/*
	 * Properties
	 */
//	static String nodeHashKey = null;
	static String name = null;
	static int numOfCrawlerThread = 0;
	static int numOfCrawlerTask = 0;
	static double maxDocSize = Double.MAX_VALUE;
	static String localIP = null;
	static int linkPort = -1;
	static int commandPort = -1;
	static String masterIPAddr = null;
	static int masterPort = -1;
	static List<NodeWrapper> crawlerNodes;
	static String linksFileDir;
	static boolean isSetup = false;
	
	public static String getLinksFileDir() { 
		return linksFileDir;
	}
	
	public static void setLinksFileDir(String dir) { 
		linksFileDir = dir;
	}
	/**
	 * This function sets up all configurations of crawler
	 * 
	 * @param localIP
	 * @param linkPort
	 * @param commandPort
	 * @param masterIPAddr
	 * @param masterPort
	 * @param numOfCrawlerThread
	 * @param numOfCrawlerTask
	 * @param maxDocSize
	 * @param linksFileDir
	 * @param name
	 */
	public static void setup(String localIP, int linkPort, int commandPort,
			String masterIPAddr, int masterPort, int numOfCrawlerThread,
			int numOfCrawlerTask, double maxDocSize, String linksFileDir,
			String name) {
		if (!CrawlerConfiguration.isSetup) {
			CrawlerConfiguration.localIP = localIP; //IPUtil.getLocalIP();
			CrawlerConfiguration.linkPort = linkPort;
//			CrawlerConfiguration.nodeHashKey = SHA1Util.byteToString(SHA1Util
//					.generateSHA(CrawlerConfiguration.localIP + ":"
//							+ CrawlerConfiguration.linkPort));
			CrawlerConfiguration.name = name;
			CrawlerConfiguration.commandPort = commandPort;
			CrawlerConfiguration.masterIPAddr = masterIPAddr;
			CrawlerConfiguration.masterPort = masterPort;
			CrawlerConfiguration.numOfCrawlerThread = numOfCrawlerThread;
			CrawlerConfiguration.numOfCrawlerTask = numOfCrawlerTask;
			CrawlerConfiguration.maxDocSize = maxDocSize;
			CrawlerConfiguration.linksFileDir = linksFileDir;
			File linksFile = new File(CrawlerConfiguration.linksFileDir);
			if (linksFile.exists()) {
				linksFile.delete();
				linksFile.mkdirs();
			}
			CrawlerConfiguration.isSetup = false;
		}
	}

	/**
	 * This function sorts crawler nodes according to their hash key
	 * 
	 * @param crawlerNodes
	 */
	public static void sortCrawlerNodes(List<NodeWrapper> crawlerNodes) {
		CrawlerConfiguration.crawlerNodes = crawlerNodes;
		CrawlerConfiguration.crawlerNodes = SortUtil
				.sortNodes(CrawlerConfiguration.crawlerNodes);
		System.out.println("All Nodes:");
		for (NodeWrapper nodeAddr : CrawlerConfiguration.crawlerNodes) {
			System.out.println(nodeAddr.toString());
		}
	}
}
