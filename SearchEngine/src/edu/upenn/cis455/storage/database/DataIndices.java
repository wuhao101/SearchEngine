package edu.upenn.cis455.storage.database;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/**
 * This class stores all primary indices
 * 
 * @author martinng
 *
 */
public class DataIndices {
	PrimaryIndex<String, DocEntity> docIndex;
	PrimaryIndex<String, ImgEntity> imgIndex;
	PrimaryIndex<String, HtmlEntity> htmlIndex;
	PrimaryIndex<String, ContentEntity> contentIndex;
	PrimaryIndex<String, DocumentDescriptionEntity> descriptionIndex;
//	PrimaryIndex<String, ForwardURLEntity> forwardURLndex;
	
	/**
	 * Constructor: initialize all primary indices
	 * 
	 * @param store
	 */
	public DataIndices(EntityStore store) {
		this.docIndex = store.getPrimaryIndex(String.class, DocEntity.class);
		this.imgIndex = store.getPrimaryIndex(String.class, ImgEntity.class);
		this.htmlIndex = store.getPrimaryIndex(String.class, HtmlEntity.class);
		this.contentIndex = store.getPrimaryIndex(String.class, ContentEntity.class);
		this.descriptionIndex = store.getPrimaryIndex(String.class, DocumentDescriptionEntity.class);
//		this.forwardURLndex = store.getPrimaryIndex(String.class, ForwardURLEntity.class);
	}
}
