package com.rebwon.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.rebwon.restapi.common.ControllerTests;

public class EventControllerTests extends ControllerTests {

	@Test
	@DisplayName("정상적으로 이벤트를 생성하는 테스트")
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
			.andExpect(jsonPath("free").value(false))
			.andExpect(jsonPath("offline").value(true))
			.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.query-events").exists())
			.andExpect(jsonPath("_links.update-event").exists())
			.andDo(document("create-event",
				links(
					linkWithRel("self").description("link to self"),
					linkWithRel("query-events").description("link to query events"),
					linkWithRel("update-event").description("link to update an existing event"),
					linkWithRel("profile").description("link to profile")
				),
				requestHeaders(
					headerWithName(HttpHeaders.ACCEPT).description("accept header"),
					headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
				),
				requestFields(
					fieldWithPath("name").description("name of new event"),
					fieldWithPath("description").description("description of new event"),
					fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
					fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
					fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
					fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
					fieldWithPath("basePrice").description("basePrice of new event"),
					fieldWithPath("maxPrice").description("maxPrice of new event"),
					fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
					fieldWithPath("location").description("location of new event")
				),
				responseHeaders(
					headerWithName(HttpHeaders.LOCATION).description("location"),
					headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
				),
				responseFields(
					fieldWithPath("id").description("identifier of new event"),
					fieldWithPath("name").description("name of new event"),
					fieldWithPath("description").description("description of new event"),
					fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
					fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
					fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
					fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
					fieldWithPath("basePrice").description("basePrice of new event"),
					fieldWithPath("maxPrice").description("maxPrice of new event"),
					fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
					fieldWithPath("location").description("location of new event"),
					fieldWithPath("free").description("it tells if this event is free or not"),
					fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
					fieldWithPath("eventStatus").description("event status"),
					fieldWithPath("_links.self.href").description("link to self"),
					fieldWithPath("_links.query-events.href").description("link to query-events"),
					fieldWithPath("_links.update-event.href").description("link to update-event"),
					fieldWithPath("_links.profile.href").description("link to profile")
				)
			));
	}

	@Test
	@DisplayName("입력 받을 수 없는 값을 사용하는 경우 에러가 발생하는 테스트")
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
	@DisplayName("입력 값이 비어있는 경우 에러가 발생하는 테스트")
	void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventPayload event = EventPayload.builder().build();

		mockMvc.perform(post("/api/events")
				.content(objectMapper.writeValueAsString(event))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("입력 값이 잘못된 경우 에러가 발생하는 테스트")
	void createEvent_Bad_Request_Wrong_Input() throws Exception {
		EventPayload event = EventPayload.builder()
			.name("Spring")
			.description("Spring Rest API")
			.beginEnrollmentDateTime(LocalDateTime.of(2020, 4, 28, 4, 1))
			.closeEnrollmentDateTime(LocalDateTime.of(2020, 4, 27, 4, 1))
			.beginEventDateTime(LocalDateTime.of(2020, 4, 2, 1, 1))
			.endEventDateTime(LocalDateTime.of(2020, 3, 1, 20, 1))
			.basePrice(10000)
			.maxPrice(200)
			.limitOfEnrollment(100)
			.location("강남 D2 스타트 팩토리")
			.build();

		mockMvc.perform(post("/api/events")
				.content(objectMapper.writeValueAsString(event))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("content[0].objectName").exists())
			.andExpect(jsonPath("content[0].defaultMessage").exists())
			.andExpect(jsonPath("content[0].code").exists())
			.andExpect(jsonPath("_links.index").exists());
	}
}
