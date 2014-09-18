package com.wackawacka.hackathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class Translator {

	private final static String WIT_BASE_URL = "https://api.wit.ai/message";
	private final static String WIT_ACCESS_TOKEN = "Bearer FEYKDMZJVX6DS2YRYWO24UNFAHZCHUSI";
	private final static String WIT_API_VERSION = "application/vnd.wit.20140620+json";
	
	private final static String HEARST_BASE_URL = "https://apis-qa.berkeley.edu/hearst_museum/select";
	private final static String HEARST_APP_ID = "99f70499";
	private final static String HEARST_APP_KEY = "acfacfd4c175cf965962562b37e2647a";
	private final static String FACETS = 
				"facet.field=objassoccult_ss&facet.field=objcollector_ss&facet.field=objcolldate_s&facet.field=objtype_txt";
	
	public final static String executeWitQuery(String query) throws IOException {
		query = URLEncoder.encode(query, "utf-8");
		String endpoint = WIT_BASE_URL + "?q=" + query;
		URL url = new URL(endpoint);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Authorization", WIT_ACCESS_TOKEN);
		urlConnection.setRequestProperty("Accept", WIT_API_VERSION);
		urlConnection.setRequestMethod("GET");
		
		BufferedReader input = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String inputLine;
		while ((inputLine = input.readLine()) != null) {
			buffer.append(inputLine);
		}
		input.close();
		return buffer.toString();
	}
	
	public final static void mapFields(JSONObject entity, 
				String inputField, String outputField, String valueKey, 
				Map<String,String> mapping) 
			throws JSONException {
		if (!entity.has(inputField)) {
			return;
		}
		
		JSONArray values = entity.getJSONArray(inputField);
		if (values != null && values.length() > 0) {
			JSONObject value = values.getJSONObject(0);
			String strValue = value.getString(valueKey);
			if (strValue != null && !strValue.isEmpty()) {
				mapping.put(outputField, strValue);
			}
		}
	}
	
	public final static Map<String,String> translate(String witJson) throws JSONException {
		Map<String,String> hearstMapping = new HashMap<String,String>();
		JSONObject root = new JSONObject(witJson);		
		JSONArray outcomes = root.getJSONArray("outcomes");
		if (outcomes != null && outcomes.length() > 0) {
			for (int i=0; i<outcomes.length(); i++) {
				JSONObject outcome = outcomes.getJSONObject(i);
				String intent = outcome.getString("intent");
				if (!intent.equals("Search")) {
					continue;
				}
				
				JSONObject entities = outcome.getJSONObject("entities");
				if (entities != null) {					
					mapFields(entities, "number", "objcolldate_txt", "value", hearstMapping);
					mapFields(entities, "location", "objfcpverbatim_txt", "value", hearstMapping);
					mapFields(entities, "wikipedia_search_query", "objassoccult_txt", "value", hearstMapping);
					mapFields(entities, "search_query", "objtype_txt", "value", hearstMapping);
					mapFields(entities, "contact", "objcollector_txt", "value", hearstMapping);									
				}
			}
		}		
		return hearstMapping;
	}
	
	public final static String composeHearstQuery(Map<String,String> parameters) {
		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String,String>> entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String,String> entry = entries.next();
			String key = entry.getKey();
			String value = entry.getValue();
			buffer.append(key + ":" + value);
			if (entries.hasNext()) {
				buffer.append(" AND ");
			}
		}
		return buffer.toString();
	}
	
	public final static String executeHearstQuery(String query) throws IOException {
		query = URLEncoder.encode(query, "utf-8");
		String params = "?q=" + query + "&wt=json&indent=on&facet=true&" + FACETS;
		String endpoint = HEARST_BASE_URL + params;
		URL url = new URL(endpoint);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("app_id", HEARST_APP_ID);
		urlConnection.setRequestProperty("app_key", HEARST_APP_KEY);
		urlConnection.setRequestMethod("GET");
		
		BufferedReader input = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String inputLine;
		while ((inputLine = input.readLine()) != null) {
			buffer.append(inputLine);
		}
		input.close();		
		return buffer.toString();
	}
}
