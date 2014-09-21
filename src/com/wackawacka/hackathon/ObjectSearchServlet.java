package com.wackawacka.hackathon;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;

@SuppressWarnings("serial")
public class ObjectSearchServlet extends HttpServlet {
		
	private final static Logger LOGGER = Logger.getLogger(ObjectSearchServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int start = 0;
		String startParam = req.getParameter("start");
		if (startParam != null && !startParam.isEmpty()) {
			try {
				start = Math.max(0, Integer.parseInt(startParam));
			} catch (NumberFormatException e) {}
		}
		
		int rows = 0;
		String rowsParam = req.getParameter("rows");
		if (rowsParam != null && !rowsParam.isEmpty()) {
			try {
				rows = Math.max(Integer.parseInt(rowsParam), 0);
			} catch (NumberFormatException e) {}
		}
		
		String response = "";	
		String solr = req.getParameter("solr");
		if (solr != null && !solr.isEmpty()) {
			response = doSolrQuery(solr, start, rows);
		} else {
			String intent = req.getParameter("intent");
			try {
				if (intent != null && !intent.isEmpty()) {
					response = doQueryWithIntent(intent, start, rows);
				} else {
					String query = req.getParameter("q");
					if (query != null && !query.isEmpty()) {			
						response = doQueryWithPhrase(query, start, rows);				
					}
				}
			} catch (JSONException e) {
				LOGGER.severe(e.getMessage());
			}
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
	
	private String doSolrQuery(String solr, int start, int rows) throws IOException {
		LOGGER.info("SOLR query: " + solr + ", start: " + start + ", rows: " + rows);
		String hearstJson = Translator.executeHearstQuery(solr, start, rows);
		return hearstJson;
	}
	
	private String doQueryWithIntent(String witJson, int start, int rows) throws IOException, JSONException {
		LOGGER.info("JSON query: " + witJson + ", start: " + start + ", rows: " + rows);
		Map<String,String> parameters = Translator.translate(witJson);
		String hearstQuery = Translator.composeHearstQuery(parameters);		
		String hearstJson = Translator.executeHearstQuery(hearstQuery, start, rows);
		return hearstJson;
	}
	
	private String doQueryWithPhrase(String phrase, int start, int rows) throws IOException, JSONException {	
		LOGGER.info("Phrase query: " + phrase + ", start: " + start + ", rows: " + rows);
		String witJson = Translator.executeWitQuery(phrase);		
		Map<String,String> parameters = Translator.translate(witJson);
		String hearstQuery = Translator.composeHearstQuery(parameters);		
		String hearstJson = Translator.executeHearstQuery(hearstQuery, start, rows);
		return hearstJson;
	}
}
