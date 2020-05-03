package com.rebwon.restapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rebwon.restapi.common.ErrorsModel;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EventValidator eventValidator;
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventPayload payload, Errors errors) {
		if(errors.hasErrors()) {
			return badRequest(errors);
		}

		eventValidator.validate(payload, errors);
		if(errors.hasErrors()) {
			return badRequest(errors);
		}

		Event event = modelMapper.map(payload, Event.class);
		event.update();
		Event newEvent = this.eventRepository.save(event);
		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
		URI uri = selfLinkBuilder.toUri();
		EventModel eventModel = new EventModel(newEvent);
		eventModel.add(linkTo(EventController.class).withRel("query-events"));
		eventModel.add(selfLinkBuilder.withRel("update-event"));
		eventModel.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(uri).body(eventModel);
	}

	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
		Page<Event> page = eventRepository.findAll(pageable);
		var pageModels = assembler.toModel(page, e -> new EventModel(e));
		pageModels.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
		return ResponseEntity.ok(pageModels);
	}

	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if(optionalEvent.isEmpty()){
			return ResponseEntity.notFound().build();
		}

		Event event = optionalEvent.get();
		EventModel eventModel = new EventModel(event);
		eventModel.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
		return ResponseEntity.ok(eventModel);
	}

	private ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsModel(errors));
	}
}
