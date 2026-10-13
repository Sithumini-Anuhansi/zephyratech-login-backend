package com.zephyratech.loginbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;
import com.zephyratech.loginbackend.repository.UserRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:login-backend-test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=false",
		"JWT_SECRET=0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
})
class LoginBackendApplicationTests {

	@Autowired
	private WebApplicationContext applicationContext;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void clearUsers() {
		userRepository.deleteAll();
	}

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

	@Test
	void registrationAndLoginNormalizeEmailAndIssueUsableToken() throws Exception {
		var registration = mockMvc().perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email":"  Alice@Example.COM ","password":"Passw0rd!"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("alice@example.com"))
				.andReturn();

		String token = JsonPath.read(registration.getResponse().getContentAsString(), "$.token");

		mockMvc().perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email":"  ALICE@example.com ","password":"Passw0rd!"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("alice@example.com"));

		mockMvc().perform(get("/api/auth/me")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("alice@example.com"));
	}

	@Test
	void registrationRejectsPasswordsShorterThanEightCharacters() throws Exception {
		mockMvc().perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email":"alice@example.com","password":"short"}
						"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void loginRejectsMalformedEmail() throws Exception {
		mockMvc().perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email":"not-an-email","password":"Passw0rd!"}
						"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void registrationRejectsEmailAlreadyRegisteredWithDifferentCase() throws Exception {
		mockMvc().perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email":"alice@example.com","password":"Passw0rd!"}
						"""))
				.andExpect(status().isCreated());

		mockMvc().perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email":"ALICE@example.com","password":"Passw0rd!"}
						"""))
				.andExpect(status().isConflict());
	}

	private MockMvc mockMvc() {
		return MockMvcBuilders.webAppContextSetup(applicationContext)
				.apply(springSecurity())
				.build();
	}
}
