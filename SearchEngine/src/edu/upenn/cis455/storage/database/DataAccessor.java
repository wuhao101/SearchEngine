package edu.upenn.cis455.storage.database;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis455.crawler.message.Link;
import edu.upenn.cis455.storage.wrapper.DescriptionWrapper;
import edu.upenn.cis455.storage.wrapper.DocumentWrapper;

/**
 * This class accesses and modifies data
 * 
 * @author martinng
 *
 */
public class DataAccessor {
	/*
	 * Properties
	 */
	private static DataAccessor m_accessor = null;
	private DataIndices m_dataIndices = null;
	private DatabaseEnvironment m_databaseEnv = null;
	
	/**
	 * This function returns the singleton of accessor
	 * 
	 * @param directoryPath
	 * @return
	 */
	public synchronized static DataAccessor getSingleton() {
		if (DataAccessor.m_accessor == null) {
			DataAccessor.m_accessor = new DataAccessor(DatabaseConfiguration.getDirPath());
		}
		return DataAccessor.m_accessor;
	}
	
	/**
	 * Constructor: set up configurations
	 * 
	 * @param directoryPath
	 */
	private DataAccessor(String directoryPath) {
		this.m_databaseEnv = new DatabaseEnvironment(directoryPath);
		this.m_dataIndices = new DataIndices(this.m_databaseEnv.getStore());
	}
	
	public void shutdown() {
		synchronized (this.m_databaseEnv) {
			this.m_databaseEnv.closeEnvironment();
			synchronized (DataAccessor.m_accessor) {
				DataAccessor.m_accessor = null;
			}
		}
	}
	
	public long getDCount() {
		return (this.m_dataIndices.descriptionIndex.count());
	}
	
	public long getCount() {
		return (this.m_dataIndices.htmlIndex.count() + 
				this.m_dataIndices.docIndex.count() +
				this.m_dataIndices.imgIndex.count());
	}
	
	public List<String> getKeys(String dir) {
		List<String> allKeys = null;
		File linksDir = new File(dir);
		File[] fileArray = null;
		if (linksDir.isDirectory()) {
			fileArray = linksDir.listFiles();
		}
		if (fileArray != null) {
			allKeys = new ArrayList<String>(fileArray.length);
		}
		for (File f : fileArray) {
			String name = f.getName();
			name = name.substring(5).trim();
			int len = name.length();
			name = name.substring(0, len - 4);
			allKeys.add(name);
		}
		return allKeys;
	}
	/**
	 * This function adds a new document to database
	 * 
	 * @param docID
	 * @param document
	 */
	public void addDocument(String docID, DocumentWrapper document, DescriptionWrapper description) {
		String contentType = document.getType().toLowerCase();
		if (contentType.contains("image/gif")
				|| contentType.contains("image/jpeg")
				|| contentType.contains("image/png")) {
			synchronized (this.m_dataIndices.imgIndex) {
				ImgEntity documentEntity = this.m_dataIndices.imgIndex.get(docID);
				if (documentEntity == null) {
					documentEntity = new ImgEntity();
				}
				documentEntity.setDocId(docID);
				documentEntity.setContent(document.getContent());
				Link link = document.getURL();
				documentEntity.setURL(link.getURL());
				documentEntity.setSize(document.getSize());
				documentEntity.setType(document.getType());
				documentEntity.addHits();
				documentEntity.setCrawledTime(document.getLastModifiedTime());
				documentEntity.setAlt(link.getAlt());
				documentEntity.setText(link.getText());
				documentEntity.setParTitle(link.getParTitle());
				documentEntity.setParURL(link.getParURL());
				documentEntity.setTitle(link.getTitle());
				this.m_dataIndices.imgIndex.put(documentEntity);
			}
		} else if (contentType.contains("application/msword")
				|| contentType.contains("application/pdf")
				|| contentType.contains("application/vnd.ms-powerpoint")
				|| contentType.contains("application/x-ppt")) {
			synchronized (this.m_dataIndices.docIndex) {
				DocEntity documentEntity = this.m_dataIndices.docIndex.get(docID);
				if (documentEntity == null) {
					documentEntity = new DocEntity();
				}
				documentEntity.setDocId(docID);
				documentEntity.setContent(document.getContent());
				Link link = document.getURL();
				documentEntity.setURL(link.getURL());
				documentEntity.setSize(document.getSize());
				documentEntity.setType(document.getType());
				documentEntity.addHits();
				documentEntity.setCrawledTime(document.getLastModifiedTime());
				documentEntity.setParTitle(link.getParTitle());
				documentEntity.setParURL(link.getParURL());
				documentEntity.setText(link.getText());
				documentEntity.setTitle(link.getTitle());
				this.m_dataIndices.docIndex.put(documentEntity);
			}
		} else {
			synchronized (this.m_dataIndices.htmlIndex) {
				HtmlEntity documentEntity = this.m_dataIndices.htmlIndex.get(docID);
				if (documentEntity == null) {
					documentEntity = new HtmlEntity();
				}
				documentEntity.setDocId(docID);
				documentEntity.setContent(document.getContent());
				Link link = document.getURL();
				documentEntity.setURL(link.getURL());
				documentEntity.setSize(document.getSize());
				documentEntity.setType(document.getType());
				documentEntity.addHits();
				documentEntity.setCrawledTime(document.getLastModifiedTime());
				documentEntity.setCharset(document.getCharset());
				this.m_dataIndices.htmlIndex.put(documentEntity);
			}
		}
		if (description != null) {
			addDescription(docID, description);
		}
	}
	
	public Entry<Long, Integer> getTimeAndHits(String docID) {
		Entry<Long, Integer> result = null;
		synchronized (this.m_dataIndices.htmlIndex) {
			HtmlEntity documentEntity = this.m_dataIndices.htmlIndex.get(docID);
			if (documentEntity != null) {
				result = new AbstractMap.SimpleEntry<Long, Integer>(
						documentEntity.getCrawledTime(),
						documentEntity.getHits());
				return result;
			}
		}
		synchronized (this.m_dataIndices.imgIndex) {
			ImgEntity documentEntity = this.m_dataIndices.imgIndex.get(docID);
			if (documentEntity != null) {
				result = new AbstractMap.SimpleEntry<Long, Integer>(
						documentEntity.getCrawledTime(),
						documentEntity.getHits());
				return result;
			}
		}
		synchronized (this.m_dataIndices.docIndex) {
			DocEntity documentEntity = this.m_dataIndices.docIndex.get(docID);
			if (documentEntity != null) {
				result = new AbstractMap.SimpleEntry<Long, Integer>(
						documentEntity.getCrawledTime(),
						documentEntity.getHits());
				return result;
			}
		}

		return null;
	}
	
	public byte[] getImgContent(String docID, String contentType) {
		contentType = contentType.toLowerCase();
		if (contentType.contains("image/gif")
				|| contentType.contains("image/jpeg")
				|| contentType.contains("image/png")) {
			synchronized (this.m_dataIndices.imgIndex) {
				ImgEntity documentEntity = this.m_dataIndices.imgIndex.get(docID);
				if (documentEntity != null) {
					return documentEntity.getContent();
				}
			}
		}
		
		return null;
	}
	/**
	 * This function updates the page rank of this page
	 * 
	 * @param docID
	 * @param pageRank
	 */
	public void updatePageRank(String docID, double pageRank) {
		synchronized (this.m_dataIndices.htmlIndex) {
			HtmlEntity documentEntity = this.m_dataIndices.htmlIndex
					.get(docID);
			if (documentEntity != null) {
				documentEntity.setPageRank(pageRank);
				this.m_dataIndices.htmlIndex.put(documentEntity);
				return;
			}
		}
		synchronized (this.m_dataIndices.imgIndex) {
			ImgEntity documentEntity = this.m_dataIndices.imgIndex
					.get(docID);
			if (documentEntity != null) {
				documentEntity.setPageRank(pageRank);
				this.m_dataIndices.imgIndex.put(documentEntity);
				return;
			}
		}
		synchronized (this.m_dataIndices.docIndex) {
			DocEntity documentEntity = this.m_dataIndices.docIndex
					.get(docID);
			if (documentEntity != null) {
				documentEntity.setPageRank(pageRank);
				this.m_dataIndices.docIndex.put(documentEntity);
				return;
			}
		}
	}
	
	/**
	 * This function adds a hit of this page
	 * 
	 * @param docID
	 */
	public void updateHits(String docID) {
		synchronized (this.m_dataIndices.htmlIndex) {
			HtmlEntity documentEntity = this.m_dataIndices.htmlIndex
					.get(docID);
			if (documentEntity != null) {
				documentEntity.addHits();
				this.m_dataIndices.htmlIndex.put(documentEntity);
				return;
			}
		}
		synchronized (this.m_dataIndices.imgIndex) {
			ImgEntity documentEntity = this.m_dataIndices.imgIndex
					.get(docID);
			if (documentEntity != null) {
				documentEntity.addHits();
				this.m_dataIndices.imgIndex.put(documentEntity);
				return;
			}
		}
		synchronized (this.m_dataIndices.docIndex) {
			DocEntity documentEntity = this.m_dataIndices.docIndex
					.get(docID);
			if (documentEntity != null) {
				documentEntity.addHits();
				this.m_dataIndices.docIndex.put(documentEntity);
				return;
			}
		}
	}
	
	/**
	 * This function deletes a document
	 * 
	 * @param docID
	 */
	public boolean deleteDocument(String docID) {
		synchronized (this.m_dataIndices.htmlIndex) {
			HtmlEntity documentEntity = this.m_dataIndices.htmlIndex
					.get(docID);
			if (documentEntity != null) {
				this.m_dataIndices.htmlIndex.delete(docID);
				deleteDescription(docID);
				return true;
			}
		}
		synchronized (this.m_dataIndices.imgIndex) {
			ImgEntity documentEntity = this.m_dataIndices.imgIndex
					.get(docID);
			if (documentEntity != null) {
				this.m_dataIndices.imgIndex.delete(docID);
				deleteDescription(docID);
				return true;
			}
		}
		synchronized (this.m_dataIndices.docIndex) {
			DocEntity documentEntity = this.m_dataIndices.docIndex
					.get(docID);
			if (documentEntity != null) {
				this.m_dataIndices.docIndex.delete(docID);
				deleteDescription(docID);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This function returns all documents
	 * 
	 * @return
	 */
	public EntityCursor<DocEntity> getAllDocs() {
		synchronized (this.m_dataIndices.docIndex) {
			return this.m_dataIndices.docIndex.entities();
		}
	}
	
	public EntityCursor<ImgEntity> getAllImgs() {
		synchronized (this.m_dataIndices.imgIndex) {
			return this.m_dataIndices.imgIndex.entities();
		}
	}
	
	public EntityCursor<HtmlEntity> getAllHtmls() {
		synchronized (this.m_dataIndices.htmlIndex) {
			return this.m_dataIndices.htmlIndex.entities();
		}
	}
	
	/**
	 * This function add a new content
	 * 
	 * @param contentSHA
	 * @param url
	 * @param address
	 */
	private void addNewContent(String contentSHA, String url, String address) {
		ContentEntity contentEntity = this.m_dataIndices.contentIndex.get(contentSHA);
		if (contentEntity == null) {
			contentEntity = new ContentEntity();
		}
		contentEntity.setContentSHA(contentSHA);
		contentEntity.setURL(url);
		contentEntity.setAddress(address);
		this.m_dataIndices.contentIndex.put(contentEntity);
	}
	
	/**
	 * This function implements content-seen-test
	 * 
	 * @param contentSHA
	 * @param url
	 * @param address
	 * @return
	 */
	public Entry<String, String> digestContent(String contentSHA, String url, String address) {
		Entry<String, String> result = null;
		synchronized (this.m_dataIndices.contentIndex) {
			ContentEntity contentEntity = this.m_dataIndices.contentIndex.get(contentSHA);
			if (contentEntity == null) {
				addNewContent(contentSHA, url, address);
			} else {
				result = new AbstractMap.SimpleEntry<String, String>(
						contentEntity.getURL(), contentEntity.getAddress());
			}
		}
		return result;
	}
	
	/**
	 * This function returns all content
	 * 
	 * @return
	 */
	public EntityCursor<ContentEntity> getAllContent() {
		synchronized (this.m_dataIndices.contentIndex) {
			return this.m_dataIndices.contentIndex.entities();
		}
	}
	
	public void addDescription(String docID, DescriptionWrapper description) {
		synchronized (this.m_dataIndices.descriptionIndex) {
			DocumentDescriptionEntity despEntity = this.m_dataIndices.descriptionIndex.get(docID);
			if (despEntity == null) {
				despEntity = new DocumentDescriptionEntity();
			}
			despEntity.setDocID(docID);
			despEntity.setContentType(description.getContentType().toLowerCase());
			String text = description.getDescription();
//			text = text.replaceAll("[^\\w\\-']", " ").replaceAll("(\\s)+", " ").toLowerCase();
			despEntity.setDescription(text);
			despEntity.setURL(description.getURL());
			despEntity.setParentURL(description.getParentURL());
			despEntity.setTitle(description.getTitle());
			this.m_dataIndices.descriptionIndex.put(despEntity);
		}
	}
	
	public DocumentDescriptionEntity getDescription(String docID) {
		synchronized (this.m_dataIndices.descriptionIndex) {
			DocumentDescriptionEntity despEntity = this.m_dataIndices.descriptionIndex.get(docID);
			return despEntity;
		}
	}
	
	public EntityCursor<DocumentDescriptionEntity> getAllDescription() {
		synchronized (this.m_dataIndices.descriptionIndex) {
			return this.m_dataIndices.descriptionIndex.entities();
		}
	}
	
	public void deleteDescription(String docID) {
		synchronized (this.m_dataIndices.descriptionIndex) {
			DocumentDescriptionEntity despEntity = this.m_dataIndices.descriptionIndex.get(docID);
			if (despEntity != null) {
				this.m_dataIndices.descriptionIndex.delete(docID);
			}
		}
	}
	/**
	 * Push new URLs to database
	 * @param urls
	 */
//	public void addForwadURLs(List<String> urls) {
//		synchronized (this.m_dataIndices.forwardURLndex) {
//			for (String url : urls) {
//				ForwardURLEntity forwardURLEntity = this.m_dataIndices.forwardURLndex.get(url);
//				if (forwardURLEntity == null) {
//					forwardURLEntity = new ForwardURLEntity();
//				}
//				forwardURLEntity.setURL(url);
//				this.m_dataIndices.forwardURLndex.put(forwardURLEntity);
//			}
//		}
//	}
	
	/**
	 * This function deletes a URL from database
	 * 
	 * @param url
	 */
//	public void deleteForwardURL(String url) {
//		synchronized (this.m_dataIndices.forwardURLndex) {
//			this.m_dataIndices.forwardURLndex.delete(url);
//		}
//	}
	
//	/**
//	 * This function returns all URLs not crawled
//	 * 
//	 * @return
//	 */
//	public EntityCursor<ForwardURLEntity> getAllURLs() {
//		synchronized (this.m_dataIndices.forwardURLndex) {
//			return this.m_dataIndices.forwardURLndex.entities();
//		}
//	}
}
