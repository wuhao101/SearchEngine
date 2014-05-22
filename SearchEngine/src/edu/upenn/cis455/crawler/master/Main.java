package edu.upenn.cis455.crawler.master;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis455.storage.database.DatabaseConfiguration;
import edu.upenn.cis455.utils.FileUtil;

public class Main {
	public static void main(String[] args) {
		int numOfCrawlerNodes = 0;
		FileUtil fileUtil = new FileUtil("./master/configuration.txt");
		List<String> configurations = fileUtil.readFile();
		int maxNumOfDoc = Integer.MAX_VALUE;
		int i = 0;
		String databaseDir = null;
		for (String line : configurations) {
			if (!line.trim().isEmpty()) {
				if (i == 0) {
					maxNumOfDoc = Integer.valueOf(line.trim()).intValue();
				} else if (i == 1){
					numOfCrawlerNodes = Integer.valueOf(line.trim()).intValue();
				} else if (i == 2){
					databaseDir = line.trim();
				}
				i++;
			}
		}
		DatabaseConfiguration.setup(databaseDir);
		List<String> entryURLs = new ArrayList<String>();
		FileUtil urlFileUtil = new FileUtil("./master/urls.txt");
		List<String> urls = urlFileUtil.readFile();
		for (String line : urls) {
			entryURLs.add(line);
		}
		List<Integer> ports = new ArrayList<Integer>(4);
		ports.add(8080);
		ports.add(8081);
		ports.add(8082);
		ports.add(8083);
		CrawlerMasterConfiguration.setup(4, ports, entryURLs,
				numOfCrawlerNodes, 5, 50000, maxNumOfDoc);
		CrawlerMaster crawlerMaster = CrawlerMaster.getSingleton();
		while (!crawlerMaster.isStopped())
			;
		crawlerMaster.stopDigestor();
		System.out.println("Finished " + CrawlerMaster.m_numOfPages);
//		crawlerMaster.stopReceiver();
//		System.exit(0);
	}
}
