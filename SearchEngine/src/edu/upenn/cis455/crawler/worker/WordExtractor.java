package edu.upenn.cis455.crawler.worker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;

import edu.upenn.cis455.global.Global;
import edu.upenn.cis455.storage.wrapper.DescriptionWrapper;

/**
 * This class is for extracting words from documents
 * 
 * @author martinng
 * 
 */
public class WordExtractor {
	/*
	 * Properties
	 */
	private static final Pattern xmlEncodePat = Pattern
			.compile(Global.XML_ENCODE_REGX);
	private static final Pattern xmlEncodeValuePat = Pattern
			.compile(Global.XML_ENCODE_VALUE_REGX);

	/**
	 * This function extracts words form HTML
	 * 
	 * @param doc
	 * @param url
	 * @param contentType
	 * @param title
	 * @return
	 */
	public static DescriptionWrapper extractHTML(Document doc, String url,
			String contentType, String title) {
		DescriptionWrapper dispWrapper = new DescriptionWrapper();
		try {
			dispWrapper.setURL(url);
			dispWrapper.setContentType(contentType);
			dispWrapper.setTitle(title);

			if (contentType != null && contentType.contains("text/html")) {
				String text = doc.text();
				text = text.replaceAll("\t|\r|\n", " ");
				dispWrapper.setDescription(text.trim());
			}
		} catch (Exception e) {

		}
		return dispWrapper;
	}

	/**
	 * This function handles plain text
	 * 
	 * @param content
	 * @param url
	 * @param contentType
	 * @param title
	 * @param text
	 * @param parentURL
	 * @param charset
	 * @return
	 */
	public static DescriptionWrapper extractPlain(byte[] content, String url,
			String contentType, String title, String text, String parentURL,
			String charset) {
		DescriptionWrapper dispWrapper = new DescriptionWrapper();
		try {
			dispWrapper.setURL(url);
			dispWrapper.setContentType(contentType);
			if (title != null) {
				dispWrapper.setTitle(title);
			} else if (text != null) {
				dispWrapper.setTitle(text);
			}
			dispWrapper.setParentURL(parentURL);

			if (contentType != null && contentType.contains("text/plain")) {
				String contentString;
				if (charset != null) {
					contentString = new String(content, charset);
				} else {
					contentString = new String(content);
				}
				contentString = contentString.replaceAll("\t|\r|\n", " ");
				dispWrapper.setDescription(contentString.trim());
			}
		} catch (Exception e) {

		}
		return dispWrapper;
	}

	/**
	 * This function extracts words from XML
	 * 
	 * @param content
	 * @param url
	 * @param contentType
	 * @param title
	 * @param text
	 * @param parentURL
	 * @param charset
	 * @return
	 */
	public static DescriptionWrapper extractXML(byte[] content, String url,
			String contentType, String title, String text, String parentURL,
			String charset) {
		DescriptionWrapper dispWrapper = new DescriptionWrapper();
		try {
			dispWrapper.setURL(url);
			dispWrapper.setContentType(contentType);
			if (title != null) {
				dispWrapper.setTitle(title);
			} else if (text != null) {
				dispWrapper.setTitle(text);
			}
			dispWrapper.setParentURL(parentURL);

			if (contentType != null && contentType.contains("text/xml")) {
				String contentString;
				if (charset != null) {
					contentString = new String(content, charset);
				} else {
					contentString = new String(content);
				}

				Matcher xmlEncodemat = WordExtractor.xmlEncodePat
						.matcher(contentString);
				if (xmlEncodemat.find()) {
					Matcher xmlEncodeValueMat = WordExtractor.xmlEncodeValuePat
							.matcher(xmlEncodemat.group().trim());
					if (xmlEncodeValueMat.find()) {
						String encode = xmlEncodeValueMat.group().trim()
								.replaceFirst("encoding(\\s)*=(\\s)*\"", "")
								.replaceFirst("\"", "");
						contentString = new String(content, encode);
					}
				}
				Document doc = Jsoup.parse(contentString, "", new Parser(
						new XmlTreeBuilder()));
				String description = doc.text();
				description = description.replaceAll("\t|\r|\n", " ");
				dispWrapper.setDescription(description.trim());
			}
		} catch (Exception e) {

		}
		return dispWrapper;
	}

	/**
	 * This function extracts words from image
	 * 
	 * @param url
	 * @param contentType
	 * @param title
	 * @param text
	 * @param alt
	 * @param parentTitle
	 * @param parentURL
	 * @return
	 */
	public static DescriptionWrapper extractIMG(String url, String contentType,
			String title, String text, String alt, String parentTitle,
			String parentURL) {
		DescriptionWrapper dispWrapper = new DescriptionWrapper();
		try {
			dispWrapper.setURL(url);
			dispWrapper.setContentType(contentType);
			dispWrapper.setParentURL(parentURL);
			String description = new String();

			if (title != null) {
				description += title + " ";
			}

			if (text != null) {
				description += text + " ";
			}

			if (alt != null) {
				description += alt + " ";
			}

			if (parentTitle != null) {
				description += parentTitle;
			}
			description = description.replaceAll("\t|\r|\n", " ");
			dispWrapper.setDescription(description.trim());
		} catch (Exception e) {

		}
		return dispWrapper;
	}

	/**
	 * This function extracts words from doc, docx, pdf, and ppt
	 * 
	 * @param url
	 * @param contentType
	 * @param title
	 * @param text
	 * @param parentTitle
	 * @param parentURL
	 * @return
	 */
	public static DescriptionWrapper extractDoc(String url, String contentType,
			String title, String text, String parentTitle, String parentURL) {
		DescriptionWrapper dispWrapper = new DescriptionWrapper();
		try {
			dispWrapper.setURL(url);
			dispWrapper.setContentType(contentType);
			dispWrapper.setParentURL(parentURL);
			String description = new String();

			if (title != null) {
				dispWrapper.setTitle(title);
			} else if (text != null) {
				dispWrapper.setTitle(text);
			}

			if (title != null) {
				description += title + " ";
			}

			if (text != null) {
				description += text + " ";
			}

			if (parentTitle != null) {
				description += parentTitle;
			}
			description = description.replaceAll("\t|\r|\n", " ");
			dispWrapper.setDescription(description.trim());
		} catch (Exception e) {

		}
		return dispWrapper;
	}
}
