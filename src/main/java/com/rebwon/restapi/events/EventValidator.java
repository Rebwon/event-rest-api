package com.rebwon.restapi.events;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

	public void validate(EventPayload eventPayload, Errors errors) {
		if(eventPayload.getBasePrice() > eventPayload.getMaxPrice() && eventPayload.getMaxPrice() > 0) {
			errors.rejectValue("basePrice", "wrongValue", "basePrice is Wrong.");
			errors.rejectValue("maxPrice", "wrongValue", "maxPrice is Wrong.");
		}
		LocalDateTime endEventDateTime = eventPayload.getEndEventDateTime();
		if(endEventDateTime.isBefore(eventPayload.getBeginEventDateTime()) ||
		endEventDateTime.isBefore(eventPayload.getCloseEnrollmentDateTime()) ||
		endEventDateTime.isBefore(eventPayload.getBeginEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is Wrong.");
		}


	}
}
