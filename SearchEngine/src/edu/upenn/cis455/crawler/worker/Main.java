package edu.upenn.cis455.crawler.worker;

import java.io.IOException;
import java.util.List;

import edu.upenn.cis455.storage.database.DatabaseConfiguration;
import edu.upenn.cis455.utils.FileUtil;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		FileUtil fileUtil = new FileUtil("./worker/configuration.txt");
		List<String> configurations = fileUtil.readFile();
		int masterPort = -1;
		String masterIP = null;
		int linkPort = -1;
		int commandPort = -1;
		double maxDocSize = 10;
		int i = 0;
		String linksDir = null;
		String databaseDir = null;
		String localIP = null;
		String name = null;
		for (String line : configurations) {
			if (!line.trim().isEmpty()) {
				if (i == 0) {
					masterPort = Integer.valueOf(line.trim()).intValue();
				} else if (i == 1) {
					masterIP = line.trim();
				} else if (i == 2) {
					linkPort = Integer.valueOf(line.trim()).intValue();
				} else if (i == 3) {
					commandPort = Integer.valueOf(line.trim()).intValue();
				} else if (i == 4) {
					localIP = line.trim();
				} else if (i == 5) {
					linksDir = line.trim();
				} else if (i == 6) {
					databaseDir = line.trim();
				} else if (i == 7) {
					name = line.trim();
				}
//				else if (i == 6) {
//					maxDocSize = Integer.valueOf(line.trim()).intValue();
//				}
				i++;
			}
		}
		DatabaseConfiguration.setup(databaseDir);
		CrawlerConfiguration.setup(localIP, linkPort, commandPort, masterIP, masterPort,
				6, 30000, maxDocSize, linksDir, name);
		CrawlerManager crawlerManager = CrawlerManager.getSingleton();
		long t1 = System.currentTimeMillis();
		while (!crawlerManager.isManagerStopped()) 
			;

		long t2 = System.currentTimeMillis();
		System.out.println("Time " + (t2 - t1) / 1000);

		System.exit(0);
	}
}
