package edu.upenn.cis455.crawler.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.upenn.cis455.crawler.message.CrawlerMessage;
import edu.upenn.cis455.crawler.message.NodeWrapper;
import edu.upenn.cis455.global.MessageType;

public class MessageReceiver implements Runnable {
	private ServerSocket m_serverSocket = null;
	private boolean m_isStopped = false;

	/**
	 * Constructor: open socket
	 */
	public MessageReceiver(int port) {
		try {
			this.m_serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// e.printStackTrace();
			// Logger.error("CrawlerServer: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		while (!isStopped() && !this.m_serverSocket.isClosed()) {
			try {
				Socket clientSocket = this.m_serverSocket.accept();
				if (clientSocket != null) {
					ObjectInputStream ois = new ObjectInputStream(
							clientSocket.getInputStream());
					CrawlerMessage newMessage = (CrawlerMessage) ois
							.readObject();
					// System.out.println("Receive from " +
					// newMessage.getSourceIP() + ":" +
					// newMessage.getSourcePort() + " " +
					// newMessage.getMessageType());
					handleMessage(newMessage);
					if (clientSocket != null) {
						clientSocket.close();
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				// Logger.error("MessageReceiver run: " + e.getMessage());
			}
		}
	}

	/**
	 * This function handles message received
	 * 
	 * @param message
	 */
	private synchronized void handleMessage(CrawlerMessage message) {
		try {
			MessageType messageType = message.getMessageType();
			if (messageType == MessageType.SEEN_DIGEST_QUERY) {
				CrawlerMaster.m_seenDigestQueue.put(message);
			} else if (messageType == MessageType.READY_TO_CRAWL) {
				System.out.println("Receive from " + message.getName() + " "
						+ message.getSourceIP() + ":" + message.getSourcePort()
						+ " " + message.getMessageType());
				String name = message.getName();
				String sourceIP = message.getSourceIP();
				int sourcePort = message.getSourcePort();
				int optionalPort = message.getOptionalPort();
				NodeWrapper commandAddress = new NodeWrapper(name, sourceIP,
						sourcePort);
				NodeWrapper linkAddress = new NodeWrapper(name, sourceIP,
						optionalPort);
				synchronized (CrawlerMaster.m_crawlerLinkAddrs) {
					synchronized (CrawlerMaster.m_crawlerCommandAddrs) {
						CrawlerMaster.m_crawlerLinkAddrs.add(linkAddress);
						CrawlerMaster.m_crawlerCommandAddrs.add(commandAddress);
						if (CrawlerMaster.m_crawlerCommandAddrs.size() == CrawlerMasterConfiguration.numOfCrawlerNodes) {
							CrawlerMasterConfiguration.sortCrawlerNodes(
									CrawlerMaster.m_crawlerLinkAddrs,
									CrawlerMaster.m_crawlerCommandAddrs);
							CrawlerMaster.m_crawlerLinkAddrs.clear();
							CrawlerMaster.m_crawlerCommandAddrs.clear();
							CrawlerMaster.getSingleton().startCrawlerNodes();
						}
					}
				}
			} else if (messageType == MessageType.WORK_COMPLETED) {
				System.out.println("Receive from " + message.getSourceIP()
						+ ":" + message.getSourcePort() + " "
						+ message.getMessageType());
				String sourceIP = message.getSourceIP();
				int sourcePort = message.getSourcePort();
				String name = message.getName();
				NodeWrapper commandAddress = new NodeWrapper(name, sourceIP,
						sourcePort);
				synchronized (CrawlerMaster.m_crawlerCommandAddrs) {
					CrawlerMaster.m_crawlerCommandAddrs.add(commandAddress);
					if (CrawlerMaster.m_crawlerCommandAddrs.size() == CrawlerMasterConfiguration.numOfCrawlerNodes) {
						System.out.println("All nodes have stopped");
						CrawlerMaster.getSingleton().setStopped();
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * This function checks if this runnable has stopped
	 * 
	 * @return
	 */
	public synchronized boolean isStopped() {
		return this.m_isStopped;
	}

	/**
	 * This function stops this runnable
	 */
	public synchronized void stop() {
		this.m_isStopped = false;
		if (this.m_serverSocket != null) {
			try {
				this.m_serverSocket.close();
			} catch (IOException e) {
				// Logger.error("CrawlerServer stop: " + e.getMessage());
			}
		}
	}
}