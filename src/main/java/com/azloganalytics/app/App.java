package com.azloganalytics.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Person;
import io.codearte.jfairy.producer.person.PersonProperties;

public class App {

		final static Logger logger = LoggerFactory.getLogger(App.class);
		
		public static void main(String[] args) {
			
			Fairy fairy = Fairy.create();
			for (int i = 0; i < 10; i++) {
				Company company = fairy.company();
				Person person = fairy.person(PersonProperties.withCompany(company));
				logger.warn("User First name:" + person.getFirstName());
				logger.info("User Last name:" + person.getLastName());
				if(person.isMale()) {
					logger.debug("User is Male");	
				}else {
					logger.debug("User is Female");	
				}
				
				logger.debug("User City:" + person.getAddress().getCity());
				logger.debug("User Company:" + person.getCompany().getEmail());
				logger.debug("User Company Email:" + person.getCompanyEmail());

			}

		}
}
