package edu.upenn.cis455.crawler.worker;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.upenn.cis455.crawler.message.Link;
import edu.upenn.cis455.global.Global;
import edu.upenn.cis455.storage.wrapper.DescriptionWrapper;

/**
 * This class parsed html document
 * 
 * @author martinng
 *
 */
public class HtmlParser {
	private static Pattern m_charsetPat = Pattern.compile(Global.CHARSET_REGX);
	private static Pattern m_equalPat = Pattern.compile(Global.EQUAL_SIGN);
	
	static List<Link> extractLinks(DescriptionWrapper[] dispWrapper,
			List<Link> links, String url, byte[] contentBytes,
			HeaderInfo headerInfo) {
		Document doc = null;
		try {
			String htmlContent = null;
			if (headerInfo.m_charset != null && !headerInfo.m_charset.isEmpty()) {
				htmlContent = new String(contentBytes, headerInfo.m_charset);
				doc = Jsoup.parse(htmlContent);
			} else {
				htmlContent = new String(contentBytes, "UTF-8");
				doc = Jsoup.parse(htmlContent);
				Elements metas = doc.select("meta"); 
				/*
				 * Find charset
				 */
				for (Element meta: metas) {
					String content = meta.attr("content");
					Matcher charsetMat = m_charsetPat.matcher(content);
					if (charsetMat.find()) {
						String charset = m_equalPat.split(charsetMat.group())[1].trim();
						htmlContent = new String(contentBytes, charset);
						doc = Jsoup.parse(htmlContent);
						break;
					}
				}
			}
			
			if (doc != null) {
				String title = doc.title();
				Elements hlinks = doc.select("a[href]"); 
				for (Element link : hlinks) {
					String linkContent = link.attr("href");
					linkContent = parseLink(url, linkContent);
					if (linkContent != null && !linkContent.isEmpty()) {
						Link newlink = new Link();
						newlink.setURL(linkContent);
						newlink.setParURL(url);
						newlink.setParTitle(title);
						newlink.setText(link.text());
						newlink.setTitle(link.attr("title"));
						links.add(newlink);
					}
				}
				Elements ilinks = doc.select("img[src]"); 
				for (Element link : ilinks) {
					String linkContent = link.attr("src");
					linkContent = parseLink(url, linkContent);
					if (linkContent != null && !linkContent.isEmpty()) {
						Link newlink = new Link();
						newlink.setURL(linkContent);
						newlink.setParURL(url);
						newlink.setParTitle(title);
						newlink.setText(link.text());
						newlink.setTitle(link.attr("title"));
						newlink.setAlt(link.attr("alt"));
						links.add(newlink);
					}
				}
				
				dispWrapper[0] = WordExtractor.extractHTML(doc, url,
						headerInfo.m_contentType, title);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return links;
	}
	
	private static String parseLink(String parURL, String link) {
		/*
		 * Check if original URL ends with "/". If not, add "/" to the end;
		 */
		String path = parURL;
		int doubleSplashIndex = parURL.indexOf(Global.DOUBLE_SPLASH);
		int splashIndex = parURL.indexOf(Global.SPLASH, doubleSplashIndex
				+ Global.DOUBLE_SPLASH.length());
		if (splashIndex <= -1) {
			path += Global.SPLASH;
		}

		URI uri = URI.create(path);

		if (link.length() != 0 && !link.startsWith("#")
				&& !link.matches("(?i)^javascript:.*")
				&& !link.matches("(?i)^mailto:.*")) {
			try {
				link = link.replaceAll("\\s", "%20").replaceAll(
						"&amp;", "&");
				URI linkUri = new URI(link);
				URI resolvedUri = uri.resolve(linkUri);
				String newLink = resolvedUri.toString();
				if (newLink.endsWith(Global.SPLASH)) {
					int len = newLink.length();
					newLink = newLink.substring(0, len - 1);
				}
				return newLink.trim();
			} catch (Exception e) {
//				Logger.error("parseLinks " + oriURL.toString() + ": "
//						+ e.getMessage());
			}
		}
		return null;
	}
}
