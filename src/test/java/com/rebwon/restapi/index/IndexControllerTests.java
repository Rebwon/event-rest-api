package com.rebwon.restapi.index;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

import com.rebwon.restapi.common.ControllerTests;

public class IndexControllerTests extends ControllerTests {

	@Test
	void indexPage() throws Exception {
		mockMvc.perform(get("/api"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_links.events").exists())
			.andDo(document("index",
				links(
					linkWithRel("events").description("link to events")
				),
				responseFields(fieldWithPath("_links.events.href").description("link to events"))
			));
	}
}
