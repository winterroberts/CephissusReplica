package net.aionstudios.cephissus.util;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;

public class FormatUtils {

	/**
	 * Creates a new {@link JSONObject}, forcing it to use a LinkedHashMap instead of an unlinked one.
	 * @return An empty {@link JSONObject} with modified structure.
	 */
	public static JSONObject getLinkedJsonObject() {
		JSONObject j = new JSONObject();
		Field map;
		try {
			map = j.getClass().getDeclaredField("map");
			map.setAccessible(true);
			map.set(j, new LinkedHashMap<>());
			map.setAccessible(false);
		} catch (NoSuchFieldException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		}
		return j;
	}
	
	public static String makeStringLength(String s, int length) {
		if(s.length()>length) {
			return s.substring(0, length);
		} else if (s.length()==length) {
			return s;
		} else {
			while(s.length()<length) {
				s += " ";
			}
			return s;
		}
	}
	
	public static String join(String[] toJoin, String joinBy) {
		if(toJoin.length>1) {
			String joined = toJoin[0];
			for (int i = 1; i < toJoin.length; i++) {
				joined = joined + joinBy + toJoin[i];
			}
			return joined;
		} else {
			return toJoin.length>0?toJoin[0]:"";
		}
	}
	
	public static String join(List<String> toJoin, String joinBy) {
		if(toJoin.size()>1) {
			String joined = toJoin.get(0);
			for (int i = 1; i < toJoin.size(); i++) {
				joined = joined + joinBy + toJoin.get(i);
			}
			return joined;
		} else {
			return toJoin.size()>0?toJoin.get(0):"";
		}
	}
	
}
