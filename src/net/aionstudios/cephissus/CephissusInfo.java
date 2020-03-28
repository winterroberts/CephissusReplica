package net.aionstudios.cephissus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.cephissus.util.FormatUtils;

public class CephissusInfo {
	
	public static String CEPHISSUS_VER = "1.0.0";
	public static String CEPHISSUS_CONFIG = "./replica.json";
	private static JSONObject cpconfig = FormatUtils.getLinkedJsonObject();
	
	private static String primaryHost = "";
	private static String apiKey = "";
	private static String apiSecret = "";
	
	private static String apiToken = "";
	
	private static long deltaLast = 0;
	
	public static boolean readConfigsAtStart() {
		try {
			File ccf = new File(CEPHISSUS_CONFIG);
			if(!ccf.exists()) {
				ccf.getParentFile().mkdirs();
				ccf.createNewFile();
				System.err.println("Fill out the config at "+ccf.getCanonicalPath()+" and restart the server!");
				cpconfig.put("cp_primary_host", "");
				cpconfig.put("cp_key", "");
				cpconfig.put("cp_secret", "");
				writeConfig(cpconfig, ccf);
				System.exit(0);
			} else {
				cpconfig = readConfig(ccf);
				primaryHost = cpconfig.getString("cp_primary_host");
				apiKey = cpconfig.getString("cp_key");
				apiSecret = cpconfig.getString("cp_secret");
				HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> hostname.equals(primaryHost));
			}
			return true;
		} catch (IOException e) {
			System.err.println("Encountered an IOException during config file operations!");
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			System.err.println("Encountered an JSONException during config file operations!");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Writes the provided {@link JSONObject} to the file system, optimistically as a configuration file.
	 * 
	 * @param j	The {@link JSONObject} to be serialized into the file system.
	 * @param f	The {@link File} object identifying where the {@link JSONObject} should be saved onto the file system.
	 * @return True if the file was written without error, false otherwise.
	 */
	public static boolean writeConfig(JSONObject j, File f) {
		try {
			if(!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
				System.out.println("Created config file '"+f.toString()+"'!");
			}
			PrintWriter writer;
			File temp = File.createTempFile("temp_andf", null, f.getParentFile());
			writer = new PrintWriter(temp.toString(), "UTF-8");
			writer.println(j.toString(2));
			writer.close();
			Files.deleteIfExists(f.toPath());
			temp.renameTo(f);
			return true;
		} catch (IOException e) {
			System.err.println("Encountered an IOException while writing config: '"+f.toString()+"'!");
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			System.err.println("Encountered a JSONException while writing config: '"+f.toString()+"'!");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deserializes a {@link JSONObject} from a file on the file system and returns it.
	 * 
	 * @param f	The {@link File} object, representing a file containing JSON data on the file system.
	 * @return	A {@link JSONObject} representing the file provided or null if it could not be read.
	 */
	public static JSONObject readConfig(File f) {
		if(!f.exists()) {
			System.err.println("Failed reading config: '"+f.toString()+"'. No such file!");
			return null;
		}
		String jsonString = "";
		try (BufferedReader br = new BufferedReader(new FileReader(f.toString()))) {
		    for (String line; (line = br.readLine()) != null;) {
		    	jsonString += line;
		    }
		    br.close();
		    return new JSONObject(jsonString);
		} catch (IOException e) {
			System.err.println("Encountered an IOException while reading config: '"+f.toString()+"'!");
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			System.err.println("Encountered a JSONException while reading config: '"+f.toString()+"'!");
			e.printStackTrace();
			return null;
		}
	}

	public static String getPrimaryHost() {
		return primaryHost;
	}

	public static String getApiKey() {
		return apiKey;
	}

	public static String getApiSecret() {
		return apiSecret;
	}

	public static String getApiToken() {
		return apiToken;
	}
	
	public static void setApiToken(String token) {
		apiToken = token;
	}

	public static long getLastDelta() {
		return deltaLast;
	}
	
	public static void setLastDelta(long ld) {
		deltaLast = ld;
	}
	
}
