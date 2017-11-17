package com.azloganalytics.logback;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.azloganalytics.http.HTTPDataCollector;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class LogbackAzLogAnalyticsAppender extends UnsynchronizedAppenderBase<ILoggingEvent>{

	private String workspaceId;
	private String sharedKey;
	private String logType;
	private String azureApiVersion;
	private String component;
	private String version;
	private String environment;
	private int threadPoolSize;
	private String proxyHost;
	private Integer proxyPort;
	private Boolean appendLogger;
	private Boolean appendLogLevel;
	private static HTTPDataCollector httpDataCollector;
	private LoggingEventSerializer serializer;

	public String getWorkspaceId() {
		return workspaceId;
	}

	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	public String getSharedKey() {
		return sharedKey;
	}

	public void setSharedKey(String sharedKey) {
		this.sharedKey = sharedKey;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getAzureApiVersion() {
		return azureApiVersion;
	}

	public void setAzureApiVersion(String azureApiVersion) {
		this.azureApiVersion = azureApiVersion;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	public boolean getAppendLogger() {
		return appendLogger == null ? true : appendLogger;
	}

	public void setAppendLogger(boolean appendLogger) {
		this.appendLogger = appendLogger;
	}

	public boolean getAppendLogLevel() {
		return appendLogLevel == null ? true : appendLogLevel;
	}

	public void setAppendLogLevel(boolean appendLogLevel) {
		this.appendLogLevel = appendLogLevel;
	}


	/**
	 * @see org.apache.Logback.AppenderSkeleton#activateOptions()
	 */
	public void activateOptions() {
		try {
			// Close previous connections if reactivating
			if (httpDataCollector != null) {
				//httpDataCollector.close();
				close();
			}

			if (StringUtils.isEmpty(workspaceId)) {
				throw new Exception(
						String.format("the LogbackALAAppender property workspaceId [%s] shouldn't be empty (Logback.xml)",this.workspaceId));
			}
			if (StringUtils.isEmpty(sharedKey)) {
				throw new Exception(String.format("the LogbackALAAppender property sharedKey [%s] shouldn't be empty (Logback.xml)", this.sharedKey));
			}
			if (StringUtils.isEmpty(logType)) {
				throw new Exception(String.format("the LogbackALAAppender property logType [%s] shouldn't be empty (Logback.xml)", this.logType));
			}

			serializer = new LoggingEventSerializer();

			httpDataCollector = new HTTPDataCollector(this.workspaceId, this.sharedKey,this.threadPoolSize <= 0 ? 1000 : this.threadPoolSize, this, this.proxyHost, this.proxyPort);

		} catch (Exception e) {
			logError(String.format("Unexpected exception while initialising HTTPDataCollector.%1s", e.getMessage()), e);
		}
	}

	@Override
	protected void append(ILoggingEvent  loggingEvent) {
		try {
			
			if (httpDataCollector != null) {
				String content = serializer.serializeLoggingEvents(new ArrayList<ILoggingEvent>(Arrays.asList(loggingEvent)), this);

				httpDataCollector.collect(logType, content,StringUtils.isEmpty(azureApiVersion) ? "2016-04-01" : azureApiVersion, "DateValue");
			} else {
				System.out.println("Couldn't append log message during the HTTPDataCollector isn't initialized.");
			}
		} catch (Exception ex) {
			logError(String.format("Unable to send data to Azure Log Analytics: %1s", ex.getMessage()), ex);
		}

	}

	
	public void close() {
		httpDataCollector.close();
	}

	public void logError(String message, Exception e) {
		e.printStackTrace();

	}



}
