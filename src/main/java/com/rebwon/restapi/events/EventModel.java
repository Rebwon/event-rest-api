package com.rebwon.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class EventModel extends EntityModel<Event> {
	public EventModel(Event event, Link... links) {
		super(event, links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}
}
