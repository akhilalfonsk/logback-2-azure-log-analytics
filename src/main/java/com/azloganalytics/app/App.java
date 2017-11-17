package com.azloganalytics.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

		final static Logger logger = LoggerFactory.getLogger(App.class);
		
		public static void main(String[] args) {
			logger.info("Msg #1");
			logger.warn("Msg #2");
			logger.error("Msg #3");
			logger.debug("Msg #4");
		}
}
