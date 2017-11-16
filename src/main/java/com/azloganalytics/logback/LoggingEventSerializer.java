package com.azloganalytics.logback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.LoggingEvent;

public class LoggingEventSerializer {

	private ObjectMapper jsonMapper = new ObjectMapper();

	public String serializeLoggingEvents(ArrayList<LoggingEvent> loggingEvents, LogbackAzLogAnalyticsAppender appender) {
		StringBuffer sb = new StringBuffer();

		loggingEvents.forEach(loggingEvent -> {
			try {
				sb.append(serializeLoggingEvent(loggingEvent, appender));
			} catch (JsonProcessingException e) {
				appender.addError("Error serializing logging event", e);
			}
			sb.append(System.lineSeparator());
		});

		return sb.toString();
	}

	private String serializeLoggingEvent(LoggingEvent loggingEvent, LogbackAzLogAnalyticsAppender appender)
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

}
