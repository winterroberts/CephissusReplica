package net.aionstudios.cephissus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An API for making http requests out of this server.
 * @author Winter Roberts
 */
public class OutgoingRequest {
	
	private String content = "";
	private Map<String, List<String>> headers;
	
	/**
	 * Creates a new Outgoing Request.
	 * @param pageContent The content to be sent in this request.
	 * @param reqHeaders The headers to be sent in this request.
	 */
	public OutgoingRequest(String pageContent, Map<String, List<String>> reqHeaders) {
		content = pageContent != null ? pageContent : "";
		headers = reqHeaders != null ? reqHeaders : new HashMap<String, List<String>>();
	}
	
	/**
	 * @return The content to be sent in this request.
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Changes the content to be sent in this request.
	 * @param c The content to be sent in this request.
	 */
	public void setContent(String c) {
		content = c;
	}
	
	/**
	 * @return The headers to be sent in this request.
	 */
	public Map<String, List<String>> getHeaders() {
		return headers;
	}
	
	/**
	 * Changes the header to be sent in this request.
	 * @param hs The headers to be sent in this request.
	 */
	public void setHeaders(Map<String, List<String>> hs) {
		headers = hs;
	}
	
	/**
	 * @param header The string name of a request header.
	 * @return A list of values assigned to this header.
	 */
	public List<String> getHeader(String header) {
		return headers.containsKey(header) ? headers.get(header) : null;
	}
	
	/**
	 * @param header The string name of a request header.
	 * @param arrayLocation The position, in list of values assigned to this header,
	 * which should be returned.
	 * @return The value at the location or null if it doesn't exist.
	 */
	public String getHeader(String header, int arrayLocation) {
		if(headers.containsKey(header)) {
			List<String> h = headers.get(header);
			if(h.size() > 0 && arrayLocation < h.size() && arrayLocation >= 0) {
				return h.get(arrayLocation);
			}
		}
		return null;
	}
	
	/**
	 * @param header The string name of a request header.
	 * @return The last value assigned to this header.
	 */
	public String getLastHeader(String header) {
		if(headers.containsKey(header)) {
			List<String> h = headers.get(header);
			if(h.size() > 0) {
				return h.get(h.size());
			}
		}
		return null;
	}

}
