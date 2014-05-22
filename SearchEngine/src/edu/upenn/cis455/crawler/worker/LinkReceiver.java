package edu.upenn.cis455.crawler.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import edu.upenn.cis455.crawler.message.Link;
import edu.upenn.cis455.crawler.message.LinkMessage;

/**
 * This class is a runnable for receiving links
 * 
 * @author martinng
 *
 */
public class LinkReceiver implements Runnable {
	private ServerSocket m_serverSocket = null;
	private boolean m_isStopped = false;
	
	/**
	 * Constructor: open socket
	 */
	public LinkReceiver() {
		try {
			this.m_serverSocket = new ServerSocket(
					CrawlerConfiguration.linkPort);
		} catch (Exception e) {
//			e.printStackTrace();
//			Logger.error("LinkReceiver: " + e.getMessage());
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
					LinkMessage newMessage = (LinkMessage) ois
							.readObject();
//					 System.out.println("Receive links from " +
//					 newMessage.getSourceIP() + ":" +
//					 newMessage.getSourcePort());
					handleMessage(newMessage);
					if (clientSocket != null) {
						clientSocket.close();
					}
				}
			} catch (Exception e) {
//				Logger.error("LinkReceiver run: " + e.getMessage());
			}
		}
	}
	
	/**
	 * This function handles message received
	 * 
	 * @param message
	 */
	private synchronized void handleMessage(LinkMessage message) {
		List<Link> links = message.getLinks();
//			System.out.println("Receive links "
//					+ links.size() + " from "
//					+ message.getSourceIP() + message.getSourcePort());
//			DataAccessor.getSingleton().addForwadURLs(links);
		for (Link link : links) {
			CrawlerManager.m_urlFrontier.enqueue(link);
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
			} catch (IOException e) {
//				Logger.error("LinkReceiver stop: " + e.getMessage());
			}
		}
	}
}

