package org.keycloak.quickstart;/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.quickstart.test.FluentTestsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link OAuth2ResourceServerApplication}.
 *
 * @author Josh Cummings
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthzResourceServerTest {

    public static final String KEYCLOAK_URL = "http://localhost:8180";

    private static FluentTestsHelper fluentTestsHelper;

    @AfterAll
	public static void cleanUp() {
        fluentTestsHelper.deleteRealm("quickstart");
	}

	@BeforeAll
	public static void onBeforeClass() {
		try {
			fluentTestsHelper = new FluentTestsHelper(KEYCLOAK_URL,
					FluentTestsHelper.DEFAULT_ADMIN_USERNAME,
					FluentTestsHelper.DEFAULT_ADMIN_PASSWORD,
					FluentTestsHelper.DEFAULT_ADMIN_REALM,
					FluentTestsHelper.DEFAULT_ADMIN_CLIENT,
					"quickstart")
					.init("/realm-import.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	MockMvc mvc;

	@Test
	void testValidBearerToken() throws Exception {
		this.mvc.perform(get("/").with(bearerTokenFor("alice")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, alice!")));
	}

	@Test
	void testOnlyPremiumUsers() throws Exception {
		this.mvc.perform(get("/protected/premium").with(bearerTokenFor("jdoe")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, jdoe!")));

		this.mvc.perform(get("/protected/premium").with(bearerTokenFor("alice")))
				.andExpect(status().isForbidden());
	}

	@Test
	void testInvalidBearerToken() throws Exception {
		this.mvc.perform(get("/"))
				.andExpect(status().isForbidden());
	}

	private RequestPostProcessor bearerTokenFor(String username) {
		String token = getToken(username, username);

		return new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.addHeader("Authorization", "Bearer " + token);
				return request;
			}
		};
	}

	public String getToken(String username, String password) {
		Keycloak keycloak = Keycloak.getInstance(
				"http://localhost:8180",
				"quickstart",
				username,
				password,
				"authz-servlet",
				"secret");
		return keycloak.tokenManager().getAccessTokenString();
	}
}
