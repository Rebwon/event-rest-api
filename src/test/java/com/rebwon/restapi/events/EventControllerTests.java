package com.rebwon.restapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class EventControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void createEvent() throws Exception {
		Event event = Event.builder()
			.name("Spring")
			.description("Spring Rest API")
			.beginEnrollmentDateTime(LocalDateTime.of(2020, 4, 27, 4, 1))
			.closeEnrollmentDateTime(LocalDateTime.of(2020, 4, 28, 4, 1))
			.beginEventDateTime(LocalDateTime.of(2020, 4, 30, 1, 1))
			.endEventDateTime(LocalDateTime.of(2020, 5, 1, 20, 1))
			.basePrice(100)
			.maxPrice(200)
			.limitOfEnrollment(100)
			.location("강남 D2 스타트 팩토리")
			.build();

		mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON)
					.content(objectMapper.writeValueAsString(event)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());
	}
}
