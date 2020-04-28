package com.rebwon.restapi.events;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

	public void validate(EventPayload eventPayload, Errors errors) {
		if(eventPayload.getBasePrice() > eventPayload.getMaxPrice() && eventPayload.getMaxPrice() > 0) {
			errors.reject("wrongPrices", "Values to prices are wrong.");
		}
		LocalDateTime endEventDateTime = eventPayload.getEndEventDateTime();
		if(endEventDateTime.isBefore(eventPayload.getBeginEventDateTime()) ||
		endEventDateTime.isBefore(eventPayload.getCloseEnrollmentDateTime()) ||
		endEventDateTime.isBefore(eventPayload.getBeginEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is Wrong.");
		}
	}
}
