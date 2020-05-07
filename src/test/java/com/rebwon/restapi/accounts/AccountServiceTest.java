package com.rebwon.restapi.accounts;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rebwon.restapi.common.ControllerTests;

class AccountServiceTest extends ControllerTests {

	@Autowired
	AccountService accountService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	void loadUserByUsername() {
		// given
		String username = "rebwon@gmail.com";
		String password = "rebwon";
		Account account = Account.builder()
			.email(username)
			.password(password)
			.roles(Set.of(AccountRole.USER, AccountRole.ADMIN))
			.build();
		this.accountService.saveAccount(account);

		// when
		UserDetailsService userDetailsService = accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// then
		assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
	}

	@Test
	void loadUserByUsernameFail() {
		// given
		String username = "random@gmail.com";

		// when
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
			accountService.loadUserByUsername(username);
		});

		// then
		assertEquals(username, exception.getMessage());
	}
}