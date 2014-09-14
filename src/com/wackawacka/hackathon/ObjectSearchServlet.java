package com.wackawacka.hackathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ObjectSearchServlet extends HttpServlet {
	
	public final static String BASE_URL = "https://apis-qa.berkeley.edu/hearst_museum/select";
	public final static String APP_ID = "99f70499";
	public final static String APP_KEY = "acfacfd4c175cf965962562b37e2647a";
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String response = "";
		String query = req.getParameter("q");
		if (query != null) {			
			response = search(query);
		}
		
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setCharacterEncoding("utf-8");
	    resp.setContentType("application/json");
	    resp.getWriter().println(response);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}
	
	private String search(String object) throws IOException {		
		String endpoint = BASE_URL + "?q=objname_s:" + object + "&wt=json&indent=on";		
		URL url = new URL(endpoint);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("app_id", APP_ID);
		urlConnection.setRequestProperty("app_key", APP_KEY);
		urlConnection.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			buffer.append(inputLine);			
		}
		in.close();
		return buffer.toString();
	}
}
