package com.rebwon.restapi.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import com.rebwon.restapi.index.IndexController;

public class ErrorsModel extends EntityModel<Errors> {
	public ErrorsModel(Errors content, Link... links) {
		super(content, links);
		add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
	}
}
