package com.rebwon.restapi.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.rebwon.restapi.accounts.Account;
import com.rebwon.restapi.accounts.AccountRole;
import com.rebwon.restapi.accounts.AccountService;
import com.rebwon.restapi.common.ControllerTests;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthServerConfigTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountService accountService;

	@Test
	@DisplayName("인증 토큰을 발급 받는 테스트")
	void getAuthToken() throws Exception {
		// given
		String username = "rebwon@gmail.com";
		String password = "rebwon";
		Account account = Account.builder()
			.email(username)
			.password(password)
			.roles(Set.of(AccountRole.USER, AccountRole.ADMIN))
			.build();
		this.accountService.saveAccount(account);

		String clientId = "myApp";
		String clientSecret = "pass";

		this.mockMvc.perform(post("/oauth/token")
				.with(httpBasic(clientId, clientSecret))
				.param("username", username)
				.param("password", password)
				.param("grant_type", "password"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("access_token").exists());
	}
}