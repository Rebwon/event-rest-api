package com.rebwon.restapi.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class EventControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void createEvent() throws Exception {
		mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON))
			.andExpect(status().isCreated());
	}
}
