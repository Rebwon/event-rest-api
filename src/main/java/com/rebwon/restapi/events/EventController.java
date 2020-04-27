package com.rebwon.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

	@PostMapping
	public ResponseEntity createEvent(@RequestBody Event event) {
		URI uri = linkTo(EventController.class).slash("/{id}").toUri();
		event.setId(1);
		return ResponseEntity.created(uri).body(event);
	}
}
