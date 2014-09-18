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
		String response = "";		
		String intent = req.getParameter("intent");
		try {
			if (intent != null && !intent.isEmpty()) {
				response = doQueryWithIntent(intent);
			} else {
				String query = req.getParameter("q");
				if (query != null && !query.isEmpty()) {			
					response = doQueryWithPhrase(query);				
				}
			}
		} catch (JSONException e) {
			LOGGER.severe(e.getMessage());
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
	
	private String doQueryWithIntent(String witJson) throws IOException, JSONException {
		Map<String,String> parameters = Translator.translate(witJson);
		String hearstQuery = Translator.composeHearstQuery(parameters);		
		LOGGER.info("Hearst query - " + hearstQuery);
		
		String hearstJson = Translator.executeHearstQuery(hearstQuery);
		LOGGER.info("Hearst result - " + hearstJson);
		return hearstJson;
	}
	
	private String doQueryWithPhrase(String phrase) throws IOException, JSONException {		
		String witJson = Translator.executeWitQuery(phrase);		
		LOGGER.info("Wit result - " + witJson);
		
		Map<String,String> parameters = Translator.translate(witJson);
		String hearstQuery = Translator.composeHearstQuery(parameters);		
		LOGGER.info("Hearst query - " + hearstQuery);
		
		String hearstJson = Translator.executeHearstQuery(hearstQuery);
		LOGGER.info("Hearst result - " + hearstJson);
		return hearstJson;
	}
}
