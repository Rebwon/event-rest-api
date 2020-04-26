package com.rebwon.restapi.events;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventTest {

	@Test
	void builder() {
		Event event = Event.builder()
			.name("Spring Rest Api")
			.build();
		assertThat(event).isNotNull();
	}

	@Test
	void javaBean() {
		// given
		String name = "JavaBean Setting";
		String description = "Rest Api JavaBeans";

		// when
		Event event = new Event();
		event.setName(name);
		event.setDescription(description);

		// then
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(description);
	}
}