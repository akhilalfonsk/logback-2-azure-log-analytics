package com.azloganalytics.logback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggingEventSerializer {

	private ObjectMapper jsonMapper = new ObjectMapper();

	public String serializeLoggingEvents(ArrayList<ILoggingEvent> loggingEvents, LogbackAzLogAnalyticsAppender appender) {
		StringBuffer sb = new StringBuffer();

		loggingEvents.forEach(loggingEvent -> {
			try {
				sb.append(serializeLoggingEvent(loggingEvent, appender));
			} catch (JsonProcessingException e) {
				appender.logError("Error serializing logging event", e);
			}
			sb.append(System.lineSeparator());
		});

		return sb.toString();
	}

	private String serializeLoggingEvent(ILoggingEvent loggingEvent, LogbackAzLogAnalyticsAppender appender)
			throws JsonProcessingException {
		// http://stackoverflow.com/questions/11294307/convert-java-date-to-utc-string
		String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		HashMap<String, Object> payload = new HashMap<String, Object>();
		
		Object logMessage = loggingEvent.getMessage();

		if (!StringUtils.isEmpty(appender.getEnvironment()) && !StringUtils.isEmpty(appender.getComponent())
				&& !StringUtils.isEmpty(appender.getVersion())
				&& !jsonMapper.writeValueAsString(logMessage).toLowerCase().contains("component")
				&& !jsonMapper.writeValueAsString(logMessage).toLowerCase().contains("environment")
				&& !jsonMapper.writeValueAsString(logMessage).toLowerCase().contains("version")) {
			HashMap<String, Object> message = new HashMap<String, Object>();
			message.put("Component", appender.getComponent());
			message.put("Version", appender.getVersion());
			message.put("Environment", appender.getEnvironment());
			message.put("Message", logMessage);
			message.put("Source", "");
			message.put("SequenceId", "");
			message.put("ScriptName", "");
			payload.put("LogMessage", message);
		}else{
			payload.put("LogMessage", logMessage);
		}

		payload.put("DateValue", sdf.format(new Date(loggingEvent.getTimeStamp())));
		
		if(appender.getAppendLogger()){
			payload.put("Logger", loggingEvent.getLoggerName());
		}
		
		if(appender.getAppendLogLevel() && loggingEvent.getLevel() != null){
			payload.put("Level", loggingEvent.getLevel().toString().toUpperCase());
		}

		return jsonMapper.writeValueAsString(payload);
	}
	
	
	public static Map<String,Object> parseJSONObjectToMap(JSONObject jsonObject) throws JSONException{
	    Map<String, Object> mapData = new HashMap<String, Object>();
	    Iterator<String> keysItr = jsonObject.keys();
	        while(keysItr.hasNext()) {
	            String key = keysItr.next();
	            Object value = jsonObject.get(key);

	            if(value instanceof JSONArray) {
	                value = parseJSONArrayToList((JSONArray) value);
	            }else if(value instanceof JSONObject) {
	                value = parseJSONObjectToMap((JSONObject) value);
	            }
	            mapData.put(key, value);
	        }
	    return mapData;
	}

	public static List<Object> parseJSONArrayToList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = parseJSONArrayToList((JSONArray) value);
	        }else if(value instanceof JSONObject) {
	            value = parseJSONObjectToMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}

}
