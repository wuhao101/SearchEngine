package edu.upenn.cis455.crawler.master;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.upenn.cis455.crawler.message.NodeWrapper;
import edu.upenn.cis455.utils.IPUtil;
import edu.upenn.cis455.utils.SortUtil;

/**
 * This function manages basic configurations of crawler master
 * 
 * @author martinng
 * 
 */
public class CrawlerMasterConfiguration {
	/*
	 * Properties
	 */
	static int numOfReceivers = 0;
	static List<Integer> receiverPorts = null;
	static String localIP = null;
	static List<String> entryURLs = null;
	static int numOfCrawlerNodes = 0;
	static List<NodeWrapper> crawlerCommandAddr;
	static List<NodeWrapper> crawlerLinkAddr;
	static int numOfDigestorThread = 0;
	static int numOfDigestorTask = 0;
	static int maxNumOfPage = 0;

	/**
	 * This function sets up all configurations of crawler master
	 * 
	 * @param numOfReceivers
	 * @param receiverPorts
	 * @param entryURLs
	 * @param numOfCrawlerNodes
	 * @param numOfDigestorThread
	 * @param numOfDigestorTask
	 * @param maxNumOfPage
	 */
	public static void setup(int numOfReceivers, List<Integer> receiverPorts,
			List<String> entryURLs, int numOfCrawlerNodes,
			int numOfDigestorThread, int numOfDigestorTask, int maxNumOfPage) {
		CrawlerMasterConfiguration.numOfReceivers = numOfReceivers;
		CrawlerMasterConfiguration.localIP = IPUtil.getLocalIP();
		CrawlerMasterConfiguration.receiverPorts = receiverPorts;
		CrawlerMasterConfiguration.entryURLs = entryURLs;
		CrawlerMasterConfiguration.numOfCrawlerNodes = numOfCrawlerNodes;
		CrawlerMasterConfiguration.crawlerLinkAddr = new ArrayList<>(numOfCrawlerNodes);
		CrawlerMasterConfiguration.crawlerCommandAddr = new ArrayList<>(numOfCrawlerNodes);
		CrawlerMasterConfiguration.numOfDigestorThread = numOfDigestorThread;
		CrawlerMasterConfiguration.numOfDigestorTask = numOfDigestorTask;
		CrawlerMasterConfiguration.maxNumOfPage = maxNumOfPage;
	}
	
	/**
	 * This function sets crawler nodes addresses and sorts crawler nodes
	 * according to their hash key
	 * 
	 * @param crawlerNodes
	 */
	public static void sortCrawlerNodes(Set<NodeWrapper> crawlerLinkAddrs,
			Set<NodeWrapper> crawlerCommandAddrs) {
		for (NodeWrapper nodeAddr : crawlerCommandAddrs) {
			CrawlerMasterConfiguration.crawlerCommandAddr.add(nodeAddr);
		}
		for (NodeWrapper nodeAddr : crawlerLinkAddrs) {
			CrawlerMasterConfiguration.crawlerLinkAddr.add(nodeAddr);
		}
		CrawlerMasterConfiguration.crawlerLinkAddr = SortUtil
				.sortNodes(CrawlerMasterConfiguration.crawlerLinkAddr);
		CrawlerMasterConfiguration.crawlerCommandAddr = SortUtil
				.sortNodes(CrawlerMasterConfiguration.crawlerCommandAddr);
		System.out.println("Command:");
		for (NodeWrapper nodeAddr : CrawlerMasterConfiguration.crawlerCommandAddr) {
			System.out.println(nodeAddr.toString());
		}

		System.out.println("Links:");
		for (NodeWrapper nodeAddr : CrawlerMasterConfiguration.crawlerLinkAddr) {
			System.out.println(nodeAddr.toString());
		}
	}
}
