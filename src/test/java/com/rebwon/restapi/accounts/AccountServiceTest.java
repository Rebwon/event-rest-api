package com.rebwon.restapi.accounts;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

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
		this.accountRepository.save(account);

		// when
		UserDetailsService userDetailsService = accountService;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// then
		assertThat(userDetails.getPassword()).isEqualTo(password);
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