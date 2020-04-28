package com.rebwon.restapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	protected void setUp(WebApplicationContext webApplicationContext) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void createEvent() throws Exception {
		EventPayload event = EventPayload.builder()
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
			.andExpect(jsonPath("id").exists())
			.andExpect(header().exists(HttpHeaders.LOCATION))
			.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE.concat(";charset=UTF-8")))
			.andExpect(jsonPath("id").value(Matchers.not(100)))
			.andExpect(jsonPath("free").value(Matchers.not(true)))
			.andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED.name())));
	}

	@Test
	void createEvent_Bad_Request() throws Exception {
		Event event = Event.builder()
			.id(100)
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
			.free(true)
			.offline(false)
			.eventStatus(EventStatus.PUBLISHED)
			.build();

		mockMvc.perform(post("/api/events")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaTypes.HAL_JSON)
			.content(objectMapper.writeValueAsString(event)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventPayload event = EventPayload.builder().build();

		this.mockMvc.perform(post("/api/events")
				.content(objectMapper.writeValueAsString(event))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
}
