package edu.upenn.cis455.crawler.test;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis455.crawler.worker.CrawlerConfiguration;
import edu.upenn.cis455.storage.database.DataAccessor;
import edu.upenn.cis455.storage.database.DatabaseConfiguration;
import edu.upenn.cis455.storage.database.DocEntity;
import edu.upenn.cis455.storage.database.DocumentDescriptionEntity;
import edu.upenn.cis455.storage.database.HtmlEntity;
import edu.upenn.cis455.storage.database.ImgEntity;
import edu.upenn.cis455.utils.FileUtil;
import edu.upenn.cis455.utils.SHA1Util;

public class CheckMain {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//System.out.println(SHA1Util.byteToString(SHA1Util.generateSHA("http://news.yahoo.com")));
		DatabaseConfiguration.setup("E://worker4/worker/database");
	
		CrawlerConfiguration.setLinksFileDir("E://worker4/worker/linksData");
		List<String> keys = DataAccessor.getSingleton().getKeys("E://worker4/worker/linksData");
		FileUtil fileUtil = new FileUtil("E://worker4/worker/testfile.txt");
		for (String key : keys) {
			DocumentDescriptionEntity d = DataAccessor.getSingleton().getDescription(key);
			//System.out.println(d.getDocID() + " " + d.getURL() + " " + d.getContentType() + "\n_____________________________________________________________________________");
			String newID = SHA1Util.byteToString(SHA1Util.generateSHA(d.getURL()));
			fileUtil.writeFile(d.getDocID() + " " + d.getURL() + " " + d.getContentType() + " " + newID
					+ "\r\n\r\n_________________________________________________________________________________\r\n\r\n");
		}
//		EntityCursor<ImgEntity> documents = DataAccessor.getSingleton()
//				.getAllImgs();
//		int j = 0;
//		for (ImgEntity d : documents) {
//			j ++;
//		}
//		System.out.println("Num: " + j);
//		
//		EntityCursor<DocEntity> ddocuments = DataAccessor.getSingleton()
//				.getAllDocs();
//		j = 0;
//		for (DocEntity d : ddocuments) {
//			j ++;
//		}
//		System.out.println("Num: " + j);
//
//		EntityCursor<HtmlEntity> hdocuments = DataAccessor.getSingleton()
//				.getAllHtmls();
//		j = 0;
//		for (HtmlEntity d : hdocuments) {
//			j ++;
//		}
//		System.out.println("Num: " + j);
//		
//		EntityCursor<DocumentDescriptionEntity> description = DataAccessor.getSingleton().getAllDescription();
//		j = 0;
//		for (DocumentDescriptionEntity d : description) {
//			j ++;
////			System.out.println(d.getDocID());
////			System.out.println(d.getParentURL());
////			System.out.println(d.getContentType());
////			System.out.println(d.getDescription());
////			System.out.println("______________________________________________________________________________________________");
//		}
//		System.out.println("Num: " + j);
//		// Logger.write();
//
//		System.exit(0);
	}
}
