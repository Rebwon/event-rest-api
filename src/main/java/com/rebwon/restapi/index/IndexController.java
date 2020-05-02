package com.rebwon.restapi.index;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rebwon.restapi.events.EventController;

@RestController
public class IndexController {

	@GetMapping("/api")
	public RepresentationModel index() {
		var index = new RepresentationModel<>();
		index.add(linkTo(EventController.class).withRel("events"));
		return index;
	}
}
