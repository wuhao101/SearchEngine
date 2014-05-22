package edu.upenn.cis455.crawler.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LinkPusher {
	public void sendMessage(LinkMessage message) {
		Socket socket = null;
		ObjectOutputStream os = null;
		try {
			String destIP = message.getDestIP();
			int destPort = message.getDestPort();
			//System.out.println(this.m_threadID + " sends to " + destIP + ":" + destPort);
			socket = new Socket(destIP, destPort);
			os = new ObjectOutputStream(socket.getOutputStream());
			os.writeObject(message);
			os.flush();
		} catch (Exception e) {
//			Logger.error("sendMessage: " + e.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
//					Logger.error("sendMessage: "  + e.getMessage());
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
//					Logger.error("sendMessage: "  + e.getMessage());
				}
			}
		}
	}
}
