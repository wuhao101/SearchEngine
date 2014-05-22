package edu.upenn.cis455.crawler.master;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.upenn.cis455.crawler.message.CrawlerMessage;
import edu.upenn.cis455.crawler.message.Link;
import edu.upenn.cis455.crawler.message.MessagePusher;
import edu.upenn.cis455.crawler.message.NodeWrapper;
import edu.upenn.cis455.global.MessageType;
import edu.upenn.cis455.storage.database.DataAccessor;
import edu.upenn.cis455.utils.BlockingQueue;
import edu.upenn.cis455.utils.SHA1Util;

/**
 * This class manages all crawler nodes
 * 
 * @author martinng
 * 
 */
public class CrawlerMaster {
	/*
	 * Properties
	 */
	// static Map<byte[], IPAddress> m_seenDigestToAddr;
	// static Map<byte[], String> m_seenDigestToURL;
	static BlockingQueue<CrawlerMessage> m_seenDigestQueue;
	static Integer m_numOfPages;
	static Set<NodeWrapper> m_crawlerLinkAddrs;
	static Set<NodeWrapper> m_crawlerCommandAddrs;

	private static CrawlerMaster m_crawlerMaster = null;

	private List<MessageReceiver> m_messageReceiverRunnables = null;
	private List<Thread> m_messageReceiverThreads = null;
	private List<SeenDigestor> m_seenDigestorRunnables = null;
	private List<Thread> m_seenDigestorThreads = null;
	private Integer m_isStopped;

	/**
	 * This function returns the singleton of crawler master
	 * 
	 * @return
	 */
	public static CrawlerMaster getSingleton() {
		if (CrawlerMaster.m_crawlerMaster == null) {
			CrawlerMaster.m_crawlerMaster = new CrawlerMaster();
		}
		return CrawlerMaster.m_crawlerMaster;
	}

	/**
	 * Constructor: sets up and start message receiver
	 */
	private CrawlerMaster() {
		this.m_isStopped = 0;
		CrawlerMaster.m_seenDigestQueue = new BlockingQueue<CrawlerMessage>(
				CrawlerMasterConfiguration.numOfDigestorTask);
		// CrawlerMaster.m_seenDigestToAddr = new HashMap<byte[], IPAddress>();
		// CrawlerMaster.m_seenDigestToURL = new HashMap<byte[], String>();
		CrawlerMaster.m_numOfPages = 0;
		CrawlerMaster.m_crawlerLinkAddrs = new HashSet<NodeWrapper>(
				CrawlerMasterConfiguration.numOfCrawlerNodes);
		CrawlerMaster.m_crawlerCommandAddrs = new HashSet<NodeWrapper>(
				CrawlerMasterConfiguration.numOfCrawlerNodes);
		startMsgReceiver();
	}

	/**
	 * This function initializes message receivers thread and starts them
	 */
	private void startMsgReceiver() {
		this.m_messageReceiverRunnables = new ArrayList<MessageReceiver>(
				CrawlerMasterConfiguration.numOfReceivers);
		this.m_messageReceiverThreads = new ArrayList<Thread>(
				CrawlerMasterConfiguration.numOfReceivers);
		for (int i = 0; i < CrawlerMasterConfiguration.numOfReceivers; i++) {
			MessageReceiver receiver = new MessageReceiver(
					CrawlerMasterConfiguration.receiverPorts.get(i));
			this.m_messageReceiverRunnables.add(receiver);
			this.m_messageReceiverThreads.add(new Thread(receiver));
		}
		for (Thread receiverThread : this.m_messageReceiverThreads) {
			receiverThread.start();
		}
	}

	/**
	 * This function notifies all crawler nodes to start to crawl
	 */
	public void startCrawlerNodes() {
		startDigestor();
		distributeEntryURLs(CrawlerMasterConfiguration.entryURLs);
	}

	/**
	 * This function initializes seen digestor threads and starts them
	 */
	private void startDigestor() {
		this.m_seenDigestorRunnables = new ArrayList<SeenDigestor>(
				CrawlerMasterConfiguration.numOfDigestorThread);
		this.m_seenDigestorThreads = new ArrayList<Thread>(
				CrawlerMasterConfiguration.numOfDigestorThread);
		for (int i = 0; i < CrawlerMasterConfiguration.numOfDigestorThread; i++) {
			SeenDigestor digestor = new SeenDigestor(i + 1);
			this.m_seenDigestorRunnables.add(digestor);
			this.m_seenDigestorThreads.add(new Thread(digestor));
		}
		for (Thread seenDigestorThread : this.m_seenDigestorThreads) {
			seenDigestorThread.start();
		}
	}

	/**
	 * This function finds which crawler node the given link needs to be sent to
	 * 
	 * @param linkSHA1
	 * @param numOfCrawler
	 * @return
	 */
	private int findCrawlerNode(String linkSHA1, int numOfCrawler) {
		BigInteger linkHash = new BigInteger(linkSHA1, 16);
		BigInteger num = new BigInteger(String.valueOf(numOfCrawler), 10);
		int index = linkHash.mod(num).intValue();
		if (index == 0) {
			return (numOfCrawler - 1);
		}
		return (index - 1);
	}

	/**
	 * This function distributes entry URLs to each node
	 * 
	 * @param links
	 */
	private void distributeEntryURLs(List<String> links) {
		int num = CrawlerMasterConfiguration.crawlerCommandAddr.size();
		List<CrawlerMessage> messages = new ArrayList<CrawlerMessage>(num);
		MessagePusher messagePusher = new MessagePusher();
		for (int i = 0; i < num; i++) {
			try {
				String ip = CrawlerMasterConfiguration.crawlerCommandAddr
						.get(i).getIPAddr();
				int port = CrawlerMasterConfiguration.crawlerCommandAddr.get(i)
						.getPort();
				CrawlerMessage message = new CrawlerMessage(
						CrawlerMasterConfiguration.localIP, -1, ip, port,
						MessageType.START_CRAWL);
				message.setCrawlerNodes(CrawlerMasterConfiguration.crawlerLinkAddr);
				messages.add(message);
			} catch (Exception e) {
				// Logger.error("distributeEntryURLs parse address " + address
				// + " " + e.getMessage());
			}
		}
		for (String link : links) {
			String linkSHA1 = SHA1Util.byteToString(SHA1Util.generateSHA(link));
			int indexOfNode = findCrawlerNode(linkSHA1, num);
			if (indexOfNode != -1) {
				CrawlerMessage message = messages.get(indexOfNode);
				Link entryLink = new Link();
				entryLink.setURL(link);
				message.addEntryURL(entryLink);
			}
		}
		for (CrawlerMessage message : messages) {
			// System.out.println(this.m_threadID + " " +
			// message.getLinks().toString());
			System.out.println("Sending Start to " + message.getDestIP() + ":"
					+ message.getDestPort());
			messagePusher.sendMessage(message);
		}
	}

	/**
	 * This function stops digestor threads
	 */
	public void stopDigestor() {
		/*
		 * Stop seen digestor threads
		 */
		DataAccessor.getSingleton().shutdown();
		for (SeenDigestor seenDigestorRunnable : this.m_seenDigestorRunnables) {
			seenDigestorRunnable.stop();
		}
		synchronized (CrawlerMaster.m_seenDigestQueue) {
			// CrawlerMaster.m_seenDigestQueue.clear();
			for (Thread seenDigestorThread : this.m_seenDigestorThreads) {
				seenDigestorThread.interrupt();
			}
		}
	}

	/**
	 * This function stops message receiver threads
	 */
	public void stopReceiver() {
		for (MessageReceiver receiverRunnable : this.m_messageReceiverRunnables) {
			receiverRunnable.stop();
		}
		for (Thread receiverThread : this.m_messageReceiverThreads) {
			receiverThread.interrupt();
		}
	}

	/**
	 * This function checks if master has stopped all node
	 * 
	 * @return
	 */
	public boolean isStopped() {
		synchronized (this.m_isStopped) {
			return this.m_isStopped == 1 ? true : false;
		}
	}

	/**
	 * This function set stop flag as true. It's invoked after master receives
	 * complete notification from all crawler nodes.
	 */
	public void setStopped() {
		synchronized (this.m_isStopped) {
			this.m_isStopped = 1;
		}
	}

	/**
	 * This function notifies all nodes to stop
	 */
	public void stopAllNodes() {
		int num = CrawlerMasterConfiguration.crawlerCommandAddr.size();
		List<CrawlerMessage> messages = new ArrayList<CrawlerMessage>(num);
		for (int i = 0; i < num; i++) {
			try {
				String ip = CrawlerMasterConfiguration.crawlerCommandAddr
						.get(i).getIPAddr();
				int port = CrawlerMasterConfiguration.crawlerCommandAddr.get(i)
						.getPort();
				CrawlerMessage message = new CrawlerMessage(
						CrawlerMasterConfiguration.localIP, -1, ip, port,
						MessageType.STOP_CRAWL);
				messages.add(message);
			} catch (Exception e) {
				// Logger.error("stopAllNodes parse address " + address + " "
				// + e.getMessage());
			}
		}
		for (CrawlerMessage message : messages) {
			System.out.println("Sending Stop to " + message.getDestIP() + ":"
					+ message.getDestPort());
			MessagePusher messagePusher = new MessagePusher();
			messagePusher.sendMessage(message);
		}
	}
}
