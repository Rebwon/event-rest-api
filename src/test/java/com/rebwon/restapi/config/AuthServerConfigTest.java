package com.rebwon.restapi.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.rebwon.restapi.accounts.AccountService;
import com.rebwon.restapi.common.AppProperties;
import com.rebwon.restapi.common.ControllerTests;

class AuthServerConfigTest extends ControllerTests{

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountService accountService;

	@Autowired
	AppProperties appProperties;

	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	void getAuthToken() throws Exception {
		this.mockMvc.perform(post("/oauth/token")
				.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
				.param("username", appProperties.getUserUsername())
				.param("password", appProperties.getUserPassword())
				.param("grant_type", "password"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("access_token").exists());
	}
}