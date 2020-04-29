package com.rebwon.restapi.events;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

	@ParameterizedTest
	@MethodSource("eventPriceAndFree")
	void eventFree(int basePrice, int maxPrice, boolean isFree) {
		// given
		Event event = Event.builder()
			.basePrice(basePrice)
			.maxPrice(maxPrice)
			.build();

		// when
		event.update();

		// then
		assertThat(event.isFree()).isEqualTo(isFree);
	}

	static Stream<Arguments> eventPriceAndFree() {
		return Stream.of(
			arguments(0, 0, true),
			arguments(100, 0, false),
			arguments(0, 100, false)
		);
	}

	@ParameterizedTest
	@MethodSource("eventLocationOffLine")
	void eventOnlineOrOffLine(String location, boolean isOffline) {
		// given
		Event event = Event.builder()
			.location(location)
			.build();

		// when
		event.update();

		// then
		assertThat(event.isOffline()).isEqualTo(isOffline);
	}

	static Stream<Arguments> eventLocationOffLine() {
		return Stream.of(
			arguments("강남 D2 스타트업 팩토리", true),
			arguments("", false)
		);
	}
}