package edu.upenn.cis455.crawler.worker;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.upenn.cis455.crawler.message.CrawlerMessage;
import edu.upenn.cis455.crawler.message.Link;
import edu.upenn.cis455.crawler.message.LinkMessage;
import edu.upenn.cis455.crawler.message.LinkPusher;
import edu.upenn.cis455.crawler.worker.robots.Robots;
import edu.upenn.cis455.crawler.worker.robots.RobotsFetcher;
import edu.upenn.cis455.global.Global;
import edu.upenn.cis455.global.MessageType;
import edu.upenn.cis455.storage.database.DataAccessor;
import edu.upenn.cis455.storage.wrapper.DescriptionWrapper;
import edu.upenn.cis455.storage.wrapper.DocumentWrapper;
import edu.upenn.cis455.utils.FileUtil;
import edu.upenn.cis455.utils.SHA1Util;

public class Crawler implements Runnable {
	private boolean m_isStopped = false;
	private boolean m_isWorkCompleted = false;
	private Pattern m_charsetPat = Pattern.compile(Global.CHARSET_REGX);
	private Pattern m_equalPat = Pattern.compile(Global.EQUAL_SIGN);

	/**
	 * Constructor: sets the value of thread ID
	 * 
	 * @param threadID
	 */
	public Crawler(int threadID) {
	}

	/**
	 * This function checks if a URL is allowed by the robots
	 * 
	 * @param webURL
	 * @return
	 */
	public boolean allows(URL url) {
		try {
			String host = url.getHost();
			String path = url.getPath();
			Robots robots = null;
			boolean isAllowed = true;
			/*
			 * Check if this robots has been downloaded
			 */
			synchronized (CrawlerManager.m_hostToRobots) {
				robots = CrawlerManager.m_hostToRobots.get(host);
				if (robots != null && robots.needsRefetch()) {
					synchronized (robots) {
						CrawlerManager.m_hostToRobots.remove(host);
						robots = null;
					}
				}
				/*
				 * If there's no robots for this host or the robots has expired,
				 * send GET request to server
				 */
				if (robots == null) {
					robots = RobotsFetcher.generateRobots(url);
					// Put the robots in map
					if (robots != null && host != null) {
						if ((CrawlerManager.m_hostToRobots.size() + 1) >= CrawlerManager.m_maxRobotsNum) {
							String minHost = null;
							long minAccessTime = Long.MAX_VALUE;
							for (Entry<String, Robots> entry : CrawlerManager.m_hostToRobots
									.entrySet()) {
								if (entry.getValue().getLastAccessTime() < minAccessTime) {
									minAccessTime = entry.getValue()
											.getLastAccessTime();
									minHost = entry.getKey();
								}
							}
							CrawlerManager.m_hostToRobots.remove(minHost);
							CrawlerManager.m_hostToRobots.put(host, robots);
						} else {
							CrawlerManager.m_hostToRobots.put(host, robots);
						}
					}
				}

				if (robots != null) {
					synchronized (robots) {
						isAllowed = robots.allows(path);
					}
				} else {
					isAllowed = true;
				}
			}
			return isAllowed;
		} catch (Exception e) {
			// Logger.error("allows: " + e.getMessage());
			return false;
		}
	}

	/**
	 * This function gets next allowable URL
	 * 
	 * @return
	 */
	private Link getNextUrl() {
		try {
			Link nextUrl = null;
			while (nextUrl == null) {
				Link targetLink = CrawlerManager.m_urlFrontier.dequeue();
				// DataAccessor.getSingleton().deleteForwardURL(targetURLString);
				if (targetLink != null) {
					String url = targetLink.getURL();
					URL targetURL = new URL(url);
					byte[] urlSHA1 = SHA1Util.generateSHA(url);
					// System.out.println(this.m_threadID + " check " + url);
					if (isSearched(urlSHA1)) {
						nextUrl = null;
						continue;
					}
					/*
					 * Check if this URL is allowed by the robots
					 */
					if (allows(targetURL)) {
						/*
						 * If it's allowable, check if it's searched in the last
						 * "delay" milliseconds
						 */
						// long lastAccessTime = -1;
						// // Get last access time of this host
						// if
						// (CrawlerManager.m_hostToLastAccessTime.containsKey(targetURL
						// .getHost())) {
						// lastAccessTime =
						// CrawlerManager.m_hostToLastAccessTime
						// .get(targetURL.getHost());
						// }
						// long delay;
						// // Get the crawl delay of this host
						// synchronized (CrawlerManager.m_hostToRobots) {
						// delay =
						// CrawlerManager.m_hostToRobots.get(targetURL.getHost())
						// .getDelay();
						// }
						// // Check the difference of current time and last
						// if (lastAccessTime != -1
						// && ((System.currentTimeMillis() - lastAccessTime) <
						// (delay * 1000))) {
						// CrawlerManager.m_urlFrontier.put(targetURL);
						// } else {
						// nextUrl = url;
						// }
						nextUrl = targetLink;
					} else {
						// System.out.println("Disallow " + url);
					}
				}
			}
			// System.out.println("Thread " + this.m_threadID + " is crawling "
			// + nextUrl.getURL());
			return nextUrl;
		} catch (Exception e) {
			// Logger.error("getNextUrl: " + e.getMessage());
			return null;
		}
	}

	/**
	 * This function checks if this URL has been searched
	 * 
	 * @param urlSHA1
	 * @return
	 */
	private boolean isSearched(byte[] urlSHA1) {
		synchronized (CrawlerManager.m_searchedURLs) {
			for (byte[] searchedURL : CrawlerManager.m_searchedURLs) {
				if (Arrays.equals(searchedURL, urlSHA1)) {
					return true;
				}
			}
			CrawlerManager.m_searchedURLs.add(urlSHA1);
			return false;
		}
	}

	/**
	 * This function checks whether the content has been seen
	 * 
	 * @param digest
	 * @return
	 */
	public boolean hasSeenDigest(String url, byte[] digest) {
		try {
			CrawlerMessage message = new CrawlerMessage(
					CrawlerConfiguration.localIP,
					CrawlerConfiguration.commandPort,
					CrawlerConfiguration.masterIPAddr,
					CrawlerConfiguration.masterPort,
					MessageType.SEEN_DIGEST_QUERY);
			message.setDigest(digest);
			message.setURLSHA(url);

			ObjectOutputStream os = null;
			// ObjectInputStream is = null;
			/*
			 * Send message to master to check if the content has been seen
			 */
			Socket socket = new Socket(CrawlerConfiguration.masterIPAddr,
					CrawlerConfiguration.masterPort);
			os = new ObjectOutputStream(socket.getOutputStream());
			os.writeObject(message);
			os.flush();
			if (socket != null) {
				socket.close();
			}
			return false;
			// // /*
			// // * Receive response message from master
			// // */
			// // is = new ObjectInputStream(new BufferedInputStream(
			// // socket.getInputStream()));
			// // Object obj = is.readObject();
			// //
			// // if (socket != null) {
			// // socket.close();
			// // }
			// // if (obj != null) {
			// // CrawlerMessage respMessage = (CrawlerMessage) obj;
			// // System.out.println("Recv " + respMessage.isSeen());
			// // return respMessage.isSeen();
			// // } else {
			// // System.out.println("Not Recv false");
			// // return false;
			// // }
		} catch (IOException e) {
			// Logger.error("hasSeenDigest: " + e.getMessage());
			return false;
		}
	}

	@Override
	public void run() {
		// int count = 1;
		while (!isStopped()) {
			Link url = getNextUrl();
			// System.out.println(this.m_threadID + " " + count + ": "
			// + url.toString());
			// System.out.println("Thread " + this.m_threadID + " is crawling "
			// + url.getURL());
			if (url != null) {
				crawl(url);
			}

			// System.out.println("Thread " + this.m_threadID +
			// " completed crawling " + url.getURL());
			// count++;
		}
		completeWork();
	}

	/**
	 * This function checks whether this content type is allowed
	 * 
	 * @param contentType
	 * @return
	 */
	private boolean isAllowedType(String contentType) {
		for (String type : Global.ALLOWED_TYPE) {
			if (contentType.toLowerCase().contains(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function sends HEAD request before crawling to check
	 * if-modified-since and document size
	 * 
	 * @param url
	 * @param ifmodifiedsince
	 * @return
	 */
	private HeaderInfo headReqCheck(Link url, long ifmodifiedsince,
			List<Link> links) {
		HeaderInfo headerInfor = null;
		HttpURLConnection connection = null;
		try {
			String urlString = url.getURL();
			URL conURL = new URL(urlString);
			if (conURL.getProtocol() != null && conURL.getHost() != null) {
				connection = (HttpURLConnection) conURL.openConnection();
				connection.setConnectTimeout(3000);
				connection.setReadTimeout(3000);
				connection.setRequestMethod("HEAD");
				connection.setRequestProperty("User-agent", Global.USER_AGENT);
				// connection.setIfModifiedSince(ifmodifiedsince);
				connection.connect();

				int statusCode = connection.getResponseCode();
				/*
				 * Update the last access time of this host
				 */
				synchronized (CrawlerManager.m_hostToLastAccessTime) {
					CrawlerManager.m_hostToLastAccessTime.put(conURL.getHost(),
							System.currentTimeMillis());
				}
				if (statusCode == HttpURLConnection.HTTP_OK) {
					/*
					 * If 200 OK, check the content length
					 */
					String contentType = connection.getContentType();
					int oriContentLength = connection.getContentLength();
					double contentLength = (oriContentLength / 1024) / 1024;
					if (contentType != null && isAllowedType(contentType)) {
						if (contentLength <= CrawlerConfiguration.maxDocSize) {
							headerInfor = new HeaderInfo();
							headerInfor.m_contentLength = oriContentLength;
							headerInfor.m_contentType = contentType;
							Matcher charsetMat = this.m_charsetPat
									.matcher(contentType);
							if (charsetMat.find()) {
								headerInfor.m_charset = this.m_equalPat
										.split(charsetMat.group())[1].trim();
							}
						}
					}
				} else if (statusCode == HttpURLConnection.HTTP_MOVED_PERM
						|| statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					/*
					 * If need redirection, get header location
					 */
					String redirectURL = connection.getHeaderField("Location");
					if (redirectURL != null && !redirectURL.isEmpty()) {
						Link newLink = new Link();
						newLink.setURL(redirectURL);
						links.add(newLink);
					}
				}
			}
			return headerInfor;
		} catch (Exception e) {
			// Logger.error("headReqCheck " + url.toString() + ": "
			// + e.getMessage());
			// e.printStackTrace();
			// System.err.println("1111 " + url.getParURL());
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * This function crawls the given URL
	 * 
	 * @param url
	 */
	private void crawl(Link url) {
		long ifmodifiedsince = 0;
		HttpURLConnection connection = null;
		List<Link> links = new ArrayList<Link>();
		try {
			/*
			 * Send HEAD request to check document size and modified-time
			 */

			HeaderInfo headerInfo = headReqCheck(url, ifmodifiedsince, links);
			if (headerInfo != null) {
				/*
				 * If document isn't html, plain or xml, check if it has enough
				 * information
				 */
				String contentType = headerInfo.m_contentType;
				boolean isNecessary = true;
				if (!contentType.toLowerCase().contains("text/html")) {
					if ((url.getAlt() == null || url.getAlt().isEmpty())
							&& (url.getTitle() == null || url.getTitle()
									.isEmpty())
							&& (url.getText() == null || url.getText()
									.isEmpty())) {
						isNecessary = false;
					}
				}

				if (isNecessary) {
					/*
					 * Get content
					 */
					// System.out.println("Thread " + this.m_threadID +
					// " is downloading " + url.getURL());
					URL connectionURL = new URL(url.getURL());
					if (connectionURL.getProtocol() != null
							&& connectionURL.getHost() != null) {
						connection = (HttpURLConnection) connectionURL
								.openConnection();
						connection.setConnectTimeout(3000);
						connection.setReadTimeout(3000);
						connection.connect();
						InputStream in = new BufferedInputStream(
								connection.getInputStream());
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int n = 0;
						while (-1 != (n = in.read(buf))) {
							out.write(buf, 0, n);
						}
						out.close();
						in.close();
						byte[] response = out.toByteArray();

						String contentString = null;
						if (headerInfo.m_charset != null
								&& !headerInfo.m_charset.isEmpty()) {
							contentString = new String(response,
									headerInfo.m_charset);
						} else {
							contentString = new String(response);
						}
						byte[] contentSHA1 = SHA1Util
								.generateSHA(contentString);

						/*
						 * Check if the content has been seen
						 */
						if (!hasSeenDigest(url.getURL(), contentSHA1)) {
							// System.out.println(url.getURL());
							/*
							 * Extract links
							 */
							DescriptionWrapper[] dispWrapper = new DescriptionWrapper[1];
							// System.out.println(url.getURL() + " " +
							// headerInfo.m_contentType);
							if (headerInfo.m_contentType.toLowerCase()
									.startsWith("text/html")) {
								HtmlParser.extractLinks(dispWrapper, links,
										url.getURL(), response, headerInfo);
							} else if (headerInfo.m_contentType.toLowerCase()
									.contains("text/xml")
									|| headerInfo.m_contentType.toLowerCase()
											.contains("application/xml")) {
								dispWrapper[0] = WordExtractor.extractXML(
										response, url.getURL(),
										headerInfo.m_contentType,
										url.getTitle(), url.getText(),
										url.getParURL(), headerInfo.m_charset);
							} else if (headerInfo.m_contentType.toLowerCase()
									.contains("text/plain")) {
								dispWrapper[0] = WordExtractor.extractPlain(
										response, url.getURL(),
										headerInfo.m_contentType,
										url.getTitle(), url.getText(),
										url.getParURL(), headerInfo.m_charset);
							} else if (headerInfo.m_contentType.toLowerCase()
									.contains("image/gif")
									|| headerInfo.m_contentType.toLowerCase()
											.contains("image/jpeg")
									|| headerInfo.m_contentType.toLowerCase()
											.contains("image/png")) {
								dispWrapper[0] = WordExtractor.extractIMG(
										url.getURL(), headerInfo.m_contentType,
										url.getTitle(), url.getText(),
										url.getAlt(), url.getParTitle(),
										url.getParURL());
							} else if (headerInfo.m_contentType.toLowerCase()
									.contains("application/msword")
									|| headerInfo.m_contentType.toLowerCase()
											.contains("application/pdf")
									|| headerInfo.m_contentType
											.toLowerCase()
											.contains(
													"application/vnd.ms-powerpoint")
									|| headerInfo.m_contentType.toLowerCase()
											.contains("application/x-ppt")) {
								dispWrapper[0] = WordExtractor.extractDoc(
										url.getURL(), headerInfo.m_contentType,
										url.getTitle(), url.getText(),
										url.getParTitle(), url.getParURL());
							}
							/*
							 * Store document
							 */
							DocumentWrapper document = new DocumentWrapper(
									response, url, headerInfo.m_contentLength,
									headerInfo.m_contentType,
									headerInfo.m_charset);
							String docID = SHA1Util.byteToString(SHA1Util
									.generateSHA(url.getURL()));
							DataAccessor.getSingleton().addDocument(docID,
									document, dispWrapper[0]);
							writeLinksToFile(url.getURL(), links);
							synchronized (CrawlerManager.m_numOfPageCrawled) {
								CrawlerManager.m_numOfPageCrawled++;
							}
						}
					}
				}
			}
			// System.out.println("11111111Thread " + this.m_threadID +
			// " completed crawling " + url.getURL());
			sendLinks(links);
		} catch (Exception e) {
			// e.printStackTrace();
			// Logger.error("crawl " + url.toString() + ": " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * This function stops this thread
	 */
	public void stop() {
		this.m_isStopped = true;
	}

	/**
	 * This function checks if this thread has stopped
	 * 
	 * @return
	 */
	private boolean isStopped() {
		CrawlerManager.getNumofPageCrawled();
		return this.m_isStopped;
	}

	private void completeWork() {
		this.m_isWorkCompleted = true;
		synchronized (CrawlerManager.m_numOfCrawlerStopped) {
			CrawlerManager.m_numOfCrawlerStopped++;
		}
	}

	public boolean isWorkCompleted() {
		return this.m_isWorkCompleted;
	}

	/**
	 * This function pushes links parsed to queue
	 * 
	 * @param url
	 * @param links
	 */
	private void writeLinksToFile(String url, List<Link> links) {
		File dir = new File(CrawlerConfiguration.linksFileDir);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		String linkSHA1 = SHA1Util.byteToString(SHA1Util.generateSHA(url));
		String filePath = dir.getPath() + "/link_" + linkSHA1 + ".txt";
		List<String> lines = new ArrayList<String>();
		for (Link link : links) {
			String valueSHA1 = SHA1Util.byteToString(SHA1Util.generateSHA(link
					.getURL()));
			String structure = linkSHA1 + "\t" + valueSHA1 + Global.CRLF;
			// String structure = url + "\t" + link.getURL() + Global.CRLF;
			lines.add(structure);
		}
		FileUtil fileUtil = new FileUtil(filePath);
		fileUtil.writeFile(lines);
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
	 * This function pushes links to each crawler node
	 * 
	 * @param links
	 */
	private void sendLinks(List<Link> links) {
		int num = CrawlerConfiguration.crawlerNodes.size();
		List<LinkMessage> messages = new ArrayList<LinkMessage>(num);
		LinkPusher messagePusher = new LinkPusher();
		for (int i = 0; i < num; i++) {
			try {
				String ip = CrawlerConfiguration.crawlerNodes.get(i).getIPAddr();
				int port = CrawlerConfiguration.crawlerNodes.get(i).getPort();
				LinkMessage message = new LinkMessage(
						CrawlerConfiguration.localIP,
						CrawlerConfiguration.linkPort, ip, port);
				messages.add(message);
			} catch (Exception e) {
				// Logger.error("sendLinks parse address " + address + " "
				// + e.getMessage());
			}
		}
		for (Link link : links) {
			String linkSHA1 = SHA1Util.byteToString(SHA1Util.generateSHA(link
					.getURL()));
			int indexOfNode = findCrawlerNode(linkSHA1, num);
			if (indexOfNode != -1) {
				LinkMessage message = messages.get(indexOfNode);
				message.addLink(link);
			}
		}
		for (LinkMessage message : messages) {
			// System.out.println(this.m_threadID + " " +
			// message.getLinks().toString());
			if (message.getLinks().size() > 0) {
				messagePusher.sendMessage(message);
			}
		}
	}
}
