package com.rebwon.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody EventPayload payload) {
		Event event = modelMapper.map(payload, Event.class);
		Event newEvent = this.eventRepository.save(event);
		URI uri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(uri).body(newEvent);
	}
}
