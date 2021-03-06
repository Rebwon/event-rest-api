package com.rebwon.restapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import com.rebwon.restapi.accounts.Account;
import com.rebwon.restapi.accounts.AccountRepository;
import com.rebwon.restapi.accounts.AccountRole;
import com.rebwon.restapi.accounts.AccountService;
import com.rebwon.restapi.common.AppProperties;
import com.rebwon.restapi.common.ControllerTests;

public class EventControllerTests extends ControllerTests {

	@Autowired
	EventRepository eventRepository;

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AppProperties appProperties;

	@BeforeEach
	void setUp() {
		eventRepository.deleteAll();
		accountRepository.deleteAll();
	}

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
					.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
					fieldWithPath("name").type(JsonFieldType.STRING).description("<<name,name of new event>>"),
					fieldWithPath("description").type(JsonFieldType.STRING).description("<<description,description of new event>>"),
					fieldWithPath("beginEnrollmentDateTime").type(JsonFieldType.STRING).description("<<beginEnrollmentDateTime,beginEnrollmentDateTime of new event>>"),
					fieldWithPath("closeEnrollmentDateTime").type(JsonFieldType.STRING).description("<<closeEnrollmentDateTime,closeEnrollmentDateTime of new event>>"),
					fieldWithPath("beginEventDateTime").type(JsonFieldType.STRING).description("<<beginEventDateTime,beginEventDateTime of new event>>"),
					fieldWithPath("endEventDateTime").type(JsonFieldType.STRING).description("<<endEventDateTime,endEventDateTime of new event>>"),
					fieldWithPath("basePrice").type(JsonFieldType.NUMBER).description("<<basePrice,basePrice of new event>>"),
					fieldWithPath("maxPrice").type(JsonFieldType.NUMBER).description("<<maxPrice,maxPrice of new event>>"),
					fieldWithPath("limitOfEnrollment").type(JsonFieldType.NUMBER).description("<<limitOfEnrollment,limitOfEnrollment of new event>>"),
					fieldWithPath("location").type(JsonFieldType.STRING).description("<<location,location of new event>>")
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
					fieldWithPath("manager.id").description("event manager id"),
					fieldWithPath("_links.self.href").description("link to self"),
					fieldWithPath("_links.query-events.href").description("link to query-events"),
					fieldWithPath("_links.update-event.href").description("link to update-event"),
					fieldWithPath("_links.profile.href").description("link to profile")
				)
			));
	}

	private String getBearerToken() throws Exception {
		return "Bearer" + getAccessToken(true);
	}

	private String getBearerToken(boolean needToAccount) throws Exception {
		return "Bearer" + getAccessToken(needToAccount);
	}

	private String getAccessToken(boolean needToAccount) throws Exception {
		// given
		if(needToAccount) {
			createAuthAccount();
		}

		ResultActions perform = this.mockMvc.perform(post("/oauth/token")
			.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
			.param("username", appProperties.getUserUsername())
			.param("password", appProperties.getUserPassword())
			.param("grant_type", "password"));

		var responseBody = perform.andReturn().getResponse().getContentAsString();
		Jackson2JsonParser parser = new Jackson2JsonParser();
		return parser.parseMap(responseBody).get("access_token").toString();
	}

	private Account createAuthAccount() {
		Account account = Account.builder()
			.email(appProperties.getUserUsername())
			.password(appProperties.getUserPassword())
			.roles(Set.of(AccountRole.USER, AccountRole.ADMIN))
			.build();
		return this.accountService.saveAccount(account);
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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.content(objectMapper.writeValueAsString(event))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("content[0].objectName").exists())
			.andExpect(jsonPath("content[0].defaultMessage").exists())
			.andExpect(jsonPath("content[0].code").exists())
			.andExpect(jsonPath("_links.index").exists())
			.andDo(document("errors",
				links(
					linkWithRel("index").description("link to index")
				),
				relaxedResponseFields(
					fieldWithPath("content[0].objectName").description("error objectName"),
					fieldWithPath("content[0].code").description("error code"),
					fieldWithPath("content[0].defaultMessage").description("error message"),
					fieldWithPath("_links.index.href").description("link to index")
				)
			));
	}

	@Test
	@DisplayName("기존 이벤트를 수정하려는 경우")
	void updateEvent() throws Exception {
		Account account = this.createAuthAccount();
		Event dbEvent = generateEvent(130, account);
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

		mockMvc.perform(put("/api/events/{id}", dbEvent.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("update-event",
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
					fieldWithPath("name").description("name of update event"),
					fieldWithPath("description").description("description of update event"),
					fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of update event"),
					fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of update event"),
					fieldWithPath("beginEventDateTime").description("beginEventDateTime of update event"),
					fieldWithPath("endEventDateTime").description("endEventDateTime of update event"),
					fieldWithPath("basePrice").description("basePrice of update event"),
					fieldWithPath("maxPrice").description("maxPrice of update event"),
					fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of update event"),
					fieldWithPath("location").description("location of update event")
				),
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
				),
				relaxedResponseFields(
					fieldWithPath("id").description("identifier of update event"),
					fieldWithPath("name").description("name of update event"),
					fieldWithPath("description").description("description of update event"),
					fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of update event"),
					fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of update event"),
					fieldWithPath("beginEventDateTime").description("beginEventDateTime of update event"),
					fieldWithPath("endEventDateTime").description("endEventDateTime of update event"),
					fieldWithPath("basePrice").description("basePrice of update event"),
					fieldWithPath("maxPrice").description("maxPrice of update event"),
					fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of update event"),
					fieldWithPath("location").description("location of update event"),
					fieldWithPath("free").description("it tells if this event is free or not"),
					fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
					fieldWithPath("eventStatus").description("event status"),
					fieldWithPath("_links.self.href").description("link to self"),
					fieldWithPath("_links.query-events.href").description("link to query-events"),
					fieldWithPath("_links.update-event.href").description("link to update-event"),
					fieldWithPath("_links.profile.href").description("link to profile")
				))
			);
	}

	@Test
	@DisplayName("없는 이벤트를 수정하려는 경우")
	void updateEvent_does_not_exist() throws Exception {
		EventPayload event = EventPayload.builder()
			.build();

		mockMvc.perform(put("/api/events/1234")
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.content(objectMapper.writeValueAsString(event))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
	void updateEvent_Bad_request_Wrong_Input() throws Exception {
		Event dbEvent = generateEvent(102);
		EventPayload event = EventPayload.builder()
			.build();

		mockMvc.perform(put("/api/events/{id}", dbEvent.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(event)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("content[0].objectName").exists())
			.andExpect(jsonPath("content[0].defaultMessage").exists())
			.andExpect(jsonPath("content[0].code").exists())
			.andExpect(jsonPath("_links.index").exists());
	}

	@Test
	@DisplayName("도메인 로직 검증에서 실패한 테스트")
	void updateEvent_Bad_request_domain_error() throws Exception {
		Event dbEvent = generateEvent(125);
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

		mockMvc.perform(put("/api/events/{id}", dbEvent.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(event)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("content[0].objectName").exists())
			.andExpect(jsonPath("content[0].defaultMessage").exists())
			.andExpect(jsonPath("content[0].code").exists())
			.andExpect(jsonPath("_links.index").exists());
	}

	@Test
	@DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	void queryEvents() throws Exception {
		IntStream.range(0, 30).forEach(this::generateEvent);

		mockMvc.perform(get("/api/events")
				.param("page", "1")
				.param("size", "10")
				.param("sort", "name,DESC"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("page").exists())
			.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists())
			.andDo(document("query-events",
				links(
					linkWithRel("first").description("link to query events first page"),
					linkWithRel("prev").description("link to query events previous page"),
					linkWithRel("self").description("self link to query events"),
					linkWithRel("next").description("link to query events next page"),
					linkWithRel("last").description("link to query events last page"),
					linkWithRel("profile").description("link to profile")
				),
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
				),
				relaxedResponseFields(
					fieldWithPath("_embedded.eventList[0].id").description("identifier of new event"),
					fieldWithPath("_embedded.eventList[0].name").description("name of new event"),
					fieldWithPath("_embedded.eventList[0].description").description("description of new event"),
					fieldWithPath("_embedded.eventList[0].beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
					fieldWithPath("_embedded.eventList[0].closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
					fieldWithPath("_embedded.eventList[0].beginEventDateTime").description("beginEventDateTime of new event"),
					fieldWithPath("_embedded.eventList[0].endEventDateTime").description("endEventDateTime of new event"),
					fieldWithPath("_embedded.eventList[0].basePrice").description("basePrice of new event"),
					fieldWithPath("_embedded.eventList[0].maxPrice").description("maxPrice of new event"),
					fieldWithPath("_embedded.eventList[0].limitOfEnrollment").description("limitOfEnrollment of new event"),
					fieldWithPath("_embedded.eventList[0].location").description("location of new event"),
					fieldWithPath("_embedded.eventList[0].free").description("it tells if this event is free or not"),
					fieldWithPath("_embedded.eventList[0].offline").description("it tells if this event is offline meeting or not"),
					fieldWithPath("_embedded.eventList[0].eventStatus").description("event status"),
					fieldWithPath("_embedded.eventList[0]._links.self.href").description("event status"),
					fieldWithPath("_links.first.href").description("link to query events first page"),
					fieldWithPath("_links.prev.href").description("link to query events previous page"),
					fieldWithPath("_links.self.href").description("self link to query events"),
					fieldWithPath("_links.next.href").description("link to query events next page"),
					fieldWithPath("_links.last.href").description("link to query events last page"),
					fieldWithPath("_links.profile.href").description("link to profile"),
					fieldWithPath("page.size").description("current events size"),
					fieldWithPath("page.totalElements").description("total events size"),
					fieldWithPath("page.totalPages").description("total events page size"),
					fieldWithPath("page.number").description("current page number")
				)
			));
	}

	@Test
	@DisplayName("인증된 사용자가 30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	void queryEventsWithAuthentication() throws Exception {
		IntStream.range(0, 30).forEach(this::generateEvent);

		mockMvc.perform(get("/api/events")
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.param("page", "1")
				.param("size", "10")
				.param("sort", "name,DESC"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("page").exists())
			.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists())
			.andExpect(jsonPath("_links.create-event").exists());
	}

	@Test
	@DisplayName("기존의 이벤트를 하나 조회하기")
	void findEvent() throws Exception {
		Event event = this.generateEvent(100);

		mockMvc.perform(get("/api/events/{id}", event.getId()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").exists())
			.andExpect(jsonPath("id").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists())
			.andDo(document("get-event",
				links(
					linkWithRel("self").description("link to self"),
					linkWithRel("profile").description("link to profile")
				),
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
				),
				relaxedResponseFields(
					fieldWithPath("id").description("identifier of update event"),
					fieldWithPath("name").description("name of update event"),
					fieldWithPath("description").description("description of update event"),
					fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of update event"),
					fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of update event"),
					fieldWithPath("beginEventDateTime").description("beginEventDateTime of update event"),
					fieldWithPath("endEventDateTime").description("endEventDateTime of update event"),
					fieldWithPath("basePrice").description("basePrice of update event"),
					fieldWithPath("maxPrice").description("maxPrice of update event"),
					fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of update event"),
					fieldWithPath("location").description("location of update event"),
					fieldWithPath("free").description("it tells if this event is free or not"),
					fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
					fieldWithPath("eventStatus").description("event status"),
					fieldWithPath("_links.self.href").description("link to self"),
					fieldWithPath("_links.profile.href").description("link to profile")
				)
				));
	}

	@Test
	@DisplayName("인증된 사용자가 기존의 이벤트를 하나 조회하기")
	void findEventWithAuthentication() throws Exception {
		Account account = this.createAuthAccount();
		Event event = this.generateEvent(100, account);

		mockMvc.perform(get("/api/events/{id}", event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken(false)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").exists())
			.andExpect(jsonPath("id").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists())
			.andExpect(jsonPath("_links.update-event").exists());
	}

	@Test
	@DisplayName("없는 이벤트를 조회했을 때 404 응답하기")
	void findEvent_does_not_exist() throws Exception {
		mockMvc.perform(get("/api/events/121"))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	private Event generateEvent(int i, Account account) {
		Event event = buildEvent(i);
		event.setManager(account);
		return this.eventRepository.save(event);
	}

	private Event generateEvent(int i) {
		Event event = buildEvent(i);
		return this.eventRepository.save(event);
	}

	private Event buildEvent(int i) {
		return Event.builder()
			.name("event " + i)
			.description("test event")
			.build();
	}
}
