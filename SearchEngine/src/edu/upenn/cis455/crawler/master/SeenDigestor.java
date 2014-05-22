package edu.upenn.cis455.crawler.master;

import java.util.Map.Entry;
import java.util.regex.Pattern;

import edu.upenn.cis455.crawler.message.CrawlerMessage;
import edu.upenn.cis455.crawler.message.MessagePusher;
import edu.upenn.cis455.global.Global;
import edu.upenn.cis455.global.IPAddress;
import edu.upenn.cis455.global.MessageType;
import edu.upenn.cis455.storage.database.DataAccessor;
import edu.upenn.cis455.utils.SHA1Util;

public class SeenDigestor implements Runnable {
	/*
	 * Properties
	 */
	private boolean m_isStopped = false;
	@SuppressWarnings("unused")
	private int m_threadID;
	private final Pattern m_addressPat = Pattern.compile(Global.COLON);

	/**
	 * Constructor: sets the thread ID and socket
	 * 
	 * @param threadID
	 */
	public SeenDigestor(int threadID) {
		this.m_threadID = threadID;
	}

	/**
	 * This function checks whether the content has been seen
	 * 
	 * @param digest
	 * @return
	 */
	public CrawlerMessage hasSeenDigest(byte[] digest, String urlSHA,
			IPAddress sourceAddress) {
		String contentSHA = SHA1Util.byteToString(digest);
		String address = sourceAddress.getIPAddress() + ":"
				+ sourceAddress.getPort();
		Entry<String, String> contentEntry = DataAccessor.getSingleton()
				.digestContent(contentSHA, urlSHA, address);
		CrawlerMessage hitMessage = null;
		if (contentEntry != null) {
			String[] pair = this.m_addressPat.split(contentEntry.getValue());
			String ipAddr = pair[0].trim();
			int port = Integer.valueOf(pair[1].trim()).intValue();
			hitMessage = new CrawlerMessage(CrawlerMasterConfiguration.localIP,
					-1, ipAddr, port, MessageType.SEEN_HIT);
			hitMessage.setURLSHA(contentEntry.getKey());
		}
		return hitMessage;
	}

	@Override
	public void run() {
		while (!isStopped()) {
			/*
			 * Extract information from message wrapper
			 */
			CrawlerMessage message;
			message = CrawlerMaster.m_seenDigestQueue.dequeue();

			// CrawlerMessage message = pair.message;
			// Socket socket = pair.clientSocket;
			if (message != null) {
				String urlSHA = message.getURLSHA();
				byte[] digest = message.getSeenDigest();
				String sourceIP = message.getSourceIP();
				int sourcePort = message.getSourcePort();
				IPAddress sourceAddress = new IPAddress(sourceIP, sourcePort);
				/*
				 * Create response message
				 */
				MessagePusher messagePusher = new MessagePusher();
				CrawlerMessage respMessage = new CrawlerMessage(
						CrawlerMasterConfiguration.localIP, -1, sourceIP,
						sourcePort, MessageType.DEL_DOC);
				CrawlerMessage hitMessage = hasSeenDigest(digest, urlSHA,
						sourceAddress);
				if (hitMessage != null) {
					/*
					 * If the content has been seen, notify the owner node
					 */
					respMessage.setURLSHA(urlSHA);
					messagePusher.sendMessage(respMessage);
					messagePusher.sendMessage(hitMessage);
				} else {
					/*
					 * Check current number of pages crawled
					 */
					synchronized (CrawlerMaster.m_numOfPages) {
						if (CrawlerMaster.m_numOfPages != -1) {
							CrawlerMaster.m_numOfPages++;
							System.out.println("Current "
									+ CrawlerMaster.m_numOfPages);
							if (CrawlerMaster.m_numOfPages >= CrawlerMasterConfiguration.maxNumOfPage) {
								CrawlerMaster.getSingleton().stopAllNodes();
								CrawlerMaster.m_numOfPages = -1;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This function checks if this thread has been stopped
	 * 
	 * @return
	 */
	public synchronized boolean isStopped() {
		return this.m_isStopped;
	}

	/**
	 * This function stops this thread
	 */
	public synchronized void stop() {
		this.m_isStopped = true;
	}
}
