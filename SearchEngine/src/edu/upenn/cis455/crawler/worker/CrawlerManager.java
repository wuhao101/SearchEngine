package edu.upenn.cis455.crawler.worker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.upenn.cis455.crawler.message.CrawlerMessage;
import edu.upenn.cis455.crawler.message.Link;
import edu.upenn.cis455.crawler.message.MessagePusher;
import edu.upenn.cis455.crawler.worker.robots.Robots;
import edu.upenn.cis455.global.MessageType;
import edu.upenn.cis455.storage.database.DataAccessor;
import edu.upenn.cis455.utils.BlockingQueue;
/**
 * This class is a manager that control all threads related to crawler
 * 
 * @author martinng
 *
 */
public class CrawlerManager {
	/*
	 * Properties
	 */
	static BlockingQueue<Link> m_urlFrontier;
	static Set<byte[]> m_searchedURLs;
	static Integer m_numOfPageCrawled;
	static Map<String, Robots> m_hostToRobots;
	static Map<String, Long> m_hostToLastAccessTime;
	static List<Link> m_entryURL;
	static Integer m_numOfCrawlerStopped = 0;
	static int m_maxRobotsNum = 5000;
	
	private static CrawlerManager m_crawlerManager = null;

	private List<Crawler> m_crawlerRunnables = null;
	private List<Thread> m_crawlerThreads = null;
	private LinkReceiver m_linkReceiverRunnable = null;
	private Thread m_linkReceiverThread = null;
	private CommandReceiver m_commandReceiverRunnable = null;
	private Thread m_commandReceiverThread = null;
	private Integer m_isManagerStopped;

	/**
	 * This function returns singleton of crawler manager. If it's null, create
	 * a new one
	 * 
	 * @return
	 */
	public static CrawlerManager getSingleton() {
		if (CrawlerManager.m_crawlerManager == null) {
			CrawlerManager.m_crawlerManager = new CrawlerManager();
		}
		return CrawlerManager.m_crawlerManager;
	}

	/**
	 * Constructor: sets up and start crawler server thread
	 * 
	 * @throws FileNotFoundException
	 */
	private CrawlerManager() {
		this.m_isManagerStopped = 0;
		CrawlerManager.m_numOfCrawlerStopped = 0;
		CrawlerManager.m_urlFrontier = new BlockingQueue<Link>(
				CrawlerConfiguration.numOfCrawlerTask);
		CrawlerManager.m_searchedURLs = new HashSet<byte[]>();
		CrawlerManager.m_numOfPageCrawled = 0;
		CrawlerManager.m_hostToRobots = new HashMap<String, Robots>(
				CrawlerManager.m_maxRobotsNum);
		CrawlerManager.m_hostToLastAccessTime = new HashMap<String, Long>();
		startReceivers();
		this.m_crawlerRunnables = new ArrayList<Crawler>(
				CrawlerConfiguration.numOfCrawlerThread);
		this.m_crawlerThreads = new ArrayList<Thread>(
				CrawlerConfiguration.numOfCrawlerThread);
		/*
		 * Initialize threads
		 */
		for (int i = 0; i < CrawlerConfiguration.numOfCrawlerThread; i++) {
			Crawler crawler = new Crawler(i + 1);
			this.m_crawlerRunnables.add(crawler);
			this.m_crawlerThreads.add(new Thread(crawler));
		}
		MessagePusher messagePusher = new MessagePusher();
		CrawlerMessage message = new CrawlerMessage(
				CrawlerConfiguration.localIP, CrawlerConfiguration.commandPort,
				CrawlerConfiguration.masterIPAddr,
				CrawlerConfiguration.masterPort, MessageType.READY_TO_CRAWL);
		message.setName(CrawlerConfiguration.name);
		message.setOptionalPort(CrawlerConfiguration.linkPort);
		messagePusher.sendMessage(message);
	}
	
	/**
	 * This function starts to crawl
	 */
	public void startCrawl() {
		/*
		 * Start all threads
		 */
		if (m_entryURL != null) {
			for (Link url : CrawlerManager.m_entryURL) {
//				System.out.println("Receive URL " + url.getURL());
				if (url != null) {
					CrawlerManager.m_urlFrontier.enqueue(url);
				}
			}
		}

		startCrawlerThreads();
	}

	/**
	 * This function initialized crawler threads and starts them
	 */
	private void startCrawlerThreads() {
		/*
		 * Start threads
		 */
		for (Thread crawlerThread : this.m_crawlerThreads) {
			crawlerThread.start();
		}
	}

	/**
	 * This function initialize receiver threads and starts them
	 */
	private void startReceivers() {
		/*
		 * Start command receiver
		 */
		this.m_commandReceiverRunnable = new CommandReceiver();
		this.m_commandReceiverThread = new Thread(this.m_commandReceiverRunnable);
		this.m_commandReceiverThread.start();
		
		/*
		 * Start link receiver
		 */
		this.m_linkReceiverRunnable = new LinkReceiver();
		this.m_linkReceiverThread = new Thread(this.m_linkReceiverRunnable);
		this.m_linkReceiverThread.start();
	}

	/**
	 * This function returns current number of page crawled
	 * 
	 * @return
	 */
	static int getNumofPageCrawled() {
		synchronized (CrawlerManager.m_numOfPageCrawled) {
			System.out.println("Number of pages "
					+ CrawlerManager.m_numOfPageCrawled);
			return CrawlerManager.m_numOfPageCrawled.intValue();
		}
	}
	
	/**
	 * This function stops crawler threads
	 * 
	 * @throws IOException
	 */
	public void stopCrawler() {
		stopServer();
		/*
		 * Stop crawler
		 */
		for (Crawler crawlerRunnable : this.m_crawlerRunnables) {
			crawlerRunnable.stop();
		}
		while(!isCrawlerStopped())
			;
		
		DataAccessor.getSingleton().shutdown();
		
		// Release blocking queue resource
		synchronized (CrawlerManager.m_urlFrontier) {
//			CrawlerManager.m_urlFrontier.clear();;
			CrawlerManager.m_urlFrontier.notifyAll();
			for (Thread crawlerThread : this.m_crawlerThreads) {
				crawlerThread.interrupt();
			}
		}
		
		/*
		 * Notify master this crawler node has stopped
		 */
		CrawlerMessage message = new CrawlerMessage(
				CrawlerConfiguration.localIP, CrawlerConfiguration.commandPort,
				CrawlerConfiguration.masterIPAddr,
				CrawlerConfiguration.masterPort, MessageType.WORK_COMPLETED);
		message.setName(CrawlerConfiguration.name);
		MessagePusher messagePusher = new MessagePusher();
		messagePusher.sendMessage(message);

		stopManager();
	}
	
	/**
	 * This function stops receiver threads
	 */
	public void stopServer() {
		/*
		 * Stop link receiver
		 */
		this.m_linkReceiverRunnable.stop();
		this.m_linkReceiverThread.interrupt();
		
		/*
		 * Stop command receiver
		 */
		this.m_commandReceiverRunnable.stop();
		this.m_commandReceiverThread.interrupt();
	}
	
	/**
	 * This function checks if crawlers have completed work
	 * 
	 * @return
	 */
	private boolean isCrawlerStopped() {
//		for (Crawler crawlerRunnable : this.m_crawlerRunnables) {
//			if (!crawlerRunnable.isWorkCompleted()) {
//				return false;
//			}
//		}
//		return true;
		synchronized (CrawlerManager.m_numOfCrawlerStopped) {
			if (CrawlerManager.m_numOfCrawlerStopped >= CrawlerConfiguration.numOfCrawlerThread) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isManagerStopped() {
		synchronized (this.m_isManagerStopped) {
			return this.m_isManagerStopped == 1 ? true : false;
		}
	}
	
	private void stopManager() {
		synchronized (this.m_isManagerStopped) {
			this.m_isManagerStopped = 1;
			System.out.println(String.valueOf(this.m_isManagerStopped));
		}
	}
}
