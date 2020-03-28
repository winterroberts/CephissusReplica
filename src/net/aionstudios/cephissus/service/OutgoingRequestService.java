package net.aionstudios.cephissus.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A content processor service for making outgoing requests.
 * @author Winter Roberts
 */
public class OutgoingRequestService {
	
	/**
	 * Makes a post request to the target URL with the given parameters.
	 * @param targetURL The URL to which a request should be made.
	 * @param urlParameters The URL parameters that should be included in this request.
	 * @param or An {@link OutgoingRequest}.
	 * @return The response from this request.
	 */
	public static String executePost(String targetURL, String urlParameters, OutgoingRequest or) {
		  HttpsURLConnection connection = null;

		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpsURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", 
		        "application/x-www-form-urlencoded");

		    connection.setRequestProperty("Content-Length", 
		        Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("Content-Language", "en-US");  

		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.close();
		    
		    Map<String, List<String>> h = connection.getHeaderFields();
		    
		    or.setHeaders(h);
		    
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    or.setContent(response.toString());
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		}
	
	/**
	 * Makes a get request to the target URL with the given parameters.
	 * @param targetURL The URL to which a request should be made.
	 * @return The response from this request.
	 */
	public static String executeGet(String targetURL) {
		  HttpsURLConnection connection = null;
		  
		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpsURLConnection) url.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setRequestProperty("Content-Type", 
		        "application/x-www-form-urlencoded");

		    connection.setRequestProperty("Content-Length", "0");
		    connection.setRequestProperty("Content-Language", "en-US");  

		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.close();

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		}
	
	/**
	 * Converts a map of string, string pairs to a string to be added to the request header.
	 * @param postQuery The map which should be converted.
	 * @return The string conversion.
	 */
	public static String postMapToString(Map<String, String> postQuery) {
		String postString = "";
		for(int i = 0; i < postQuery.size(); i++) {
			String postVal = (String) postQuery.keySet().toArray()[i];
			postString = postString+postVal+"="+(postQuery.get(postVal))+(i < postQuery.size()-1 ? "&" : "");
		}
		return postString;
	}

}
