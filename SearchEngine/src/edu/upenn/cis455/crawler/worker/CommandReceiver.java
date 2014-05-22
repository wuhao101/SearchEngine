package edu.upenn.cis455.crawler.worker;

import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.upenn.cis455.crawler.message.CrawlerMessage;
import edu.upenn.cis455.global.MessageType;
import edu.upenn.cis455.storage.database.DataAccessor;

public class CommandReceiver implements Runnable {
	private ServerSocket m_serverSocket = null;
	private boolean m_isStopped = false;
	
	/**
	 * Constructor: open socket
	 */
	public CommandReceiver() {
		try {
			this.m_serverSocket = new ServerSocket(
					CrawlerConfiguration.commandPort);
		} catch (Exception e) {
			e.printStackTrace();
//			Logger.error("CommandReceiver: " + e.getMessage());
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
//					 System.out.println("Receive from " +
//					 newMessage.getSourceIP() + ":" +
//					 newMessage.getSourcePort() + " " + newMessage.getMessageType());
					handleMessage(newMessage);
					if (clientSocket != null) {
						clientSocket.close();
					}
				}
			} catch (Exception e) {
//				Logger.error("CommandReceiver run: " + e.getMessage());
			}
		}
	}
	
	/**
	 * This function handles message received
	 * 
	 * @param message
	 */
	private synchronized void handleMessage(CrawlerMessage message) {
		MessageType messageType = message.getMessageType();
		if (messageType == MessageType.START_CRAWL) {
			CrawlerConfiguration.sortCrawlerNodes(message.geetCrawlerNodes());
			if (message.isHashEntryURL()) {
				System.out.println("Receive URL from "
						+ message.getSourceIP() + ":" +  message.getSourcePort());
				CrawlerManager.m_entryURL = message.getEntryLinks();
			}
			CrawlerManager.getSingleton().startCrawl();
		} else if (messageType == MessageType.STOP_CRAWL) {
			System.out.println("Receive Stop from "
					+ message.getSourceIP() + ":" +  message.getSourcePort());
			CrawlerManager.getSingleton().stopCrawler();
		} else if (messageType == MessageType.SEEN_HIT) {
			DataAccessor dataAccessor = DataAccessor.getSingleton();
			String urlSHA = message.getURLSHA();
			System.out.println("Receive hits " + urlSHA + " from "
					+ message.getSourceIP() + ":" + message.getSourcePort());
			dataAccessor.updateHits(urlSHA);
		} else if (messageType == MessageType.DEL_DOC) {
			DataAccessor dataAccessor = DataAccessor.getSingleton();
			String urlSHA = message.getURLSHA();
			System.out.println("Receive delete " + urlSHA + " from "
					+ message.getSourceIP() + ":" + message.getSourcePort());
			dataAccessor.deleteDocument(urlSHA);
			String filePath = CrawlerConfiguration.linksFileDir + "/link_"
					+ urlSHA + ".txt";
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * This function checks if this runnable has stopped
	 * 
	 * @return
	 */
	public boolean isStopped() {
		return this.m_isStopped;
	}
	
	/**
	 * This function stops this runnable
	 */
	public void stop() {
		this.m_isStopped = false;
		if (this.m_serverSocket != null) {
			try {
				this.m_serverSocket.close();
			} catch (Exception e) {
//				Logger.error("CommandReceiver stop: " + e.getMessage());
			}
		}
	}
}

