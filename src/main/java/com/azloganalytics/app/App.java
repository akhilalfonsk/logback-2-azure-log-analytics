package com.azloganalytics.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Person;
import io.codearte.jfairy.producer.person.PersonProperties;

public class App {

	final static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		plainPayLoad();
	}

	public static void plainPayLoad() {
		Fairy fairy = Fairy.create();
		for (int i = 0; i < 10; i++) {
			Company company = fairy.company();
			Person person = fairy.person(PersonProperties.withCompany(company));
			logger.warn("User FirstName:" + person.getFirstName());
			logger.info("User LastName:" + person.getLastName());
			if (person.isMale()) {
				logger.debug("User is Male");
			} else {
				logger.debug("User is Female");
			}

			logger.debug("User City:" + person.getAddress().getCity());
			logger.debug("User Company:" + person.getCompany().getEmail());
			logger.debug("User CompanyEmail:" + person.getCompanyEmail());

		}

	}

	public static void jsonPayLoad() {
		Fairy fairy = Fairy.create();
		for (int i = 0; i < 1; i++) {
			Company company = fairy.company();
			Person person = fairy.person(PersonProperties.withCompany(company));
			Gson gson = new Gson();
			String jsonPerson = gson.toJson(person);
			logger.debug("User",jsonPerson);


		}
	}
}
