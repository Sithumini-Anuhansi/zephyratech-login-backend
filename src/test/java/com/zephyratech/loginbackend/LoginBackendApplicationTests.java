package com.zephyratech.loginbackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LoginBackendApplicationTests {

	@Autowired
	private WebApplicationContext applicationContext;

	@Test
	void contextLoads() {
	}

	@Test
	void healthEndpointIsPublic() throws Exception {
		mockMvc().perform(get("/api/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void currentUserEndpointRequiresAuthentication() throws Exception {
		mockMvc().perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized());
	}

	private MockMvc mockMvc() {
		return MockMvcBuilders.webAppContextSetup(applicationContext)
				.apply(springSecurity())
				.build();
	}
}
