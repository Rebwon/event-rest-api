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

	@Test
	void eventFree() {
		// given
		Event event = Event.builder()
			.basePrice(0)
			.maxPrice(0)
			.build();

		// when
		event.update();

		// then
		assertThat(event.isFree()).isTrue();

		// given
		event = Event.builder()
			.basePrice(100)
			.maxPrice(0)
			.build();

		// when
		event.update();

		// then
		assertThat(event.isFree()).isFalse();

		// given
		event = Event.builder()
			.basePrice(0)
			.maxPrice(100)
			.build();

		// when
		event.update();

		// then
		assertThat(event.isFree()).isFalse();
	}

	@Test
	void eventOnlineOrOffLine() {
		// given
		Event event = Event.builder()
			.location("강남역 네이버 D2 스타트업 팩토리")
			.build();

		// when
		event.update();

		// then
		assertThat(event.isOffline()).isTrue();

		// given
		event = Event.builder()
			.build();

		// when
		event.update();

		// then
		assertThat(event.isOffline()).isFalse();
	}
}