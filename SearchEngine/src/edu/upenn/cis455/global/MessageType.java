package edu.upenn.cis455.global;;

public enum MessageType {
	SEEN_DIGEST_QUERY,
	SEEN_DIGEST_RESP,
	SEEN_HIT,
	DEL_DOC,
	READY_TO_CRAWL,
	START_CRAWL,
	STOP_CRAWL,
	WORK_COMPLETED
}
